/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

/**
 *
 * @author Usuario
 */
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import apendix.Constants;
import apendix.Routes;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;
import query.Searcher;





public class Indexer {
    private IndexWriter writer;
    private static Indexer indexer;
    
    private Indexer() throws IOException{
        initializeIndexer();
    }
    private void initializeIndexer() throws IOException{
               //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(new File(Routes.indexDir));
        //create the indexer
        writer = new IndexWriter(indexDirectory,
        new StandardAnalyzer(Version.LUCENE_36),false,
        IndexWriter.MaxFieldLength.UNLIMITED); 
    }
    public static Indexer getIndexer() throws IOException{
        if (indexer==null) indexer= new Indexer();
        return indexer;
    }
    
    public boolean exists(Document document) throws IOException, ParseException{
        //print("exists?");
        String query=Constants.ORIGINAL_PATH+":\""+document.get(Constants.ORIGINAL_PATH)+"\"";
        //query=Constants.TEXTO+":"+"textopalabra1";
        //print("Consulta de existencia de archivos: \""+query+"\"");
        TopDocs results=Searcher.getSearcher().search(query);
        //print("\t\tExistencias: "+results.totalHits+"");
        //return false;
        return results.totalHits!=0;
    }
    
    private Document getDocument(File file) throws IOException{
        Document document = new Document();
        //index file contents
        //String json=new String(Files.readAllBytes(Paths.get(file.getPath())));
        Doc doc= new Doc(file);
        
        Field textField = new Field(
               Constants.TEXTO,
               doc.texto,
               Field.Store.YES,
               Field.Index.ANALYZED);
               //new StringReader(doc.texto));
               //new StringReader("texto"));
        
        Field refField = new Field(
               Constants.REF,
               doc.ref,
               Field.Store.YES,
               Field.Index.ANALYZED);
               //new StringReader(doc.ref));
               //new StringReader("referencia"));
        
        Field originalPath = new Field(
               Constants.ORIGINAL_PATH,
               "\""+doc.originalPath+"\"",
               Field.Store.YES,
               Field.Index.NOT_ANALYZED);
        
        Field sourcePath = new Field(
               Constants.SOURCE_PATH,
               "\""+doc.sourcePath+"\"",
               Field.Store.YES,
               Field.Index.NOT_ANALYZED);
        
        document.add(textField);
        document.add(refField);
        document.add(originalPath);
        document.add(sourcePath);
        //print("        -"+document.get(Constants.SOURCE_PATH));
        //print("        -"+document.get(Constants.ORIGINAL_PATH));
        //print("        -"+document.get(Constants.TEXTO));
        //print("        -"+document.get(Constants.REF));
        return document;
        }
    private int indexFile(File file) throws IOException, ParseException{
        //System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file);
        if (!exists(document)){ 
            writer.addDocument(document);
            return 1;
        }
        else{
            print("Documento \n\t\t"+file.getCanonicalPath()+" ya indexado"); 
            return 0;
        }
    }
    public int addToIndex(String dataDirPath, FileFilter filter)
        throws IOException, ParseException{
        //get all files in the data directory
        int cantidad=0;
        File[] files = new File(dataDirPath).listFiles();
        for (File file : files) {
        if(!file.isDirectory()
            && !file.isHidden()
            && file.exists()
            && file.canRead()
            && filter.accept(file)
            ){
                //System.out.println(file.getCanonicalPath());
                cantidad+=indexFile(file);
            }
        }
        
        return cantidad;
    }
    public void clean() throws IOException {
        
        writer.deleteUnusedFiles();
        writer.deleteAll();
        writer.close();
        this.initializeIndexer();
    }
    
    public boolean processCollection(String ruta) throws IOException
    {
        String[] cmd = {Constants.ScriptCommand,Routes.scriptPath,ruta+">"+Routes.stopWords+">"+Routes.dataDir};
        for (String s: cmd){System.out.println(s);}
        
        Process pr = Runtime.getRuntime().exec(cmd);
        
        BufferedReader bfr = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "",output="";
        while((line = bfr.readLine()) != null) {output+=line+"\n"; }
        System.out.println("Script Output:\n"+output);
        return (output.contains(Constants.okFlag));
    }
    
    public String size() {
        try {
            return writer.numDocs()+"-"+writer.numRamDocs();
        } catch (IOException ex) {
            return "error";
        }
    }
        public void commit() throws CorruptIndexException, IOException{
        writer.commit();
    }
    public void close() throws CorruptIndexException, IOException{
        writer.close();
    }
   public void print(String s){
       System.out.println(s);
   }
}
