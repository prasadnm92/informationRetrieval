# PROGRAMMING ASSIGNMENT 4 - INDEXING & RANKING USING LUCENE
## JYOTHI PRASAD NAMA MAHESH

##### Instructions to run the code:

The program requires four external libraries lucene-core-4.7.2.jar, lucene-queryparser-4.7.2.jar, lucene-analyzers-common-4.7.2.jar and jsoup-1.8.3.jar which is provided along with my code. Keep all the files in the same folder while running.

Compile the code using:
```javac -cp lucene-core-4.7.2.jar:lucene-queryparser-4.7.2.jar:lucene-analyzers-common-4.7.2.jar:jsoup-1.8.3.jar HW4.java```

Execute the program using:
```java -cp .:lucene-core-4.7.2.jar:lucene-queryparser-4.7.2.jar:lucene-analyzers-common-4.7.2.jar:jsoup-1.8.3.jar HW4```

> Note: Use ';' instead of ':' to separate the class path entries if the code is run on windows system.

There are no command line arguments required but it asks for inputs while running. The printed statement will explain what input it expects.

##### Explaining the code:

For this implementation, we use Lucene's Simple Analyser that ignores all non-letters (special characters and digits) while indexing the corpus data. There are other analysers like the Standard Analyser but for the purpose of this assignment we use the Simple Analyser.

The program creates an index from a set of .xml/.htm/.html/.txt files that is provided as an input when prompted for (in this case we provide CACM corpus which is also included in the submission). Before indexing, we remove &lt;html&gt; and &lt;pre&gt; tags so that these don't form a part of the text corpus and rewrite the input files.
> Note: do not create the index multiple times. This will result in duplication of information.

From the generated index, we create a LinkedHashMap of the terms and their frequencies in descending order of their frequencies.

Next, we input queries, one at a time, to generate the top ranked documents. But before doing this, the query string is checked for occurances of special characters and digits that are removed and all characters are converted to lower case. The ranked documents are displayed with the file name and its score.

##### Final Result

Submitted files:

1. this = ReadMe.md;

2. The source code is HW4.java with dependency libraries (3, as mentioned above)

3. The sorted list of (term, term_freq) pairs are provided in the "Term Frequencies.txt" file

4. The plot of the resulting Zipfian curve is submitted in the "Zipf_Curve.pdf"
The Zipf CUrve is plotted on a log-log plot with the rank of the terms on X-axis and the term frequency on the Y-axis

5. The list of top docIDs for top 100 documents for each query is present in the "Top 100 docs.txt"

6. The table comparing the number of documents retrieved by the Lucene search engine and our previous search engine that employed BM25 ranking algorithmis provided in the "Comparison_Lucene_BM25.pdf"