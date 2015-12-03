package gr.imu.ntua.cruise.personalization;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 18/07/13
 * Time: 11:50 AM
 */
public class SocialContext  implements ConnectionSignUp, SignInAdapter {

    /**
     * Use a random number generator to generate IDs to avoid cookie clashes
     * between server restarts
     */
    private static Random rand;

    /**
     * Manage cookies - Use cookies to remember state between calls to the
     * server(s)
     */
    private final UserCookieGenerator userCookieGenerator;

    /** Store the user id between calls to the server */
    private static final ThreadLocal<String> currentUser = new ThreadLocal<String>();

    private final UsersConnectionRepository connectionRepository;

    private final Twitter twitter;

    public SocialContext(UsersConnectionRepository connectionRepository, UserCookieGenerator userCookieGenerator, Twitter twitter) {
        this.connectionRepository = connectionRepository;
        this.userCookieGenerator = userCookieGenerator;
        this.twitter = twitter;

        rand = new Random(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public String signIn(String userId, Connection<?> connection, NativeWebRequest request) {
        userCookieGenerator.addCookie(userId, request.getNativeResponse(HttpServletResponse.class));
        return null;
    }

    @Override
    public String execute(Connection<?> connection) {
        return Long.toString(rand.nextLong());
    }

    public boolean isSignedIn(HttpServletRequest request, HttpServletResponse response) {

        boolean retVal = false;
        String userId = userCookieGenerator.readCookieValue(request);
        if (isValidId(userId)) {

            if (isConnectedFacebookUser(userId)) {
                retVal = true;
            } else {
                userCookieGenerator.removeCookie(response);
            }
        }

        currentUser.set(userId);
        return retVal;
    }

    private boolean isValidId(String id) {
        return isNotNull(id) && (id.length() > 0);
    }

    private boolean isNotNull(Object obj) {
        return obj != null;
    }

    private boolean isConnectedFacebookUser(String userId) {

        ConnectionRepository connectionRepo = connectionRepository.createConnectionRepository(userId);
        Connection<Twitter> facebookConnection = connectionRepo.findPrimaryConnection(Twitter.class);
        return facebookConnection != null;
    }

    public String getUserId() {

        return currentUser.get();
    }

    public Twitter getTwitter() {
        return twitter;
    }

}
