
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author JYOTHIPRASAD
 */

final class Relevance {
    final static TreeMap<Integer,HashSet<String>> relevanceValues = new TreeMap<>();
    static String relevance_file = "";
    
    public Relevance(String fname) throws IOException{
        relevance_file = fname;
        generateRelevanceValues();
    }
    
    void generateRelevanceValues() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(relevance_file));
        String aLine;
        while((aLine=br.readLine())!=null)
            updateValues(aLine);
        br.close();
        
    }
    
    void updateValues(String value){
        String[] items = value.split(" ");
        int queryID = Integer.parseInt(items[0]);
        String docID = items[2];
        HashSet<String> this_value;
        if(relevanceValues.containsKey(queryID))
            this_value = relevanceValues.get(queryID);
        else
            this_value = new HashSet<>();
        this_value.add(docID);
        relevanceValues.put(queryID, this_value);
        
    }
    
    public TreeMap<Integer,HashSet<String>> getRelevanceValues(){
        return relevanceValues;
    }
}


final class QueryResults{
    static TreeMap<Integer, LinkedHashMap<String, Double>> queryResults = new TreeMap<>();
    String resultsFile;
    static int[] qIDs = {12,13,19}; //changing query IDs to match queries of given standard
    
    public QueryResults(String fname) throws IOException{
        resultsFile = fname;
        generateResults();
    }
    
    void generateResults() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(resultsFile));
        String aLine;
        while((aLine=br.readLine())!=null)
            updateMap(aLine);
        br.close();

    }
    
    void updateMap(String eachResult){
        String[] items = eachResult.split(" ");
        int queryID = qIDs[Integer.parseInt(items[0])-1]; //changing queryID to given standard
        String docID = "CACM-"+items[2]; //keeping docID consistant
        Double score = Double.parseDouble(items[4]);
        LinkedHashMap<String, Double> thisResult;
        if(queryResults.containsKey(queryID))
            thisResult = queryResults.get(queryID);
        else
            thisResult = new LinkedHashMap<>();
        thisResult.put(docID, score);
        queryResults.put(queryID, thisResult);
    }
    
    TreeMap<Integer, LinkedHashMap<String, Double>> getQueryResults(){
        return queryResults;
    }
}


public class Evaluater {
    static TreeMap<Integer,HashSet<String>> relevanceValues = new TreeMap<>();
    static TreeMap<Integer, LinkedHashMap<String, Double>> queryResults = new TreeMap<>();
    static TreeMap<Integer, LinkedHashMap<String, List<Double>>> evaluationResults = new TreeMap<>();
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException{
        System.out.println("Enter the path where the relevance judgment data is present:");
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
	String relevanceFile = br1.readLine();
        Relevance r = new Relevance(relevanceFile);
        relevanceValues = r.getRelevanceValues();
        
        System.out.println("Enter the path where the query results data for BM25 is present:");
        BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
	String queryResultsFile = br2.readLine();
        QueryResults q = new QueryResults(queryResultsFile);
        queryResults = q.getQueryResults();
        
        double MAP = 0.0;
        List<Double> pAtK = new ArrayList<>();
        int A, //number of relevant documents
            B; //number of retrieved documents
        for(Integer qID : queryResults.keySet()){
            HashSet<String> relevantDocs = relevanceValues.get(qID);
            LinkedHashMap<String, Double> topDocs = queryResults.get(qID);
            LinkedHashMap<String, List<Double>> thisQueryResults = new LinkedHashMap<>();
            A = relevantDocs.size();
            B = 0;
            int relevantFound = 0;
            double AvP = 0.0; //Average Precision
            int count = 0; //rank
            double prevDCG = 0.0; //to compute discounted cumulative gain
            double prevIDCG = 0.0; //to compute discounted cumulative gain (for ideal retrieval)
            for(String docID : topDocs.keySet()){
                count++; //next rank
                B++; //one more document retrieved
                Double precision, recall, ndcg;
                List<Double> thisDocValues= new ArrayList<>();
                
                Double score = topDocs.get(docID);
                thisDocValues.add(score);
                
                Double relevanceLevel = relevantDocs.contains(docID) ? 1.0 : 0.0;
                thisDocValues.add(relevanceLevel);
                
                if(relevanceLevel == 1.0) relevantFound++;
                precision = (double)relevantFound/B;
                thisDocValues.add(precision);
                
                if(relevanceLevel == 1.0) AvP+=precision;
                if(count==20) pAtK.add(precision); //for p@20
                
                recall = (double)relevantFound/A;
                thisDocValues.add(recall);
                
                double dcg, idcg;
                if(count==1){
                    dcg = relevanceLevel;
                    idcg = 1.0;
                } //rank 1
                else{
                    dcg = relevanceLevel/(Math.log(count)/Math.log(2));
                    if(count<=A)
                        idcg = 1.0/(Math.log(count)/Math.log(2));
                    else
                        idcg = 0.0;
                }
                dcg+=prevDCG;
                idcg+=prevIDCG;
                ndcg = dcg/idcg;
                thisDocValues.add(ndcg);
                
                thisQueryResults.put(docID, thisDocValues);
                
                prevDCG = dcg;
                prevIDCG = idcg;
            }
            AvP/=relevantFound;
            MAP+=AvP;
            evaluationResults.put(qID, thisQueryResults);
        }
        MAP/=3; //Average of average precision for each query
        printEvaluationResults();
        System.out.println("\np@20:"+pAtK+"; MAP = "+MAP);
    }
    
    static void printEvaluationResults(){
        int i=1;
        for(Integer qID : evaluationResults.keySet()){
            System.out.println("\n**********************************************("+(i++)+")"+qID+"**********************************************");
            int rank = 1;
            LinkedHashMap<String, List<Double>> thisQueryResults = evaluationResults.get(qID);
            for(String docID : thisQueryResults.keySet()){
                String tabspace = "\t";
                if(docID.length()<8) tabspace+=tabspace;
                
                System.out.print((rank++)+"\t"+docID+tabspace);
                
                List<Double> thisDocResults = thisQueryResults.get(docID);
                int k = 0;
                for(Double value : thisDocResults){
                    if(++k == 2) System.out.print((int)Math.floor(value)+"\t");
                    else System.out.printf("%.14f\t",value);
                }
                System.out.println();
            }
        }
    }
}
