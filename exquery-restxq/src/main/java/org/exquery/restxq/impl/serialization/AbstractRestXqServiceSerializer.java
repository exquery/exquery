/*
Copyright (c) 2012, Adam Retter
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of Adam Retter Consulting nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.exquery.restxq.impl.serialization;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.exquery.InternetMediaType;
import org.exquery.http.HttpResponse;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.RestXqServiceException;
import org.exquery.restxq.RestXqServiceSerializer;
import org.exquery.restxq.impl.serialization.XmlWriter.Attribute;
import org.exquery.serialization.annotation.MediaTypeAnnotation;
import org.exquery.serialization.annotation.MethodAnnotation;
import org.exquery.serialization.annotation.MethodAnnotation.SupportedMethod;
import org.exquery.serialization.annotation.SerializationAnnotation;
import org.exquery.xquery.Sequence;
import org.exquery.xquery.Type;
import org.exquery.xquery.TypedValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serializes the result of a RESTXQ Service invokation
 *
 * @author Adam Retter <adam.retter@googelmail.com>
 */
public abstract class AbstractRestXqServiceSerializer implements RestXqServiceSerializer {
    
    private final static String DEFAULT_ENCODING = "UTF-8";
    private final static String DEFAULT_INTERNET_MEDIA_TYPE = InternetMediaType.APPLICATION_XML.getMediaType();
    private final static String DEFAULT_CONTENT_TYPE = DEFAULT_INTERNET_MEDIA_TYPE + "; charset=" + DEFAULT_ENCODING;
    
    private final static Map<SerializationProperty, String> DEFAULT_SERIALIZATION_PROPERTIES = new EnumMap<SerializationProperty, String>(SerializationProperty.class);
    static {
        DEFAULT_SERIALIZATION_PROPERTIES.put(SerializationProperty.INDENT, "yes");
        DEFAULT_SERIALIZATION_PROPERTIES.put(SerializationProperty.ENCODING, DEFAULT_ENCODING);
        DEFAULT_SERIALIZATION_PROPERTIES.put(SerializationProperty.MEDIA_TYPE, DEFAULT_INTERNET_MEDIA_TYPE);
    }
    
    /**
     * Gets the Default Encoding
     * 
     * (can be overridden!)
     * 
     * @return The default encoding, i.e. 'UTF-8'
     */
    protected String getDefaultEncoding() {
        return DEFAULT_ENCODING;
    }
    
    /**
     * Get the Default Content Type
     * 
     * @return The default content type
     */
    protected String getDefaultContentType() {
        return DEFAULT_CONTENT_TYPE;
    }
    
    /**
     * Get the Default Serialization Properties;
     *
     * @return The default Serialization Properties
     */
    protected Map<SerializationProperty, String> getDefaultSerializationProperties() {
        return DEFAULT_SERIALIZATION_PROPERTIES;
    }
    
    /**
     * Serializes the result of a RESTXQ Service
     *
     * @param result The result of the RESTXQ Service's Resource Function invokation
     * @param serializationAnnotations Serialization Annotations which were present on the Resource Function
     * @param response The HTTP Response to Serialize the result to
     *
     * @throws RestXqServiceException If an error occurs during serialization
     */
    @Override
    public void serialize(final Sequence result, final Set<SerializationAnnotation> serializationAnnotations, final HttpResponse response) throws RestXqServiceException {
        
        // some xquery functions can write directly to the output stream
        // (response:stream-binary() etc...)
        // so if output is already written then dont overwrite here
        if(response.isCommitted()) {
            return;
        }
        
        final Iterator<TypedValue> itResult = result.iterator();
        if(itResult.hasNext()) {
            final TypedValue firstResultPart = itResult.next();
            
            //determine if the first element in the sequence is rest:response
            Element elem = null;
            
            if(firstResultPart.getType().equals(Type.DOCUMENT)) {
                elem = ((Document)firstResultPart.getValue()).getDocumentElement();
            } else if(firstResultPart.getType().equals(Type.ELEMENT)) {
                elem = (Element)firstResultPart.getValue();
            }
            
            final Map<SerializationProperty, String> serializationProperties = new EnumMap<SerializationProperty, String>(SerializationProperty.class);
            serializationProperties.putAll(getDefaultSerializationProperties());
            
            //serialize either 1) rest:response and optional body, or 2) just the body
            if(elem != null && new QName(elem.getNamespaceURI(), elem.getLocalName()).equals(RestResponseHandler.REST_RESPONSE_ELEMENT_NAME)) {
                //set the rest:response and serialize the body if it exists
                
                processSerializationAnnotations(serializationAnnotations, serializationProperties);
                new RestResponseHandler().process(elem, serializationProperties, response);
                if(itResult.hasNext()) {
                    
                    final Sequence seqBody = result.tail();
                    serializeBody(seqBody, response, serializationProperties);
                }
            } else {
                //serialize just the body
                processSerializationAnnotations(serializationAnnotations, serializationProperties);
                serializeBody(result, response, serializationProperties);
            }
        }
    }
    
