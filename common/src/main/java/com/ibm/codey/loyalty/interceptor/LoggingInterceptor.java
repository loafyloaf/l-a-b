package com.ibm.codey.loyalty.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

/*
 * This interceptor is used with the JAXRS resource classes to log any exception and return a 500 status code to the client.
 * This could have been accomplished with an ExceptionMapper as well but an interceptor lets us also log information about
 * the failing method and input parameters.
 */
public class LoggingInterceptor {

    private static final Logger log = Logger.getLogger(LoggingInterceptor.class.getName());

    @AroundInvoke
    public Object logInvocation(InvocationContext ctx) {
        try {
            Object result = ctx.proceed();
            return result;
        } catch(Throwable e) {
            String clz = ctx.getMethod().getDeclaringClass().getName();
            String method = ctx.getMethod().getName();
            Object[] params = ctx.getParameters();
            if (params != null && params.length > 0) {
                log.log(Level.SEVERE, "***** Exception in " + clz + "." + method, params);
            } else {
                log.log(Level.SEVERE, "***** Exception in " + clz + "." + method);
            }
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
       }
    }


}