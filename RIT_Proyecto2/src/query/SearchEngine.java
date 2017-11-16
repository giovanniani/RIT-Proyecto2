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
   
   public SearchEngine()throws IOException
   {
        indexer = Indexer.getIndexer();//new Indexer(Routes.indexDir);
        searcher = Searcher.getSearcher();
   }
   public int index(String[] dirs) throws IOException, ParseException{
       int cantidad=0;
       boolean result=true;
       for (String s:dirs){
           //cantidad+=updateIndex(s);
           result &=indexer.processCollection(s);
       }
       if (result)
       {
            return updateIndex();
       }
       return 0;
   }
   private void proccessDir(String s){
       //procesar el directorio ingresador y ponerlo en programData
   }
   private int updateIndex()throws IOException, ParseException{
        int cantidad;
        long inicio = System.currentTimeMillis();
        //Routes.setDataDir(dataDir);
        cantidad = indexer.addToIndex(Routes.dataDir, new TextFileFilter());
        long fin = System.currentTimeMillis();
        indexer.commit();
        long totalIndexacion=fin-inicio;
        return cantidad;
   }
    public ArrayList<ArrayList> search(String query)throws IOException, ParseException{
       System.out.println("Canditad de documentos: "+indexer.size());
       //TopDocsToArray(doSearch("filename:Pagina1"));
       return TopDocsToArray(doSearch(query));
    }
    private TopDocs doSearch(String query) throws IOException, ParseException{
        searcher= Searcher.getSearcher();
        print("Consulta: \""+query+"\"");
        TopDocs hits = searcher.search(query);
        return hits;
   }
    
   public ArrayList<ArrayList> TopDocsToArray(TopDocs hits)throws IOException, ParseException{
        ArrayList<String> docInfo;//= new ArrayList();
        ArrayList<ArrayList> results= new ArrayList();
        System.out.println("Resultado:");
        System.out.println("\t\tSe han encontrado "+hits.scoreDocs.length+" documentos relevantes.");
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
           docInfo= new ArrayList();
           Document doc = searcher.getDocument(scoreDoc);
           docInfo.add(doc.get(Constants.ORIGINAL_PATH));
           docInfo.add(scoreDoc.score + " ");
           System.out.print("Score: "+ scoreDoc.score + " ");
           System.out.println("HTML: "+ doc.get(""));
           //System.out.println("File: "+ doc.get(Constants.ORIGINAL_PATH));
           results.add(docInfo);
        }
        searcher.close();
        return results;
   }
    public void clean() throws IOException {
        indexer.clean();
        
    }
   public void print(String s){System.out.println(s);}
    
    /*
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
    			br.commit();
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
   }*/
}
