/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jmeter.protocol.http.sampler;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.jmeter.protocol.http.control.AuthManager;
import org.apache.jmeter.protocol.http.control.Authorization;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.util.HTTPConstants;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;

import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.SSLManager;

import org.apache.jorphan.logging.LoggingManager;

import org.apache.log.Logger;

/**
 * A sampler which understands all the parts necessary to read statistics about
 * HTTP requests, including cookies and authentication.
 *
 */
public class HTTPJavaImpl extends HTTPAbstractImpl {
    private static final boolean OBEY_CONTENT_LENGTH =
        JMeterUtils.getPropDefault("httpsampler.obey_contentlength", false); // $NON-NLS-1$

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final int MAX_CONN_RETRIES =
        JMeterUtils.getPropDefault("http.java.sampler.retries" // $NON-NLS-1$
                ,10); // Maximum connection retries

    static {
        log.info("Maximum connection retries = "+MAX_CONN_RETRIES); // $NON-NLS-1$
        // Temporary copies, so can set the final ones
    }

    private static final byte[] NULL_BA = new byte[0];// can share these

    /** Handles writing of a post or put request */
    private transient PostWriter postOrPutWriter;

    private volatile HttpURLConnection savedConn;

    protected HTTPJavaImpl(HTTPSamplerBase base) {
        super(base);
    }

    /**
     * Set request headers in preparation to opening a connection.
     *
     * @param conn
     *            <code>URLConnection</code> to set headers on
     * @exception IOException
     *                if an I/O exception occurs
     */
    protected void setPostHeaders(URLConnection conn) throws IOException {
        postOrPutWriter = new PostWriter();
        postOrPutWriter.setHeaders(conn, testElement);
    }

    private void setPutHeaders(URLConnection conn) throws IOException {
        postOrPutWriter = new PutWriter();
        postOrPutWriter.setHeaders(conn, testElement);
    }

    /**
     * Send POST data from <code>Entry</code> to the open connection.
     * This also handles sending data for PUT requests
     *
     * @param connection
     *            <code>URLConnection</code> where POST data should be sent
     * @return a String show what was posted. Will not contain actual file upload content
     * @exception IOException
     *                if an I/O exception occurs
     */
    protected String sendPostData(URLConnection connection) throws IOException {
        return postOrPutWriter.sendPostData(connection, testElement);
    }

    private String sendPutData(URLConnection connection) throws IOException {
        return postOrPutWriter.sendPostData(connection, testElement);
    }

    /**
     * Returns an <code>HttpURLConnection</code> fully ready to attempt
     * connection. This means it sets the request method (GET or POST), headers,
     * cookies, and authorization for the URL request.
     * <p>
     * The request infos are saved into the sample result if one is provided.
     *
     * @param u
     *            <code>URL</code> of the URL request
     * @param method
     *            GET, POST etc
     * @param res
     *            sample result to save request infos to
     * @return <code>HttpURLConnection</code> ready for .connect
     * @exception IOException
     *                if an I/O Exception occurs
     */
    protected HttpURLConnection setupConnection(URL u, String method, HTTPSampleResult res) throws IOException {
        SSLManager sslmgr = null;
        if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
            try {
                sslmgr=SSLManager.getInstance(); // N.B. this needs to be done before opening the connection
            } catch (Exception e) {
                log.warn("Problem creating the SSLManager: ", e);
            }
        }

        final HttpURLConnection conn;
        final String proxyHost = getProxyHost();
        final int proxyPort = getProxyPortInt();
        if (proxyHost.length() > 0 && proxyPort > 0){
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            //TODO - how to define proxy authentication for a single connection?
            // It's not clear if this is possible
//            String user = getProxyUser();
//            if (user.length() > 0){
//                Authenticator auth = new ProxyAuthenticator(user, getProxyPass());
//            }
            conn = (HttpURLConnection) u.openConnection(proxy);
        } else {
            conn = (HttpURLConnection) u.openConnection();
        }

        // Update follow redirects setting just for this connection
        conn.setInstanceFollowRedirects(getAutoRedirects());

        int cto = getConnectTimeout();
        if (cto > 0){
            conn.setConnectTimeout(cto);
        }

