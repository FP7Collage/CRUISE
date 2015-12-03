package gr.imu.ntua.tweetinspire.ui.controller;

import gr.imu.ntua.cruise.social.UserMethods;
//import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
 * @author imu-user
 */
@Controller
@RequestMapping("/about")
@SessionAttributes("user")
public class OAuthController {
    
    @RequestMapping(method = RequestMethod.POST, value ="/AuthControl")
    @ResponseBody//@ResponseStatus(HttpStatus.OK)
    public String post( @RequestBody   String user,  HttpServletRequest request, HttpServletResponse response)
    {
        ArrayList<String> usercredentials = new ArrayList<String>();
        usercredentials = parseJson(user);
        UserMethods usermethods = new UserMethods();
        usermethods.setCurrentUser (usercredentials);  
        request.getSession().setAttribute("user", "usercredentials");
        response.setContentType("text/plain");

        response.setCharacterEncoding("UTF-8");
       
        return "{\"success\":\"\"}";
		

    }
     public ArrayList<String> parseJson(String json){

        ArrayList<String> user=new ArrayList<String>();
        ObjectMapper ob = new ObjectMapper();
      /*  try {
            Map map = ob.readValue(json, Map.class);

            Object results = (Map)map.get("user");
            int i=0;
            

            if(results instanceof ArrayList){

                for(Object o: (ArrayList)results){


                   String userId = (String)  ((Map) o).get("id");
                   String password = (String)  ((Map) o).get("psw");
                   
                                    

                    user.add(userId);
                    user.add(password);
                }
            }

        } catch (IOException e) {
           
        }*/
        try {
	            JSONObject rootObject = new JSONObject(json); // Parse the JSON to a JSONObject
	            
	            JSONArray rows =  rootObject.getJSONArray("user") ; // Get all JSONArray rows

	            for(int i=0; i < rows.length(); i++) { // Loop over each each row
	               
	                    JSONObject element =  rows.getJSONObject(i); // Get the element object
	                 
	                    user.add(element.getString("id"));
	                    user.add(element.getString("psw"));    
	          
	         
	            }
	        } catch (JSONException e) {
	            // JSON Parsing error
	            e.printStackTrace();
	        }

       
        return user;

    }

    
}



