package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 04/07/13
 * Time: 1:40 PM
 */
public interface CrawlSearchService {


    List<SearchTerm> search(String terms, List<String> strings, Integer threshold) throws Exception;

}
