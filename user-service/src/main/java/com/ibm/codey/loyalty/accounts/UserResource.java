package com.ibm.codey.loyalty.accounts;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ibm.codey.loyalty.BaseResource;
import com.ibm.codey.loyalty.accounts.dao.UserDao;
import com.ibm.codey.loyalty.accounts.deletion.AccountDeletionProcessor;
import com.ibm.codey.loyalty.accounts.json.UserRegistration;
import com.ibm.codey.loyalty.accounts.models.User;
import com.ibm.codey.loyalty.interceptor.LoggingInterceptor;

@RequestScoped
@Interceptors(LoggingInterceptor.class)
@Path("v1/users")
public class UserResource extends BaseResource {

    @Inject
    private UserDao userDAO;

    @Inject
    private AccountDeletionProcessor accountDeletionProcessor;

    /**
     * This method creates a new user.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response registerUser(UserRegistration userRegistration) {
        String subject = this.getCallerSubject();
        if (subject == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing subject").build();
        }
        if (userDAO.findUserByRegistryId(subject) != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User is already registered").build();
        }
        User newUser = new User();
        newUser.setSubject(subject);
        newUser.setConsentGiven(userRegistration.isConsentGiven());
        userDAO.createUser(newUser);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * This method returns the user registration data for a user.
     */
    @GET
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUser() {
        String subject = this.getCallerSubject();
        if (subject == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing subject").build();
        }
        User prevUser = userDAO.findUserByRegistryId(subject);
        if (prevUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User is not registered").build();
        } 
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setConsentGiven(prevUser.isConsentGiven());
        return Response.status(Response.Status.OK).entity(userRegistration).build();
    }

    /**
     * This method updates the user registration data for a user.
     */
    @PUT
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateUser(UserRegistration userRegistration) {
        String subject = this.getCallerSubject();
        if (subject == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing subject").build();
        }
        User prevUser = userDAO.findUserByRegistryId(subject);
        if (prevUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User is not registered").build();
        }
        if (prevUser.isDeleteRequested()) {
            return Response.status(Response.Status.CONFLICT).entity("User has requested deletion").build();
        }
        prevUser.setConsentGiven(userRegistration.isConsentGiven());
        userDAO.updateUser(prevUser);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    /**
     * This method schedules an asynchronous process to remove the user from the system.
     */
    @DELETE
    @Path("self")
    @Transactional
    public Response deleteUser() {
        String subject = this.getCallerSubject();
        if (subject == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing subject").build();
        }
        User prevUser = userDAO.findUserByRegistryId(subject);
        if (prevUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User is not registered").build();
        }
        prevUser.setDeleteRequested(true);
        prevUser.setConsentGiven(false);
        userDAO.updateUser(prevUser);
        try {
            accountDeletionProcessor.delete(subject, false);
        } catch (EJBException ejbex) {  // cannot schedule asynchronous method right now (pool full)
            accountDeletionProcessor.addRetry(subject);
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}