package com.secqme.crimedata.rs.provider;


import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.ErrorType;
import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by edward on 09/04/2015.
 */
@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

    private final static Logger myLog = Logger.getLogger(ThrowableExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        myLog.error("ERROR 500 INTERNAL_SERVER_ERROR", e);

        CoreException ex = new CoreException(ErrorType.INTERNAL_SERVER_ERROR);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type("application/json")
                .entity(ex.getMessageAsJSON().toString())
                .build();
    }
}
