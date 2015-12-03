
package gr.imu.ntua.tweetinspire.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/25/12
 * Time: 10:31 PM
 */
@Controller
@RequestMapping("/")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value="/",method = RequestMethod.GET)
    public String index(){
        return "redirect:/explore";
    }

    @RequestMapping(value="/about",method = RequestMethod.GET)
    public String about(){
        return "about";
    }
    @RequestMapping(value="/twitterSignIn",method = RequestMethod.GET)
    public String twitterSignIn(){
        return "twitterSignIn";
    }
}
