package gr.imu.ntua.cruise.crawlers.run;

import gr.imu.ntua.cruise.crawlers.services.Crawler4jWepageCrawlingService;
import gr.imu.ntua.cruise.crawlers.services.LuceneWebIndexer;
import gr.imu.ntua.cruise.crawlers.services.WebIndexer;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 02/07/13
 * Time: 6:01 PM
 */
public class Crawl {

    public static void main(String[] args) throws Exception {

        Properties systemProperties = new Properties();
        systemProperties.load(Crawl.class.getResourceAsStream("/system.properties"));

        final LuceneWebIndexer luceneWebIndexer = new LuceneWebIndexer();
        luceneWebIndexer.setSystemProperties(systemProperties);
        luceneWebIndexer.init();

        Crawler4jWepageCrawlingService service = new Crawler4jWepageCrawlingService();
        service.setSystemProperties(systemProperties);
        service.setWebIndexer(luceneWebIndexer);

        service.init();

        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                System.out.println("Cleaning up ....");
                luceneWebIndexer.cleanup();
            }
        });

    }
}
