import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Servidor {

    public final DatagramSocket socketDoServidor;

    Servidor(String porta) throws IOException {
        this.socketDoServidor = new DatagramSocket(Integer.parseInt(porta));
    }

    public void enviarMensagem(String comando, List<String> portas) throws IOException {
        DatagramPacket pacote;
        for (String porta : portas) {

            pacote = new DatagramPacket(comando.getBytes(StandardCharsets.ISO_8859_1),
                    comando.length(),
                    InetAddress.getLocalHost(),
                    Integer.parseInt(porta)
            );

            socketDoServidor.send(pacote);
        }
    }

}
