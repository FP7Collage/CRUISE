package gr.imu.ntua.tweetinspire.services;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 10/8/12
 * Time: 12:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class XmlService {

    private Logger logger = LoggerFactory.getLogger(XmlService.class);

    public List<String> extractSkillsFromString(String xml){

        List<String> ret = new ArrayList<String>();

        try {

            XPath xPath  = XPathFactory.newInstance().newXPath();
            Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    IOUtils.toInputStream(xml)
            );
            XPathExpression pathExpr = xPath.compile("//skill/name/text()");
            NodeList nodeList= (NodeList) pathExpr.evaluate(parse, XPathConstants.NODESET);

            for(int i=0; i< nodeList.getLength();i++){
                Node item = nodeList.item(i);
                ret.add(item.getTextContent());
            }





        } catch (XPathExpressionException e) {
            logger.warn("Couldn't get the skills",e);
        } catch (SAXException e) {
            logger.warn("Couldn't get the skills", e);
        } catch (ParserConfigurationException e) {
            logger.warn("Couldn't get the skills", e);
        } catch (IOException e) {
            logger.warn("Couldn't get the skills", e);
        } finally {
        }



        return ret;

    }
}
