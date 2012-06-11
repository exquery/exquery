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

import java.io.InputStream;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Sequence;
import org.exquery.xquery.Type;
import org.exquery.xquery.TypedArgumentValue;
import org.exquery.xquery.TypedValue;
import org.exquery.xquery.impl.SequenceImpl;
import org.exquery.xquery.impl.StringValue;

/**
 * Implementation of RESTXQ Form Parameter Annotation
 * i.e. %rest:form-param
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class FormParameterAnnotation extends AbstractParameterAnnotation {
    
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
    public TypedArgumentValue extractParameter(final HttpRequest request) {
        
        
        /* Taken from section 6.1, page 31 of the JAX-RS JSR-311 specification:
         * 
         * Servlet ﬁlters may trigger consumption of a request body by accessing request parameters. In a servlet
         * container the @FormParam annotation and the standard entity provider for application/x-www-form--
         * urlencoded MUST obtain their values from the servlet request parameters if the request body has already
         * been consumed. Servlet APIs do not differentiate between parameters in the URI and body of a request so
         * URI-based query parameters may be included in the entity parameter
         */
        
        return new TypedArgumentValue() {

            @Override
            public String getArgumentName() {
                return getParameterAnnotationMapping().getFunctionArgumentName();
            }

            @Override
            public Sequence getTypedValue() {
                final Object formParam = request.getFormParam(getParameterAnnotationMapping().getParameterName());
                if(formParam == null) {
                    final Literal defaultLiteral = getParameterAnnotationMapping().getDefaultValue();
                    return new SequenceImpl(new StringValue(defaultLiteral.getValue()));
                }
                
                if(formParam instanceof String) {
                    return new SequenceImpl(new StringValue((String)formParam));
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
                
                if(formParam instanceof InputStream) {
                    /*try {
                        return BinaryValueFromInputStream.getInstance(context, new Base64BinaryValueType(), (InputStream)formParam);
                    } catch(XPathException xpe) {
                        //TODO log
                        return null;
                    }*/
                    return new SequenceImpl<InputStream>(new TypedValue<InputStream>(){
                        @Override
                        public Type getType() {
                            return Type.BASE64_BINARY;
                        }

                        @Override
                        public InputStream getValue() {
                            return (InputStream)formParam;
                        }
                    });
                }
                
                return null;
            }
        };
    }

    //<editor-fold desc="Error Codes">
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamsErr()
     */
    @Override
    protected RestXqErrorCode getInvalidAnnotationParamsErr() {
        return RestXqErrorCodes.RQST0014;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidKeyErr()
     */
    @Override
    protected RestXqErrorCode getInvalidParameterNameErr() {
        return RestXqErrorCodes.RQST0015;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidValueErr()
     */
    @Override
    protected RestXqErrorCode getInvalidFunctionArgumentNameErr() {
        return RestXqErrorCodes.RQST0016;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueErr()
     */
    @Override
    protected RestXqErrorCode getInvalidDefaultValueErr() {
        return RestXqErrorCodes.RQST0017;
    }

    /**
     * @see AbstractParameterAnnotation#getInvalidDefaultValueTypeErr()
     */
    @Override
    protected RestXqErrorCode getInvalidDefaultValueTypeErr() {
        return RestXqErrorCodes.RQST0018;
    }
    
    /**
     * @see AbstractParameterAnnotation#getInvalidAnnotationParamSyntaxErr()
     */
    @Override
    protected RestXqErrorCode getInvalidAnnotationParametersSyntaxErr() {
        return RestXqErrorCodes.RQST0019;
    }
    
    //<editor-fold>
}