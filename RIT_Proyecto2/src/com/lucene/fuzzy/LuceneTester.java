/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucene.fuzzy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.lucene.fuzzy.Indexer;
import com.lucene.fuzzy.TextFileFilter;


public class LuceneTester {
	
	 String indexDir = "C:\\lucene-3.6.2\\ProductIndex";
	 String dataDir = "C:\\lucene-3.6.2\\ProductData";
	 Indexer indexer;
   Searcher searcher;

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
    		
    	    String[] searcharray = choice.split(" ");
    	    for (int i = 0; i < searcharray.length; i++)
    	    {
    	    	System.out.println("Search results for word " + (i+1) + ": " + searcharray[i]);
    	    	tester.searchUsingFuzzyQuery(searcharray[i]);
    	    }
    		
     		 //Uncomment below line for one word queries            
    		
    		 //tester.searchUsingFuzzyQuery(choice);

         
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }
   
   private void createIndex() throws IOException{
	   indexer = new Indexer(indexDir);
	   int numIndexed;
	   long startTime = System.currentTimeMillis();
	   numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
	   long endTime = System.currentTimeMillis();
	   indexer.close();
	   System.out.println(numIndexed+" File indexed, time taken: "
	   +(endTime-startTime)+" ms");
	   }
   
   
   private void searchUsingFuzzyQuery(String searchQuery)
      throws IOException, ParseException{
      searcher = new Searcher(indexDir);
      long startTime = System.currentTimeMillis();
      //create a term to search file name
      //Term term = new Term(LuceneConstants.FILE_NAME, searchQuery);
      Term term = new Term(LuceneConstants.CONTENTS, searchQuery);
      //create the term query object
      Query query = new FuzzyQuery(term);
      //do the search
      
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
}
