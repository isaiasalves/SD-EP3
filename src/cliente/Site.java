 
package cliente;

import java.util.ArrayList;
 
public class Site {
    private String url;
    private ArrayList<String> links = new ArrayList<>();

    public Site(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public void setLinks(String link ) {
        this.links.add(link);
    }
    
    public void setLinks(ArrayList<String> links ) {
        this.links = links;
    }
    
    
    
}
