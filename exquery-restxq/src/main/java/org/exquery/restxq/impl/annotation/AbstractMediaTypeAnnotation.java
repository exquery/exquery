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
import java.util.regex.Pattern;
import org.exquery.http.InternetMediaType;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.MediaTypeAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;

/**
 * Base class for RESTXQ Media Type Annotation Implementations
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractMediaTypeAnnotation extends AbstractRestAnnotation implements MediaTypeAnnotation {
    
    //Regular Expression to match any Internet Media Type
    private final static Pattern ptnMediaType = Pattern.compile(InternetMediaType.mediaType_regExp);
    
    private Pattern ptnMatchMediaTypes;
    
    /**
     * Checks that the Parameter Annotation is compatible
     * with the function which it annotates
     *
     * @throws RestAnnotationException if the Parameter could not be parsed
     */
    @Override
    public void initialise() throws RestAnnotationException {
        super.initialise();
        this.ptnMatchMediaTypes = parseAnnotationValue();
    }
    
    protected Pattern getMediaTypesPatternMatcher() {
        return ptnMatchMediaTypes;
    }
    
    /**
     * Parses the Media Type Annotation Value
     * 
     * @return The RegularExpression describing the media types against which a media type may be matched
     * @throws RestAnnotationException if the media type annotations values are invalid
     */
    protected Pattern parseAnnotationValue() throws RestAnnotationException {
        final Literal[] annotationLiterals = getLiterals();
        
        if(annotationLiterals.length == 0) {
            throw new RestAnnotationException(getEmptyAnnotationParamsErr());
        }
        
        return parseAnnotationLiterals(annotationLiterals);
    }
    
    /**
     * Parses the Media Type Annotations Literal Values
     * 
     * @param mediaTypesLiterals The literals of the Media Type annotation
     * 
     * @return The RegularExpression describing the media types against which a media type may be matched
     * 
     * @throws RestAnnotationException if the media type annotations values are invalid
     */
    protected Pattern parseAnnotationLiterals(final Literal mediaTypesLiterals[]) throws RestAnnotationException {

        Matcher mtcMediaType = null;
        
        final StringBuilder builder = new StringBuilder();
        
        for(final Literal mediaTypeLiteral : mediaTypesLiterals) {
        
            if(mediaTypeLiteral.getType() != Type.STRING) {
                throw new RestAnnotationException(getInvalidMediaTypeLiteralErr());
            }
        
            final String mediaType = mediaTypeLiteral.getValue();
            if(mediaType.isEmpty()) {
                throw new RestAnnotationException(getInvalidMediaTypeErr());
            }
            
            if(mtcMediaType == null) {
                mtcMediaType = ptnMediaType.matcher(mediaType);
            } else {
                mtcMediaType = mtcMediaType.reset(mediaType);
            }
            
            if(!mtcMediaType.matches()) {
                throw new RestAnnotationException(getInvalidMediaTypeErr());
            }
            
            //add to match pattern
            if(builder.length() != 0) {
                builder.append("|");
            }
            builder.append("(?:(");
            builder.append(encodeAsRegExp(mediaType));
            builder.append("))");
        }
        
        return Pattern.compile(builder.toString());
    }
    
    private String encodeAsRegExp(String mediaType) {
        
        //expand subtype wildcard to valid regexp
        mediaType = mediaType.replace("*", InternetMediaType.subtypeName_regExp);
        
        //escape chars in an Internet Media Type that have significance in a regexp
        mediaType = mediaType.replace("$", "\\$");
        mediaType = mediaType.replace(".", "\\.");
        mediaType = mediaType.replace("+", "\\+");
        mediaType = mediaType.replace("-", "\\-");
        mediaType = mediaType.replace("^", "\\^");
        
        return mediaType;
    }

    protected abstract RestXqErrorCode getEmptyAnnotationParamsErr();

    protected abstract RestXqErrorCode getInvalidMediaTypeLiteralErr();

    protected abstract RestXqErrorCode getInvalidMediaTypeErr();
}
