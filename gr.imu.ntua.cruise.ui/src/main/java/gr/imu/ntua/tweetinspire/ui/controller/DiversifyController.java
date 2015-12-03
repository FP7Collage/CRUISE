package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.tweetinspire.services.AbstractDiversifyService;
import gr.imu.ntua.tweetinspire.services.CrawlDiversifyService;
import gr.imu.ntua.tweetinspire.services.DiversificationServiceProvider;
import gr.imu.ntua.tweetinspire.services.DiversifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 17/04/13
 * Time: 2:12 PM
 */
@Controller
@RequestMapping(("/diversify"))
public class DiversifyController {

    @Autowired
    DiversifyService diversifyService;

    @Autowired
    CrawlDiversifyService crawlDiversifyService;


    @Autowired
    DiversificationServiceProvider diversificationServiceProvider;

    @RequestMapping(value = "/bing", method = RequestMethod.POST)
    public ModelAndView realTime(
            HttpSession session,
            @RequestParam("filter[]") String[] filter
    ) {

        ModelAndView mv = new ModelAndView("partials/diversify-single");

        Map<String, List<AbstractDiversifyService.Result>> resultsFor = diversifyService.getResultsFor(filter);

        mv.addObject("searchEngine", diversifyService.getSearchEngineName());
        mv.addObject("original", resultsFor.get("original"));
        mv.addObject("diversified", resultsFor.get("diversified"));

        return mv;

    }

    @RequestMapping(value = "/{engine}", method = RequestMethod.POST)
    public ModelAndView byEngineName(
            @PathVariable String engine,
            @RequestParam("filter[]") String[] filter
    ){



        ModelAndView mv = new ModelAndView("partials/diversify-single");


        if("flickr".equals(engine)){
            return flickr(filter);
        }


        DiversifyService service = diversificationServiceProvider.getService(engine);
        if(service == null){
            throw new RuntimeException("That engine does not exist");
        }

        Map<String, List<AbstractDiversifyService.Result>> resultsFor = service.getResultsFor(filter);

        mv.addObject("searchEngine", service.getSearchEngineName());
        mv.addObject("original", resultsFor.get("original"));
        mv.addObject("diversified", resultsFor.get("diversified"));

        return mv;

    }

    @RequestMapping(value = "/flickr", method = RequestMethod.POST)
    public ModelAndView flickr(
            @RequestParam("filter[]") String[] filter
    ) {

        ModelAndView mv = new ModelAndView("partials/flickr");

        mv.addObject("images", diversifyService.getImagesFor(filter));

        return mv;

    }

    @RequestMapping(value = "/waag", method = RequestMethod.POST)
    public ModelAndView waag(
            HttpSession session,
            @RequestParam("filter[]") String[] filter
    ) {

        ModelAndView mv = new ModelAndView("partials/diversify-single");
        Map<String, List<AbstractDiversifyService.Result>> resultsFor = crawlDiversifyService.getResultsFor(filter);
        mv.addObject("searchEngine", crawlDiversifyService.getSearchEngineName());
        mv.addObject("original", resultsFor.get("original"));
        mv.addObject("diversified", resultsFor.get("diversified"));
        return mv;

    }


}
