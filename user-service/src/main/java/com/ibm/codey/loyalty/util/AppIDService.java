package com.ibm.codey.loyalty.util;

import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Dependent
@RegisterRestClient
public interface AppIDService {

    @GET
    @Path("/management/v4/{tenantId}/users/{userId}/profile")
    @Produces({MediaType.APPLICATION_JSON})
    public AppIDServiceGetUserProfileResponse getProfile(
      @HeaderParam("Authorization") String authorizationHeader,
      @PathParam("tenantId") String tenantId,
      @PathParam("userId") String userId
    );

    @DELETE
    @Path("/management/v4/{tenantId}/cloud_directory/remove/{userId}")
    public void removeUser(
      @HeaderParam("Authorization") String authorizationHeader,
      @PathParam("tenantId") String tenantId,
      @PathParam("userId") String userId
    );

}