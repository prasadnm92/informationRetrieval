# PROGRAMMING ASSIGNMENT 1 - WEBCRAWLER
## JYOTHI PRASAD NAMA MAHESH


##### Introduction to the problem: **Focused Crawling**

Implement your own web crawler, with the following properties:

- Be polite and use a delay of at least one second between requests to the web server.
- You should start from the seed document http://en.wikipedia.org/wiki/Hugh_of_Saint-Cher, the Wikipedia article on Hugh of Saint-Cher, one of the originators of a key information retrieval technology.
- You should only follow links with the prefix http://en.wikipedia.org/wiki/. In other words, do not follow links to non-English articles or to non-Wikipedia pages.
- Do not follow links with a colon (:) in the rest of the URL. This will help filter out Wikipedia help and administration pages.
- Do not follow links to the main page http://en.wikipedia.org/wiki/Main_Page.
- Crawl to at most depth 5 from the seed page. The seed page is at depth 1.
- Wikipedia has a lot of links, so you should also stop when you reach 1000 unique URLs.
- Your crawler should take two arguments: the seed page and an optional "keyphrase" that must be present, in any combination of upper and lower case, on any page you crawl (after the seed). Don't worry about tokenization: just match the characters ignoring case. If the keyphrase is not present, stop crawling. This is a very simple version of focused crawling, where the presence or absence of a single feature is used to determine whether a document is relevant.
- Hand in your code and instructions on how to (compile and) run it. In addition, hand in two lists of URLs, each with at most 1000 entries:
	* the pages crawled when the crawler is run with no keyphrase, in other words all Wikipedia pages meeting the requirements above to a depth of 5 from the starting seed
	* the pages crawled when the keyphrase is ‘concordance’
	* also, tell us what proportion of the total pages were retrieved by the focused crawler for ‘concordance’. Keep in mind that this will be a significant overestimate of the prevalence of Wikipedia articles on indexing and information retrieval.


##### Instructions to run the code

The program requires an external library for JSOUP (jsoup-1.8.3.jar), which is provided along with my code. Keep both the files in the same folder while running.

Compile the code using:
```javac -cp jsoup-1.8.3.jar WebCrawler.java```

Execute the program using:
```java -cp .:jsoup-1.8.3.jar WebCrawler <seed_link> [<keyphrase>]```

The program does not print any statement. If required, change the doPrint flag to true before execution. A sample output for seed(https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher) and keyphrase(concordance) is also submitted.


###### Final Results

Proportion of the total pages that were retrieved by the focused crawler for ‘concordance’

i) If the proportion asked is the common links between the two cases, then I found it to be 17 links.
ii) If by proportion you mean to ask how many were crawled vs discovered, then this program crawled a total of 567 pages out of the (approx)26700 discovered. Implies, ~2% relevance to keyphrase.