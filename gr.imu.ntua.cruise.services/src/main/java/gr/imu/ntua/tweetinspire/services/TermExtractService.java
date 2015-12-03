package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/07/13
 * Time: 1:17 PM
 */
public interface  TermExtractService {
    List<SearchTerm> search(IndexReader ir, Analyzer analyzer, String terms, List<String> filter, int threshold, String source) throws Exception;
}
