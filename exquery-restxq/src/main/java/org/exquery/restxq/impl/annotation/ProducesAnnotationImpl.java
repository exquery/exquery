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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exquery.http.AcceptHeader;
import org.exquery.http.AcceptHeader.Accept;
import org.exquery.http.HttpHeaderName;
import org.exquery.http.HttpRequest;
import org.exquery.http.InternetMediaType;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.ProducesAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;

/**
 * Implementation of RESTXQ Produces Annotation
 * i.e. %rest:produces
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ProducesAnnotationImpl extends AbstractMediaTypeAnnotation implements ProducesAnnotation {
    
    //Regular Expression to match any Internet Media Type
    private final static Pattern ptnMediaType = Pattern.compile(InternetMediaType.mediaType_regExp);
    
    private Set<String> internetMediaTypes;
    
    /**
     * Checks that the Parameter Annotation is compatible
     * with the function which it annotates
     *
     * @throws RestAnnotationException if the Parameter could not be parsed
     */
    @Override
    public void initialise() throws RestAnnotationException {
        super.initialise();
        this.internetMediaTypes = parseAnnotationValue();
    }
    
    /**
     * Parses the Media Type Annotation Value
     * 
     * @return The media type which we will produce
     * @throws RestAnnotationException if the media type annotations values are invalid
     */
    //protected Set<String> parseAnnotationValue() throws RestAnnotationException {
    protected Set<String> parseAnnotationValue() throws RestAnnotationException {
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
    protected Set<String> parseAnnotationLiterals(final Literal mediaTypesLiterals[]) throws RestAnnotationException {

        Matcher mtcMediaType = null;
        
        final Set<String> mediaTypes = new HashSet<String>();
        
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
            
            mediaTypes.add(mediaType);
        }
        
        return mediaTypes;
    }
    
    @Override
    public boolean matchesMediaType(final HttpRequest request) {
        String acceptHeaderValue = request.getHeader(HttpHeaderName.Accept.toString());

        if (acceptHeaderValue == null)
            acceptHeaderValue = "*/*";

        return matchesMediaType(acceptHeaderValue);
    }
    
    @Override
    public boolean matchesMediaType(final String mediaType) {
        
        final AcceptHeader acceptHeader = new AcceptHeader(mediaType);
        for(final Accept accept : acceptHeader.getAccepts()) {
        
            final Pattern pEncodedMediaType = Pattern.compile(encodeAsRegExp(accept.getMediaRange()));
            Matcher mtcMediaType = null;

            for(final String internetMediaType : internetMediaTypes) {
                if(mtcMediaType == null) {
                    mtcMediaType = pEncodedMediaType.matcher(internetMediaType);
                } else {
                    mtcMediaType = mtcMediaType.reset(internetMediaType);
                }

                if(mtcMediaType.matches()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    protected RestXqErrorCode getEmptyAnnotationParamsErr() {
        return RestXqErrorCodes.RQST0030;
    }

    @Override
    protected RestXqErrorCode getInvalidMediaTypeLiteralErr() {
        return RestXqErrorCodes.RQST0031;
    }

    @Override
    protected RestXqErrorCode getInvalidMediaTypeErr() {
        return RestXqErrorCodes.RQST0032;
    }
}
