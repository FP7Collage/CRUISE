/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.cruise.lucene;

/**
 *
 * @author imu-user
 */
import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author maryger
 */
public class NGramExtractor  {

  
    private static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i].toLowerCase());
        return sb.toString();
    }

    public static ArrayList<String> getNGrams(String s,int minNgram, int maxNgram){
        ArrayList<String> ngrams = new ArrayList<String>();   
        for (int n = minNgram; n <= maxNgram; n++) {
            for (String ngram : ngrams(n, s))
            {  ngrams.add(ngram);}
           
        }
    
    return ngrams;
    }
}