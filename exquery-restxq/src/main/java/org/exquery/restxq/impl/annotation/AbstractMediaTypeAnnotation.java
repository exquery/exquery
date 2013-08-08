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

import org.exquery.http.InternetMediaType;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.MediaTypeAnnotation;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.Type;

/**
 * Base class for RESTXQ Media Type Annotation Implementations
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractMediaTypeAnnotation extends AbstractRestAnnotation implements MediaTypeAnnotation {
    
    protected String encodeAsRegExp(String mediaType) {
        
        //escape chars in an Internet Media Type that have significance in a regexp
        mediaType = mediaType.replace("$", "\\$");
        mediaType = mediaType.replace(".", "\\.");
        mediaType = mediaType.replace("+", "\\+");
        mediaType = mediaType.replace("-", "\\-");
        mediaType = mediaType.replace("^", "\\^");
        mediaType = mediaType.replace("/", "\\/");
        
        //expand subtype wildcard to valid regexp
        mediaType = mediaType.replace("*", InternetMediaType.subtypeName_regExp);
        
        return mediaType;
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
    
    //<editor-fold desc="Error Codes">
    
    /**
     * Get the Error Code to use when the Annotation Parameters are Empty
     * 
     * @return The error code
     */
    protected abstract RestXqErrorCode getEmptyAnnotationParamsErr();

    /**
     * Get the Error Code to use when the Annotation has an invalid Media Type literal
     * 
     * @return The error code
     */
    protected abstract RestXqErrorCode getInvalidMediaTypeLiteralErr();

    /**
     * Get the Error Code to use when the Annotation has an invalid Media Type
     * 
     * @return The error code
     */
    protected abstract RestXqErrorCode getInvalidMediaTypeErr();
    
    //</editor-fold>
}
