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
import org.exquery.xquery.Literal;

/**
 * Serialization Media Type Annotation
 * i.e. %output:media-type
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class MediaTypeAnnotation extends AbstractSerializationAnnotation {
    
    private final static Pattern ptnMediaType = Pattern.compile("[a-z]+/[a-z\\-](?:(\\+[a-z\\-]+)?)");
    
    private String mediaType;
    
    /**
     * Ensures that the Media Type Annotation
     * is compatible with the Function Signature
     * and extracts the media type for later use
     * and ensures it is supported
     * 
     * @throws SerializationAnnotationException if the Media Type Annotation is not compatible
     * with the function signature or if the method is malformed or unsupported
     */
    @Override
    public void initialise() throws SerializationAnnotationException {
        super.initialise();
        this.mediaType = parseAnnotationValue();
    }
    
    public String getMediaType() {
        return mediaType;
    }
    
    private String parseAnnotationValue() throws SerializationAnnotationException {
        final Literal[] annotationValue = getLiterals();
        
        if(annotationValue.length > 1) {
            throw new SerializationAnnotationException(SerializationAnnotationErrorCodes.SEST0010);
        } else if(annotationValue.length != 1) {
            return null;
        } else {
            return parseMediaType(annotationValue[0]);
        }
    }

    private String parseMediaType(final Literal mediaType) throws SerializationAnnotationException {
        
        final String mediaTypeStr = mediaType.getValue();
        if(mediaTypeStr.isEmpty()) {
            throw new SerializationAnnotationException(SerializationAnnotationErrorCodes.SEST0011);
        }

        //validate the mediaTypeStr
        final Matcher mtcMediaType = ptnMediaType.matcher(mediaTypeStr);
        if(!mtcMediaType.matches()) {
            throw new SerializationAnnotationException(SerializationAnnotationErrorCodes.SEST0012);
        }

        return mediaTypeStr;
    }
}