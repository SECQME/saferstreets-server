package com.secqme.crimedata.rs.provider;

import com.muantech.rollbar.java.RollbarNotifier;
import com.secqme.crimedata.CoreException;
import com.secqme.crimedata.ErrorType;
import com.sun.jersey.api.NotFoundException;
import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edward on 09/04/2015.
 */
@Provider
public class CoreExceptionMapper implements ExceptionMapper<CoreException> {

    private final static Logger myLog = Logger.getLogger(CoreExceptionMapper.class);

    @Override
    public Response toResponse(CoreException e) {
//        myLog.error("ERROR 404 NOT_FOUND", e);

        // CoreException ex = new CoreException(ErrorType.PATH_NOT_FOUND);
        // Map<String,Object> context = new HashMap<String,Object>();
        // context.put("platform","Java");
        // try {
        //     RollbarNotifier.init("https://api.rollbar.com/api/1/item/", "34df2ad2a4ad4dcb96c5bc3df1d16c1f", "local");
        //     throw new NotFoundException("testing rollbar");
        // } catch(Throwable throwable) {
        //     myLog.debug("asdf");
        //     RollbarNotifier.notify(RollbarNotifier.Level.ERROR,"Hello World", context);
        // }

        return Response.status(422)
                .type("application/json")
                .entity(e.getMessageAsJSON().toString())
                .build();
    }
}
