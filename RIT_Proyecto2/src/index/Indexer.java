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
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;
import query.Searcher;





public class Indexer {
    private IndexWriter writer;
    private static Indexer indexer;
    
    private Indexer() throws IOException{
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(new File(Routes.indexDir));
        //create the indexer
        writer = new IndexWriter(indexDirectory,
        new StandardAnalyzer(Version.LUCENE_36),true,
        IndexWriter.MaxFieldLength.UNLIMITED);

    }
    public static Indexer getIndexer() throws IOException{
        if (indexer==null) indexer= new Indexer();
        return indexer;
    }
    
    public boolean exists(Document document) throws IOException, ParseException{
        print("exists?");
        print(Constants.ORIGINAL_PATH+":"+document.getFieldable(Constants.ORIGINAL_PATH));
        TopDocs results=Searcher.getSearcher().search(Constants.ORIGINAL_PATH+":"+document.getFieldable(Constants.ORIGINAL_PATH));
        print("\t\tRepeticiones: "+results.totalHits+"");
        return results.totalHits!=0;
    }
    
    private Document getDocument(File file) throws IOException{
        print("getDocument");
        Document document = new Document();
        //index file contents
        //String json=new String(Files.readAllBytes(Paths.get(file.getPath())));
        Doc doc= new Doc(file);
        print("orgPath: "+doc.originalPath);
        print("soucePath: "+doc.sourcePath);
        Field textField = new Field(
               Constants.TEXTO,
               new StringReader(doc.texto));
        
        Field refField = new Field(
               Constants.TEXTO,
               new StringReader(doc.texto));
        
        //index file name
        Field originalPath = new Field(
               Constants.ORIGINAL_PATH,
               doc.originalPath,
               Field.Store.YES,
               Field.Index.NOT_ANALYZED);
        
        Field sourcePath = new Field(
               Constants.SOURCE_PATH,
               doc.sourcePath,
               Field.Store.YES,
               Field.Index.NOT_ANALYZED);
        
        document.add(textField);
        document.add(refField);
        document.add(originalPath);
        document.add(sourcePath);
        print("Added"); 
        return document;
        }
    private void indexFile(File file) throws IOException, ParseException{
        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file);
        if (!exists(document)) writer.addDocument(document);
        else{print("Documento \n\t\t"+file.getCanonicalPath()+" ya indexado");}
    }
    public int addToIndex(String dataDirPath, FileFilter filter)
        throws IOException, ParseException{
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();
        print(files.length+"");
        for (File file : files) {
        if(!file.isDirectory()
        && !file.isHidden()
        && file.exists()
        && file.canRead()
        && filter.accept(file)
        ){
            //System.out.println(file.getCanonicalPath());
            indexFile(file);
        }
        }
        return writer.numDocs();
    }
    public void clean() throws IOException {
        writer.deleteAll();
    }
    public void commit() throws CorruptIndexException, IOException{
        writer.commit();
    }
    public void close() throws CorruptIndexException, IOException{
        writer.close();
    }
   public void print(String s){System.out.println(s);}
}
