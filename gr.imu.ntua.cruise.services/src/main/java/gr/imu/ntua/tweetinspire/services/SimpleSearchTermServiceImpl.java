package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 17/09/13
 * Time: 11:12 AM
 */
public class SimpleSearchTermServiceImpl implements SimpleSearchTermService{


    private Logger logger = LoggerFactory.getLogger(SimpleSearchTermServiceImpl.class);
    @Autowired
    TermExtractService termExtractService;

    @Autowired
    private transient TwitterService twitterService;


//    private CruiseAnalyzer analyzer;

    @PostConstruct
    public void init(){

//        analyzer = new CruiseAnalyzer();
    }


    @Override
    public List<SearchTerm> getSearchTermsForAccounts(float max, String terms, List<String> acl, double v) {

        List<SearchTerm> searchTerms = new ArrayList<>();
        HashMap<String, Integer> frequencies = new HashMap<String, Integer>();


        if(acl == null || acl.size() <=0 ){
            return searchTerms;
        }

        try {

            Map<Long,Tweet> tweetsFor = twitterService.getTweetsFor(terms.replace(" "," OR "), acl, 100);

            if(tweetsFor.size() <=0){
                return searchTerms;
            }

            RAMDirectory ramDirectory = new RAMDirectory();
            CruiseAnalyzer analyzer = new CruiseAnalyzer();
            IndexWriter iw= new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, analyzer));

            for(Long id: tweetsFor.keySet()){

                Tweet tweet = tweetsFor.get(id);

                Document d = new Document();

                d.add(new StringField("id",String.valueOf(tweet.getId()), Field.Store.YES));
                d.add(new TextField("content",tweet.getText(), Field.Store.YES ));

                iw.updateDocument(
                        new Term("id",String.valueOf(tweet.getId())),
                        d
                );

            }

            iw.close();

            IndexReader ir = DirectoryReader.open(ramDirectory);
            IndexSearcher is = new IndexSearcher(ir);

            TermStats[] contents = HighFreqTerms.getHighFreqTerms(ir, 100, "content");

            logger.trace("List<SearchTerm> getSearchTermsForAccounts([terms, acl, v]) {} ",contents);

            int termStatLength = contents.length;
            
            Terms termsofTweets = MultiFields.getFields(ir).terms("content");
            TermsEnum termsEnum = termsofTweets.iterator(null);
            BytesRef text;
            while((text = termsEnum.next()) != null) {
                String term = text.utf8ToString();
                int freq = (int) termsEnum.totalTermFreq();
                frequencies.put(term,freq)  ;
            }
//

            int id= Integer.MAX_VALUE;
            for(TermStats t: contents){

                if (StringUtils.isEmpty(t.termtext.utf8ToString())){
                    continue;
                }

                List<String> documents=new ArrayList<>();
                long totalTermFreq = frequencies.get(t.termtext.utf8ToString()); //ir.totalTermFreq(new Term(t.field, t.termtext));

//                This is commented out because it return 0 for high frequency words which apeaar in all
//                documents
//
                TFIDF tfidf = new TFIDF(totalTermFreq, termStatLength, ir.numDocs()/*numDocsInFull*/, t.docFreq);

                logger.trace("List<SearchTerm> getSearchTermsForAccounts([terms, acl, v]) {} => {} ? {}",
                        new Object[]{
                                t,
                                totalTermFreq,
                                tfidf.getValue()
                        });


                /***For every term find all the related tweets ****/
                TopDocs search = is.search(new TermQuery(new Term(t.field, t.termtext)), 30);
                ScoreDoc[] scoreDocs = search.scoreDocs;

                for(ScoreDoc sd : scoreDocs){
                    documents.add(ir.document(sd.doc).get("content"));
                }

                float frequency =
                        max-(max*(totalTermFreq / (float)termStatLength));

                SearchTerm e = new SearchTerm(
                        "customize-results",
                        id,
                        frequency,
                        t.termtext.utf8ToString(),
                        "",
                        documents
                );
                logger.trace("List<SearchTerm> getSearchTermsForAccounts([terms, acl, v]) {}",e);
                searchTerms.add(
                        e
                );


                id--;


            }   //end of for ts

            ir.close();

        } catch (Exception e) {
            logger.warn("Error whilst trying to extract tweets {}",e.getLocalizedMessage());
            e.printStackTrace();
        }


        return searchTerms;
    }
}
