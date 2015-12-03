/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import gr.imu.ntua.tweetinspire.services.bean.Tweet;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import twitter4j.TwitterException;

/**
 *
 * @author imu-user
 */
public interface ImagesService {
    
    List<SearchTerm> getImagesResults(float maxtermfrequency, String  terms, int resultsSize);
  
    
}
