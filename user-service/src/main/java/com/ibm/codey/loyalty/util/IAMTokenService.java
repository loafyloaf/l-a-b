package com.ibm.codey.loyalty.util;

import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Dependent
@RegisterRestClient
public interface IAMTokenService {

    public final static String GRANT_TYPE_APIKEY = "urn:ibm:params:oauth:grant-type:apikey";

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    public IAMTokenServiceResponse getIAMTokenFromAPIKey(
      @FormParam("grant_type") String grantType,
      @FormParam("apikey") String apiKey
    );

}