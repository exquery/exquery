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
import org.exquery.http.ContentTypeHeader;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.ConsumesAnnotation;

/**
 * Implementation of RESTXQ Consumes Annotation
 * i.e. %rest:consumes
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ConsumesAnnotationImpl extends AbstractMediaTypeAnnotation implements ConsumesAnnotation {
    
    @Override
    public boolean matchesMediaType(final HttpRequest request) {
        final String contentType = request.getContentType();
        
        //if there is no contentType provided in the request then we cannot consume it!
        //TODO what about if we consume */* - perhaps that should not be allowed, as that is the same
        //as having no consumes annotation
        if(contentType == null) {
            return false;
        }
        
        final ContentTypeHeader contentTypeHeader = new ContentTypeHeader(contentType);
        
        final Matcher mtcConsumesContentType = getMediaTypesPatternMatcher().matcher(contentTypeHeader.getInternetMediaType());
        return mtcConsumesContentType.matches();
    }
    
    @Override
    protected RestXqErrorCode getEmptyAnnotationParamsErr() {
        return RestXqErrorCodes.RQST0027;
        
    }

    @Override
    protected RestXqErrorCode getInvalidMediaTypeLiteralErr() {
        return RestXqErrorCodes.RQST0028;
    }

    @Override
    protected RestXqErrorCode getInvalidMediaTypeErr() {
        return RestXqErrorCodes.RQST0029;
    }
}
