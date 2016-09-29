package com.secqme.crimedata.filter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.io.*;

/**
 * Created by edward on 04/05/2015.
 */
public class RollbarFilter implements Filter {

    private final static Logger myLog = Logger.getLogger(RollbarFilter.class);
    private final static String[] NON_SERIALIZED_FIELDS = new String[] {"password", "currentPassword", "newPassword", "passwordResetPin"};

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            MDC.put("request", servletRequest);

            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null) MDC.put("session", session);

            if (MediaType.APPLICATION_JSON.equals(httpServletRequest.getHeader("Content-Type"))) {
                ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) servletRequest);
                servletRequest = wrappedRequest;
                MDC.put("request", wrappedRequest);

                try {
                    JSONObject jsonObject = new JSONObject(new JSONTokener(wrappedRequest.getReader()));

                    for (String field : NON_SERIALIZED_FIELDS) {
                        if (jsonObject.has(field)) {
                            jsonObject.put(field, "***CONFIDENTIAL***");
                        }
                    }

                    MDC.put("json", jsonObject.toString());
                } catch (JSONException ex) {
                    // myLog.debug("Can't parse request to JSONObject", ex);
                } finally {
                    wrappedRequest.resetInputStream();
                }
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            myLog.error(ex.getMessage(), ex);
        } finally {
            MDC.remove("json");
            MDC.remove("request");
            MDC.remove("session");
            MDC.remove("user");
        }
    }

    @Override
    public void destroy() {
    }

    private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }


        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }


        private class ResettableServletInputStream extends ServletInputStream {

            private InputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }
        }
    }
}
