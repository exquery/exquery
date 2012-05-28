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
package org.exquery.restxq.impl;

import java.net.URI;
import java.util.Set;
import javax.xml.namespace.QName;
import org.exquery.EXQueryException;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.ResourceFunction;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.restxq.annotation.ParameterAnnotation;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.serialization.annotation.SerializationAnnotation;
import org.exquery.xquery3.Annotation;

/**
 * Factory for Creating Resource Functions
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ResourceFunctionFactory {
    
    public static boolean isResourceFunctionAnnotation(final QName annotationName) {
        return annotationName.getNamespaceURI().equals(Namespace.ANNOTATION_NS)
                || annotationName.getNamespaceURI().equals(org.exquery.serialization.Namespace.ANNOTATION_NS);
    }
    
    /**
     * Creates a Resource Function from the provided annotations
     * 
     * @param xQueryLocation The URI of the XQuery that contains this Resource Function
     * @param annotations Annotations found on the XQuery Function which describe it as a Resource Function
     * 
     * @return The Resource Function
     * 
     * @throws EXQueryException if provided annotations are not either RESTXQ or Serialization Annotations
     */
    public static ResourceFunction create(final URI xQueryLocation, final Set<Annotation> annotations) throws EXQueryException {
        final ResourceFunctionImpl resourceFunction = new ResourceFunctionImpl();
        resourceFunction.setXQueryLocation(xQueryLocation);
        
        for(final Annotation annotation : annotations) {
            if(!isResourceFunctionAnnotation(annotation.getName())) {
                throw new EXQueryException("Annotation is not a valid EXQuery RESTXQ or Serialization Annotation");
            }
            
            if(annotation instanceof PathAnnotation) {
                resourceFunction.setPathAnnotation((PathAnnotation)annotation);
            } else if(annotation instanceof HttpMethodAnnotation) {
                resourceFunction.getHttpMethodAnnotations().add((HttpMethodAnnotation)annotation);
            } else if(annotation instanceof ParameterAnnotation) {
                resourceFunction.getParameterAnnotations().add((ParameterAnnotation)annotation);
            } else if(annotation instanceof SerializationAnnotation) {
                resourceFunction.getSerializationAnnotations().add((SerializationAnnotation)annotation);
            }
        }
        
        if(resourceFunction.getPathAnnotation() == null) {
            throw new EXQueryException("Resource Function must have a Path Annotation");
        }
        
        //borrow the function signature from the PathAnnotation which is mandatory (it will be the same anyways)
        resourceFunction.setFunctionSignature(resourceFunction.getPathAnnotation().getFunctionSignature());
        
        return resourceFunction;
    }
}