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
package org.exquery.restxq.annotation;

import java.util.Map;

/**
 * PathAnnotation
 * 
 * Represents the XQuery RESTXQ Annotation %rest:path
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface PathAnnotation extends ConstraintAnnotation {
    
    /**
     * Determines if the Path in this Path Annotation
     * would match the provided path
     * 
     * @param path The path to attempt to
     * match against this Path Annotation
     * 
     * @return true if the provided path would be matched
     * by this annotation, false otherwise
     */
    public boolean matchesPath(final String path);
    
    /**
     * Extracts the parameters of any URI Templates described
     * by this Path Annotation from the provided URI path
     * 
     * @param uriPath The URI path from which to extract templated parameters
     * 
     * @return A Map where the Key is the URI template name, and the Value is extracted from the path
     */
    public Map<String, String> extractPathParameters(final String uriPath); //TODO consider that the left side is a function parameter name?
    
    /**
     * Gets the number of Path Segments in the Path described by the Path Annotation
     * 
     * @return The total number of Path Segments
     */
    public int getPathSegmentCount();
}