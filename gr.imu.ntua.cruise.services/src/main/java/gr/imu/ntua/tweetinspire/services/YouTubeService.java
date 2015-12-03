/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;
import java.util.List;

/**
 *
 * @author maria
 */
public interface YouTubeService {
    
    public List<SearchTerm> getVideosFor(float maxtermfreq,String terms, int resultsSize);
    
}
