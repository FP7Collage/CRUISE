package gr.imu.ntua.cruise.crawlers.services;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.store.SingleInstanceLockFactory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 02/07/13
 * Time: 7:10 PM
 */
public class LuceneWebIndexer implements WebIndexer{

    private Logger logger = LoggerFactory.getLogger(LuceneWebIndexer.class);

    private ReentrantLock lock;
    private StandardAnalyzer standardAnalyzer;

    private Properties systemProperties;
    private File directoryPath;
    private SimpleFSDirectory dir;
    private IndexWriter iw;

    @PostConstruct
    public void init(){

        lock=new ReentrantLock();
        try {

            List<String> strings = IOUtils.readLines(
                    new InputStreamReader(
                            this.getClass().getResourceAsStream("/stopwords.txt"),
                            "UTF-8"));

            standardAnalyzer = new
                    StandardAnalyzer(
                    Version.LUCENE_41,
                    new CharArraySet(Version.LUCENE_41,strings, true));


            directoryPath = new File(systemProperties.getProperty("lucene.directory"));
            FileUtils.forceMkdir(directoryPath);
            logger.trace(" () Created {} ",directoryPath);
            dir = new SimpleFSDirectory(directoryPath, new SingleInstanceLockFactory());
            iw = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_41, standardAnalyzer));

        } catch (IOException e) {
            throw new RuntimeException("Couldn't initialize the indexer",e);
        }

    }

    @Override
    public void addToIndex(String source, String url, String text){

        logger.trace("void addToIndex([source, url, text]) Locking...");
        lock.lock();


        try {

            logger.trace("void addToIndex([source, url, text]) Adding {} => {}",url,text);
            Document d = new Document();

            d.add(new StringField("url",url, Field.Store.YES ));
            d.add(new StringField("source",source, Field.Store.YES ));
            d.add(new TextField("content",text, Field.Store.YES ));

            //http://stackoverflow.com/questions/9731671/lucene-how-to-add-document-without-duplication
            iw.updateDocument(
                    new Term("url",url),
                    d
            );
        } catch (IOException e) {
            logger.warn("Couldn't add to index {} ",url);

        } finally {
            try {
                iw.commit();
            } catch (IOException e) {
                logger.error("Exception commiting index",e);
            }
        }

        logger.trace("void addToIndex([source, url, text]) Un - Locking...");
        lock.unlock();

    }



    public void cleanup(){
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

    public void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }
}
