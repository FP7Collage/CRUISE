package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.cruise.db.domain.Bookmarks;
import gr.imu.ntua.cruise.social.SecurityContext;
import gr.imu.ntua.cruise.social.UserMethods;
import gr.imu.ntua.tweetinspire.services.AbstractDiversifyService;
import gr.imu.ntua.tweetinspire.services.AllResultsIntegrationService;
import gr.imu.ntua.tweetinspire.services.CrawlDiversifyService;
import gr.imu.ntua.tweetinspire.services.DiversifyService;
import gr.imu.ntua.tweetinspire.services.ImagesService;
import gr.imu.ntua.tweetinspire.services.PersonalSearchTermService;
import gr.imu.ntua.tweetinspire.services.SimpleSearchTermService;
import gr.imu.ntua.tweetinspire.services.YouTubeService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 20/08/13
 * Time: 4:31 PM
 */

@Controller
@RequestMapping(("/explore"))
public class ExploreController extends DefaultCruiseController{

    private Logger logger = LoggerFactory.getLogger(ExploreController.class);

    @Autowired
    UserTracking userTracking;

    @Autowired
    DiversifyService diversifyService;

    @Autowired
    CrawlDiversifyService crawlDiversifyService;

    @Autowired
    SimpleSearchTermService simpleSearchTermService;

    @Autowired
    PersistanceService persistanceService;
    
    @Autowired
    ImagesService imagesService;
     
    @Autowired
    PersonalSearchTermService personalSearchTermService;
    
    @Autowired
    YouTubeService youTubeService;
    
    @Autowired 
    AllResultsIntegrationService allresultsIntegrationService;
    
    
    List<String> acl;
    String low ;
    String medium;
    String deep ;
    String publiconly;
    String personal;
    String bothstreams;

    @PostConstruct
    public void init(){

        acl=new ArrayList<>();
        low = "low";
        medium = "medium" ;
        deep = "deep";
        publiconly = "public";
        personal = "personal";
        bothstreams = "both";        


        try {
            acl = IOUtils.readLines(ExploreController.class.getResourceAsStream("/twitter-people-nl.txt"));
        } catch (IOException e) {
            logger.warn("Couldn't load customize people resutls");
        }



    }

