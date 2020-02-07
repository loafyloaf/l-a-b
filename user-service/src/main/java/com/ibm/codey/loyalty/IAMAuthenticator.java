package com.ibm.codey.loyalty;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import com.ibm.codey.loyalty.util.IAMTokenService;
import com.ibm.codey.loyalty.util.IAMTokenServiceResponse;

@Singleton
public class IAMAuthenticator {

    private static final Logger log = Logger.getLogger(IAMAuthenticator.class.getName());
    
    private static final int EXPIRATION_BUFFER_SECS = 5*60;

    @Inject
    @ConfigProperty(name = "IAM_SERVICE_URL")
    private URL iamServiceURL;

    @Inject
    @ConfigProperty(name="IAM_APIKEY")
    String IAM_APIKEY;

    private String iamToken;
    private long expiration;

    /**
     * Get IAM access token.  Reuse it until it is about to expire.
     */
    public String getIamAccessToken() {
        if (iamToken != null && ((expiration - EXPIRATION_BUFFER_SECS) > (System.currentTimeMillis()/1000))) {
            return iamToken;
        }
        log.log(Level.INFO, "Obtaining IAM access token");
        IAMTokenService iamTokenService = RestClientBuilder.newBuilder().baseUrl(iamServiceURL).build(IAMTokenService.class);
        IAMTokenServiceResponse tokenResponse = iamTokenService.getIAMTokenFromAPIKey(IAMTokenService.GRANT_TYPE_APIKEY, IAM_APIKEY);
        iamToken = tokenResponse.getAccessToken();
        expiration = tokenResponse.getExpiration();
        return iamToken;
    }
}