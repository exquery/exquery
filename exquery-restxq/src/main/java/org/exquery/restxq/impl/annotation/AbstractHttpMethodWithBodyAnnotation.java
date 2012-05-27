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

import java.util.regex.Matcher;
import org.exquery.restxq.RESTXQAnnotationException;
import org.exquery.restxq.RESTXQErrorCodes;
import org.exquery.restxq.annotation.HttpMethodWithBodyAnnotation;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;

/**
 * Base class for RESTXQ Method Annotation Implementations
 * which have an optional body component, e.g. POST and PUT
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractHttpMethodWithBodyAnnotation extends AbstractHttpMethodAnnotation implements HttpMethodWithBodyAnnotation {

    private String bodyParameterName;

    /**
     * Checks that a method body annotation
     * is applicable to the function signature
     * and keeps its name for later use
     * 
     * @see AbstractHttpMethodAnnotation#initialise()
     */
    @Override
    public void initialise() throws RESTXQAnnotationException {
        super.initialise();
        this.bodyParameterName = parseAnnotationValue();
    }
    
    @Override
    public String getBodyParameterName() {
        return bodyParameterName;
    }
    
    private String parseAnnotationValue() throws RESTXQAnnotationException {
        final Literal[] annotationLiterals = getLiterals();
        
        if(annotationLiterals.length > 1) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0010);
        } else if(annotationLiterals.length != 1) {
            return null;
        } else {
            return parseMethodBodyValue(annotationLiterals[0]);
        }
    }
    
    private String parseMethodBodyValue(final Literal methodValue) throws RESTXQAnnotationException {
        
        if(methodValue.getType() != Type.STRING) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0011);
        }
        
        final String methodStr = methodValue.getValue();
        if(methodStr.isEmpty()) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0012);
        }

        //validate the methodStr
        final Matcher mtcFunctionArgument = functionArgumentPattern.matcher(methodStr);
        
        if(!mtcFunctionArgument.matches()) {
            throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0013);
        }

        final String bodyContentParamName = mtcFunctionArgument.group(1);

        //check the function that has this annotation has parameters as declared by the annotation
        checkFnDeclaresParameter(getFunctionSignature(), bodyContentParamName);

        return bodyContentParamName;
    }
}