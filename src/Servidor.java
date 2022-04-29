import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collections;

public class Servidor extends Thread {

    private final DatagramSocket socket;
    private byte[] buffer = new byte[65000];

    Servidor(String endereco) throws IOException {
        String[] valores = endereco.split(":");
        this.socket = new DatagramSocket(Integer.parseInt(valores[1]), InetAddress.getByName(valores[0]));
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket pacote = new DatagramPacket(buffer, 0, buffer.length);
                socket.receive(pacote);
                String mensagem = new String(pacote.getData());
                processarMensagem(mensagem);
                buffer = new byte[65000];
            } catch (Exception ignored) {

            }
        }
    }

    private void processarMensagem(String mensagem) throws IOException {
        JsonObject mensagemJson = Json.createReader(new StringReader(mensagem)).readObject();
        StringBuilder stringBuilder = new StringBuilder();

        if (mensagemJson.containsKey(Utils.COMANDO)) {
            System.out.println("======================= EXECUTANDO COMANDO '" + mensagemJson.getString(Utils.COMANDO) + "' =======================");
            try {
                Process resultado = Runtime.getRuntime().exec(mensagemJson.getString(Utils.COMANDO));
                BufferedReader reader = Utils.criarBufferedReader(resultado.getInputStream());

                String linha;
                while ((linha = reader.readLine()) != null) {
                    stringBuilder.append(linha).append("\n");
                    System.out.println(linha);
                }

                resultado.waitFor();
                System.out.println("exit: " + resultado.exitValue());
                resultado.destroy();
                System.out.println("- FIM EXECUCAO -");
                enviarMensagemDeVolta(stringBuilder.toString(), mensagemJson.getString(Utils.SERVIDOR));
            } catch (IOException | InterruptedException e) {
                System.out.println("Erro na execução do comando");
                enviarMensagemDeVolta("Erro na exeução do comando", mensagemJson.getString(Utils.SERVIDOR));
            }

        } else if (mensagemJson.containsKey(Utils.RESPOSTA)) {
            System.out.println("========================= RETORNO DE [" + mensagemJson.getString(Utils.NOME_PEER) + "] =========================");
            System.out.println(mensagemJson.getString(Utils.RESPOSTA));
            System.out.println("- FIM RETORNO -");
        }
        System.out.print("> ");

    }

    private void enviarMensagemDeVolta(String resposta, String endereco) throws IOException {
        StringWriter string = new StringWriter();

        try (JsonWriter json = Json.createWriter(string)) {
            json.writeObject(
                    Json.createObjectBuilder()
                            .add(Utils.SERVIDOR, endereco)
                            .add(Utils.NOME_PEER, Peer.nomePeer)
                            .add(Utils.RESPOSTA, resposta)
                            .build()
            );
        }

        Peer.cliente.enviarMensagem(string.toString(), Collections.singletonList(endereco));
    }


}
