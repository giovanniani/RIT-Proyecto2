/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

/**
 *
 * @author Usuario
 */
import apendix.Constants;
import apendix.Routes;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
	
   IndexSearcher indexSearcher;
   QueryParser queryParser;
   Query query;
   private static Searcher searcher=null;

   private Searcher() throws IOException{
      Directory indexDirectory = 
      FSDirectory.open(new File(Routes.indexDir));
      indexSearcher = new IndexSearcher(indexDirectory);
      queryParser = new QueryParser(Version.LUCENE_36,
         Constants.TEXTO,
         new StandardAnalyzer(Version.LUCENE_36));
   }
   public static Searcher getSearcher() throws IOException{
       return searcher= new Searcher();
    }
   public TopDocs search( String searchQuery) 
      throws IOException, ParseException{
      
      query = queryParser.parse(searchQuery);
      return search(query);//indexSearcher.search(query, Constants.MAX_SEARCH);
   }
   
   public TopDocs search(Query query) throws IOException, ParseException{
      return indexSearcher.search(query, Constants.MAX_SEARCH);
   }

   public Document getDocument(ScoreDoc scoreDoc) 
      throws CorruptIndexException, IOException{
     return indexSearcher.doc(scoreDoc.doc);	
   }

   public void close() throws IOException{
      indexSearcher.close();
   }
}
