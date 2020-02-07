package com.ibm.codey.loyalty.accounts.deletion;

import java.net.URL;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.Timeout;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import com.ibm.codey.loyalty.IAMAuthenticator;
import com.ibm.codey.loyalty.accounts.dao.UserDao;
import com.ibm.codey.loyalty.accounts.models.User;
import com.ibm.codey.loyalty.util.AppIDService;
import com.ibm.codey.loyalty.util.AppIDServiceGetUserProfileResponse;

@Stateless
public class AccountDeletionProcessor {

    private static final Logger log = Logger.getLogger(AccountDeletionProcessor.class.getName());

    private static final String PROVIDER = "cloud_directory";
    private static final int TIMER_DURATION_MS = 5*60*1000;

    @Inject
    private UserDao userDAO;

    @Inject
    private IAMAuthenticator iamAuthenticator;

    @Inject
    @ConfigProperty(name = "APPID_SERVICE_URL")
    private URL appIdServiceURL;

    @Inject
    @ConfigProperty(name="APPID_TENANTID")
    String appIdTenantId;

    @Resource TimerService timerService;

    private Queue<String> retryQueue = new ConcurrentLinkedQueue<String>();

    /**
     * This method deletes the user's App ID profile and cloud directory id.
     * This method removes the subject from the user row.  This breaks the association of the data to an individual.
     * Anonymous user data remains for reporting purposes.
     */
    @Asynchronous
    public void delete(String subject, boolean isRetry) {
        try {
            log.log(Level.INFO, "Deleting user");
            User prevUser = userDAO.findUserByRegistryId(subject);
            if (prevUser == null) {
                log.log(Level.INFO, "User not found");
                return;
            }
            userDAO.lockUser(prevUser);
            // Get an IAM token for authentication to App ID API.
            String iamToken = iamAuthenticator.getIamAccessToken();
            String authHeader = "Bearer " + iamToken;
            // Get the user's profile from App ID.
            AppIDService appIdService = RestClientBuilder.newBuilder().baseUrl(appIdServiceURL).build(AppIDService.class);
            AppIDServiceGetUserProfileResponse profileResponse = appIdService.getProfile(authHeader, appIdTenantId, subject);
            // Get the user's Cloud Directory ID from the profile.
            AppIDServiceGetUserProfileResponse.Identity[] identities = profileResponse.getIdentities();
            if (identities != null && identities.length == 1 && identities[0].getProvider().equals(PROVIDER)) {
                String cloudDirectoryId = identities[0].getId();
                // Remove the user.  This deletes the user's cloud directory ID and the user's profile.
                appIdService.removeUser(authHeader, appIdTenantId, cloudDirectoryId);
                prevUser.setSubject(null);
                userDAO.updateUser(prevUser);
                log.log(Level.INFO, "Delete successful");
            } else {
                log.log(Level.SEVERE, "Cannot delete user.  Unexpected identities from App ID: " + Arrays.toString(identities));
            };
        } catch(Throwable t) {
            log.log(Level.SEVERE, "***** Exception in AccountDeletionProcessor", t);
            t.printStackTrace();
            if (!isRetry) {
                addRetry(subject);
            }
        }
    }

    public void addRetry(String subject) {
        retryQueue.add(subject);
        timerService.createSingleActionTimer(TIMER_DURATION_MS, new TimerConfig(null, false));
    }

    @Timeout
    public void doRetry() {
        try {
            String subject = retryQueue.remove();
            delete(subject, true);
        } catch(NoSuchElementException nse) {
        } catch(Throwable t) {
            log.log(Level.SEVERE, "***** Exception in AccountDeletionProcessor", t);
            t.printStackTrace();
        }
    }
}