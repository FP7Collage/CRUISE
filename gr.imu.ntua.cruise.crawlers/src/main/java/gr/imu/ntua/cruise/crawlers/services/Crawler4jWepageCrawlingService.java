package gr.imu.ntua.cruise.crawlers.services;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import gr.imu.ntua.cruise.crawlers.crawler4j.DaCrawler;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 02/07/13
 * Time: 5:52 PM
 */
public class Crawler4jWepageCrawlingService implements WebpageCrawlingService {

    Properties systemProperties;
    WebIndexer webIndexer;

    @PostConstruct
    public void init() throws Exception {

        String crawlStorageFolder = systemProperties.getProperty("crawl.directory");
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(1000);
        config.setFollowRedirects(true);
        config.setResumableCrawling(true);
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
        controller.addSeed("http://waag.org/nl");

        Map<String,Object> customData = new HashMap<>();
        customData.put("webIndexer",webIndexer);
        customData.put("systemProperties",systemProperties);
        controller.setCustomData(customData);

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(DaCrawler.class, numberOfCrawlers);

    }

    public void setWebIndexer(WebIndexer webIndexer) {
        this.webIndexer = webIndexer;
    }

    public void setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
    }
}
