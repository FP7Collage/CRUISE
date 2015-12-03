package gr.imu.ntua.tweetinspire.services;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import gr.imu.ntua.cruise.lucene.NGramExtractor;
import gr.imu.ntua.cruise.lucene.NGramFilter;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.util.Map.Entry;


/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/07/13
 * Time: 1:17 PM
 */
public class LuceneTermExtractService implements TermExtractService {

    private final int HITS = 100;

    private Logger logger = LoggerFactory.getLogger(LuceneTermExtractService.class);
    
    public  int NumDocs = 0; 
    
   
    @Override
    public List<SearchTerm> search(IndexReader ir, Analyzer analyzer, String terms, List<String> filter, int threshold, String source) throws Exception {


        logger.trace("List<SearchTerm> search([ir, analyzer, terms, filter, threshold, source]) Number of documents {} ",ir.numDocs());
        IndexSearcher is = new IndexSearcher(ir);


        TermStats[] termStats;
        
        if(StringUtils.isEmpty(terms)){

            logger.info("The terms are empty so we are retrieving the top {} terms for this session",HITS);
            termStats = HighFreqTerms.getHighFreqTerms(ir, HITS, "content");
        }else{

            TopScoreDocCollector collector = TopScoreDocCollector.create(HITS,true);
            logger.info("Searching for",HITS);


            logger.info("Lucene query {}",terms);

            Query q = new QueryParser(Version.LUCENE_41, "content", analyzer).parse(terms);

            is.search(q,collector);

            if(collector.getTotalHits() <=0){
                return new ArrayList<SearchTerm>();
            }

            
            termStats = getTermStatsForSearchResults(ir, analyzer,collector);

        }
        /**
         * The following code might not be necessary but I can figure out how to do this
         * at the moment
         */
        return createSearchTermsForTermStats(is, ir, analyzer, termStats, new ArrayList<String>(),filter,threshold,source);

    }

