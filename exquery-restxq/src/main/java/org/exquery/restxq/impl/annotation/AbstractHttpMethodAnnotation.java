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
package org.exquery.restxq.impl.annotation;

import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.restxq.annotation.RestAnnotationName;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.Type;
import org.exquery.xquery3.Annotation;

/**
 * Base class for RESTXQ Method Annotation Implementations
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractHttpMethodAnnotation extends AbstractRestAnnotation implements HttpMethodAnnotation {

    //TODO cosider allowing a catch all Method Annotation, i.e. with no Path Annotation, should work as the PathSegmentCount would just be 0 or -1 for sorting
    
    /**
     * Checks that a Path Annotation is present
     * as you cannot have a Method Annotation without a Path Annotation
     * 
     * @throws RestAnnotationException if the initialisation of this annotation fails
     */
    @Override
    public void initialise() throws RestAnnotationException {
        super.initialise();
        checkForPathAnnotation();
    }

    /**
     * Ensures that the Resource Function which declared
     * this HTTP Method Annotation, also declares a Path Annotation
     * 
     * @throws RestAnnotationException if the ResourceFunction
     * does not also declare a Path Annotation
     */
    protected void checkForPathAnnotation() throws RestAnnotationException {
        for(Annotation annotation : getFunctionSignature().getAnnotations()) {
            if(annotation.getName().equals(RestAnnotationName.path.getQName())) {
                return;
            }
        }
	        
        throw new RestAnnotationException(RestXqErrorCodes.RQST0009);
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getRequiredFunctionParameterCardinality()
     */
    @Override
    protected Cardinality getRequiredFunctionParameterCardinality() {
        throw new UnsupportedOperationException("Not required.");
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getInvalidFunctionParameterCardinalityErr()
     */
    @Override
    protected RestXqErrorCode getInvalidFunctionParameterCardinalityErr() {
        throw new UnsupportedOperationException("Not required.");
    }
    
    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getRequiredFunctionParameterType()
     */
    @Override
    protected Type getRequiredFunctionParameterType() {
        throw new UnsupportedOperationException("Not required.");
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getInvalidFunctionParameterTypeErr()
     */
    @Override
    protected RestXqErrorCode getInvalidFunctionParameterTypeErr() {
        throw new UnsupportedOperationException("Not required.");
    }
}