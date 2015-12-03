package gr.imu.ntua.tweetinspire.services;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 10/8/12
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class XmlServiceTest {

    @Test
    public void textExtract() throws IOException {

        String s = IOUtils.toString(XmlServiceTest.class.getResourceAsStream("/skills.xml"));
        List<String> strings = new XmlService().extractSkillsFromString(s);

        Assert.assertNotNull(strings);
        Assert.assertEquals(41,strings.size(),0);


    }
}
