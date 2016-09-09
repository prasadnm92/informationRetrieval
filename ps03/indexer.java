import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author JYOTHIPRASAD
 */
class Corpus{
    final private String fileName;
    
    static LinkedHashMap<Integer, List<String>> docs = new LinkedHashMap();
    static LinkedHashMap<String, HashMap<Integer,Integer>> terms = new LinkedHashMap();
    static HashMap<Integer, Integer> docLength = new HashMap();
    
    
    public Corpus(String fName){
        fileName = fName;
    }
    
    public void ReadFile() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String aLine;
        while((aLine=br.readLine())!=null)
            updateData(aLine);

        for(Integer docID : docs.keySet()){
            Integer len = docs.get(docID).size();
            docLength.put(docID, len);
        }
        br.close();
    }
    
    void updateData(String line){
        List<String> docTerms = new ArrayList<>();
        
        if(line.startsWith("#")){
            int docID = Integer.parseInt(line.split(" ")[1]);
            docs.put(docID, docTerms);
        }
        else{
            Integer thisDocID = getLastDoc(docs);
            List<String> thisTerms = docs.get(thisDocID);
            
            String[] words = line.split(" ");
            String numPattern = "^\\d+$"; //ignore words with only digits
            Pattern p = Pattern.compile(numPattern);
            for(String word : words){
                Matcher m = p.matcher(word);
                if(!m.find())
                    docTerms.add(word);
            }
            thisTerms.addAll(docTerms);
            docs.put(thisDocID, thisTerms);
        }
    }
    
    Integer getLastDoc(LinkedHashMap<Integer,List<String>> d){
        Integer result = null;
        for(Integer i : d.keySet())
            result = i;
        return result;
    }
    
    public HashMap<Integer, Integer> getDocLengths(){
        return docLength;
    }
    
    public LinkedHashMap<String, HashMap<Integer,Integer>> invertedIndex(){
        for(Integer docID : docs.keySet()){
            List<String> thisTerms = docs.get(docID);
            for(String term : thisTerms){
                HashMap<Integer, Integer> termFreq = new HashMap<>();
                if(terms.containsKey(term)){
                    termFreq = terms.get(term);
                    if(termFreq.keySet().contains(docID)){
                        Integer tf = termFreq.get(docID);
                        termFreq.put(docID, (tf+1));
                    }
                    else termFreq.put(docID, 1);
                }
                else
                    termFreq.put(docID,1);
                terms.put(term, termFreq);
            }
        }
        return terms;
    }
}

public class indexer {
    static LinkedHashMap<String, HashMap<Integer,Integer>> terms = new LinkedHashMap();
    static HashMap<Integer, Integer> docLength = new HashMap();
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException{
        String ipFile = args[0];
        String opFile = args[1];

        Corpus c = new Corpus(ipFile);
        c.ReadFile();
        docLength = c.getDocLengths();
        terms = c.invertedIndex();
        
        BufferedWriter out = new BufferedWriter(new FileWriter(opFile));
        JSONArray invertedIndex = new JSONArray();
        for(String term:terms.keySet()){
            JSONObject eachTerm = new JSONObject();
            JSONArray termDocs = new JSONArray();
            HashMap<Integer,Integer> docs = terms.get(term);
            for(Integer docID : docs.keySet()){
                JSONObject eachDoc = new JSONObject();
                eachDoc.put(docID, docs.get(docID));
                termDocs.add(eachDoc);
            }
            eachTerm.put(term,termDocs);
            invertedIndex.add(eachTerm);
        }
        
        out.write(invertedIndex.toJSONString());
        out.flush();
        out.close();
    }
}
