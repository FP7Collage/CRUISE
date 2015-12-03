package gr.imu.ntua.tweetinspire.services.bean;

import twitter4j.Status;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 15/06/13
 * Time: 5:19 PM
 */
public class Tweet {
    private long id;
    private Status tweet;

    public Tweet(long id, Status tweet) {
        this.id = id;
        this.tweet = tweet;
    }

    public long getId() {
        return id;
    }

    public Status getTweet() {
        return tweet;
    }

    public String getText() {
        return tweet.getText();
    }

    public String getFromUser() {
        return tweet.getUser().getName();
    }
}
