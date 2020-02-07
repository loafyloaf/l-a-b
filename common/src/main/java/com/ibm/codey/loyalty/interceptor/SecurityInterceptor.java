package com.ibm.codey.loyalty.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.Claim;

import com.ibm.codey.loyalty.interceptor.binding.RequiresAuthorization;

/*
 * This interceptor is used with the JAXRS resource classes to enforce a client scope for authorization purposes.
 */
@RequiresAuthorization @Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class SecurityInterceptor {

    private static final Logger log = Logger.getLogger(LoggingInterceptor.class.getName());

    // @Inject
    // @Claim("scope")
    // private String scope;

    @AroundInvoke
    public Object checkScope(InvocationContext ctx) throws Exception {
        // Allow non-admin through for now.  Uncomment following code when security is enabled.
        // String[] scopeList = scope.split(" ");
        // for(String hasScope : scopeList) {
        //     if (hasScope.equals("admin")) {
        //         Object result = ctx.proceed();
        //         return result;
        //     }
        // }
        // return Response.status(Response.Status.FORBIDDEN).entity("admin permission required").build();
        Object result = ctx.proceed();
        return result;
    }


}