package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.tweetinspire.services.AbstractDiversifyService.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/07/13
 * Time: 3:39 PM
 */
public class WaagDiversifyService implements CrawlDiversifyService {


    private Logger logger = LoggerFactory.getLogger(WaagDiversifyService.class);

    @Autowired
    private DiversifyService diversifyService;

    @Autowired
    Properties systemProperties;
    private CruiseAnalyzer cruiseAnalyzer;


    @PostConstruct
    public void init(){
        cruiseAnalyzer = new CruiseAnalyzer();
    }

    @Override
    public Map<String, List<AbstractDiversifyService.Result>> getResultsFor(String[] filter) {

        Map<String,List<AbstractDiversifyService.Result>> results = new HashMap<>();
        results.put("original",new ArrayList<AbstractDiversifyService.Result>());
        results.put("diversified",new ArrayList<AbstractDiversifyService.Result>());


        int max = Integer.valueOf(systemProperties.getProperty("diversify.maxResults"));
        List<AbstractDiversifyService.Result> original = new ArrayList<>();
        try{

            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");


            IndexReader ir = DirectoryReader.open(new SimpleFSDirectory(
                    new File(systemProperties.getProperty("crawler.dir.lucene")),
                    NoLockFactory.getNoLockFactory()
            ));


            TopScoreDocCollector collector = TopScoreDocCollector.create(100,true);

            IndexSearcher is = new IndexSearcher(ir);
            Query q = new QueryParser(Version.LUCENE_41, "content", cruiseAnalyzer).parse(
                    StringUtils.join(filter," ")
            );

            is.search(q,collector);

            TopDocs topDocs = collector.topDocs();
            ScoreDoc[] scoreDocs1 = topDocs.scoreDocs;



            for(ScoreDoc sd: scoreDocs1){
                Document document = ir.document(sd.doc);

                String content = StringUtils.trimToEmpty(document.get("content"));
                original.add(new AbstractDiversifyService.Result(
                   sd.doc,
                    document.get("url"),
                    document.get("url"),
                    StringUtils.substring(content,0,400),
                    new String(sha1.digest(document.get("url").getBytes()))
                ));
            }

            ir.close();

            results.put("original",original);
            if(original.size() > max){
                results.put("original",original.subList(0,max));
            }


            if(original.size() >0){

                //diversify
                List<Result> diversify = diversifyService.diversify(original);

                if(diversify.size() > max){
                    results.put("diversified",diversify.subList(0,max));
                }else{
                    results.put("diversified",diversify);
                }
            }

        } catch (IOException | NoSuchAlgorithmException | ParseException e) {
            logger.warn("Couldn't diversify results, using original ");
            results.put("diversified",results.get("original"));

        }

        return results;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSearchEngineName() {
        return "Waag";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
