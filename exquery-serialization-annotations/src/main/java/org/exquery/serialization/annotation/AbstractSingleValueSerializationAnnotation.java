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

import org.exquery.serialization.annotation.SerializationAnnotationErrorCodes.SerializationAnnotationErrorCode;
import org.exquery.xquery.Literal;

/**
 * Base class for EXQuery Serialization Annotation Implementations
 * which have a single string literal value
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractSingleValueSerializationAnnotation extends AbstractSerializationAnnotation {

    private String value;

    /**
     * Ensures that the Annotation
     * is compatible with the Function Signature
     * and extracts the value for later use
     * and ensures it is supported
     *
     * @throws org.exquery.serialization.annotation.SerializationAnnotationException if the Annotation is not compatible
     * with the function signature or if the value is malformed or unsupported
     */
    @Override
    public void initialise() throws SerializationAnnotationException {
        super.initialise();
        this.value = parseAnnotationValue();
    }

    public String getValue() {
        return value;
    }

    private String parseAnnotationValue() throws SerializationAnnotationException {
        final Literal[] annotationValue = getLiterals();

        if(annotationValue.length > 1) {
            throw new SerializationAnnotationException(getTooManyLiteralsErr());
        } else if(annotationValue.length != 1) {
            return null;
        } else {
            return parseValue(annotationValue[0]);
        }
    }

    protected String parseValue(final Literal value) throws SerializationAnnotationException {

        final String valueStr = value.getValue();
        if(valueStr.isEmpty()) {
            throw new SerializationAnnotationException(getEmptyAnnotationParamsErr());
        }

        //validate the encodingStr
        if(!validate(valueStr)) {
            throw new SerializationAnnotationException(getInvalidValueErr());
        }

        return valueStr;
    }

    /**
     * Validate the value extracted from the annotation parameter literal
     *
     * @param value The value extracted from the annotation
     *
     * @return true if value, false otherwise
     */
    protected abstract boolean validate(final String value);

    /**
     * Get the Error Code to use when the Annotation has too many Parameters
     *
     * @return The error code
     */
    protected abstract SerializationAnnotationErrorCode getTooManyLiteralsErr();

    /**
     * Get the Error Code to use when the Annotation Parameters are Empty
     *
     * @return The error code
     */
    protected abstract SerializationAnnotationErrorCode getEmptyAnnotationParamsErr();

    /**
     * Get the Error Code to use when the Annotation has an invalid value
     *
     * @return The error code
     */
    protected abstract SerializationAnnotationErrorCode getInvalidValueErr();

}
