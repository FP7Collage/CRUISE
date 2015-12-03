package gr.imu.ntua.tweetinspire.services;

import gr.imu.ntua.tweetinspire.services.bean.SearchTerm;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 17/09/13
 * Time: 11:12 AM
 */
public interface  SimpleSearchTermService {

    List<SearchTerm> getSearchTermsForAccounts(float max, String terms, List<String> acl, double v);
}
