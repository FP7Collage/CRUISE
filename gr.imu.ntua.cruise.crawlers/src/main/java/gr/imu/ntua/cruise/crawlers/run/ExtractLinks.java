package gr.imu.ntua.cruise.crawlers.run;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import gr.imu.ntua.cruise.crawlers.crawler4j.ExtractTwitterLinksCrawler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 18/07/13
 * Time: 4:12 PM
 */
public class ExtractLinks {


    public static void main(String[] args) throws Exception {

        Properties systemProperties = new Properties();
        systemProperties.load(Crawl.class.getResourceAsStream("/system.properties"));

        String crawlStorageFolder = "/tmp/crawl/twitter";

        int numberOfCrawlers = 10;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(1000);
        config.setFollowRedirects(true);
//        config.setResumableCrawling(true);
        config.setUserAgentString("IMU Collage (http://imu.ntua.gr/project/collage");


                /*
                 * Instantiate the controller for this crawl.
                 */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("http://www.creativemornings.com/");
        controller.addSeed("http://www.nrc.nl/");

        Map<String,Object> customData = new HashMap<>();
        customData.put("url","http://www.creativemornings.com/");
        customData.put("systemProperties",systemProperties);
        controller.setCustomData(customData);

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(ExtractTwitterLinksCrawler.class, numberOfCrawlers);
    }


}
