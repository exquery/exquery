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
package org.exquery.restxq.annotation;

import org.exquery.http.HttpRequest;

/**
 * Media Type Annotations restrict the scope of REST Requests
 * to which a Resource Function can be applied based on
 * Internet Media Type properties of the Request
 * 
 * Base interface for the XQuery RESTXQ Annotations %rest:consumes and %rest:produces
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface MediaTypeAnnotation extends ConstraintAnnotation {
    
    /**
     * Determines whether the Http Request matches
     * the Media Type Annotation
     * 
     * @param mediaType the Internet Media Type to check against
     * the Media Type Annotation constraints
     * 
     * @return true if the media type matches, false otherwise
     */
    public boolean matchesMediaType(final String mediaType);
    
    /**
     * Determines whether the Http Request matches
     * the Media Type Annotation
     * 
     * @param request the Http Request to check against
     * the Media Type Annotation constraints
     * 
     * @return true if the request matches, false otherwise
     */
    public boolean matchesMediaType(final HttpRequest request);
}
