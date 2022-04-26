import javax.json.Json;
import javax.json.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Peer {

    public static Servidor servidor;
    public static String nomePeer;
    private static BufferedReader leitor;
    private static String enderecoCliente;
    private static List<String> enderecos;

    public static void main(String[] args) throws IOException {
        leitor = Utils.criarBufferedReader(System.in);
        enderecos = new ArrayList<>();
        System.out.print("> Digite um nome para este peer: ");
        nomePeer = leitor.readLine();

        System.out.print("> Digite o número da porta para o servidor deste peer: ");
        String portaPeer = leitor.readLine();

        servidor = new Servidor(portaPeer);

        System.out.print("> Digite o número da porta para a qual o cliente deste peer deve receber mensagens: ");
        String porta = leitor.readLine();
        String hostname = InetAddress.getLocalHost().getHostAddress();
        enderecoCliente = hostname + ":" + porta;
        new Cliente(hostname, porta).start();

        atualizarPeersConectados();
    }

    private static void atualizarPeersConectados() throws IOException {
        System.out.println("> Digite, separados por espaço, os enderecos que deseja enviar mensagens (e.g. 123.12.123:1111 321.21.321:2222): ");
        System.out.print("> ");

        String resposta = leitor.readLine();

        Collections.addAll(enderecos, resposta.split("\\s"));

        iniciarComunicacao();
    }

    private static void iniciarComunicacao() {
        try {
            System.out.println("> Você pode começar a comunicação agora. Digite 's' para sair ou 'a' para adicionar peers");
            System.out.print("> ");
            while (true) {
                String comando = leitor.readLine();
                if (comando.equals("s")) {
                    break;
                } else if (comando.equals("a")) {
                    atualizarPeersConectados();
                } else {
                    enviarComando(comando);
                }
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarComando(String comando) throws IOException {
        StringWriter string = new StringWriter();

        try (JsonWriter json = Json.createWriter(string)) {
            json.writeObject(
                    Json.createObjectBuilder()
                            .add(Utils.SERVIDOR, enderecoCliente)
                            .add(Utils.NOME_PEER, nomePeer)
                            .add(Utils.COMANDO, comando)
                            .build()
            );
        }

        servidor.enviarMensagem(string.toString(), enderecos);
    }

}
