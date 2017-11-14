package query;

import apendix.LuceneConstants;
import apendix.TextFileFilter;
import apendix.Routes;
import index.Indexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
   Indexer indexer;
   Searcher searcher;
   
   
   public void index(String[] dirs){
       for (String s:dirs){
           
       }
   }
   private void addToIndex(String dataDir) throws IOException{
        int cantidad;
        Routes.setDataDir(dataDir);
        indexer = new Indexer(Routes.indexDir);

        long inicio = System.currentTimeMillis();
        cantidad = indexer.addToIndex(Routes.dataDir, new TextFileFilter());
        long fin = System.currentTimeMillis();
        TextFileFilter s= new TextFileFilter();

        indexer.close();
        long totalIndexacion=fin-inicio;
   }
   
           
    private void search(String query)
        throws IOException, ParseException{
        searcher = new Searcher(Routes.indexDir);
        Term term1 = new Term(LuceneConstants.CONTENTS, query);

        TopDocs hits = searcher.search(query);
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits +
           " documents found. Time :" + (endTime - startTime) + "ms");
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
           Document doc = searcher.getDocument(scoreDoc);
           System.out.print("Score: "+ scoreDoc.score + " ");
           System.out.println("File: "+ doc.get(LuceneConstants.FILE_PATH));
        }
        searcher.close();
   }
      public static void main(String[] args) {
	   LuceneTester tester;
      
      try {
    	  
    	  tester = new LuceneTester();
    	  tester.createIndex();
    	  //Search text here
    	  
    	  
    	  BufferedReader br;
    		String choice = "";
    		System.out.println("***** Lucene Index, Search Tester ******");
    		System.out.println("Enter the name to search:");
    		
    		br = new BufferedReader(new InputStreamReader(System.in));
    		try {
    			choice = br.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		try {
    			br.close();
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		
    		//Optional for phrases (multi words)
    		
    	    //String[] searcharray = choice.split(" ");
    	    //for (int i = 0; i < searcharray.length; i++)
    	    //{
    	    	//System.out.println("Search results for word " + (i+1) + ": " + searcharray[i]);
    	    	//tester.search(searcharray[i]);
    	    	tester.search(choice);
    	    //}
    		
     		 //Uncomment below line for one word queries            
    		
    		 //tester.search(choice);

         
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }
}
