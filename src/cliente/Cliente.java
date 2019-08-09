package cliente;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Cliente {

    private static Requisicao clienteRequisicao;
    private static int porta = 9975; //PORTA UTILIZADA PARA COMUNICAÇÃO VIA UDP COM OS PEERS
    private static String ipatual = "127.0.0.1"; //IP LOCAL UTILIZADO PARA COMUNICAÇÃO

    private static String ipCoordenador = "127.0.0.1";
    private static int portaCoordenador = 9901;

    public static void main(String args[]) throws Exception {
        clienteRequisicao = new Requisicao();
        clienteRequisicao.setId(ipatual + ":" + porta);
        String arquivo = "C:\\Dir_Projeto3\\URLS.txt";
        carregaURLs(arquivo);

        //UMA VEZ COM A LISTA DE URLS CARREGADA, ENVIAMOS AO COORDENADOR UM OBJETO REQUISIÇÃO
        if (clienteRequisicao.getURLS().isEmpty() == false) {
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(clienteRequisicao, Requisicao.class);
            Envia(json, ipCoordenador, portaCoordenador);
        }

    }

    public static void Envia(String sendData, String ip, int porta) throws Exception {
        // endereço IP do host remoto (server)
        InetAddress IPAddress = InetAddress.getByName(ip);
        // canal de comunicação não orientado à conexão
        DatagramSocket clientSocket = new DatagramSocket();
        // criação do datagrama
        DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.length(), IPAddress, porta);
        // envio do datagrama ao host remoto
        clientSocket.send(sendPacket);
        //salva a porta que o cliente utilizou para enviar a mensagem
        System.out.println("\n Thread " + Thread.currentThread().getName() + " - Enviando: " + sendData + " ao peer :" + ip + ":" + porta);
    }

    public static void carregaURLs(String arquivo) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(arquivo));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);

            clienteRequisicao.setURL(line);

        }

    }

}
