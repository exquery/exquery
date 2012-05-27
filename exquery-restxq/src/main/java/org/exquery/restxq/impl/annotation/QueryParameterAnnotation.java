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

import org.exquery.http.HttpRequest;
import org.exquery.restxq.RESTXQErrorCodes;
import org.exquery.restxq.RESTXQErrorCodes.RESTXQErrorCode;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;
import org.exquery.xquery.TypedArgumentValue;
import org.exquery.xquery.TypedValue;

/**
 * Implementation of RESTXQ Query Parameter Annotation
 * i.e. %rest:query-param
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class QueryParameterAnnotation extends AbstractParameterAnnotation {

    /**
     * @see AbstractParameterAnnotation#canProvideDefaultValue()
     * 
     * @return Always returns true
     */
    @Override
    protected boolean canProvideDefaultValue() {
        return true;
    }
    
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
            public TypedValue<String> getTypedValue() {
                final Object queryParam = request.getQueryParam(getParameterAnnotationMapping().getParameterName());
                if(queryParam == null) {
                    final Literal defaultLiteral = getParameterAnnotationMapping().getDefaultValue();
                    return new TypedValue<String>(){
                        @Override
                        public Type getType() {
                            return defaultLiteral.getType();
                        }

                        @Override
                        public String getValue() {
                           return defaultLiteral.getValue();
                        }
                    };
                } else if(queryParam instanceof String) {
                    return new TypedValue<String>(){
                        @Override
                        public Type getType() {
                            return Type.STRING;
                        }

                        @Override
                        public String getValue() {
                            return (String)queryParam;
                        }
                    };
                }
                
                //TODO cope with the situation whereby there may be more than a single value
                /*
                if(formField instanceof List) {
                    final List<String> fieldValues = (List<String>)formField;
                    final ValueSequence vals = new ValueSequence();
                    for(String fieldValue : fieldValues) {
                        vals.add(new StringValue(fieldValue));
                    }
                    
                    return vals;
                }*/
                
                return null;
            }
            
        };
    }

    //<editor-fold desc="Error Codes">
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamsErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidAnnotationParamsErr() {
        return RESTXQErrorCodes.RQST0020;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidKeyErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidParameterNameErr() {
        return RESTXQErrorCodes.RQST0021;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidValueErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidFunctionArgumentNameErr() {
        return RESTXQErrorCodes.RQST0022;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueErr()
     */
    @Override
    final protected RESTXQErrorCode getInvalidDefaultValueErr() {
        return RESTXQErrorCodes.RQST0023;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueTypeErr()
     */
    @Override
    protected RESTXQErrorCode getInvalidDefaultValueTypeErr() {
        return RESTXQErrorCodes.RQST0024;
    }
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamSyntaxErr()
     */
    @Override
    protected RESTXQErrorCode getInvalidAnnotationParametersSyntaxErr() {
        return RESTXQErrorCodes.RQST0025;
    }
    
    //</editor-fold>
}