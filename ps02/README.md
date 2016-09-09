# PROGRAMMING ASSIGNMENT 2 - PAGERANK
## JYOTHI PRASAD NAMA MAHESH


##### Introduction to the problem: **Page rank**

Compute PageRank on a collection of 183,811 web documents. Use the array depiction of the in-link representation. Perform the following:

- Implement the iterative BM-25 PageRank algorithm. Test your code on a six-node example using the input representation mentioned above.
Please hand in: a list of the PageRank values you obtain for each of the six vertices after 1, 10, and 100 iterations of the PageRank algorithm.

- Download the in-links file for the WT2g collection, a 2GB crawl of a subset of the web. This in-links file is in the array format described above. Run your iterative version of PageRank algorithm until your PageRank values "converge". To test for convergence, calculate the perplexity of the PageRank distribution, where perplexity is simply 2 raised to the (Shannon) entropy of the PageRank distribution, i.e., 2H(PR). Perplexity is a measure of how "skewed" a distribution is: the more "skewed" (i.e., less uniform) a distribution is, the lower its preplexity. Technically, the perplexity of a distribution with entropy h is the number of elements n such that a uniform distribution over n elements would also have entropy h. Run your iterative PageRank algorthm, outputting the perplexity of your PageRank distibution until the change in perplexity is less than 1 for at least four consecutive iterations.
Please hand in: a list of the perplexity values you obtain in each round until convergence as described above.

- Sort the collection of web pages by the PageRank values you obtain.
Please hand in:
	* a list of the document IDs of the top 50 pages as sorted by PageRank
	* a list of the document IDs of the top 50 pages by in-link count
	* the proportion of pages with no in-links (sources)
	* the proportion of pages with no out-links (sinks)
	* the proportion of pages whose PageRank is less than their initial, uniform values.

- Examine the top 10 pages by PageRank and the top 10 by in-link count in the Lemur web interface to the collection by using the "e=docID" option with database "d=0", which is the index of the WT2g collection. For example, the link
http://fiji4.ccs.neu.edu/~zerg/lemurcgi_IRclass/lemur.cgi?d=0&e=WT04-B22-268 
or
http://karachi.ccs.neu.edu/~zerg/lemurcgi_IRclass/lemur.cgi?d=0&e=WT04-B22-268
will bring up document WT04-B22-268, which is an article on the Comprehensive Test Ban Treaty.
Please hand in: Speculate why these documents have high PageRank values. Are all of these documents ones that users would likely want to see in response to an appropriate query? Give some examples of ones that are and ones that are not. For those that are not "interesting" documents, why might they have high PageRank values? How do the pages with high PageRank compare to the pages with many in-links? In short, give an analysis of the PageRank results you obtain.

##### Instructions to run the code

Compile the code using:
```javac pagerank.java```

Execute the program using:
```java pagerank <path_to_inlinkFile>```

##### Final Results

The answers to the sub-questions are included in this zip, named 'q1.txt', 'q2.txt', so on. '6node-example.txt' has the input for the 6 node graph for q1. 'wt2g_inlinks.txt' is the file given in the project page. The program outputs the page ID and its rank, in order of their rank.