    /**
     * Processes the Serialization Annotations
     * and sets Serialization Properties that will
     * be used during serialization of the response
     *
     * @param serializationAnnotations The Serialization Annotations to process  
     * @param serializationProperties The Serialization properties derived from the Serialization Annotations
     */
    protected void processSerializationAnnotations(final Set<SerializationAnnotation> serializationAnnotations, final Map<SerializationProperty, String> serializationProperties) {
        
        String mediaType = null;
        
        //get the serialzation annotations
        for(SerializationAnnotation serializationAnnotation : serializationAnnotations) {
            if(serializationAnnotation instanceof MethodAnnotation) {
                final String methodProp = ((MethodAnnotation)serializationAnnotation).getMethod();
                serializationProperties.put(SerializationProperty.METHOD, methodProp);
                
                SupportedMethod method = null;
                try {
                    method = (methodProp == null ? SupportedMethod.xml : SupportedMethod.valueOf(methodProp));
                } catch(final IllegalArgumentException iae) {
                    //do nothing

                    //TODO debugging
                    System.out.println(iae.getMessage());
                }
                
                //set the default media-type for the method
                if(method != null) {
                    if(method.equals(SupportedMethod.xml) || method.equals(SupportedMethod.xhtml)) {
                        serializationProperties.put(SerializationProperty.MEDIA_TYPE, getDefaultContentType());
                    } else if(method.equals(SupportedMethod.html) || method.equals(SupportedMethod.html5)) {
                        serializationProperties.put(SerializationProperty.MEDIA_TYPE, InternetMediaType.TEXT_HTML.getMediaType() + "; charset=" + getDefaultEncoding());
                    } else if(method.equals(SupportedMethod.json)) {
                        serializationProperties.put(SerializationProperty.MEDIA_TYPE, InternetMediaType.APPLICATION_JSON.getMediaType() + "; charset=" + getDefaultEncoding());
                    } else if(method.equals(SupportedMethod.binary)) {
                        serializationProperties.put(SerializationProperty.MEDIA_TYPE, InternetMediaType.APPLICATION_OCTET_STREAM.getMediaType());
                    }
                }
                
            } else if(serializationAnnotation instanceof MediaTypeAnnotation) {
                mediaType = ((MediaTypeAnnotation)serializationAnnotation).getMediaType();
            }
        }

        //%output:media-type overrides the defaults
        if(mediaType != null) {
            serializationProperties.put(SerializationProperty.MEDIA_TYPE, mediaType);
        }
    }
    
