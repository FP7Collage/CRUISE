package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.cruise.lucene.CruiseAnalyzer;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/07/13
 * Time: 1:41 PM
 */
public class CrawlSearchServiceImpl implements CrawlSearchService {

    @Autowired
    TermExtractService termExtractService;

    @Autowired
    Properties systemProperties;
    private CruiseAnalyzer cruiseAnalyzer;
   // private NLPServiceImpl nlpAnalyser;
  

    @PostConstruct
    public void init(){

        cruiseAnalyzer = new CruiseAnalyzer();
      //  nlpAnalyser = new NLPServiceImpl();
    }

    @Override
    public List<SearchTerm> search(String terms, List<String> strings, Integer threshold) throws Exception {
        IndexReader ir = DirectoryReader.open(new SimpleFSDirectory(
                new File(systemProperties.getProperty("crawler.dir.lucene")),
                NoLockFactory.getNoLockFactory()
        ));
     
        List<SearchTerm> search = termExtractService.search(ir, cruiseAnalyzer, terms, strings, threshold,"crawler");
        ir.close();

        return search;
    }
}
