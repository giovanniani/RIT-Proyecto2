package query;

import apendix.Constants;
import apendix.TextFileFilter;
import apendix.Routes;
import index.Indexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class SearchEngine {
   Indexer indexer;
   Searcher searcher;
   
   public SearchEngine()throws IOException{
        indexer = new Indexer(Routes.indexDir);
   }
   public int index(String[] dirs) throws IOException{
       int cantidad=0;
       for (String s:dirs){
           //cantidad+=updateIndex(s);
           proccessDir(s);
       }
       return cantidad;
   }
   private void proccessDir(String s){
       //procesar el directorio ingresador y ponerlo en programData
   }
   private int updateIndex()throws IOException{
        int cantidad;
        long inicio = System.currentTimeMillis();
        //Routes.setDataDir(dataDir);
        cantidad = indexer.addToIndex(Routes.dataDir, new TextFileFilter());
        long fin = System.currentTimeMillis();
        //indexer.close();
        long totalIndexacion=fin-inicio;
        return cantidad;
   }
    public ArrayList<ArrayList> search(String query)throws IOException, ParseException{
       return TopDocsToArray(doSearch(query));
    }
    private TopDocs doSearch(String query) throws IOException, ParseException{
        searcher = new Searcher(Routes.indexDir);
        TopDocs hits = searcher.search(query);
        return hits;
   }
   public ArrayList<ArrayList> TopDocsToArray(TopDocs hits)throws IOException, ParseException{
        ArrayList<String> docInfo= new ArrayList();
        ArrayList<ArrayList> results= new ArrayList();
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
           Document doc = searcher.getDocument(scoreDoc);
           
           System.out.print("Score: "+ scoreDoc.score + " ");
           System.out.println("File: "+ doc.get(Constants.FILE_PATH));
        }
        searcher.close();
    
   }
    
    
      public static void main(String[] args) {
	   SearchEngine tester;
      
      try {
    	  
    	  tester = new SearchEngine();
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
