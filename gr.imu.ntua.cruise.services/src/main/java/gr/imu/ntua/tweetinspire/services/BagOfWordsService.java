/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 *
 * @author maria
 */
public interface BagOfWordsService {
    
     public LinkedHashMap<String,Integer> getUserProfile(Twitter twitter, Long from_user,String exploreterms)  throws TwitterException;
     public List<String> getTweets(Long twitteruser);
     public List<String> getTermsFromTweets (List<String> tweets);
}