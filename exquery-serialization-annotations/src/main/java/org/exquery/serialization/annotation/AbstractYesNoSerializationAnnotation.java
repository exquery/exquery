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
package org.exquery.serialization.annotation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exquery.serialization.annotation.SerializationAnnotationErrorCodes.SerializationAnnotationErrorCode;
import org.exquery.xquery.Literal;

/**
 * Base class for EXQuery Serialization Annotation Implementations
 * whoose value is either "yes" or "no"
 *
 * @author Adam Retter
 */
public abstract class AbstractYesNoSerializationAnnotation extends AbstractSerializationAnnotation {
    
    private final static String YES = "yes";
    private final static String NO = "no";
    
    private final static Pattern ptnYesNo = Pattern.compile(YES + "|" + NO);
    
    private boolean yes;
    
    /**
     * Ensures that the yes/no Annotation
     * is compatible with the Function Signature
     * and extracts the yes or no value for later use
     * and ensures it is supported
     * 
     * @throws SerializationAnnotationException if the yes/no Annotation is not compatible
     * with the function signature or if the yes/no is malformed or unsupported
     */
    @Override
    public void initialise() throws SerializationAnnotationException {
        super.initialise();
        this.yes = parseAnnotationValue();
    }
    
    public boolean isYes() {
        return yes;
    }
    
    public String getStringValue() {
        final String stringValue;
        if(yes) {
            stringValue = YES;
        } else {
            stringValue = NO;
        }
        return stringValue;
    }
    
    private boolean parseAnnotationValue() throws SerializationAnnotationException {
        final Literal[] annotationValue = getLiterals();
        
        if(annotationValue.length > 1) {
            throw new SerializationAnnotationException(getAnnotationParamsCardinalityErr());
        } else if(annotationValue.length == 0) {
            throw new SerializationAnnotationException(getAnnotationMissingParamsErr());
        } else {
            return parseYesNoParam(annotationValue[0]);
        }
    }
    
    private boolean parseYesNoParam(final Literal yesNoValue) throws SerializationAnnotationException {
        
        final String yesNoStr = yesNoValue.getValue();
        if(yesNoStr.isEmpty()) {
            throw new SerializationAnnotationException(getAnnotationMissingParamsErr());
        }

        //validate the methodStr
        final Matcher mtcYesNo = ptnYesNo.matcher(yesNoStr);
        if(!mtcYesNo.matches()) {
            throw new SerializationAnnotationException(getInvalidAnnotationParamsErr());
        }

        return yesNoStr.equals(YES);
    }
    
    /**
     * Get the Error Code for Annotation Parameters with the wrong cardinality
     * 
     * @return The ErrorCode for Annotation Parameters with the wrong cardinality
     */
    protected abstract SerializationAnnotationErrorCode getAnnotationParamsCardinalityErr();
    
    /**
     * Get the Error Code for missing Annotation Parameters
     * 
     * @return The ErrorCode for missing Annotation Parameters
     */
    protected abstract SerializationAnnotationErrorCode getAnnotationMissingParamsErr();
    
    /**
     * Get the Error Code for invalid Annotation Parameters
     * 
     * @return The ErrorCode for invalid Annotation Parameters
     */
    protected abstract SerializationAnnotationErrorCode getInvalidAnnotationParamsErr();
}