        int rto = getResponseTimeout();
        if (rto > 0){
            conn.setReadTimeout(rto);
        }

        if (HTTPConstants.PROTOCOL_HTTPS.equalsIgnoreCase(u.getProtocol())) {
            try {
                if (null != sslmgr){
                    sslmgr.setContext(conn); // N.B. must be done after opening connection
                }
            } catch (Exception e) {
                log.warn("Problem setting the SSLManager for the connection: ", e);
            }
        }

        // a well-bahaved browser is supposed to send 'Connection: close'
        // with the last request to an HTTP server. Instead, most browsers
        // leave it to the server to close the connection after their
        // timeout period. Leave it to the JMeter user to decide.
        if (getUseKeepAlive()) {
            conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.KEEP_ALIVE);
        } else {
            conn.setRequestProperty(HTTPConstants.HEADER_CONNECTION, HTTPConstants.CONNECTION_CLOSE);
        }

        conn.setRequestMethod(method);
        setConnectionHeaders(conn, u, getHeaderManager(), getCacheManager());
        String cookies = setConnectionCookie(conn, u, getCookieManager());

        setConnectionAuthorization(conn, u, getAuthManager());

        if (method.equals(HTTPConstants.POST)) {
            setPostHeaders(conn);
        } else if (method.equals(HTTPConstants.PUT)) {
            setPutHeaders(conn);
        }

        if (res != null) {
            res.setRequestHeaders(getConnectionHeaders(conn));
            res.setCookies(cookies);
        }

        return conn;
    }

    /**
     * Reads the response from the URL connection.
     *
     * @param conn
     *            URL from which to read response
     * @return response content
     * @exception IOException
     *                if an I/O exception occurs
     */
    protected byte[] readResponse(HttpURLConnection conn, SampleResult res) throws IOException {
        BufferedInputStream in;

        final int contentLength = conn.getContentLength();
        if ((contentLength == 0)
            && OBEY_CONTENT_LENGTH) {
            log.info("Content-Length: 0, not reading http-body");
            res.setResponseHeaders(getResponseHeaders(conn));
            res.latencyEnd();
            return NULL_BA;
        }

        // works OK even if ContentEncoding is null
        boolean gzipped = HTTPConstants.ENCODING_GZIP.equals(conn.getContentEncoding());
        InputStream instream = null;
        try {
            instream = new CountingInputStream(conn.getInputStream());
            if (gzipped) {
                in = new BufferedInputStream(new GZIPInputStream(instream));
            } else {
                in = new BufferedInputStream(instream);
            }
        } catch (IOException e) {
            if (! (e.getCause() instanceof FileNotFoundException))
            {
                log.error("readResponse: "+e.toString());
                Throwable cause = e.getCause();
                if (cause != null){
                    log.error("Cause: "+cause);
                    if(cause instanceof Error) {
                        throw (Error)cause;
                    }
                }
            }
            // Normal InputStream is not available
            InputStream errorStream = conn.getErrorStream();
            if (errorStream == null) {
                log.info("Error Response Code: "+conn.getResponseCode()+", Server sent no Errorpage");
                res.setResponseHeaders(getResponseHeaders(conn));
                res.latencyEnd();
                return NULL_BA;
            }

            log.info("Error Response Code: "+conn.getResponseCode());

            if (gzipped) {
                in = new BufferedInputStream(new GZIPInputStream(errorStream));
            } else {
                in = new BufferedInputStream(errorStream);
            }
        } catch (Exception e) {
            log.error("readResponse: "+e.toString());
            Throwable cause = e.getCause();
            if (cause != null){
                log.error("Cause: "+cause);
                if(cause instanceof Error) {
                    throw (Error)cause;
                }
            }
            in = new BufferedInputStream(conn.getErrorStream());
        }
        // N.B. this closes 'in'
        byte[] responseData = readResponse(res, in, contentLength);
        if (instream != null) {
            res.setBodySize(((CountingInputStream) instream).getCount());
            instream.close();
        }
        return responseData;
    }

    /**
     * Gets the ResponseHeaders from the URLConnection
     *
     * @param conn
     *            connection from which the headers are read
     * @return string containing the headers, one per line
     */
    protected String getResponseHeaders(HttpURLConnection conn) {
        StringBuilder headerBuf = new StringBuilder();
        headerBuf.append(conn.getHeaderField(0));// Leave header as is
        // headerBuf.append(conn.getHeaderField(0).substring(0, 8));
        // headerBuf.append(" ");
        // headerBuf.append(conn.getResponseCode());
        // headerBuf.append(" ");
        // headerBuf.append(conn.getResponseMessage());
        headerBuf.append("\n"); //$NON-NLS-1$

        String hfk;
        for (int i = 1; (hfk=conn.getHeaderFieldKey(i)) != null; i++) {
            headerBuf.append(hfk);
            headerBuf.append(": "); // $NON-NLS-1$
            headerBuf.append(conn.getHeaderField(i));
            headerBuf.append("\n"); // $NON-NLS-1$
        }
        return headerBuf.toString();
    }

    /**
     * Extracts all the required cookies for that particular URL request and
     * sets them in the <code>HttpURLConnection</code> passed in.
     *
     * @param conn
     *            <code>HttpUrlConnection</code> which represents the URL
     *            request
     * @param u
     *            <code>URL</code> of the URL request
     * @param cookieManager
     *            the <code>CookieManager</code> containing all the cookies
     *            for this <code>UrlConfig</code>
     */
    private String setConnectionCookie(HttpURLConnection conn, URL u, CookieManager cookieManager) {
        String cookieHeader = null;
        if (cookieManager != null) {
            cookieHeader = cookieManager.getCookieHeaderForURL(u);
            if (cookieHeader != null) {
                conn.setRequestProperty(HTTPConstants.HEADER_COOKIE, cookieHeader);
            }
        }
        return cookieHeader;
    }

    /**
     * Extracts all the required headers for that particular URL request and
     * sets them in the <code>HttpURLConnection</code> passed in
     *
     * @param conn
     *            <code>HttpUrlConnection</code> which represents the URL
     *            request
     * @param u
     *            <code>URL</code> of the URL request
     * @param headerManager
     *            the <code>HeaderManager</code> containing all the cookies
     *            for this <code>UrlConfig</code>
     * @param cacheManager the CacheManager (may be null)
     */
    private void setConnectionHeaders(HttpURLConnection conn, URL u, HeaderManager headerManager, CacheManager cacheManager) {
        // Add all the headers from the HeaderManager
        if (headerManager != null) {
            CollectionProperty headers = headerManager.getHeaders();
            if (headers != null) {
                PropertyIterator i = headers.iterator();
                while (i.hasNext()) {
                    Header header = (Header) i.next().getObjectValue();
                    String n = header.getName();
                    String v = header.getValue();
                    conn.addRequestProperty(n, v);
                }
            }
        }
        if (cacheManager != null){
            cacheManager.setHeaders(conn, u);
        }
    }

    /**
     * Get all the headers for the <code>HttpURLConnection</code> passed in
     *
     * @param conn
     *            <code>HttpUrlConnection</code> which represents the URL
     *            request
     * @return the headers as a string
     */
    private String getConnectionHeaders(HttpURLConnection conn) {
        // Get all the request properties, which are the headers set on the connection
        StringBuilder hdrs = new StringBuilder(100);
        Map<String, List<String>> requestHeaders = conn.getRequestProperties();
        for(Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
            String headerKey=entry.getKey();
            // Exclude the COOKIE header, since cookie is reported separately in the sample
            if(!HTTPConstants.HEADER_COOKIE.equalsIgnoreCase(headerKey)) {
                // value is a List of Strings
                for (String value : entry.getValue()){
                    hdrs.append(headerKey);
                    hdrs.append(": "); // $NON-NLS-1$
                    hdrs.append(value);
                    hdrs.append("\n"); // $NON-NLS-1$
                }
            }
        }
        return hdrs.toString();
    }

    /**
     * Extracts all the required authorization for that particular URL request
     * and sets it in the <code>HttpURLConnection</code> passed in.
     *
     * @param conn
     *            <code>HttpUrlConnection</code> which represents the URL
     *            request
     * @param u
     *            <code>URL</code> of the URL request
     * @param authManager
     *            the <code>AuthManager</code> containing all the cookies for
     *            this <code>UrlConfig</code>
     */
    private void setConnectionAuthorization(HttpURLConnection conn, URL u, AuthManager authManager) {
        if (authManager != null) {
            Authorization auth = authManager.getAuthForURL(u);
            if (auth != null) {
                conn.setRequestProperty(HTTPConstants.HEADER_AUTHORIZATION, auth.toBasicHeader());
            }
        }
    }

    /**
     * Samples the URL passed in and stores the result in
     * <code>HTTPSampleResult</code>, following redirects and downloading
     * page resources as appropriate.
     * <p>
     * When getting a redirect target, redirects are not followed and resources
     * are not downloaded. The caller will take care of this.
     *
     * @param url
     *            URL to sample
     * @param method
     *            HTTP method: GET, POST,...
     * @param areFollowingRedirect
     *            whether we're getting a redirect target
     * @param frameDepth
     *            Depth of this target in the frame structure. Used only to
     *            prevent infinite recursion.
     * @return results of the sampling
     */
    @Override
    protected HTTPSampleResult sample(URL url, String method, boolean areFollowingRedirect, int frameDepth) {
        HttpURLConnection conn = null;

        String urlStr = url.toString();
        log.debug("Start : sample " + urlStr);

        HTTPSampleResult res = new HTTPSampleResult();
        res.setMonitor(isMonitor());

        res.setSampleLabel(urlStr);
        res.setURL(url);
        res.setHTTPMethod(method);

        res.sampleStart(); // Count the retries as well in the time

        // Check cache for an entry with an Expires header in the future
        final CacheManager cacheManager = getCacheManager();
        if (cacheManager != null && HTTPConstants.GET.equalsIgnoreCase(method)) {
           if (cacheManager.inCache(url)) {
               res.sampleEnd();
               res.setResponseNoContent();
               res.setSuccessful(true);
               return res;
           }
        }

        try {
            // Sampling proper - establish the connection and read the response:
            // Repeatedly try to connect:
            int retry;
            // Start with 0 so tries at least once, and retries at most MAX_CONN_RETRIES times
            for (retry = 0; retry <= MAX_CONN_RETRIES; retry++) {
                try {
                    conn = setupConnection(url, method, res);
                    // Attempt the connection:
                    savedConn = conn;
                    conn.connect();
                    break;
                } catch (BindException e) {
                    if (retry >= MAX_CONN_RETRIES) {
                        log.error("Can't connect after "+retry+" retries, "+e);
                        throw e;
                    }
                    log.debug("Bind exception, try again");
                    if (conn!=null) {
                        savedConn = null; // we don't want interrupt to try disconnection again
                        conn.disconnect();
                    }
                    setUseKeepAlive(false);
                    continue; // try again
                } catch (IOException e) {
                    log.debug("Connection failed, giving up");
                    throw e;
                }
            }
            if (retry > MAX_CONN_RETRIES) {
                // This should never happen, but...
                throw new BindException();
            }
            // Nice, we've got a connection. Finish sending the request:
            if (method.equals(HTTPConstants.POST)) {
                String postBody = sendPostData(conn);
                res.setQueryString(postBody);
            }
            else if (method.equals(HTTPConstants.PUT)) {
                String putBody = sendPutData(conn);
                res.setQueryString(putBody);
            }
            // Request sent. Now get the response:
            byte[] responseData = readResponse(conn, res);

            res.sampleEnd();
            // Done with the sampling proper.

            // Now collect the results into the HTTPSampleResult:

            res.setResponseData(responseData);

            @SuppressWarnings("null") // Cannot be null here
            int errorLevel = conn.getResponseCode();
            String respMsg = conn.getResponseMessage();
            String hdr=conn.getHeaderField(0);
            if (hdr == null) {
                hdr="(null)";  // $NON-NLS-1$
            }
            if (errorLevel == -1){// Bug 38902 - sometimes -1 seems to be returned unnecessarily
                if (respMsg != null) {// Bug 41902 - NPE
                    try {
                        errorLevel = Integer.parseInt(respMsg.substring(0, 3));
                        log.warn("ResponseCode==-1; parsed "+respMsg+ " as "+errorLevel);
                      } catch (NumberFormatException e) {
                        log.warn("ResponseCode==-1; could not parse "+respMsg+" hdr: "+hdr);
                      }
                } else {
                    respMsg=hdr; // for result
                    log.warn("ResponseCode==-1 & null ResponseMessage. Header(0)= "+hdr);
                }
            }
            if (errorLevel == -1) {
                res.setResponseCode("(null)"); // $NON-NLS-1$
            } else {
                res.setResponseCode(Integer.toString(errorLevel));
            }
            res.setSuccessful(isSuccessCode(errorLevel));

            if (respMsg == null) {// has been seen in a redirect
                respMsg=hdr; // use header (if possible) if no message found
            }
            res.setResponseMessage(respMsg);

            String ct = conn.getContentType();
            if (ct != null){
                res.setContentType(ct);// e.g. text/html; charset=ISO-8859-1
                res.setEncodingAndType(ct);
            }

            String responseHeaders = getResponseHeaders(conn);
            res.setResponseHeaders(responseHeaders);
            if (res.isRedirect()) {
                res.setRedirectLocation(conn.getHeaderField(HTTPConstants.HEADER_LOCATION));
            }
            
            // record headers size to allow HTTPSampleResult.getBytes() with different options
            res.setHeadersSize(responseHeaders.replaceAll("\n", "\r\n") // $NON-NLS-1$ $NON-NLS-2$
                    .length() + 2); // add 2 for a '\r\n' at end of headers (before data) 
            if (log.isDebugEnabled()) {
                log.debug("Response headersSize=" + res.getHeadersSize() + " bodySize=" + res.getBodySize()
                        + " Total=" + (res.getHeadersSize() + res.getBodySize()));
            }
            
            // If we redirected automatically, the URL may have changed
            if (getAutoRedirects()){
                res.setURL(conn.getURL());
            }

            // Store any cookies received in the cookie manager:
            saveConnectionCookies(conn, url, getCookieManager());

            // Save cache information
            if (cacheManager != null){
                cacheManager.saveDetails(conn, res);
            }

            res = resultProcessing(areFollowingRedirect, frameDepth, res);

            log.debug("End : sample");
            return res;
        } catch (IOException e) {
            res.sampleEnd();
            savedConn = null; // we don't want interrupt to try disconnection again
            // We don't want to continue using this connection, even if KeepAlive is set
            if (conn != null) { // May not exist
                conn.disconnect();
            }
            conn=null; // Don't process again
            return errorResult(e, res);
        } finally {
            // calling disconnect doesn't close the connection immediately,
            // but indicates we're through with it. The JVM should close
            // it when necessary.
            savedConn = null; // we don't want interrupt to try disconnection again
            disconnect(conn); // Disconnect unless using KeepAlive
        }
    }

    protected void disconnect(HttpURLConnection conn) {
        if (conn != null) {
            String connection = conn.getHeaderField(HTTPConstants.HEADER_CONNECTION);
            String protocol = conn.getHeaderField(0);
            if ((connection == null && (protocol == null || !protocol.startsWith(HTTPConstants.HTTP_1_1)))
                    || (connection != null && connection.equalsIgnoreCase(HTTPConstants.CONNECTION_CLOSE))) {
                conn.disconnect();
            } // TODO ? perhaps note connection so it can be disconnected at end of test?
        }
    }

    /**
     * From the <code>HttpURLConnection</code>, store all the "set-cookie"
     * key-pair values in the cookieManager of the <code>UrlConfig</code>.
     *
     * @param conn
     *            <code>HttpUrlConnection</code> which represents the URL
     *            request
     * @param u
     *            <code>URL</code> of the URL request
     * @param cookieManager
     *            the <code>CookieManager</code> containing all the cookies
     *            for this <code>UrlConfig</code>
     */
    private void saveConnectionCookies(HttpURLConnection conn, URL u, CookieManager cookieManager) {
        if (cookieManager != null) {
            for (int i = 1; conn.getHeaderFieldKey(i) != null; i++) {
                if (conn.getHeaderFieldKey(i).equalsIgnoreCase(HTTPConstants.HEADER_SET_COOKIE)) {
                    cookieManager.addCookieFromHeader(conn.getHeaderField(i), u);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean interrupt() {
        HttpURLConnection conn = savedConn;
        if (conn != null) {
            savedConn = null;
            conn.disconnect();
        }
        return conn != null;
    }
}
