import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Client {

    public final DatagramSocket socketDoServidor;

    Client(String porta) throws IOException {
        this.socketDoServidor = new DatagramSocket(Integer.parseInt(porta));
    }

    public void enviarMensagem(String comando, List<String> enderecos) throws IOException {
        DatagramPacket pacote;
        for (String endereco : enderecos) {
            String[] end = endereco.split(":");
            pacote = new DatagramPacket(comando.getBytes(StandardCharsets.ISO_8859_1),
                    comando.length(),
                    InetAddress.getByName(end[0]),
                    Integer.parseInt(end[1])
            );

            socketDoServidor.send(pacote);
        }
    }

}
