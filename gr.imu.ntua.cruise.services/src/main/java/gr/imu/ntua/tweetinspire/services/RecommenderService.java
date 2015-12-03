/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gr.imu.ntua.tweetinspire.services;

import java.util.List;

/**
 *
 * @author maria
 */
public interface RecommenderService {
    public List</*SearchTerm*/String> getSearchTermsForAccounts(/*float max, */String terms, String token, String tokensecret/*, String level, int resultsSize*/) ;
    
}
