# PROGRAMMING ASSIGNMENT 5 - EVALUATION OF IR SYSTEM IMPLEMENTED USING BM25 RANKING ALGORITHM
## JYOTHI PRASAD NAMA MAHESH

##### Instructions to run the code:

Compile the code using:
```javac Evaluater.java```

Execute the program using:
```java Evaluater```

This program does not require any command line arguments but asks for 2 inputs while running. They are paths for the location of files that contain relevance judgement information and query results of the IR system of your choice.

##### Explaining the code:

The implementation is simple and straight forward.

First I parse two files input by the user:
 - Relevance Judgement information(eg:cacm.rel): the data in this file is read and stored as a TreeMap(so that the order is maintained based on queryIDs). Each entry is a key-value pair with queryID as key and List<String> of docIDs that are relevant to this query. The name of this TreeMap in the program is relevanceValues.
 - Query Result information(eg:results.eval): the data in this file is read and stored as a TreeMap(for the same reason as above), where queryID is the key and the value is a LinkedHashMap<String,Double> with docID as the key and its score as the value. Using LinkedHashMap ensures the insertion order which maintains the ranking order. This TreeMap is named as queryResults in the program.

Then, by iterating through the 'queryResults' data structure, I compute the required precision, recall and NDCG (Normalized Discounted Cumulative Gain) values. Since the values are in the order of insertion (which is in the order of rank) the values computed are also in order of the rank.
 - Precision is the proportion of relevant documents that are retrieved (= relevant found so far / retrieved so far)
 - Recall is the proportion of retrieved documents that are relevant (= relavant found so far / total relevant)
 - NDCG = DCG/IDCG; where DCG(at rank r) = relevance of document at rank 1 + SUM[relevance level of doc i/log-base-2 i] {i ranges from 2->r}
                   ; IDCG is calculated using same formula as DCG, except that the ranking of documents are done such that all relevant documents are retrieved first before any irrelevant document
                   ; as the name suggests, this is a cumulative gain measure. Hence the value computed at the previous rank is used to compute the current value (as seen by the variables prevDCG & prevIDCG)

Finally, p@K for rank 20 is stored in a list and the precision value is added to this list when it is computed at that rank. For MAP, the AvP for each query is calculated and averaged to obtain this value.

##### Final Results

Submitted files:

1. this = ReadMe.txt;

2. source code = Evaluater.java;

3. "evaluationResults(2).txt" contains the 3 tables (1 per query) with the computed values for precision, recall & NDCG

4. "evaluationResults(3).txt" contains the 3 values for p@K and MAP, explaining how I computed the same

5. "cacm.rel.txt" contains the relevance judgement information

6. "queryResults.txt" contains the ouput of my BM25 ranking algorithm. For this assignment, only the first 3 queries were required and that is what is there in this file. Also, to match the queries, the IDs from BM25 ranking is mapped onto its corrosponding IDs using the 'qIDs' array in the class where I parse the file.