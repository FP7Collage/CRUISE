package gr.imu.ntua.cruise.crawlers.crawler4j;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import gr.imu.ntua.cruise.crawlers.services.WebIndexer;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 02/07/13
 * Time: 5:52 PM
 *
 * https://code.google.com/p/crawler4j/
 *
*/
public class DaCrawler extends WebCrawler {

    private Logger logger = LoggerFactory.getLogger(DaCrawler.class);
    private WebIndexer webIndexer;

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
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
        return !FILTERS.matcher(href).matches() && href.startsWith("http://waag.org/en");
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
            try {
                TagNode tagNode = new HtmlCleaner().clean(htmlParseData.getHtml());
                CleanerProperties props = new CleanerProperties();
                props.setNamespacesAware(false);
                org.w3c.dom.Document doc = new DomSerializer(props).createDOM(tagNode);
                XPath xPath  = XPathFactory.newInstance().newXPath();
                String content = xPath.evaluate(" //*[@id=\"content\"]/div", doc);

//                String boilerplateText = DefaultExtractor.INSTANCE.getText(content);
//                logger.trace("void visit([page]) \n***************************************\n{}\n" +
//                        "***************************************\n{}\n***************************************", content,boilerplateText);


                webIndexer.addToIndex(
                        page.getWebURL().getDomain(),
                        url,
                        content
                );

            } catch (Exception e) {
                logger.warn("Couldn't extract text from {}\n{}\n\n",html,e);
            }


            logger.trace("void visit([page]) \n\n\n");
        }
    }


    public void setWebIndexer(WebIndexer webIndexer) {
        this.webIndexer = webIndexer;
    }
}
