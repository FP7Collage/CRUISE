package gr.imu.ntua.cruise.crawlers.crawler4j;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import gr.imu.ntua.cruise.crawlers.services.WebIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractTwitterLinksCrawler extends WebCrawler {

        private Logger logger = LoggerFactory.getLogger(ExtractTwitterLinksCrawler.class);
        private WebIndexer webIndexer;

        private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
                + "|png|tiff?|mid|mp2|mp3|mp4"
                + "|wav|avi|mov|mpeg|ram|m4v|pdf"
                + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");


        private Map<String,Object> customData;

        @Override
        public void onStart() {
            customData = (Map<String,Object>) myController.getCustomData();
            webIndexer = (WebIndexer) customData.get("webIndexer");
        }



        /**
         * You should implement this function to specify whether
         * the given url should be crawled or not (based on your
         * crawling logic).
         */
        @Override
        public boolean shouldVisit(WebURL url) {

            String href = url.getURL().toLowerCase();

            return !FILTERS.matcher(href).matches() && href.startsWith((String) customData.get("url"));
        }

        /**
         * This function is called when a page is fetched and ready
         * to be processed by your program.
         */
        @Override
        public void visit(Page page) {
            String url = page.getWebURL().getURL();
            System.out.println("URL: " + url);

            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                String html = htmlParseData.getHtml();
                String text = ((HtmlParseData) page.getParseData()).getText();
                Pattern twitterUsers = Pattern.compile("(@[A-Za-z0-9_]{1,15})");


                Matcher matcher = twitterUsers.matcher(text);
                while(matcher.find()){
                    logger.trace("{}",matcher.group(1));

                }

                Pattern hashes = Pattern.compile("(#\\w+)");

                matcher = hashes.matcher(text);
                while(matcher.find()){
                    logger.trace("{}",matcher.group(1));

                }
                logger.trace("void visit([page]) \n\n\n");
            }
        }


        public void setWebIndexer(WebIndexer webIndexer) {
            this.webIndexer = webIndexer;
        }
    }