package cliente;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Mapper1 {

    private String ip = "127.0.0.1";
    private static int porta = 9902;
    public static ArrayList<Site> listaDeSites = new ArrayList<>();
    private static DatagramSocket serverSocket;

    public static void main(String[] args) throws Exception {
         System.out.println("************************************************************ MAPPER1 OUVINDO NA PORTA: " + porta + "  ************************************************************");
        
        serverSocket = new DatagramSocket(porta);
         
         

        Recebe();
    }

    public static Requisicao escuta(int porta) throws Exception {
        //System.out.println("Escutando na porta: " + porta);
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
        //ArrayList<Requisicao> listaDeRequisicao = new ArrayList<>();
        Requisicao rec = gson.fromJson(informacao, Requisicao.class);
        System.out.println(">>> RECEBIDO: " + rec + "\n");

        return rec;
    }

    public static void Recebe() throws Exception {
        
        //ArrayList<Requisicao> listaDeRequisicao = new ArrayList<Requisicao>();
        Requisicao rec = new Requisicao();
        rec = escuta(porta);
        Document doc = null;

            //for (Requisicao rec : listaDeRequisicao) {

                for (String url : rec.getURLS()) {
                    System.out.println("Site na varredura:" + url);
                    //String url = "https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html";

                    //CRIA UM OBJETO SITE QUE IRÁ CONTER O SITE A SER VASCULHADO E OS LINKS ENCONTRADOS
                    Site siteAtual = new Site(url);
                    try {
                        doc = Jsoup.connect(url).get();
                    } catch (Exception e) {
                        System.out.println("Não foi possível acessar o link"+e );
                    }
                    
                    Elements links = doc.select("a[href]");
                    print("\nLinks: (%d)", links.size());

                    //CHAMA ROTINA QUE IRÁ VASCULHAR TODA A URL ATUAL E RETORNARÁ OS LINKS ENCONTRADOS
                    siteAtual.setLinks(buscaLinks(links));
                    //ADICIONA À NOSSA LISTA A URL EM QUESTÃO COM TODOS OS LINKS QUE ELA POSSUI
                    listaDeSites.add(siteAtual);
                }
             

          for (Site s : listaDeSites) {
            if (s.getUrl().isEmpty() == false) {
                System.out.println(s.getUrl());
                for (String link : s.getLinks()) {
                    System.out.println("    " + link);
                }
            }

        }
    }

//***************************************************************************************************************************************//    
    private static ArrayList<String> buscaLinks(Elements links) {
        ArrayList<String> linksEncontrados = new ArrayList<>();
        for (Element link : links) {
            //ADICIONA UM A UM OS LINKS ENCONTRADOS
            linksEncontrados.add(link.attr("abs:href"));
        }
        return linksEncontrados;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width - 1) + ".";
        } else {
            return s;
        }
    }
}
