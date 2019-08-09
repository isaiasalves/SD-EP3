package cliente;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
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
        serverSocket = new DatagramSocket(porta);
        Mapper1 mapper = new Mapper1();
        mapper.listaDeSites.add(null);
        
        String url = "https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html";
         
        //CRIA UM OBJETO SITE QUE IRÁ CONTER O SITE A SER VASCULHADO E OS LINKS ENCONTRADOS
        Site siteAtual = new Site(url);
        
        Document doc = Jsoup.connect(url).get();
         
        Elements links = doc.select("a[href]");
        // Elements media = doc.select("[src]");
        // Elements imports = doc.select("link[href]");
         
        print("\nLinks: (%d)", links.size());
        //        for (Element link : links) {
        //            //print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        //            
        //            //ADICIONA UM A UM OS LINKS ENCONTRADOS
        //            siteAtual.setLinks(link.attr("abs:href"));
        //        }
         
//        siteAtual.setLinks(buscaLinks(links));
//        listaDeSites.add(siteAtual);
//        
//        
//        for (String s : siteAtual.getLinks())
//        {
//            System.out.println(s);
//        }
        
        
        Recebe(porta);
    }
    
    
            
    public static ArrayList<Requisicao> Recebe(int porta) throws Exception {
        System.out.println("Escutando na porta: "+porta);
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
        ArrayList<Requisicao> listaDeRequisicoes = new ArrayList<>();
        Requisicao rec = gson.fromJson(informacao, Requisicao.class);
        System.out.println(">>> RECEBIDO: " + rec + "\n");

        return listaDeRequisicoes;
    }
    
    
//***************************************************************************************************************************************//    
    private static ArrayList<String> buscaLinks(Elements links){
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
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
 }
