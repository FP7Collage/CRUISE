/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.NGramExtractor;
import gr.imu.ntua.cruise.lucene.NGramFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.TermStats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author imu-user
 */
public class NGramsImpl {
    
    public void NGramsImpl(){
    }
    
    public HashMap<String, ArrayList<String>> getNGrams(IndexReader ir,TermStats[] termStats, ArrayList<String> nouns) throws IOException{
            
        HashMap<String, ArrayList<String>> filteredListMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> ngramsAll = new ArrayList<String>();
        ArrayList<String> ngramsTmp = new ArrayList<String>();
        ArrayList<String> ngramsDuplicates = new ArrayList<String>();
        ArrayList<String> filteredList = new ArrayList<String>();

        for (int i = 0; i < ir.maxDoc(); i++) {
                NGramExtractor extractor = new NGramExtractor();
                Document doc = ir.document(i);            
                String text = doc.getField("content").toString();
             //   String text = "I definitely wanna have a dragon ball z marathon now. Maria is gonna play footbal";   


            ArrayList<String> test = extractor.getNGrams(text,2,2);
                  for (String ngram : test ){
                        ngramsAll.add(ngram);
                       // System.out.println(ngram);
                  } //end of for loop ngram
            }//end of for loop tmpIr

            for (int k = 0;k< ngramsAll.size();k++){
                if (ngramsTmp.contains(ngramsAll.get(k)))
                {
                    ngramsDuplicates.add(ngramsAll.get(k));
                //    System.out.println("Namai kai pali"+ngramsAll.get(k));
                } // end of if

                else
                {
                    ngramsTmp.add(ngramsAll.get(k));
                }//end of else        

            }// end of for loop       

            NGramFilter ngramfilter = new NGramFilter();
          //  filteredList = NGramFilter.Filter(ngramsDuplicates,termStats,nouns);
           //  for (int noun=0;noun < filteredList.size(); noun++){
            // System.out.println(filteredList.get(noun));  
        // }
            filteredListMap = ngramfilter.getWordsOfNGram(ngramsDuplicates,termStats,nouns);
            for (String key:filteredListMap.keySet()){
               //System.out.println("Key: "+key+"Value: "+filteredListMap.get(key));                
            }
            
            return filteredListMap;
        
    }
    
}
