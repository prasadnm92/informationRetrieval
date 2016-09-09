# PROGRAMMING ASSIGNMENT 2 - BM25 RANKING
## JYOTHI PRASAD NAMA MAHESH


##### Instructions to run the code

The program requires an external library for JSON (json-simple-1.1.1.jar), which is provided along with my code. Keep both the files in the same folder while running.

Compile the code using:
```
javac -cp json-simple-1.1.1.jar indexer.java
javac -cp json-simple-1.1.1.jar bm25.java
```
Execute the program using:
```
java -cp .:json-simple-1.1.1.jar indexer <stemmed document collection> <index output file>
java -cp .:json-simple-1.1.1.jar bm25 <index file> <query file> <no of ranks to display>
[java -cp .:json-simple-1.1.1.jar bm25 <index file> <query file> <no of ranks> > <output redirect file>]
```

The program prints the top ranked documents for each query in the given format:
```<query_id> Q0 <doc_id> <rank> <BM25_score> <system_name = namamahesh.j>```

##### Final Results

The output for the given query file and top 100 ranks is submitted in the results.eval file.

##### Report
- First, the corpus file is read and a data structure for the inverted index is created using a LinkedHashMap<String, HashMap<Integer,Integer>> where:
	* String is the term
	* HashMap<Integer,Integer> is a mapping from the docID in which the term is present in and its term frequency in that document.

- Second, the inverted index generated is formatted into a JSON object and written into a file.

- Next, the bm25 program reads this file and regenerates the inverted index in the same format as mentioned above.

- Next, the queries file is read and stored in a data structure like HashMap<Integer, List<String>> where:
	* Integer is the query ID
	* List<String> is the list of each word in the query in the order they appear

- Next, the bm25 score for each (q,d) is calculated using the formula:
```
bm25(d,q) =  SUM   [part1 * part2 * part3]
            i in q

^part1 = log( ((r_i+0.5)/(R-r_i+0.5) / ((n_i-r_i+0.5)/(N-n_i-R+r_i+0.5)) ), R=r_i=0 beacause there is no relevance information
       = log( (N-n_i+0.5) / (n_i+0.5) ), where
	N - total number of documents
	n_i - number of documents containing the word w

^part2 = ( (f_i * (1+k1))/( f_i+k1*((1-b)+b*(dl/avdl)) ) ), where
	f_i - term frequency of i in document d {obtained from the inverted index}
	dl - this document's length
	avdl - average document length of entire corpus {=total number of terms/D}
	b - a constant {=0.75}
	k1 - a constant {=1.2}

^part3 = ( (qf_i*(1+k2))/(qf_i+k2) )
	qf_i - term frequency of w in this query q {obtained from query data structure}
	k2 - a constant {=100}
```

- The BM25 score is generated for every docuument-query pair and is stored in a data structure like HashMap<Integer, LinkedHashMap<Integer, Double>> where:
	* Integer is the query ID
	* LinkedHashMap<Integer, Double> is an ordered mappings of docID mapped to its BM25 score and ordered by this score in descending order

- Finally, the top ranks as specified by the input is printed in the required format