import java.io.*;
import static java.lang.Math.*;
import java.util.*;

/**
 *
 * @author JYOTHIPRASAD
 */
// P is the set of all pages; |P| = N
// S is the set of sink nodes, i.e., pages that have no out links
// M(p) is the set (without duplicates) of pages that link to page p
// L(q) is the number of out-links (without duplicates) from page q
// d is the PageRank damping/teleportation factor; here d = 0.85

public class pagerank {
    static String fileName;
    static Set<String> pages = new HashSet<>();
    static Set<String> sinkNodes = new HashSet<>();
    static Set<String> sourceNodes = new HashSet<>();
    static LinkedHashMap<String, Set> inlink = new LinkedHashMap();
    static LinkedHashMap<String, Integer> noOfOutlink = new LinkedHashMap();
    static LinkedHashMap<String, Double> PR = new LinkedHashMap();
    static final double d = 0.85;
    static final double log2 = log(2);
    static int N;
    
    public static void main(String args[]) throws IOException{
        fileName = args[0];
        BufferedReader br;
        String line;
        
        try{
            br = new BufferedReader(new FileReader(fileName));
            while((line = br.readLine()) != null)
                updateData(line);
            br.close();
        }
        catch(FileNotFoundException ex){
            System.out.println(ex);
            System.exit(0);
        }
        for(String page : pages){
            if(!noOfOutlink.containsKey(page))
                sinkNodes.add(page);
        }
        N = pages.size();
        findRanks();
        LinkedHashMap<String, Double> sortedPR = sortByValue(PR, false);
        for(String page : sortedPR.keySet()){
            System.out.println(page+"\t"+sortedPR.get(page));
        }
    }
    
    public static void updateData(String entry){
        String[] curPage = entry.split(" ");
        pages.add(curPage[0]);
        if(curPage.length == 1)
            sourceNodes.add(curPage[0]);
        else{
            Set<String> linkedPages;
            if(inlink.containsKey(curPage[0]))
                linkedPages = new HashSet<>(inlink.get(curPage[0]));
            else
                linkedPages = new HashSet<>();
            linkedPages.addAll(Arrays.asList
        (Arrays.copyOfRange(curPage, 1, curPage.length)));
            inlink.put(curPage[0], linkedPages);
            for(String page : linkedPages){
                int number = 1;
                if(noOfOutlink.containsKey(page))
                    number = noOfOutlink.get(page)+1;
                noOfOutlink.put(page, number);
            }
        }
    }
    
    public static void findRanks(){
        LinkedHashMap<String, Double> newPR = new LinkedHashMap();
        for(String page : pages)
            PR.put(page, (double)1/N);

        int perplexityChange = 0;
        double perplexity = 0;
        
        while(perplexityChange<4){
            double sinkPR = 0;
            for(String sink : sinkNodes)
                sinkPR += PR.get(sink);
            
            for(String p : pages){
                newPR.put(p, (double)(1-d)/N);
                double t1 = newPR.get(p)+(d*sinkPR/N);
                newPR.put(p, t1);
                Set<String> Mp;
                if((Mp=inlink.get(p)) != null){
                    for(String q : Mp){
                        double t2 = newPR.get(p)+d*PR.get(q)/noOfOutlink.get(q);
                        newPR.put(p,t2);
                    }
                }
            }
            PR.putAll(newPR);
            double perp = calcPerplexity();
            if(abs(perp-perplexity) <= 1)
                perplexityChange++;
            else
                perplexityChange=0;
            perplexity = perp;
        }
    }
    
    public static double calcPerplexity(){
        double p=0;
        for(String page : PR.keySet())
            p -= PR.get(page)*(log(PR.get(page))/log2);     
        return pow(2,p);
    }

    /**
     * @param <K> key
     * @param <V> value
     * @param map map of key-value pairs
     * @param order boolean value: true - ascending; false - descending
     * @return a sorted map, sorted by the value
     */
    public static <K, V extends Comparable<? super V>> 
        LinkedHashMap<K, V> sortByValue(Map<K, V> map, boolean order){
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return order ? 
                        (o1.getValue()).compareTo(o2.getValue()): 
                        (o2.getValue()).compareTo(o1.getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
            result.put( entry.getKey(), entry.getValue() );
        return result;
    }
}