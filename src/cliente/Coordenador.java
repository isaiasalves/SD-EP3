package cliente;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

 
public class Coordenador {

    private static int portaAtualUDP = 9901;  //PORTA UTILIZADA PARA COMUNICAÇÃO VIA UDP COM OS PEERS
    private static String ipatual = "127.0.0.1"; //IP LOCAL UTILIZADO PARA COMUNICAÇÃO
    private static DatagramSocket serverSocket;
    private static ArrayList<Requisicao> listaDeRequisicoes = new ArrayList<>();
    private static String[] Mappers = new String[3];

    public static void main(String args[]) throws SocketException {
        System.out.println("************************************************************ COORDENADOR OUVINDO NA PORTA: " + portaAtualUDP + "  ************************************************************");
        serverSocket = new DatagramSocket(portaAtualUDP);
        
        Requisicao m1 = new Requisicao(); //REQUISIÇÃO PARA O MAPPER1
        Requisicao m2 = new Requisicao(); //REQUISIÇÃO PARA O MAPPER2
        Requisicao m3 = new Requisicao(); //REQUISIÇÃO PARA O MAPPER3
        listaDeRequisicoes.add(m1);
        listaDeRequisicoes.add(m2);
        listaDeRequisicoes.add(m3);

        //CONFIGURAÇÃO MANUAL DOS MAPPERS
        Mappers[0] = "127.0.0.1:9902";
        Mappers[1] = "127.0.0.1:9903";
        Mappers[2] = "127.0.0.1:9904";
        
        escuta();
    }

    public static Requisicao Recebe(int porta) throws Exception {
        //System.out.println("Escutando na porta: "+porta);
        // declaração do buffer de recebimento (caso haja)
        byte[] recBuffer = new byte[1024];
        // criação do datagrama a ser recebido
        DatagramPacket recPkt = new DatagramPacket(recBuffer, recBuffer.length);
        // recebimento do datagrama do host remoto (método bloqueante)
        serverSocket.receive(recPkt);
        // obtenção da informação vinda no datagrama
        String informacao = new String(recPkt.getData(), recPkt.getOffset(), recPkt.getLength());

        //TRANSFORMA A INFORMAÇÃO RECEBIDA EM JSON
        Gson gson = new GsonBuilder().create();
        Requisicao rec = gson.fromJson(informacao, Requisicao.class);
        System.out.println(">>> RECEBIDO: " + rec + "\n");

        return rec;
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
        System.out.println("\n Thread " + Thread.currentThread().getName() + " - Enviando: " + sendData + " ao MAPPER :" + ip + ":" + porta);
    }

    public static void escuta() {
        Thread T0 = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Requisicao rec = Recebe(portaAtualUDP);
                        //APÓS RECEBIDA A REQUISIÇÃO,IREMOS REPASSA-LA AOS MAPPERS
                        encaminhaRequisicao(rec);
                        System.out.println("\nREQUISICAO RECEBIDA" + rec);
                    } catch (Exception ex) {
                        System.out.println("Erro ao receber mensagem (1)" + ex);
                    }

                    try {
                        TimeUnit.MICROSECONDS.sleep(5);
                    } catch (InterruptedException ex) {
                        System.out.println("Erro ao receber mensagem (2)" + ex);
                    }
                }
            }

        });
        T0.start();
    }

    private static void encaminhaRequisicao(Requisicao rec) throws Exception {
        //CADA MAPPER DEVE CONHECER O ID DO CLIENTE QUE FEZ A REQUISIÇÃO
        listaDeRequisicoes.get(0).setId(rec.getId());
        listaDeRequisicoes.get(1).setId(rec.getId());
        listaDeRequisicoes.get(2).setId(rec.getId());

        int qtdMappers = listaDeRequisicoes.size();
        int qtdURLs = rec.getURLS().size();
        int j = 0;

        //FAZ A DIVISÃO DOS LINKS A SER RECEBIDO POR CADA UM DOS MAPPERS
        for (int i = 0; i < qtdURLs; i++) {
            System.out.printf("\nMapper %d possui a URL: %s", j, rec.getURLS().get(i));
            listaDeRequisicoes.get(j).setURL(rec.getURLS().get(i));
            j++;
            if (j == qtdMappers) {
                j = 0;
            }
        }
        //DISTRUBUI PARA CADA UM DOS MAPPERS
         Gson gson = new GsonBuilder().create();
         String json = gson.toJson(listaDeRequisicoes.get(0), Requisicao.class);
         Envia(json, retornaIp(0), retornaPorta(0));
    }
    
    
    
    public static synchronized String retornaIp(int index) {
        String[] desmembra = Mappers[index].split(":");
        return desmembra[0];
    }

    public static synchronized int retornaPorta(int index) {
        String[] desmembra = Mappers[index].split(":");
        return Integer.parseInt(desmembra[1]);
    }
}
