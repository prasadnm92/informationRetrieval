import java.io.IOException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author JYOTHIPRASAD
 */
public class WebCrawler {
    
    static final int CRAWLED_PAGES = 1000;
    static final int DEPTH_LIMIT = 5;
    static final int WAIT_TIME = 1000; //number of milliseconds
    static final boolean doPrint = false; //true if output needs to be printed
     
    static List<String> visited = new ArrayList<>();
    static LinkedHashMap<String, Integer> frontier = new LinkedHashMap();
    static LinkedHashMap<String, Integer> childLinks = new LinkedHashMap();

    public static void main(String[] argv) throws IOException, InterruptedException {
        int argc = argv.length;
        String seed = "", keyphrase = "";
        
        if(argc == 2){
            seed = argv[0];
            keyphrase = argv[1];
        }
        else if(argc == 1) 
            seed = argv[0];
        else
            if(doPrint)
                System.out.println ("Invalid number of arguments\nUsage: webcrawler.java <seed URL> [<keyphrase>]");
        
        if(doPrint) System.out.println("Seed: " + seed + "\nKeyphrase: " + keyphrase);
        frontier.put(seed, 1);
        webcrawl(keyphrase);
        if(doPrint){
        for(String link : visited){
            System.out.println(link);
        }
        System.out.println("Total crawled: "+visited.size());}
    }
    
    public static void webcrawl(String keyphrase) throws IOException, InterruptedException{
        int curr_depth;
        doCrawl:
        while(!(frontier.size()==0 || visited.size()==CRAWLED_PAGES)){
            String crawl_link = "";
            getNextLink:
            while(true){
                crawl_link = getFirst(frontier);
                curr_depth = frontier.get(crawl_link);
                if(crawl_link.contentEquals("")) //empty frontier
                    break doCrawl;
                if(visited.contains(crawl_link)){
                    frontier.remove(crawl_link);
                    continue getNextLink;
                }
                break;
            }
            crawl(crawl_link, curr_depth, keyphrase);
            TimeUnit.SECONDS.sleep(1);
        }
    }
    
    public static void crawl(String seed,int curr_depth, String keyphrase) throws MalformedURLException, IOException {
        URL url = new URL(seed);
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        if (url.getProtocol().contentEquals("http"))
            url = new URL("https://"+url.getHost()+url.getPath());
            
        //Retrieve the HTML file
        br = new BufferedReader(new InputStreamReader(url.openStream()));
        String response;
        while((response = br.readLine()) != null)
            sb.append(response);
        br.close();
        response = sb.toString();
        //Normal Crawl
        if(keyphrase.contentEquals("")){
            visited.add(seed);
            //System.out.println(visited.size()+") "+seed+"-------"+frontier.get(seed));
        }
        //Focused Crawl
        else{
            if(focusedCrawl(response, keyphrase)){
                visited.add(seed);
                //System.out.println(visited.size()+") "+seed+"-------"+frontier.get(seed));
            }
            else{
                frontier.remove(seed);
                return;
            }
        }
          
        //Parse the HTML document to get links at this page
        if(curr_depth<DEPTH_LIMIT){
            Document doc = Jsoup.parse(response);
            Elements links = doc.select("a[href]");
            StringBuilder href;
            for(Element link : links){
                href = new StringBuilder(link.attr("href"));
                if(!href.toString().startsWith("#"))
                    if(validateURL(href.toString())){
                        checkProtocol(href);
                        childLinks.put(href.toString(), curr_depth+1);
                    }
                href.setLength(0);
            }

            for(String each : childLinks.keySet()){
                if(frontier.containsKey(each) || visited.contains(each))
                    continue;
                frontier.put(each, childLinks.get(each));
            }                
            childLinks.clear();
        }  
        frontier.remove(seed);
    }
    
    public static boolean focusedCrawl(String response, String keyphrase){
        Document doc = Jsoup.parse(response);
        String html_text = doc.text().toLowerCase();
        return(html_text.contains(keyphrase));
    }
    
    public static boolean validateURL(String urlString) throws MalformedURLException{
        
        StringBuilder urlSb = new StringBuilder(urlString);
        checkProtocol(urlSb);
        urlString = urlSb.toString();
        
        URL url = new URL(urlString);
        String host = url.getHost();
        String path = url.getPath();

        boolean con1 = host.contentEquals("en.wikipedia.org");
        boolean con2 = path.startsWith("/wiki/");
        boolean con3 = !path.contains(":");
        boolean con4 = !path.contentEquals("/wiki/Main_Page");
        boolean con5 = !path.contains("#");
        return (con1 && con2 && con3 && con4 && con5);
    }
 
    public static void checkProtocol(StringBuilder urlSb){
        String urlString = urlSb.toString();
        if(!urlString.startsWith("http")){
            if(urlString.startsWith("//"))
                urlString = "https:"+urlString;
            else if(urlString.startsWith("/"))
                urlString = "https://en.wikipedia.org"+urlString;    
        }
        if(urlString.startsWith("http") && !urlString.startsWith("https"))
            urlString = "https"+urlString.substring(4);
        urlSb.setLength(0);
        urlSb.append(urlString);
    }
    
    static String getFirst(LinkedHashMap<String, Integer> map){
        String first = "";
        for(String each : map.keySet()){
            first = each;
            break;
        }
        return first;
    }
}