    /**
     * Serialize to the body of the HTTP Response
     *
     * @param result
     * @param response
     * @param serializationProperties
     *
     * @throws RestXqServiceException  
     */
    protected void serializeBody(final Sequence result, final HttpResponse response, final Map<SerializationProperty, String> serializationProperties) throws RestXqServiceException {
        
        SupportedMethod method = null;
        
        try {
            final String methodProp = serializationProperties.get(SerializationProperty.METHOD);
            method = (methodProp == null ? SupportedMethod.xml : SupportedMethod.valueOf(methodProp));
        } catch(final IllegalArgumentException iae) {
            //do nothing

            //TODO debugging
            System.out.println(iae.getMessage());
        }
        
        //set the media-type
        final String mediaType = serializationProperties.get(SerializationProperty.MEDIA_TYPE);
        if(mediaType != null && !mediaType.isEmpty()) {
            response.setContentType(mediaType);
        }
        
        if(method != null && method.equals(SupportedMethod.binary)) {
            serializeBinaryBody(result, response);
        } else {
            serializeNodeBody(result, response, serializationProperties);
        }
    }
    
    /**
     * Serialize the Result as Binary content
     * 
     * @param result The result to serialize as Binary, typically a sequence of one or more xs:base64Binary or xs:hexBinary
     * @param response The HTTP Response to serialize the result to
     * 
     * @throws RestXqServiceException If an error occurred whilst serializing the result
     */
    protected abstract void serializeBinaryBody(final Sequence result, final HttpResponse response) throws RestXqServiceException;
    
    /**
     * Serialize the Result
     * 
     * The method for serialization can be obtained from
     * the map of Serialization properties using
     * the key SerializationProperty.method, if the method
     * is missing or null, then XML should be assumed.
     * 
     * @param result The result to serialize, typically a sequence of one or more documents
     * @param response The HTTP Response to serialize the result to
     * @param serializationProperties Properties for the serialization
     * 
     * @throws RestXqServiceException If an error occurred whilst serializing the result
     */
    protected abstract void serializeNodeBody(final Sequence result, final HttpResponse response, final Map<SerializationProperty, String> serializationProperties) throws RestXqServiceException;
    
    
    /**
     * Serialize the Java Exception
     * 
     * Provides a simple serialization of a Java Exception
     * 
     * @param e The Exception to serialize
     * @param writer The XML Writer which will receive the exception
     * 
     * @throws RestXqServiceException if an error occurs during serialization
     */
    public void serializeExceptionResponse(final Exception e, final XmlWriter writer) throws RestXqServiceException {
        
        try {
            writer.setProperties(getDefaultSerializationProperties());
            
            final QName qnResponse = new QName("response", Namespace.ANNOTATION_NS);
            final QName qnException = new QName("exception", Namespace.ANNOTATION_ERROR_NS);
            final QName message = new QName("message", Namespace.ANNOTATION_ERROR_NS);
            final QName qnStack = new QName("stack", Namespace.ANNOTATION_ERROR_NS);

            writer.startDocument();
            writer.startElement(qnResponse);
            writer.startElement(qnException);
            writer.startElement(message);
            writer.characters(e.getClass().getName() + ": " + e.getLocalizedMessage());
            
            final StackTraceElement[] trace = e.getStackTrace();
            for(final StackTraceElement element : trace) {
                
                final Attribute attributes[] = {
                    attribute(new QName("file"), element.getFileName()),
                    attribute(new QName("class"), element.getClassName()),
                    attribute(new QName("method"), element.getMethodName()),
                    attribute(new QName("line"), Integer.toString(element.getLineNumber()))
                };
                
                writer.startElement(qnStack, attributes);
                writer.endElement();
            }
            
            writer.endElement();
            writer.endElement();
            writer.endElement();
            writer.endDocument();
            
        } catch(IOException ioe) {    
            throw new RestXqServiceException("Error while serializing XML for exception '" + e.getClass().getName() + ":" + e.getMessage() + "': " + ioe.toString(), ioe);
        }
    }
    
    /**
     * Constructs a simple Attribute
     * 
     * @param name The name of the attribute
     * @param value The value of the attribute
     * 
     * @return The attribute
     */
    protected Attribute attribute(final QName name, final String value) {
        return new Attribute() {
            @Override
            public QName getName() {
                return name;
            }

            @Override
            public String getValue() {
                return value;
            }
        };
    }
}