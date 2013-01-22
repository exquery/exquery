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
import org.exquery.http.HttpHeaderName;
import org.exquery.http.HttpRequest;
import org.exquery.http.InternetMediaType;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.ProducesAnnotation;

/**
 * Implementation of RESTXQ Produces Annotation
 * i.e. %rest:produces
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ProducesAnnotationImpl extends AbstractMediaTypeAnnotation implements ProducesAnnotation {
    
    @Override
    public boolean matchesMediaType(final HttpRequest request) {
        final String acceptHeader = request.getHeader(HttpHeaderName.Accept.toString());
        
        if(acceptHeader.equals(org.exquery.InternetMediaType.ANY.getMediaType())) {
            return true;
        }
        
        Matcher mtcProducesContentType = null;
        final String accepts[] = acceptHeader.split("\\,");
        for(String accept : accepts) {
            
            //to lowercase as media types are case insensitive
            //and all our processing is done in lower case
            accept = accept.toLowerCase();
            
            //remove whitespace
            accept = accept.replaceAll("\\s", "");
            
            //strip out qvalue and extensions
            final int idxExt = accept.indexOf(";");
            if(idxExt > -1) {
                accept = accept.substring(0, idxExt);
            }
            
            //replace "/*" in media type with "/any" to enable matching against our regexp
            accept = accept.replaceFirst(InternetMediaType.subtypeSeparator + "\\*", InternetMediaType.subtypeSeparator + "any");
            
            if(mtcProducesContentType == null) {
                mtcProducesContentType = getMediaTypesPatternMatcher().matcher(accept);
            } else {
                mtcProducesContentType = mtcProducesContentType.reset(accept);
            }
            
            if(mtcProducesContentType.matches()) {
                return true;
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