    private List<SearchTerm> createSearchTermsForTermStats(IndexSearcher is, IndexReader ir, Analyzer analyzer, TermStats[] termStats, List<String>boost, List<String> ignore,int threshold, String source) throws IOException, InvalidTokenOffsetsException {

        Map</*BytesRef*/String, Float> termFrequencyMap = new HashMap</*BytesRef*/String, Float>();
        Map</*BytesRef*/String,List<Document>> documentsPerTerm = new HashMap</*BytesRef*/String, List<Document>>();
        
     //  NLPServiceImpl nlpAnalyser = new NLPServiceImpl();
        
         Directory foundDir = new RAMDirectory();
		IndexWriter foundDirIndexWriter = new IndexWriter(foundDir,
				new IndexWriterConfig(Version.LUCENE_41, analyzer));
        
        //long tfidfofFirstDoc= termStats[0].totalTermFreq;
        int termStatsLength = termStats.length;
        int numDocs = ir.numDocs();
        float maxDocFreq = MaxFreq(termStats,termStatsLength);
        float maxFrequency = 0.0f; 
        NumDocs = numDocs;
        
         /*Code starts for n-grams*/
       
        ArrayList<String> nouns = new ArrayList<String>();
        HashMap<String, ArrayList<String>> filteredListMap = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> secondfilteredListMap = new HashMap<String, ArrayList<String>>();
        HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
        HashMap<String, Integer> termsWithMaxFreqs = new HashMap<String, Integer>();
        //Properties props = new Properties();
        //props.put("annotators", "tokenize, ssplit, pos");
        //StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
       // NLPServiceImpl nlpanalyser= new NLPServiceImpl();
        //StanfordCoreNLP pipe = nlpanalyser.pipeline;
        
        /*Code starts for NLP*/
  /*     for (int i = 0; i < ir.maxDoc(); i++) {
             Document doc1 = ir.document(i);
          
            String textnlp = doc1.getField("content").stringValue();//  .toString();//"Viki is a smart boy. He knows a lot of things.";
            Annotation document = new Annotation(textnlp);
                      
            NLPServiceImpl.pipeline.annotate(document); 
           
            List<CoreMap> sentences = document.get(SentencesAnnotation.class);// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types

            for(CoreMap sentence: sentences) {
            
              for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                
                String word = token.get(TextAnnotation.class);
                System.out.println(word);
               
                String pos = token.get(PartOfSpeechAnnotation.class);
                System.out.println(pos);
                if ("NN".equals(pos)||"NNS".equals(pos)||"JJ".equals(pos))
                    {nouns.add(word);}        
              }
            }       
         }
         for (int i=0;i<nouns.size();i++){
            System.out.println(nouns.get(i));
        }
*/
                     
        /*Code starts for n-grams*/
        nouns.add("test"); // nouns comes from NLP but here will be test mode
        NGramsImpl ngrams = new NGramsImpl();
        filteredListMap = ngrams.getNGrams(ir, termStats,nouns);
       
        /*Code ends for n-grams*/
        
        String ngramkey="";    
                         
        /**** I am going to put in the frequencyMap all the terms with their frequencies****/
       /*  for(TermStats ts1: termStats){

               System.out.println("test freq"+ts1.docFreq+"term"+ts1.termtext.utf8ToString());
            }*/
        
        Terms terms = MultiFields.getFields(ir).terms("content");
        TermsEnum termsEnum = terms.iterator(null);
        BytesRef text;
        while((text = termsEnum.next()) != null) {
            
           String term = text.utf8ToString();
           int freq = (int) termsEnum.totalTermFreq();      
          frequencies.put(term,freq)  ;
        
         }
        
        for(TermStats ts :termStats){  
            if (ts.termtext.utf8ToString().isEmpty()) //check if empty string
            {
                continue;
            }
            
            if (ts.docFreq == maxDocFreq) //check if maximum number of docs the terms appears in equals number of docs in the set. In this case we want to boost and override the fact that log1=0
            {
                termsWithMaxFreqs.put(ts.termtext.utf8ToString(), ts.docFreq);
            }       
              
            
            else{
            
                if(!documentsPerTerm.containsKey(ts.termtext.utf8ToString())) {
                    documentsPerTerm.put(/*ts.termtext*/ts.termtext.utf8ToString(),new ArrayList<Document>());
                }       
               // TFIDF myTfidf =  new TFIDF(ir.totalTermFreq(new Term(ts.field, ts.termtext)),termStatsLength,ir.numDocs(),ts.docFreq);
            //    System.out.println("Eimai o oros: " + (ts.termtext.utf8ToString())+"kai exw syxnothta"+frequencies.get(ts.termtext.utf8ToString()));
                TFIDF myTfidf =  new TFIDF(frequencies.get(ts.termtext.utf8ToString()),termStatsLength,ir.numDocs(),ts.docFreq);
                float finaltfidf = myTfidf.getValue();
              //  System.out.println("TFIDF "+finaltfidf+" NumofOccurences "+frequencies.get(ts.termtext.utf8ToString())+" Total Terms "+termStatsLength+" Total Documents "+ir.numDocs()+" DocswithTerms "+ts.docFreq);
                termFrequencyMap.put(ts.termtext.utf8ToString(), finaltfidf);


                /***For every term find all the related tweets ****/
                TopDocs search = is.search(new TermQuery(new Term(ts.field, ts.termtext.utf8ToString())), 30);
                ScoreDoc[] scoreDocs = search.scoreDocs;
                for(ScoreDoc sd : scoreDocs){
                    documentsPerTerm.get(ts.termtext.utf8ToString()).add(ir.document(sd.doc));
                }   
            }
    
        }   //end of for ts
        
        maxFrequency = getMaxValue(termFrequencyMap);      
      //  System.out.println("MaxFrequency of tweet terms is: " +maxFrequency);
        /**********Block of code concerning the terms that their docFrequency equals total Documents in the set (We add them separately because we want to boost importance(i.e frequency))**************/
        Set<Map.Entry<String, Integer>> entrySet = termsWithMaxFreqs.entrySet();
        for (Entry entry : entrySet) {
            String text2 = entry.getKey().toString();
            int val = (int) entry.getValue();
             if(!documentsPerTerm.containsKey(entry.getKey().toString())) {
                    documentsPerTerm.put(/*ts.termtext*/text2,new ArrayList<Document>());
                }       
                /* termFrequencyMap.put(ts.termtext, ts.docFreq);*/
                float maxFrequency2 = getMaxValue(termFrequencyMap);
                termFrequencyMap.put(text2, maxFrequency2);

                /***For every term find all the related tweets ****/
                TopDocs search = is.search(new TermQuery(new Term("content", text2)), 30);
                ScoreDoc[] scoreDocs = search.scoreDocs;
                for(ScoreDoc sd : scoreDocs){
                    documentsPerTerm.get(text2).add(ir.document(sd.doc));
                }         
            
        }          
        
         /***** I am going to put in the frequencyMap all the 2-grams with their frequencies****/
        if (filteredListMap!=null && !filteredListMap.isEmpty()){           
                for (String key : filteredListMap.keySet()) {
       
                    if (filteredListMap.get(key)!=null){                     
                        if (filteredListMap.get(key).get(0)!=null && filteredListMap.get(key).get(1)!=null )
                        {
                          if(!documentsPerTerm.containsKey(key)) {
                            documentsPerTerm.put(/*ts.termtext*/key,new ArrayList<Document>());
                          }//end of if   

                        ngramkey=key;
                        String firstWordOfNgram = filteredListMap.get(ngramkey).get(0);
                        String secondWordOfNgram = filteredListMap.get(ngramkey).get(1);
                        
                                          
                        Float f1 = termFrequencyMap.get(firstWordOfNgram);
                        Float f2 = termFrequencyMap.get(secondWordOfNgram);
                        ArrayList<String> listWithNgramsTerms = new  ArrayList<String>();
                        listWithNgramsTerms.add(firstWordOfNgram);
                        listWithNgramsTerms.add(secondWordOfNgram);                        
                        
                        if (f1==null || f2==null)  //normally it should never get inside that if-statement
                        {
                                if (f1 == null && f2 == null)
                                {
                                    break;
                                }
                                else if (f1==null)
                                { 
                                    float finaltfidf_2 =  f2;
                                    termFrequencyMap.put(ngramkey, finaltfidf_2);
                                    secondfilteredListMap.put(ngramkey,listWithNgramsTerms);
                                  
                                 }
                                else if(f2 == null)
                                { 
                                   float finaltfidf_1 =  f1;
                                   termFrequencyMap.put(ngramkey, finaltfidf_1); 
                                   secondfilteredListMap.put(ngramkey,listWithNgramsTerms);
                                
                                }
                        }
                        else{
                            
                            int retval = Float.compare(f1, f2);  
                            
                            if (retval>0){
                                termFrequencyMap.put(ngramkey, f2);
                                secondfilteredListMap.put(ngramkey,listWithNgramsTerms);
                                                           }
                            else if (retval<0){
                                termFrequencyMap.put(ngramkey, f1); 
                                secondfilteredListMap.put(ngramkey,listWithNgramsTerms);
                                                           }    
                            else if (retval==0)    
                            {
                                termFrequencyMap.put(ngramkey, f1);
                                secondfilteredListMap.put(ngramkey,listWithNgramsTerms);
                              
                            }

                     }
                       /*****************Construct query for ngram in order to retrieve related tweets (searching with two words now)***********************************/
                        PhraseQuery pq = new PhraseQuery();
                        pq.add(new Term("content",firstWordOfNgram), 0);
                        pq.add(new Term("content",secondWordOfNgram), 1);
                        pq.setSlop(0);                        
                        
                         /***For every 2-gram find all the related tweets ****/
                         TopDocs search = is.search(pq, 30);
                         ScoreDoc[] scoreDocs2 = search.scoreDocs;
                         for(ScoreDoc sd2 : scoreDocs2){
                              documentsPerTerm.get(ngramkey).add(ir.document(sd2.doc));
                              }   //end of for sd2
                         }
                         else break;
                      }
                      else
                          break;
           } 
           
         }    
        /****Delete the terms that are included in Bi-grams from the word cloud******/
        
        for (String key : secondfilteredListMap.keySet())
        {
              termFrequencyMap.remove(secondfilteredListMap.get(key).get(0));
              termFrequencyMap.remove(secondfilteredListMap.get(key).get(1));
              
         }        
        
        WeightedSpanTerm[] weightedSpanTerms = getWeightedSpanTerms(threshold, termFrequencyMap);        
        Highlighter highlighter = new Highlighter(new QueryScorer(weightedSpanTerms));
        highlighter.setTextFragmenter(new NullFragmenter());
        Set</*BytesRef*/String> set = termFrequencyMap.keySet();
        
        //get the top 10;
        List<SearchTerm> searchTerms = new ArrayList<SearchTerm>();
        int id=0;
        for(/*BytesRef*/String termStr: set){

           float  freq =  termFrequencyMap.get(termStr);
           // Integer freq =  termFrequencyMap.get(termStr);

            if(freq >= threshold && !(boost.contains(termStr/*.utf8ToString()*/) || ignore.contains(termStr/*.utf8ToString()*/))){

                List<String> data = new ArrayList<String>();
                List<Document> documents = documentsPerTerm.get(termStr);
                if (documents!=null){
                    for(Document d :documents){
                        String highlighterBestFragment=  highlighter.getBestFragment(analyzer,"content",d.get("content"));
                        data.add(highlighterBestFragment);
                    }
                }//end of if isempty   
               
     

                searchTerms.add(
                        new SearchTerm(
                                source,
                                id++,
                                freq,
                                termStr/*.utf8ToString()*/,
                                "",
                                data));               
            }
        }      

        ir.close();
        return searchTerms;       
    }

    
        
//<<<<<<< Updated upstream
//    private WeightedSpanTerm[] getWeightedSpanTerms(Integer threshold, Map<BytesRef,Float> map) {
//=======
private WeightedSpanTerm[] getWeightedSpanTerms(Integer threshold, Map</*BytesRef*/String,Float> map) {
//>>>>>>> Stashed changes
        Set set = map.keySet();
        Iterator iterator = set.iterator();

        //create a search term
        List<WeightedSpanTerm> weightedSpanTerms = new ArrayList<WeightedSpanTerm>();
        while (iterator.hasNext()){

            String termStr =  (String)iterator.next();
            float freq = map.get(termStr);

            if(freq > threshold){
                weightedSpanTerms.add(
                        new WeightedSpanTerm(freq,termStr)
                );
            }
        }
        return weightedSpanTerms.toArray(new WeightedSpanTerm[weightedSpanTerms.size()]);

    }

        
    private static float MaxFreq(TermStats[] tm, int termscount){

                     float maxEntry = 0.0f;

                    for (int t=0;t<termscount;t++){ 
                            if (tm[t].docFreq> maxEntry) {
                                    maxEntry = tm[t].docFreq;
                            }
                    }
                    return maxEntry;
   }
   
