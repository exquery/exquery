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
package org.exquery;

import static org.exquery.InternetMediaType.Type.APPLICATION;
import static org.exquery.InternetMediaType.Type.TEXT;

/**
 * Some common Internet Media Types
 *
 * @author Adam Retter
 */
public enum InternetMediaType {
    
    APPLICATION_OCTET_STREAM(APPLICATION, "octet-stream"),
    APPLICATION_JSON(APPLICATION, "json"),
    
    APPLICATION_XML(APPLICATION, "xml"),
    APPLICATION_XHTML_XML(APPLICATION, "xhtml+xml"),
    
    APPLICATION_PDF(APPLICATION, "pdf"),
    
    APPLICATION_ANY(APPLICATION),
    
    TEXT_HTML(TEXT, "html"),
    TEXT_PLAIN(TEXT, "plain"),
    
    ANY("*");   //would be nice to use ANY(WILDCARD) here, but it would be a compiler error due to the forward reference
    
    public final static char SUBTYPE_DELIMITER = '/';
    public final static char WILDCARD = '*';
    
    private final String typeName;
    private final String subTypeName;
    
    InternetMediaType(final Type type, final String subTypeName) {
        this.typeName = type.getName();
        this.subTypeName = subTypeName;
    }
    
    InternetMediaType(final String typeName, final String subTypeName) {
        this.typeName = typeName;
        this.subTypeName = subTypeName;
    }
    
    InternetMediaType(final Type type) {
        this.typeName = type.getName();
        this.subTypeName = String.valueOf(WILDCARD);
    }
    
    InternetMediaType(final String typeName) {
        this.typeName = typeName;
        this.subTypeName = String.valueOf(WILDCARD);
    }
    
    public final String getMediaType() {
        return typeName + SUBTYPE_DELIMITER + subTypeName;
    }
    
    public enum Type {
        APPLICATION("application"),
        TEXT("text");
        
        final String name;
        Type(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
}