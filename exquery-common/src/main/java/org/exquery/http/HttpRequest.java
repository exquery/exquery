/*
 * Copyright © 2012, Adam Retter / EXQuery
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exquery.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for a HTTP Request
 * 
 * @author Adam Retter
 */
public interface HttpRequest {
    
    /**
     * Gets the HTTP Method of the HTTP Request
     * 
     * @return the HttpMethod of the request
     */
    public HttpMethod getMethod();
    
    /**
     * Gets the HTTP Scheme of the HTTP Request
     * 
     * @return the HttpScheme of the request
     */
    public String getScheme();
    
    /**
     * Get the Name of the Host that received the HTTP Request
     * 
     * @return the Hostname of the host that received the request
     */
    public String getHostname();
    
    
    /**
     * Get the Port fragment of the Authority component of the URI of the HTTP Request.
     * If the port is not explicitly specified in the URI, then the default port for
     * the HTTP Scheme is returned (i.e. 21 for FTP, 80 for HTTP and 443 for HTTPS).
     * 
     * @return the TCP server port
     */
    public int getPort();
    
    /**
     * Get the Query Component of the HTTP Request URI
     * 
     * @return the Query component of the Request URI
     */
    public String getQuery();
    
    /**
     * Get the Path from the URI
     * 
     * @return the Path segment of the URI
     */
    public String getPath();
    
    /**
     * Get the URI of the HTTP Request
     * 
     * @return the URI of the HTTP Request
     */
    public String getURI();
    
    /**
     * Get the IP Address of the Server
     * 
     * @return The IP address of the Server
     */
    public String getAddress();
    
    /**
     * Get the fully qualified hostname of the client or the last proxy that
     * sent the HTTP Request. If the name of the remote host cannot be
     * established, or an implementation chooses not to establish the
     * remote hostname, this method behaves as request:remote-address(),
     * and returns the IP address.
     * 
     * @return the Hostname of the client that issued the request
     */
    public String getRemoteHostname();
    
    /**
     * Get the IP Address of the client or the last proxy
     * that sent the HTTP Request.
     * 
     * @return The IP address of the Client
     */
    public String getRemoteAddress();
    
    /**
     * Get the TCP port of the client
     * 
     * @return the TCP port of the client
     */
    public int getRemotePort();
    
    /**
     * Get the value of a cookie
     * 
     * @param cookieName the name of the cookie
     * 
     * @return the value of the cookie
     */
    public String getCookieValue(final String cookieName);
    
    /**
     * Gets the InputStream for reading the body of the HTTP Request
     * 
     * @return The input stream for the request body
     *
     * @throws IOException if a problem occurs when reading the request body
     */
    public InputStream getInputStream() throws IOException;
    
    /**
     * Gets the names of HTTP Header in the HTTP Request
     * 
     * @return The list of HTTP Header names
     */
    public List<String> getHeaderNames();
    
    /**
     * Gets the value of a HTTP Header
     * 
     * @param httpHeaderName The name of the HTTP Header to retrieve
     * 
     * @return The value of the header or null if the header was not present
     */
    public String getHeader(String httpHeaderName);

    public String getContentType();
    
    public int getContentLength();

    public String getCharacterEncoding();

    /**
     * Get the names of parameters available in the request
     * 
     * @return the list of parameter names in the request
     */
    public List<String> getParameterNames();
    
    public <F> F getFormParam(String key);

    public <Q> Q getQueryParam(String key);
}