   private static int getKeysFromValue(String value, ArrayList<String> list){
       int k;
       // List <Object>list = new ArrayList<Object>();
    for( k=0;k<list.size();k++){
        if(list.get(k).equals(value)) {
            
        }
    }
    return k;
  }
   
  private static float getMaxValue(Map<String, Float> map1)

	{
		Entry<String, Float> maxEntry = null;

		for (Entry<String, Float> entry : map1.entrySet()) {
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
				maxEntry = entry;
			}
		}

		return maxEntry.getValue();
	}
  

        
    private TermStats[] getTermStatsForSearchResults(IndexReader ir, Analyzer analyzer, TopScoreDocCollector collector) throws Exception {

        Directory ramDirectory = new RAMDirectory();
        IndexWriter tmpWritter = new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, /*standardAnalyzer*/analyzer));
        //IndexWriter tmpWritter = new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, standardAnalyzer));
        TopDocs topDocs = collector.topDocs();

        ScoreDoc[] scoreDocs1 = topDocs.scoreDocs;

        for(ScoreDoc sd: scoreDocs1){
            tmpWritter.addDocument(ir.document(sd.doc));
        }
        tmpWritter.commit();
        IndexReader tmpIr = DirectoryReader.open(ramDirectory);
        IndexSearcher tmpIs = new IndexSearcher(tmpIr);       
        
        TermStats[] contents= HighFreqTerms.getHighFreqTerms(tmpIr, HITS, "content");
        int termscount = contents.length;
       // logger.info("General termscount{}", termscount); 
   

       // TermStats[] tweets = HighFreqTerms.  .getHighFreqTerms(tmpIr, HITS, "tweet");

      //  TermStats [] terms = new TermStats(contents[t].term, contents[t].docFreq, myTfidf );


        tmpWritter.close();
        ramDirectory.close();

        return contents;
    
    }
    
}

        
/* Fotis  createSearchTermsForTermStats
 * private List<SearchTerm> createSearchTermsForTermStats(IndexSearcher is, IndexReader ir, Analyzer analyzer, TermStats[] termStats, List<String>boost, List<String> ignore,int threshold, String source) throws IOException, InvalidTokenOffsetsException {

        Map<BytesRef, Long> termFrequencyMap = new HashMap<BytesRef, Long>();
        Map<BytesRef,List<Document>> documentsPerTerm = new HashMap<BytesRef, List<Document>>();
        

        for(TermStats ts :termStats){

          
            if(!documentsPerTerm.containsKey(ts.termtext)){ documentsPerTerm.put(ts.termtext,new ArrayList<Document>()) ;}
           // termFrequencyMap.put(ts.termtext, ts.docFreq);
           
           int termscount = termStats.length;              
       
             termFrequencyMap.put(ts.termtext, ts.total);

            TopDocs search = is.search(new TermQuery(new Term(ts.field, ts.termtext)), 30);

            ScoreDoc[] scoreDocs = search.scoreDocs;
            for(ScoreDoc sd : scoreDocs){
                documentsPerTerm.get(ts.termtext).add(ir.document(sd.doc));
            }
        }

        WeightedSpanTerm[] weightedSpanTerms = getWeightedSpanTerms(threshold, termFrequencyMap);
        Highlighter highlighter = new Highlighter(new QueryScorer(weightedSpanTerms));
        highlighter.setTextFragmenter(new NullFragmenter());



        Set<BytesRef> set = termFrequencyMap.keySet();


        //get the top 10;
        List<SearchTerm> searchTerms = new ArrayList<SearchTerm>();

        int id=0;
        for(BytesRef termStr: set){

           Long  freq =  termFrequencyMap.get(termStr);
           // Integer freq =  termFrequencyMap.get(termStr);

            if(freq > threshold && !(boost.contains(termStr.utf8ToString()) || ignore.contains(termStr.utf8ToString()))){

                List<String> data = new ArrayList<String>();
                List<Document> documents = documentsPerTerm.get(termStr);
                for(Document d :documents){
                    String highlighterBestFragment=  highlighter.getBestFragment(analyzer,"content",d.get("content"));
                    data.add(highlighterBestFragment);
                }

                searchTerms.add(
                        new SearchTerm(
                                source,
                                id++,
                                freq,
                                termStr.utf8ToString(),
                                data));
            }
        }

        ir.close();
        return searchTerms;

    } */
        
   

  // Fotis getTermStatsForSearchResults
  //  private TermStats[] getTermStatsForSearchResults(IndexReader ir, Analyzer analyzer, TopScoreDocCollector collector) throws Exception {

  //      Directory ramDirectory = new RAMDirectory();
  //      IndexWriter tmpWritter = new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, /*standardAnalyzer*/analyzer));
  //      //IndexWriter tmpWritter = new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, standardAnalyzer));
  //      TopDocs topDocs = collector.topDocs();

  //      ScoreDoc[] scoreDocs1 = topDocs.scoreDocs;

  //      for(ScoreDoc sd: scoreDocs1){
  //          tmpWritter.addDocument(ir.document(sd.doc));
  //      }
  //      tmpWritter.commit();
  //      IndexReader tmpIr = DirectoryReader.open(ramDirectory);
  //      IndexSearcher tmpIs = new IndexSearcher(tmpIr);
        
        
  //      Map<String,Integer> terms = new HashMap<String,Integer>();

      
  //      TermStats[] contents= HighFreqTerms.getHighFreqTerms(tmpIr, HITS, "content");
  //       
     

  //      tmpWritter.close();
  //      ramDirectory.close();

  //      return contents;
  //  }
