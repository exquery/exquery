/**
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
package org.exquery.restxq;

import java.util.EnumSet;
import org.exquery.http.AcceptHeader;
import org.exquery.http.HttpMethod;
import org.exquery.http.HttpRequest;
import org.exquery.http.HttpResponse;

/**
 * Representation of a RESTful XQuery Service
 * 
 * Really just an intermediary for invoking a ResourceFunction
 * on a request and returning the response
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface RestXqService extends Comparable<RestXqService> {    
    
    /**
     * Get the XQuery ResourceFunction which
     * backs this Service
     * 
     * @return The ResourceFunction for execution by this service
     */
    public ResourceFunction getResourceFunction();
    
    /**
     * Get the HTTP Methods that this REST XQ Service
     * can service
     * 
     * @return The set of HttpMethods that this Service can service
     */
    public EnumSet<HttpMethod> getServicedMethods();
    
    /**
     * Determines if this service can Service the request
     * 
     * @param httpRequest The http request. This method must not consume the request body.
     * 
     * @return true if this Service can service the incoming request
     * 
     * @throws RestXqServiceException
     */
    public boolean canService(final HttpRequest httpRequest);
    
    /**
     * Given an Accept header calculate the Quality Factory of each
     * Internet Media Type in all ProducesAnnotations and return
     * the maximum value.
     * 
     * @param acceptHeader The HTTP Accept Header of the HTTP Request that
     * the Service may consume.
     * 
     * @return The maximum quality factor of the Produced Internet Media Types
     */
    public float maxProducesQualityFactor(final AcceptHeader acceptHeader);
    
    /**
     * Service the incoming HttpRequest with the RESTXQ Service
     * and write the response to the HttpResponse
     * 
     * @param httpRequest The request to service
     * @param httpResponse The response to write the service result to
     * @param resourceFunctionExecuter The Executer to execute the Resource Function
     * @param restXqServiceSerializer Serializer for serializing the response of the service
     * 
     * @throws RestXqServiceException If an unexpected error occured whilst processing the request
     */
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse, final ResourceFunctionExecuter resourceFunctionExecuter, final RestXqServiceSerializer restXqServiceSerializer) throws RestXqServiceException;
}