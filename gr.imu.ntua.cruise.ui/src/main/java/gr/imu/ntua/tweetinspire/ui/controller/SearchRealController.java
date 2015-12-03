package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.tweetinspire.services.CrawlDiversifyService;
import gr.imu.ntua.tweetinspire.services.CrawlSearchService;
import gr.imu.ntua.tweetinspire.services.DiversifyService;
import gr.imu.ntua.tweetinspire.services.bean.Questionaire;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 26/02/13
 * Time: 12:52 PM
 */

@Controller
@RequestMapping(("/searchreal"))
public class SearchRealController {

    private Logger logger = LoggerFactory.getLogger(SearchRealController.class);

    @Autowired
    UserTracking userTracking;


//    @Autowired
//    CrawlSearchService crawlSearchService;

    @Autowired
    Properties systemProperties;
    

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView realTimeIndex(HttpSession session){

        logger.trace("String realTimeIndex([session]) {}",session.getId());
        ModelAndView mv = new ModelAndView("realsearch");
        mv.addObject("questionaire",new Questionaire());
        return mv;
    }

    @RequestMapping(value="/realQuery",method = RequestMethod.POST)
    public ModelAndView realTime(
            @RequestParam("filter[]") String[] filter,
            @RequestParam("threshold") Integer threshold,
            @RequestParam("enableWaag") Boolean enableWaag
    ){


        userTracking.setIgnore(filter);

        ModelAndView mv = new ModelAndView("partials/real");
        
   

        List<SearchTerm> results = new ArrayList<>();

        try {

            results.addAll(

            userTracking.search(
                    userTracking.getTerms(),
                    Arrays.asList(filter),
                    enableWaag,
                    threshold));
        } catch (Exception e) {
            logger.warn("Couldn't fetch results from twitter");
        }



//        if(enableWaag){
//            try{
//                results.addAll(
//                    crawlSearchService.search(
//                        userTracking.getTerms(),
//                        Arrays.asList(filter),
//                        threshold));
//
//            } catch (Exception e) {
//                logger.warn("Cloudn't fetch results from crawl");
//            }
//        }


        mv.addObject("results",results);

        return mv;

    }


    @RequestMapping(value="/explore",method = RequestMethod.POST)
    public ModelAndView explore(
            @RequestParam("terms") String terms,
            @RequestParam("filter[]") String[] filter,
            @RequestParam("threshold") Integer threshold,
            @RequestParam("enableWaag") Boolean enableWaag
    ){


        List<SearchTerm> results = new ArrayList<SearchTerm>();
        try {


            results.addAll(userTracking.forceProcess(terms, filter, enableWaag, threshold));

        } catch (Exception e) {
            logger.warn("Couldn't force search ",e);
        }


//        if(enableWaag){
//            try{
//                results.addAll(
//                        crawlSearchService.search(
//                                userTracking.getTerms(),
//                                Arrays.asList(filter),
//                                threshold));
//
//            } catch (Exception e) {
//                logger.warn("Cloudn't fetch results from crawl",e);
//            }
//
//        }

        ModelAndView mv = new ModelAndView("partials/real");

        mv.addObject("results",results);

        return mv;

    }




    @RequestMapping(value="/form/inspired", method = RequestMethod.POST)
    @ResponseBody
    public String formInspired(@ModelAttribute("questionaire") Questionaire questionaire,HttpServletResponse response){
        logger.trace("ModelAndView formInspired([questionaire]) {}",questionaire);
        questionaire.setInspired(true);
        userTracking.setQuestionaire(questionaire);
        userTracking.reset();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        return "{'success':'ok'}";
//        userTracking.reset();
    }

    @RequestMapping(value="/form/notinspired", method = RequestMethod.POST)
    @ResponseBody

    public String formNotInspired(@ModelAttribute("questionaire") Questionaire questionaire,HttpServletResponse response){

        logger.trace("ModelAndView formInspired([questionaire]) {}",questionaire);
        questionaire.setInspired(false);
        userTracking.setQuestionaire(questionaire);
        userTracking.reset();
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

//        userTracking.reset();
        return "{'success':'ok'}";
    }


}
