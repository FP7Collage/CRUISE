package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.store.SingleInstanceLockFactory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 26/02/13
 * Time: 2:09 PM
 */
public  class DocumentIndexerImpl implements DocumentIndexer{


    private TermExtractService termExtractService;
    private final File directoryPath;
    private Logger logger = LoggerFactory.getLogger(DocumentIndexerImpl.class);

    private ReentrantLock lock;

    private final Analyzer analyzer;
  //  private transient NLPService nlpAnalyser;
    private final SimpleFSDirectory dir;
    private final IndexWriter iw;

    private Properties props;

    public DocumentIndexerImpl(String id, TermExtractService termExtractService) {

        lock=new ReentrantLock();
        try {

            analyzer = new CruiseAnalyzer();
            this.termExtractService = termExtractService;
            directoryPath = new File(System.getProperty("java.io.tmpdir"),id);
            FileUtils.forceMkdir(directoryPath);
            logger.trace(" () Created {} ",directoryPath);
            dir = new SimpleFSDirectory(directoryPath, new SingleInstanceLockFactory());
            iw = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_41, analyzer));
            //nlpAnalyser = new NLPServiceImpl().NLPServiceImpl();
          
            

        } catch (IOException e) {
            throw new RuntimeException("Couldn't initialize the indexer",e);
        }
    }

    @PreDestroy
    @Override
    public void cleanUp(){
        lock.lock();
        try {
            iw.commit();
            iw.close();
        } catch (IOException e) {
            logger.warn("Carefull here the index writter is ");
        } finally {
        }
        lock.unlock();

    }

    @Override
    public void addToIndex(Map<Long, Tweet> tweets){
        lock.lock();
        try {

            logger.trace("void addToIndex([tweets]) Adding to {}",directoryPath);

            Set<Long> longs = tweets.keySet();

            for(Long id: longs){

                Tweet tweet = tweets.get(id);



                Document d = new Document();

                d.add(new StringField("id",String.valueOf(tweet.getId()), Store.YES));
                //System.out.println(String.valueOf(tweet.getId()));
                d.add(new TextField("content",tweet.getText(), Store.YES ));
                //System.out.println(String.valueOf(tweet.getText()));
                //System.out.println(String.valueOf(tweet.getFromUser()));
                
//                d.add(new Field("id",String.valueOf(tweet.getId()), Field.Store.YES ,Field.Index.NOT_ANALYZED));
//                d.add(new Field("tweet",tweet.getText(), Field.Store.YES ,Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
//                d.add(new Field("user", tweet.getFromUser(), Field.Store.YES, Field.Index.NOT_ANALYZED));

                //http://stackoverflow.com/questions/9731671/lucene-how-to-add-document-without-duplication
                iw.updateDocument(
                        new Term("id",String.valueOf(tweet.getId())),
                        d
                );

            }
        } catch (IOException e) {
            logger.error("Exception adding tweets ",e);
        } finally {

            try {
                iw.commit();
            } catch (IOException e) {
                logger.error("Exception commiting index",e);
            } finally {
            }

        }

        lock.unlock();
    }

    public List<SearchTerm> search(String terms, List<String> filter, int threshold) throws Exception {

        lock.lock();
        List<SearchTerm> search = new ArrayList<>();
        try{


        if(!directoryPath.exists()){
            return new ArrayList<>();
        }

        IndexReader ir = DirectoryReader.open(dir);

            search = termExtractService.search(ir, analyzer, terms, filter, threshold,"twitter");
            ir.close();
        }catch (Exception e){
            throw e;
        }finally {
            lock.unlock();
        }

        return search;

    }

}

