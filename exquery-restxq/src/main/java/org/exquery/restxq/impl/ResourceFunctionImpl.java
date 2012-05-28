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
import java.util.HashSet;
import java.util.Set;
import org.exquery.restxq.ResourceFunction;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.restxq.annotation.ParameterAnnotation;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.serialization.annotation.SerializationAnnotation;
import org.exquery.xquery3.FunctionSignature;

/**
 * Default implementation of Resource Function
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ResourceFunctionImpl implements ResourceFunction {

    private URI xQueryLocation;
    private FunctionSignature functionSignature;
    private PathAnnotation pathAnnotation;
    private Set<HttpMethodAnnotation> httpMethodAnnotations = new HashSet<HttpMethodAnnotation>();
    private Set<ParameterAnnotation> parameterAnnotations = new HashSet<ParameterAnnotation>();
    private Set<SerializationAnnotation> serializationAnnotations = new HashSet<SerializationAnnotation>();
    
    @Override
    public URI getXQueryLocation() {
        return xQueryLocation;
    }
    
    void setXQueryLocation(final URI xQueryLocation) {
        this.xQueryLocation = xQueryLocation;
    }
    
    @Override
    public FunctionSignature getFunctionSignature() {
        return functionSignature;
    }
    
    void setFunctionSignature(final FunctionSignature functionSignature) {
        this.functionSignature = functionSignature;
    }

    @Override
    public PathAnnotation getPathAnnotation() {
        return pathAnnotation;
    }
    
    void setPathAnnotation(final PathAnnotation pathAnnotation) {
        this.pathAnnotation = pathAnnotation;
    }
    
    @Override
    public Set<HttpMethodAnnotation> getHttpMethodAnnotations() {
        return httpMethodAnnotations;
    }

    @Override
    public Set<ParameterAnnotation> getParameterAnnotations() {
        return parameterAnnotations;
    }

    @Override
    public Set<SerializationAnnotation> getSerializationAnnotations() {
        return serializationAnnotations;
    }
}