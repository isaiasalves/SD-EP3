package cliente;

import java.util.ArrayList;

public class Reducer {

    public static ArrayList<String> al1 = new ArrayList<>();
    public static ArrayList<String> al2 = new ArrayList<>();

    public static void main(String Args[]) {
       ArrayList<String> al3 = new ArrayList<>();
       al1.add("a");
       al2.add("b");
       
        System.out.println("Tamanho de al1="+al1.size());
        System.out.println("Tamanho de al2="+al2.size());
     
        
        al2.addAll(al1);
        
        System.out.println("Tamanho de al2="+al2.size());
    }
}
