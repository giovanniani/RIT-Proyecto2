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
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Indexer {
    private IndexWriter writer;
    public Indexer(String indexDirectoryPath) throws IOException{
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
        //create the indexer
        writer = new IndexWriter(indexDirectory,
        new StandardAnalyzer(Version.LUCENE_36),true,
        IndexWriter.MaxFieldLength.UNLIMITED);

    }
    
    private Document getDocument(File file) throws IOException{
        Document document = new Document();
        //index file contents
        String json=new String(Files.readAllBytes(Paths.get(file.getPath())));
        Doc doc= new Doc(json);
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
        return document;
        }
    private void indexFile(File file) throws IOException{
        System.out.println("Indexing "+file.getCanonicalPath());
        Document document = getDocument(file);
        writer.addDocument(document);
    }
    public int addToIndex(String dataDirPath, FileFilter filter)
        throws IOException{
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();
        for (File file : files) {
        if(!file.isDirectory()
        && !file.isHidden()
        && file.exists()
        && file.canRead()
        && filter.accept(file)
        ){
        indexFile(file);
        }
        }
        return writer.numDocs();
    }
    public void close() throws CorruptIndexException, IOException{
        writer.close();
    }
}
