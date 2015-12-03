package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import junit.framework.Assert;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 17/09/13
 * Time: 6:26 PM
 */
public class SimpleSearchTermServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(SimpleSearchTermServiceImplTest.class);


    @Test
    public void test(){

        try {


            RAMDirectory ramDirectory = new RAMDirectory();
            Analyzer analyzer = new CruiseAnalyzer();
//            analyzer = new KeywordAnalyzer();

            IndexWriter iw= new IndexWriter(ramDirectory,new IndexWriterConfig(Version.LUCENE_41, analyzer));

            Document d = new Document();

            d.add(new StringField("id", String.valueOf(1), Field.Store.YES));
            d.add(new TextField("content","@iansthomas @matthieuhug I replied to a comment on data sharing in a decomposed enterprise - any thoughts? http://t.co/KqQxpXn80T", Field.Store.YES ));


            iw.updateDocument(
                    new Term("id",String.valueOf(1)),
                    d
            );

            iw.close();


            IndexReader ir = DirectoryReader.open(ramDirectory);
            Assert.assertEquals(ir.numDocs(),1,0);

            TermStats[] contents = HighFreqTerms.getHighFreqTerms(ir, 100, "content");

            logger.trace("List<SearchTerm> getSearchTermsForAccounts([terms, acl, v]) {} ",contents);

            for(TermStats ts: contents){

                logger.trace("void test([]) {} -> {}",ts.docFreq,ts.termtext.utf8ToString());
            }
            Assert.assertTrue(contents.length > 3);



        } catch (Exception e) {
            logger.warn("Error whilst trying to extract tweets {}",e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}
