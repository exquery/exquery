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
package org.exquery.restxq;

import java.net.URI;
import java.util.Set;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.restxq.annotation.ParameterAnnotation;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.xquery3.FunctionSignature;

/**
 * Resource Function
 * 
 * Represents an XQuery 3.0 function which has
 * RESTXQ annotations applied to it
 * 
 * Resource Functions may also have Serialization
 * Annotations in addition to RESTXQ Annotations
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface ResourceFunction {
    
    /**
     * Gets the URI of the XQuery that contains this Resource Function
     * 
     * @return The URI pointing to the XQuery containing this Resource Function
     */
    public URI getXQueryLocation();
    
    /**
     * Gets the Function Signature of the Resource Function
     * 
     * @return The Function Signature of the Resource Function
     */
    public FunctionSignature getFunctionSignature();
    
    /**
     * Returns the Path Annotation applied to the Resource Function
     * 
     * @return The Path Annotation of the Resource Function
     */
    public PathAnnotation getPathAnnotation();
    
    /**
     * Returns the Set of HTTP Method Annotations applied to the Resource Function
     * 
     * @return The HTTP Method Annotations of the Resource Function
     * The Set may contain zero or more annotations.
     */
    public Set<HttpMethodAnnotation> getHttpMethodAnnotations();
    
    /**
     * Returns the Set of HTTP Parameter Annotations applied to the Resource Function
     * 
     * @return The HTTP Parameter Annotations of the Resource Function
     * The Set may contain zero or more annotations.
     */
    public Set<ParameterAnnotation> getParameterAnnotations();
}