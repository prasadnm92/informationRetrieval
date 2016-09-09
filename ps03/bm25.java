
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author JYOTHIPRASAD
 */
class Query{
    static LinkedHashMap<Integer, List<String>> queries = new LinkedHashMap<>();
    
    public Query(String queryFile) throws IOException{
        BufferedReader q = new BufferedReader(new FileReader(queryFile));
        int qID = 1;
        String query;
        while((query = q.readLine())!=null){
            List<String> qStrings = new ArrayList<>();
            qStrings.addAll(Arrays.asList(query.split(" ")));
            queries.put(qID++, qStrings);
        }
        q.close();
        //System.out.println("Queries:"+queries);
    }
    
    public LinkedHashMap<Integer, List<String>> getQueries(){
        return queries;
    }
}

public class bm25 {
    static LinkedHashMap<String, HashMap<Integer,Integer>> terms = new LinkedHashMap();
    static HashMap<Integer, Integer> docLength = new HashMap();
    static HashMap<Integer, LinkedHashMap<Integer, Double>> queryBM25 = new HashMap<>();
    static HashMap<Integer, List<String>> queries = new HashMap<>();
    static final double k1 = 1.2;
    static final double k2 = 100.0;
    static final double b = 0.75;
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException, ParseException{
        
        String indexFile = args[0];
        String queryFile = args[1];
        int ranks = Integer.parseInt(args[2]);
        
        Query q = new Query(queryFile);
        queries = q.getQueries();
        
        JSONParser parser = new JSONParser();
        JSONArray indexArr = (JSONArray) parser.parse(new FileReader(indexFile));
        updateTerms(indexArr);

        computeRanks();
        getTopRanks(ranks);
    }
    
    static void updateTerms(JSONArray indexArr){
        Iterator<JSONObject> iter1 = indexArr.iterator();
        while(iter1.hasNext()){
            JSONObject eachTerm = iter1.next();
            JSONArray eachDocs;
            HashMap<Integer, Integer> termDocs = new HashMap<>();
            Object t = eachTerm.keySet().toArray()[0];
            String thisTerm = t.toString();
            Object docs = eachTerm.get(t);
            eachDocs = (JSONArray) docs;
            Iterator<JSONObject> iter2 = eachDocs.iterator();
            while(iter2.hasNext()){
                JSONObject eachDoc = iter2.next();
                
                Object d = eachDoc.keySet().toArray()[0];
                Integer docID = Integer.parseInt(d.toString());
                
                Object dVal = eachDoc.get(d);
                Integer tf = Integer.parseInt(dVal.toString());
                
                termDocs.put(docID, tf);
                
                updateDocLengths(docID, tf);
            }
            terms.put(thisTerm, termDocs);
        }
    }
    
    static void updateDocLengths(Integer docID, Integer freq){
        Integer newFreq = freq;
        if(docLength.keySet().contains(docID))
            newFreq += docLength.get(docID);
        docLength.put(docID,newFreq);
    }
    
    static void getTopRanks(int r){
        for(Integer qID : queryBM25.keySet()){
            int i = 1;
            LinkedHashMap<Integer, Double> scores = queryBM25.get(qID);
            for(Integer docID : scores.keySet()){
                System.out.println(qID +" Q0 "+ docID +" "+ i +" "+ scores.get(docID) +" namamahesh.j");
                if(++i>r) break;
            }
        }
    }
    
    static void computeRanks(){
        double N = (double)docLength.size();
        double avdl = 0.0;
        for(Integer doc : docLength.keySet())
            avdl+=docLength.get(doc);
        avdl/=N;

        for(Integer qID : queries.keySet()){
            HashMap<Integer, Double> qBM25 = computeBM25(queries.get(qID), N, avdl);
            LinkedHashMap<Integer, Double> sortedqBM25 = sortByValue(qBM25, false); //sort in descending order
            queryBM25.put(qID, sortedqBM25);
        }
    }
    
    static HashMap<Integer, Double> computeBM25(List<String> qStrings, double N, double avdl){
        HashMap<Integer, Double> docsBM25 = new HashMap<>();
        
        for(Integer d : docLength.keySet()){
            double bm25_q_d = 0.0;
            double dl = (double)docLength.get(d);
            double K = (double) k1 * ((1-b)+(b*(dl/avdl)));
            for(String i : qStrings){
                double bm25_i_d;
                
                double n_i = 0.0;
                double f_i = 0.0;
                if(terms.containsKey(i)){
                    n_i = (double)terms.get(i).size();
                    if(terms.get(i).containsKey(d))
                        f_i = (double)terms.get(i).get(d);
                }
                double qf_i = noOfOccurances(i,qStrings);
                
                double part1 = Math.log( (N-n_i+0.5) / (n_i+0.5) );
                double part2 = ( (f_i * (1+k1)) / (f_i + K) );
                double part3 = ( (qf_i * (1+k2)) / (qf_i+k2) );
                bm25_i_d = part1*part2*part3;
                bm25_q_d+=bm25_i_d;
            }
            docsBM25.put(d,bm25_q_d);
        }        
        return docsBM25;
    }
    
    static double noOfOccurances(String w, List<String> wList){
        double result = 0.0;
        for(String each : wList) if(each.equalsIgnoreCase(w)) result+=1.0;
        return result;
    }
    
    /**
     *
     * @param <K> key
     * @param <V> value
     * @param map map of key-value pairs
     * @param order boolean value: true - ascending; false - descending
     * @return a sorted map, sorted by the value
     */
    public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map, boolean order){
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return order ? (o1.getValue()).compareTo(o2.getValue()) : (o2.getValue()).compareTo(o1.getValue());
            }
        });

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
            result.put( entry.getKey(), entry.getValue() );
        return result;
    }
}
