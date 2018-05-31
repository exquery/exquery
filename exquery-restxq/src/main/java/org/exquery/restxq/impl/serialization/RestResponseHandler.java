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
package org.exquery.restxq.impl.serialization;

import org.exquery.http.ContentTypeHeader;
import org.exquery.http.HttpHeader;
import org.exquery.http.HttpResponse;
import org.exquery.http.HttpStatus;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.RestXqServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.util.EnumMap;
import java.util.Map;

/**
 * Class to process a rest:response Element which
 * can be returned from a Resource Function
 *
 * @author Adam Retter
 */
public class RestResponseHandler {
    
    private final static String EXPATH_HTTP_CLIENT_NS_URI = "http://expath.org/ns/http-client";
    private final static String EXPATH_HTTP_CLIENT_NS_PREFIX = "http";
    
    public static QName REST_RESPONSE_ELEMENT_NAME = new QName(Namespace.ANNOTATION_NS, "response");
    private static final QName HTTP_RESPONSE_ELEMENT_NAME = new QName(EXPATH_HTTP_CLIENT_NS_URI, "response");
    private static final QName HTTP_HEADER_ELEMENT_NAME = new QName(EXPATH_HTTP_CLIENT_NS_URI, "header");
    
    private static final QName SERIALIZATION_PARAMETERS_ELEMENT_NAME = new QName(org.exquery.serialization.Namespace.XSLT_XQUERY_SERIALIZATION_NS, "serialization-parameters");
    private static final QName METHOD_ELEMENT_NAME = new QName(org.exquery.serialization.Namespace.XSLT_XQUERY_SERIALIZATION_NS, "method");
    
    private static final String STATUS_ATTR_NAME = "status";
    private static final String REASON_ATTR_NAME = "reason";
    private static final String MESSAGE_ATTR_NAME = "message";
    private static final String NAME_ATTR_NAME = "name";
    private static final String VALUE_ATTR_NAME = "value";

    /**
     * Processes a rest:response element and sets the appropriate headers and fields in the http response
     * 
     * @param restResponse The rest:response element
     * @param serializationProperties Any properties that affect serialization
     * @param response The response to serialize the result to
     *
     * @throws RestXqServiceException If a problem occurs during serialization
     */
    public void process(final Element restResponse, final Map<SerializationProperty, String> serializationProperties, final HttpResponse response) throws RestXqServiceException {
        
        final NodeList nlSerializationParameters = restResponse.getElementsByTagNameNS(SERIALIZATION_PARAMETERS_ELEMENT_NAME.getNamespaceURI(), SERIALIZATION_PARAMETERS_ELEMENT_NAME.getLocalPart());
        if(nlSerializationParameters.getLength() == 1) {
            serializationProperties.putAll(processSerializationParameters((Element)nlSerializationParameters.item(0)));
        }
        
        final NodeList nlHttpResponse = restResponse.getElementsByTagNameNS(HTTP_RESPONSE_ELEMENT_NAME.getNamespaceURI(), HTTP_RESPONSE_ELEMENT_NAME.getLocalPart());
        if(nlHttpResponse.getLength() == 1) {
            processHttpResponse((Element)nlHttpResponse.item(0), serializationProperties, response);
        }
    }
    
    protected final Map<SerializationProperty, String> processSerializationParameters(final Element serializationParameters) {
        final Map<SerializationProperty, String> serializationProperties = new EnumMap<SerializationProperty, String>(SerializationProperty.class);
        
        //get the output method
        final NodeList nlMethod = serializationParameters.getElementsByTagNameNS(METHOD_ELEMENT_NAME.getNamespaceURI(), METHOD_ELEMENT_NAME.getLocalPart());
        if(nlMethod.getLength() == 1) {
            final Element elemMethod = (Element)nlMethod.item(0);
            final String strMethod = elemMethod.getAttribute(VALUE_ATTR_NAME);
            if(strMethod != null && !strMethod.isEmpty()) {
                serializationProperties.put(SerializationProperty.METHOD, strMethod);
            }
        }
        
        return serializationProperties;
    }

    protected void processHttpResponse(final Element httpResponse, final Map<SerializationProperty, String> serializationProperties, final HttpResponse response) throws RestXqServiceException {
        
        //get the status code (if present)
        final String strStatus = httpResponse.getAttribute(STATUS_ATTR_NAME);
        HttpStatus httpStatus = null;
        if(strStatus != null && !strStatus.isEmpty()) {
            final int status = Integer.parseInt(strStatus);
            try {
                httpStatus = HttpStatus.fromStatus(status);
            } catch(final IllegalArgumentException iae) {
                throw new RestXqServiceException("Invalid HTTP Status in rest:response/@status: " + strStatus, iae);
            }
        }
        
        //get the message (if present)
        final String message = httpResponse.getAttribute(MESSAGE_ATTR_NAME);

        //get the reason (if message is not present)
        final String reason = (message == null || message.isEmpty()) ? httpResponse.getAttribute(REASON_ATTR_NAME) : message;
        
        //set the status and reason
        if(httpStatus != null) {
            if(reason != null && !reason.isEmpty()) {
                response.setStatus(httpStatus, reason);
            } else {
                response.setStatus(httpStatus);
            }
        }
        
        //process the http headers
        final NodeList nlHttpHeader = httpResponse.getElementsByTagNameNS(HTTP_HEADER_ELEMENT_NAME.getNamespaceURI(), HTTP_HEADER_ELEMENT_NAME.getLocalPart());
        processHttpHeaders(nlHttpHeader, serializationProperties, response);
    }

    protected void processHttpHeaders(final NodeList nlHttpHeader, final Map<SerializationProperty, String> serializationProperties, final HttpResponse response) {
        for(int i = 0; i < nlHttpHeader.getLength(); i++) {
            final Element elemHeader = (Element)nlHttpHeader.item(i);
            final String name = elemHeader.getAttribute(NAME_ATTR_NAME);
            final String value = elemHeader.getAttribute(VALUE_ATTR_NAME);
            
            if(name.equals(HttpHeader.CONTENT_TYPE.getHeaderName())) {
                serializationProperties.put(SerializationProperty.MEDIA_TYPE, new ContentTypeHeader(value).getInternetMediaType());
                //TODO how to select the Serializer based on the Content-Type? Should probably just use the %output:method
            }
            
            response.setHeader(name, value);
        }
    }
}