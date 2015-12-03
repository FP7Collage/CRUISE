package gr.imu.ntua.cruise.personalization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: fotis
 * Date: 18/07/13
 * Time: 11:54 AM
 */
@Configuration
public class TwitterConfig implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(TwitterConfig.class);

    private static final String consumerKey = "MMVLImYRUFwUxTsR5dt8Hg";
    private static final String consumerSecret = "RSL2thuYrNfOAvvIIjlCvyYpKs5nbDBWc6BuLHWOs";


    private SocialContext socialContext;

    private UsersConnectionRepository usersConnectionRepositiory;

    @Inject
    private javax.sql.DataSource dataSource;

    /**
     * Point to note: the name of the bean is either the name of the method
     * "socialContext" or can be set by an attribute
     *
     * @Bean(name="myBean")
     */
    @Bean
    public SocialContext socialContext() {
        return socialContext;
    }

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {

        logger.info("getting connectionFactoryLocator");
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();
        registry.addConnectionFactory(new TwitterConnectionFactory(consumerKey, consumerSecret));
        return registry;
    }

    /**
     * Singleton data access object providing access to connections across all
     * users.
     */
    @Bean
    public UsersConnectionRepository usersConnectionRepository() {

        return usersConnectionRepositiory;
    }

    /**
     * Request-scoped data access object providing access to the current user's
     * connections.
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository() {
        String userId = socialContext.getUserId();
        logger.info("Createung ConnectionRepository for user: " + userId);
        return usersConnectionRepository().createConnectionRepository(userId);
    }

    /**
     * A proxy to a request-scoped object representing the current user's
     * primary Facebook account.
     *
     * @throws NotConnectedException
     *             if the user is not connected to twitter.
     */
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public Twitter twitter() {
        return connectionRepository().getPrimaryConnection(Twitter.class).getApi();
    }

    /**
     * Create the ProviderSignInController that handles the OAuth2 stuff and
     * tell it to redirect back to /posts once sign in has completed
     */
    @Bean
    public ProviderSignInController providerSignInController() {
        ProviderSignInController providerSigninController = new ProviderSignInController(connectionFactoryLocator(),
                usersConnectionRepository(), socialContext);
        providerSigninController.setPostSignInUrl("/posts");
        return providerSigninController;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        JdbcUsersConnectionRepository usersConnectionRepositiory = new JdbcUsersConnectionRepository(dataSource,
                connectionFactoryLocator(), Encryptors.noOpText());

        socialContext = new SocialContext(usersConnectionRepositiory, new UserCookieGenerator(), twitter());

        usersConnectionRepositiory.setConnectionSignUp(socialContext);
        this.usersConnectionRepositiory = usersConnectionRepositiory;
    }


}
