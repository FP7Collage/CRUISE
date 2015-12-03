package gr.imu.ntua.tweetinspire.services;

import java.util.List;
import java.util.Map;

/**
 * User: fotis
 * Date: 17/04/13
 * Time: 2:14 PM
 */
public interface CrawlDiversifyService {

    Map<String, List<AbstractDiversifyService.Result>> getResultsFor(String[] filter);
    String getSearchEngineName();

}
