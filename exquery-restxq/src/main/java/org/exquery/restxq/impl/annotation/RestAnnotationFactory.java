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
package org.exquery.restxq.impl.annotation;

import javax.xml.namespace.QName;
import org.exquery.annotation.AnnotationException;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.annotation.RestAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.restxq.annotation.RestAnnotationName;
import static org.exquery.restxq.annotation.RestAnnotationName.headerparam;
import org.exquery.serialization.annotation.SerializationAnnotationFactory;
import org.exquery.xquery3.Annotation;

/**
 * Factory for instantiating and configuring RESTXQ Annotations
 *
 * @author Adam Retter
 */
public class RestAnnotationFactory {
    
    /**
     * Determines if the Annotation Name is a valid RESTXQ Annotation
     * RESTXQ Annotations include both REST and Serialization Annotations
     * 
     * @param annotationName The name of the annotation to test
     * 
     * @return true if the Annotation Name is a valid RESTXQ Annotation, false otherwise
     */
    public static boolean isRestXqAnnotation(final QName annotationName) {
        return isRestAnnotation(annotationName) || SerializationAnnotationFactory.isSerializationAnnotation(annotationName);
    }
    
    /**
     * Determines if the Annotation Name is a valid Rest Annotation
     * 
     * @param annotationName The name of the annotation to test
     * 
     * @return true if the Annotation Name is a valid Rest Annotation, false otherwise
     */
    public static boolean isRestAnnotation(final QName annotationName) {
        return annotationName.getNamespaceURI().equals(Namespace.ANNOTATION_NS);
    }
    
    /**
     * Gets a RESTXQ Annotation from an XQuery 3.0 Annotation
     * 
     * @param annotation The XQuery 3.0 Annotation
     * 
     * @return The RESTXQ Annotation
     * 
     * @throws AnnotationException If the provided annotation is not a valid RESTXQ Annotation
     */
    public static Annotation getAnnotation(final Annotation annotation) throws AnnotationException {
        if(annotation.getName().getNamespaceURI().equals(org.exquery.serialization.Namespace.ANNOTATION_NS)) {
            return SerializationAnnotationFactory.getAnnotation(annotation);
        } else if(annotation.getName().getNamespaceURI().equals(org.exquery.restxq.Namespace.ANNOTATION_NS)) {
            return getRESTAnnotation(annotation);
        } else {
            throw new IllegalArgumentException("Unknown annotation: " + annotation.getName().toString());
        }
    }

    protected static RestAnnotation getRESTAnnotation(final Annotation annotation) throws RestAnnotationException {
        final RestAnnotationName an = RestAnnotationName.valueOf(annotation.getName());
        
        final AbstractRestAnnotation restAnnotation;
        switch(an) {
            case GET:
                restAnnotation = new GetMethodAnnotation();
                break;
                
            case HEAD:
                restAnnotation = new HeadMethodAnnotation();
                break;
                    
            case DELETE:
                restAnnotation = new DeleteMethodAnnotation();
                break;
            
            case POST:
                restAnnotation = new PostMethodAnnotation();
                break;
            
            case PUT:
                restAnnotation = new PutMethodAnnotation();
                break;

            case OPTIONS:
                restAnnotation = new OptionsMethodAnnotation();
                break;

            case path:
                restAnnotation = new PathAnnotationImpl();
                break;
                
            case consumes:
                restAnnotation = new ConsumesAnnotationImpl();
                break;
            
            case produces:
                restAnnotation = new ProducesAnnotationImpl();
                break;
                
            case formparam:
                restAnnotation = new FormParameterAnnotation();
                break;
                
            case queryparam:
                restAnnotation = new QueryParameterAnnotation();
                break;
                
            case headerparam:
                restAnnotation = new HeaderParameterAnnotation();
                break;
                
            case cookieparam:
                restAnnotation = new CookieParameterAnnotation();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown annotation: " + annotation.getName().toString());
        }
        
        restAnnotation.setName(annotation.getName());
        restAnnotation.setFunctionSignature(annotation.getFunctionSignature());
        restAnnotation.setLiterals(annotation.getLiterals());
        restAnnotation.initialise();
        
        return restAnnotation;
    }
}