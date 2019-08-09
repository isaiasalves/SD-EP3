 
package cliente;

import java.util.ArrayList;

public class Requisicao {
    private String id;
    private ArrayList<String> URLS = new ArrayList<String>();

//    public Requisicao (String id){
//        this.id = id;
//    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getURLS() {
        return URLS;
    }

    public void setURL( String  URL ) {
        this.URLS.add(URL);
    }
    
    public void delURL(int index)
    {
        this.URLS.remove(index);
    }

}
