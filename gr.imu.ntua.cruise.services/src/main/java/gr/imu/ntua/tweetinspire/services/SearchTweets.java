/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.imu.ntua.tweetinspire.services;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class SearchTweets {

    /**
     * Usage: java twitter4j.examples.search.SearchTweets [query]
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("java twitter4j.examples.search.SearchTweets [query]");
            System.exit(-1);
        }

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("MMVLImYRUFwUxTsR5dt8Hg")
                .setOAuthConsumerSecret("RSL2thuYrNfOAvvIIjlCvyYpKs5nbDBWc6BuLHWOs")
                .setOAuthAccessToken("112405759-pN3OMsAe9EJcmPNqIEYIDQsCxHlSQWiBVhUoJjmz")
                .setOAuthAccessTokenSecret("ktHoFrEk7eGetymR9uqJ6rBzEMqw0D8iv6yF0v9u78");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
            //Query query = new Query("space from:thishappenedbrs OR from:thishappenednl OR from:thishappenededi");
            Query query = new Query("space source:thishappenedbrs OR source:thishappenednl OR source:thishappenededi OR source:thhkg");
            //space source:thishappenedbrs OR source:thishappenednl OR source:thishappenededi OR source:thhkg
            query.setCount(20);
            query.setSince("2012-12-01");

            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                  //  System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);
            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
         //   System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }
    }
}