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
package org.exquery.serialization.annotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exquery.annotation.AbstractAnnotation;
import org.exquery.xquery.Literal;

/**
 * Serialization Method Annotation
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class MethodAnnotation extends AbstractAnnotation<SerializationAnnotationException> implements SerializationAnnotation {
    
    private String method;
    
    /**
     * 
     */
    @Override
    public void initialise() throws SerializationAnnotationException {
        super.initialise();
        this.method = parseAnnotationValue();
    }
    
    public String getMethod() {
        return method;
    }
    
    private String parseAnnotationValue() throws SerializationAnnotationException {
        final Literal[] annotationValue = getLiterals();
        
        if(annotationValue.length > 1) {
            throw new SerializationAnnotationException(SerializationAnnotationErrorCodes.SEST0001);
        } else if(annotationValue.length != 1) {
            return null;
        } else {
            return parseMethod(annotationValue[0]);
        }
    }

    private String parseMethod(final Literal methodValue) throws SerializationAnnotationException {
        
        final String methodStr = methodValue.getValue();
        if(methodStr.isEmpty()) {
            throw new SerializationAnnotationException(SerializationAnnotationErrorCodes.SEST0002);
        }

        //validate the methodStr
        final Matcher mtcOutputMethod = SupportedMethod.getPattern().matcher(methodStr);
        if(!mtcOutputMethod.matches()) {
            throw new SerializationAnnotationException(SerializationAnnotationErrorCodes.SEST0003);
        }

        return methodStr;
    }
    
    /**
     * The Serialization Methods
     * supported by this Implementation
     */
    public static enum SupportedMethod {
        xml,
        xhtml,
        html5,
        json,
        text,
        binary;

        public static Pattern getPattern() {
            final SupportedMethod supportedMethods[] = values();
            final StringBuilder builder = new StringBuilder();
            for(int i = 0; i < supportedMethods.length; i++) {
                if(i > 0) {
                    builder.append("|");
                }
                builder.append(supportedMethods[i].toString());
            }
            return Pattern.compile(builder.toString());
        }
    }
}