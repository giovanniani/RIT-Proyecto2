package query;

import apendix.Constants;
import apendix.TextFileFilter;
import apendix.Routes;
import index.Indexer;
import java.io.BufferedReader;
import java.io.File;
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
       String[] dirs2={"D:\\Biblioteca\\Dropbox\\Docs Tec\\Sexto Semestre\\Recuperacion de Informacion Textual\\Proyectos\\Geografia"};
       for (String s:dirs){
           s=s.trim();
           //cantidad+=updateIndex(s);
           System.out.println("Procesando coleccion\n\t\t\""+s+"\"");
           //result &=indexer.processCollection(s);
           System.out.println("Procesado finalizado");
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
        System.out.println("\tSe han encontrado "+hits.scoreDocs.length+" documentos relevantes.");
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
           docInfo= new ArrayList();
           Document doc = searcher.getDocument(scoreDoc);
           String ruta=doc.get(Constants.ORIGINAL_PATH).replaceAll("\"", "");
           File archivo= new File(ruta);
           
           //String[] secciones=ruta.split(ruta, 0)
           docInfo.add(scoreDoc.score + "");
           docInfo.add(archivo.getName());
           docInfo.add(doc.get(Constants.ORIGINAL_PATH).replaceAll("\"", ""));
           System.out.print("Score: "+ scoreDoc.score + " ");
           System.out.println("HTML: "+ doc.get(Constants.ORIGINAL_PATH));
           results.add(docInfo);
        }
        searcher.close();
        return results;
   }
    public void clean() throws IOException {
        indexer.clean();
        
    }
   public void print(String s){System.out.println(s);}
}
