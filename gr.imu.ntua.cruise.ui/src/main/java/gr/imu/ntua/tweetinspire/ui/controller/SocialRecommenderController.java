/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.cruise.social.UserMethods;
import gr.imu.ntua.tweetinspire.services.SocialRecommenderService;
import gr.imu.ntua.tweetinspire.services.bean.UserTracking;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author maria
 */

@Controller
@RequestMapping(("/recommend"))
public class SocialRecommenderController {
    private Logger logger = LoggerFactory.getLogger(ExploreController.class);

    @Autowired
    SocialRecommenderService socialRecommenderService;
    
    @RequestMapping(method = RequestMethod.POST, value = "/tweets" )
    public ModelAndView socialrecommender(
             @RequestBody   String terms,  HttpServletRequest request, HttpServletResponse response
    ) {

        String  query ="";
        query = parseJson(terms);
        ModelAndView mv = new ModelAndView("partials/recommendations");

        mv.addObject("recommendedinfo", socialRecommenderService.getRecommendationsFor(query,UserMethods.getCurrentUser().get(0), UserMethods.getCurrentUser().get(1)));

        return mv;

    }
    
     public String parseJson(String json){

       String terms="";

        try {
	            JSONObject rootObject = new JSONObject(json); // Parse the JSON to a JSONObject
	            
	            JSONArray rows =  rootObject.getJSONArray("terms") ; // Get all JSONArray rows

	            for(int i=0; i < rows.length(); i++) { // Loop over each each row
	               
	                    JSONObject element =  rows.getJSONObject(i); // Get the element object
	                 
	                    terms=element.getString("query");             
	          
	         
	            }
	        } catch (JSONException e) {
	            // JSON Parsing error
	            e.printStackTrace();
	        }

       
        return terms;

    }

    
}
