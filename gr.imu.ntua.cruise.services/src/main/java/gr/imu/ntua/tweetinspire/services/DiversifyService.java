package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: fotis
 * Date: 17/04/13
 * Time: 2:14 PM
 */
public interface DiversifyService {


    Map<String, List<AbstractDiversifyService.Result>> getResultsFor(String[] filter);

    /**
     * TODO Remove this from the abstract service and into a single class
     * @param filter
     * @return
     */
    List<AbstractDiversifyService.Image> getImagesFor(String[] filter);
    String getSearchEngineName();

    List<AbstractDiversifyService.Result> diversify(List<AbstractDiversifyService.Result> resultList) throws IOException;
    List<AbstractDiversifyService.Result> getBingResultsForCloud(String[] filter);
    List<SearchTerm> getLinksAsTerms(float maxtermfreq, String terms, int resultsSize);
}