    @Override
    public String returnPrefix() {
        return "explore";
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(
            @RequestParam(required = false) String terms,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String source
    ){


        final ModelAndView mv = new ModelAndView(getView("index"));

        int step = 1;
        
        mv.addObject("user",
                SecurityContext.userSignedIn() ?
                SecurityContext.getCurrentUser().getId():
                null);


        List<String> queryList = extract(query);

        List<SearchTerm> list = getResultsForTerms(terms,level,source);

        mv.addObject("terms",terms == null ? "" : terms.replace(","," "));
        mv.addObject("query",query ==null ? "" : query);
        mv.addObject("level", level);
        mv.addObject("source", source);

        if(list.size() > 0){
            step =2;
            mv.addObject("results",list);
        }


        final Map<String,List<?>> enginesResults = new HashMap<>();

        if(list.size() > 0 && queryList.size() >0){

            step =3;

            final String[] filter = queryList.toArray(new String[queryList.size()]);

            final CountDownLatch cdl = new CountDownLatch(3);



            final Map<String, List<AbstractDiversifyService.Result>> resultsFor = diversifyService.getResultsFor(filter);
            enginesResults.put("bing", resultsFor.get("diversified"));

            Map<String, List<AbstractDiversifyService.Result>> resultsFor1 = crawlDiversifyService.getResultsFor(filter);
            enginesResults.put("waag", resultsFor1.get("diversified"));


            enginesResults.put("flickr", diversifyService.getImagesFor(filter));


        }
        mv.addObject("engines", enginesResults);
        mv.addObject("questionaire",new Questionaire());
        mv.addObject("step",step);


        return mv;
    }

    @RequestMapping(value = "terms", method = RequestMethod.GET)
    public ModelAndView explore(
            @RequestParam(required = true) String q,
            @RequestParam(required = true) String level,
            @RequestParam(required = true) String source
    ){


        ModelAndView mv = new ModelAndView(getPartial("cloud"));

        mv.addObject("terms",q.replace(","," "));
        mv.addObject("results",getResultsForTerms(q, level, source));

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
    

    private List<SearchTerm> getResultsForTerms(String terms, String level, String source){

        List<String> termsList = extract(terms);
        List<SearchTerm> results = new ArrayList<SearchTerm>();
        String userId;
        String userSecret;
     

       /* if(termsList.size() >0 ){

            try {
                List<SearchTerm> full = userTracking.forceProcess(
                        StringUtils.join(termsList," "),
                        new String[]{},
                        false,
                        0
                ) ;

                float cealing = 1.0f;
                if(full.size() > 0 ){
                    cealing= full.get(0).getFrequency();
                }

                List<SearchTerm> users= simpleSearchTermService.getSearchTermsForAccounts(
                        cealing ,
                        terms,
                        acl,
                        0.5

                );                
               
                
                List<SearchTerm> images = imagesService.getImagesResults(cealing, terms);
                
               
               
                if (("medium").equals(level)) {
                    
                     List<SearchTerm> binglinks = diversifyService.getLinksAsTerms(cealing, terms);
                      Collections.addAll(results,binglinks.toArray(new SearchTerm[binglinks.size()]));
                   
                }
                
                if (("deep").equals(level)) {
                    
                      List<SearchTerm> binglinks = diversifyService.getLinksAsTerms(cealing, terms, );
                      Collections.addAll(results,binglinks.toArray(new SearchTerm[binglinks.size()]));  
                      List<SearchTerm> videolinks = youTubeService.getVideosFor(cealing, terms);
                      Collections.addAll(results,videolinks.toArray(new SearchTerm[videolinks.size()]));
                }
                        
                        
                if(source.equals(bothstreams) && UserMethods.getCurrentUser()!=null ){
                    
                    
                     List<SearchTerm> myfollowers = personalSearchTermService.getSearchTermsForAccounts(
                            cealing ,
                            terms,
                            UserMethods.getCurrentUser().get(0),
                            UserMethods.getCurrentUser().get(1) ,
                            level

                    );
                    Collections.addAll(results,images.toArray(new SearchTerm[images.size()]));
                    Collections.addAll(results,full.toArray(new SearchTerm[full.size()]));
                    Collections.addAll(results,myfollowers.toArray(new SearchTerm[myfollowers.size()]));
                }
                
                else if(source.equals(personal) && UserMethods.getCurrentUser()!=null){
                     List<SearchTerm> myfollowers = personalSearchTermService.getSearchTermsForAccounts(
                            cealing ,
                            terms,
                            UserMethods.getCurrentUser().get(0),
                            UserMethods.getCurrentUser().get(1) ,
                            level

                    );
                     Collections.addAll(results,images.toArray(new SearchTerm[images.size()]));                 
                     Collections.addAll(results,myfollowers.toArray(new SearchTerm[myfollowers.size()]));
                }
                
                else {
                     Collections.addAll(results,images.toArray(new SearchTerm[images.size()]));                
                     Collections.addAll(results,full.toArray(new SearchTerm[full.size()]));               
                } 
                
                

            } catch (Exception e) {
                logger.warn("Couldn't fetch cloud",e);
            }

        }*/
       if(termsList.size() >0 ){

            try {

               /* if(UserMethods.getCurrentUser()==null ){ 
                      userId =  "";
                      userSecret = "";
                }
                else
                {
                     userId =   UserMethods.getCurrentUser().get(0);
                     userSecret =   UserMethods.getCurrentUser().get(1);  
                     
                }*/


                  results = allresultsIntegrationService.getAllResults(terms, level, source, UserMethods.getCurrentUser().get(0), UserMethods.getCurrentUser().get(1));

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
     private ArrayList<String> parseJson(String json){

        ArrayList<String> user=new ArrayList<String>();
        ObjectMapper ob = new ObjectMapper();
        try {
            Map map = ob.readValue(json, Map.class);

            Object results = ((Map)map.get("d")).get("user");
            int i=0;
            

            if(results instanceof ArrayList){

                for(Object o: (ArrayList)results){


                   String userId = (String)  ((Map) o).get("Id");
                   String password = (String)  ((Map) o).get("psw");
                   
                                    

                    user.add(userId);
                    user.add(password);
                }
            }

        } catch (IOException e) {
           
        }

        return user;

    }


}

