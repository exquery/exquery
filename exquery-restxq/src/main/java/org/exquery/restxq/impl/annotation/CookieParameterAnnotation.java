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

import org.exquery.http.HttpRequest;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.xdm.type.SequenceImpl;
import org.exquery.xdm.type.StringTypedValue;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Sequence;
import org.exquery.xquery.TypedArgumentValue;

/**
 * Implementation of RESTXQ Cookie Parameter Annotation
 * i.e. %rest:cookie-param
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class CookieParameterAnnotation extends AbstractParameterWithDefaultAnnotation {

    /**
     * @see AbstractParameterAnnotation#extractParameter(org.exquery.http.HttpRequest)
     */
    @Override
    public TypedArgumentValue<String> extractParameter(final HttpRequest request) {
       return new TypedArgumentValue<String> () {

            @Override
            public String getArgumentName() {
                return getParameterAnnotationMapping().getFunctionArgumentName();
            }

            @Override
            public Sequence<String> getTypedValue() {
                final Object queryParam = request.getCookieValue(getParameterAnnotationMapping().getParameterName());
                if(queryParam == null) {
                    
                    final Literal defaultLiterals[] = getParameterAnnotationMapping().getDefaultValues();
                    if(defaultLiterals.length > 0) {
                        return literalsToSequence(defaultLiterals);
                    } else {
                        return Sequence.EMPTY_SEQUENCE;
                    }
                } else if(queryParam instanceof String) {
                    return new SequenceImpl<String>(new StringTypedValue((String)queryParam));
                }
                return null;
            }
            
        }; 
    }

    @Override
    protected Cardinality getRequiredFunctionParameterCardinality() {
        //function parameters that take cookie values must permit be zero or one items
        return Cardinality.ZERO_OR_ONE;
    }
    
    
    
    //<editor-fold desc="Error Codes">
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamsErr()
     */
    @Override
    protected RestXqErrorCodes.RestXqErrorCode getInvalidAnnotationParamsErr() {
        return RestXqErrorCodes.RQST0041;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidParameterNameErr()
     */
    @Override
    protected RestXqErrorCodes.RestXqErrorCode getInvalidParameterNameErr() {
        return RestXqErrorCodes.RQST0042;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidFunctionArgumentNameErr()
     */
    @Override
    protected RestXqErrorCodes.RestXqErrorCode getInvalidFunctionArgumentNameErr() {
        return RestXqErrorCodes.RQST0043;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueErr()
     */
    @Override
    protected RestXqErrorCodes.RestXqErrorCode getInvalidDefaultValueErr() {
        return RestXqErrorCodes.RQST0044;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueTypeErr()
     */
    @Override
    protected RestXqErrorCodes.RestXqErrorCode getInvalidDefaultValueTypeErr() {
        return RestXqErrorCodes.RQST0045;
    }
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParametersSyntaxErr()
     */
    @Override
    protected RestXqErrorCodes.RestXqErrorCode getInvalidAnnotationParametersSyntaxErr() {
        return RestXqErrorCodes.RQST0046;
    }
    
    //</editor-fold>
}
