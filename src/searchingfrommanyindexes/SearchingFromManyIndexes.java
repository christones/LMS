/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchingfrommanyindexes;

import static junit.framework.Assert.assertEquals;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author christones
 */
public class SearchingFromManyIndexes {
private static IndexSearcher[] searchers;
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        SearchingFromManyIndexes.setUp();
        SearchingFromManyIndexes.testMulti();
    }
    
    public static void setUp() throws Exception {
String[] animals = { "aardvark", "beaver", "coati",
"dog", "elephant", "frog", "gila monster",
"horse", "iguana", "javelina", "kangaroo",
"lemur", "moose", "nematode", "orca",
"python", "quokka", "rat", "scorpion",
"tarantula", "uromastyx", "vicuna",
"walrus", "xiphias", "yak", "zebra"};
Analyzer analyzer = new WhitespaceAnalyzer();
/*Create two
directories*/
Directory aTOmDirectory = new RAMDirectory();
Directory nTOzDirectory = new RAMDirectory();

IndexWriter nTOzWriter;
    try (IndexWriter aTOmWriter = new IndexWriter(aTOmDirectory,analyzer,IndexWriter.MaxFieldLength.UNLIMITED)) {
        nTOzWriter = new IndexWriter(nTOzDirectory,analyzer,IndexWriter.MaxFieldLength.UNLIMITED);
        for (int i=animals.length - 1; i >= 0; i--) {
            Document doc = new Document();
            String animal = animals[i];
            doc.add(new Field("animal", animal,
                    Field.Store.YES, Field.Index.NOT_ANALYZED));
            if (animal.charAt(0) < 'n') {
                aTOmWriter.addDocument(doc);
//Index halves of
            } else {
//the alphabet
nTOzWriter.addDocument(doc);
            }
        }   }
nTOzWriter.close();

searchers = new IndexSearcher[2];
searchers[0] = new IndexSearcher(aTOmDirectory);
searchers[1] = new IndexSearcher(nTOzDirectory);
}
    
public static  void testMulti() throws Exception {
MultiSearcher searcher = new MultiSearcher(searchers);
TermRangeQuery query = new TermRangeQuery("animal","h","t",true, true);

//Search both indexesLeveraging term vectors
// Ranker
TopScoreDocCollector collector = TopScoreDocCollector.create(10,true);
TopDocs hits = searcher.search(query, 10);
assertEquals("tarantula not included", 12, hits.totalHits);
                        // Display results
                        System.out.println("Found " + hits.scoreDocs.length + " hits.");
                        for (int i = 0; i < hits.totalHits ; ++i) {
                            Document d = searcher.doc(i+1);
                            System.out.println((i + 1) + ". " + d.get("animal"));
                        }
                        // Close the multi searcher

}
}
