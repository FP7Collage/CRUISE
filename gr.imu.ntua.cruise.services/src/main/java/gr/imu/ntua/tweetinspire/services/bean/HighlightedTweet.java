package gr.imu.ntua.tweetinspire.services.bean;


/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 9/26/12
 * Time: 7:35 PM
 */
public class HighlightedTweet {

    private String user;
    private String highlighted;

    public HighlightedTweet(String user, String highlighted) {

        this.user= user;
        this.highlighted = highlighted;
    }

    public String getUser() {
        return user;
    }

    public String getHighlighted() {
        return highlighted;
    }
}
