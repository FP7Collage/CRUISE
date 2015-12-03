package gr.imu.ntua.cruise.crawlers.services;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 02/07/13
 * Time: 7:09 PM
 */
public interface WebIndexer {

    void addToIndex(String source, String url, String text);
}
