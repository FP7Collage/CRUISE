package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.cruise.db.domain.Bookmarks;
import gr.imu.ntua.cruise.db.domain.Rating;
import gr.imu.ntua.cruise.social.SecurityContext;
import gr.imu.ntua.tweetinspire.services.*;
import gr.imu.ntua.tweetinspire.services.bean.Questionaire;
import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import gr.imu.ntua.tweetinspire.services.db.PersistanceService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 20/08/13
 * Time: 4:31 PM
 */

@Controller
@RequestMapping(("/academic"))
public class AcademicController extends DefaultCruiseController{

    private Logger logger = LoggerFactory.getLogger(AcademicController.class);


    @Autowired
    DiversifyService diversifyService;

    @Autowired
    UserTracking userTracking;

    @Autowired
    DiversificationServiceProvider diversificationServiceProvider;

    @Autowired
    PersistanceService persistanceService;

    @Autowired
    SimpleSearchTermService simpleSearchTermService;
    private List<String> acl;

    @PostConstruct
    public void init(){


        acl =new ArrayList<>();

        try {
            acl = IOUtils.readLines(ExploreController.class.getResourceAsStream("/academic-accounts.txt"));
        } catch (IOException e) {
            logger.warn("Couldn't load customize people resutls");
        }



    }

    @Override
    public String returnPrefix() {
        return "academic";
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(
            @RequestParam(required = false) String terms,
            @RequestParam(required = false) String query
    ){


        final ModelAndView mv = new ModelAndView(getView("index"));

        int step = 1;

        mv.addObject("user",
                SecurityContext.userSignedIn() ?
                SecurityContext.getCurrentUser().getId():
                null);


        List<String> queryList = extract(query);

        List<SearchTerm> list = getResultsForTerms(terms);

        mv.addObject("terms",terms == null ? "" : terms.replace(","," "));
        mv.addObject("query",query ==null ? "" : query);

        if(list.size() > 0){
            step =2;
            mv.addObject("results",list);
        }


        final Map<String,List<?>> enginesResults = new HashMap<>();

        if(list.size() > 0 && queryList.size() >0){

            step =3;

            final String[] filter = queryList.toArray(new String[queryList.size()]);

            DiversifyService scholar = diversificationServiceProvider.getService("scholar");

            Map<String, List<AbstractDiversifyService.Result>> resultsFor= scholar.getResultsFor(filter);
            enginesResults.put("scholar", resultsFor.get("diversified"));

        }


        mv.addObject("engines", enginesResults);
        mv.addObject("questionaire",new Questionaire());
        mv.addObject("step",step);

        return mv;
    }


    @RequestMapping(value = "/{engine}", method = RequestMethod.POST)
    public ModelAndView byEngineName(
            @PathVariable String engine,
            @RequestParam("filter[]") String[] filter
    ){


        ModelAndView mv = new ModelAndView(getPartial("diversify-single"));

        DiversifyService service = diversificationServiceProvider.getService(engine);
        if(service == null){
            throw new RuntimeException("That engine does not exist");
        }

        Map<String, List<AbstractDiversifyService.Result>> resultsFor = service.getResultsFor(filter);

        mv.addObject("searchEngine", service.getSearchEngineName());
        mv.addObject("original", resultsFor.get("original"));
        mv.addObject("diversified", resultsFor.get("diversified"));

        persistanceService.recordQueryForSesssion(userTracking.getId(),StringUtils.join(filter,","),"academic");

        return mv;

    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public ModelAndView explore(
            @RequestParam(required = true) String q
    ){


        ModelAndView mv = new ModelAndView(getPartial("cloud"));
        mv.addObject("terms",q.replace(","," "));
        mv.addObject("results",getResultsForTerms(q));


        persistanceService.recordTermsForSesssion(userTracking.getId(),"q="+q,"academic");

        return mv;

    }



    @RequestMapping(value="bookmark", method = RequestMethod.POST)
    @ResponseBody
    public String bookmark(
            @RequestParam String term,
            @RequestParam String query,
            @RequestParam String url,
            @RequestParam String source,
            HttpServletResponse response
    ){

        boolean success = false;


        logger.trace("Saving bookmark {},{},{},{}",new String[]{term,query,url,source});

        try{

            Bookmarks bookmarks = persistanceService.addBookmark(
                    url,
                    term,
                    query,
                    source
            );

            success=bookmarks !=null;
        }catch (Exception e){
            logger.error("Couldn't save bookmark");
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        return "{\"success\":\""+(success ? "true":"false")+"\"}";
    }


    @RequestMapping(value="rate", method = RequestMethod.POST)
    @ResponseBody
    public String rate(
            @RequestParam String term,
            @RequestParam String query,
            @RequestParam String url,
            @RequestParam String source,
            @RequestParam String title,
            @RequestParam String rating,
            @RequestParam Boolean remove,
            HttpServletResponse response
    ){

        boolean success = false;

        logger.trace("Rating link {},{},{},{},{}",new String[]{term,query,url,source,rating});

        try{

            if(remove){
                success = persistanceService.removeRating(
                        userTracking.getId(),
                        url,
                        source,
                        rating
                );
            }else{
                Rating ratingBean = persistanceService.addRating(
                        userTracking.getId(),
                        url,
                        term,
                        query,
                        source,
                        title,
                        "academic",
                        rating
                );

                success=ratingBean !=null;
            }



        }catch (Exception e){
            logger.error("Couldn't save bookmark");
        }

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        return "{\"success\":\""+(success ? "true":"false")+"\"}";

    }


    private List<SearchTerm> getResultsForTerms(String terms){

        List<String> termsList = extract(terms);
        List<SearchTerm> results = new ArrayList<SearchTerm>();


        if(termsList.size() >0 ){

            try {


                List<SearchTerm> fullResults =  userTracking.forceProcess(
                        StringUtils.join(termsList," "),
                        new String[]{},
                        null,
                        0
                ) ;


                float max=1;
                if(fullResults.size() > 0){

                    max=fullResults.get(0).getFrequency();

                }
                List<SearchTerm> accountTerms=
                        simpleSearchTermService.getSearchTermsForAccounts(max,terms,acl, 0.5);

                Collections.addAll(results,accountTerms.toArray(new SearchTerm[accountTerms.size()]));
                Collections.addAll(results,fullResults.toArray(new SearchTerm[fullResults.size()]));

            } catch (Exception e) {
                logger.warn("Couldn't fetch cloud",e);
            }

        }


        return results;
    }


    private List<String> extract(Object object) {

        if(object == null || object.toString().trim().length() <= 0){
            return new ArrayList<>();
        }

        String[] split = object.toString().split(",");
        return Arrays.asList(split);

    }


}
