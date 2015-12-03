/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.cruise.lucene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.misc.TermStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author imu-user
 */
public class NGramFilter {

    private static Logger logger = LoggerFactory.getLogger(NGramFilter.class);
     public static ArrayList<String> Filter(ArrayList<String> duplngrams, TermStats[] ts, ArrayList<String> nounsList)
     {
        ArrayList<String> newList = new ArrayList<String>();
         for (int ngr=0; ngr< duplngrams.size(); ngr++)
        {
            
            String str = duplngrams.get((ngr)).toString();
            
             String[] arr = str.split(" ");
            for ( String ss : arr) {
                if (!localFilter(ss,ts) && !nounsList.contains(ss) ){
                    duplngrams.remove(ngr);
                    ngr--;
                    break;
                }                        
            }   
        }         
         return duplngrams;     
     }
     
     
      public static HashMap<String, ArrayList<String>> getWordsOfNGram(ArrayList<String> duplngrams, TermStats[] ts,ArrayList<String> nounsList)
     {
        
        HashMap<String, ArrayList<String>> filteredMap = new HashMap<String, ArrayList<String>>();
         int size = duplngrams.size();
         for (int ngr=0; ngr < size; ngr++)
        {

            ArrayList<String> newList = new ArrayList<String>();
            String str = duplngrams.get((ngr)).toString();

            str=str.replaceAll("[^A-Za-z\\s-]", "");
            str=str.replaceAll("(storedindexedtokenizedcontent)", "");
            String[] arr = str.split(" ");
              for ( String ss : arr) {
                  //System.out.println(ss);
                  
                  if ((!localFilter(ss,ts))){ //&& (!nounsList.contains(ss))){
                
                    break;
                } 
               
               newList.add(ss);
              }
              if (newList.size()>1)
              filteredMap.put(str, newList);          
        }         
         return filteredMap;     
     }
        
        public static boolean localFilter (String s, TermStats[] termstats)
        {
                        
           if (!isTermStat(s,termstats)){
               // System.out.println("String:"+s) ;
               // System.out.println("Termstats:"+termstats) ;
                return false;
            }
                
            if (s.length()<0x3){   
                return false;
            }
            if (s.contains("http")){            
                return false;
            }
                       
            else if (s.contains("-")){
                return true;
            }
             else if (s.equals("the")){
                return false;
            }
             else if (s.equals("and")){
                return false;
            }
             
             else if (s.equals("you")){
                return false;
            }
                         
             else if (!isLetter(s)) { 
                return false;
            }
            
             else{
                return true;
             }
        }
        
        public static boolean isLetter(String s)
        {
            while (!Character.isLetter((s.charAt(s.length()-1)))){               
              
             s.replaceAll("[^\\dA-Za-z#]", "");  
            
            }
            for (int i = 0; i < s.length(); i++){
                 if (!Character.isLetter(s.charAt(i) ) ) 
                 {return false;}
            }
            return true;
        }
        
         public static boolean isTermStat(String s, TermStats[] tstat)
         {
             for (TermStats ts:tstat)
             {
                 if (s.equals(ts.termtext.utf8ToString()))
                     
                     return true;
                     
             }
             return false;
         }
        
        
    
}
