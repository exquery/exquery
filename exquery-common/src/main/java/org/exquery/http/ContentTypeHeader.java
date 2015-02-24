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
package org.exquery.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of a HTTP Content-Type header
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ContentTypeHeader {
    
    //TODO dont just support charset parameter, add further support for parameters of media types - see http://tools.ietf.org/html/rfc2231#section-7
    
    private final static String CHARSET_SEPARATOR = ";";
    private final static String CHARSET_KEY = "charset";
    private final static String CHARSET_KEY_VALUE_SEPARATOR = "=";

    public final static String contentType_regExp =  "(" + InternetMediaType.mediaType_regExp + ")" + "(" + CHARSET_SEPARATOR + "\\s*" + CHARSET_KEY + CHARSET_KEY_VALUE_SEPARATOR + "(.+))?";
    public final static Pattern ptnContentType = Pattern.compile(contentType_regExp);

    private final static Pattern ptnInternetMediaType = Pattern.compile(InternetMediaType.mediaType_regExp);
    
    private final String internetMediaType;
    private final String charset;
    
    /**
     * @param headerValue The value of the HTTP Content-Type header
     * 
     * @throws IllegalArgumentException If the headerValue is not a valid value for a Content-Type header
     */
    public ContentTypeHeader(final String headerValue) throws IllegalArgumentException {
        final Matcher mtcContentType = ptnContentType.matcher(headerValue);
        if(!mtcContentType.matches()) {
            throw new IllegalArgumentException("Invalid Content-Type Header Value: '" + headerValue + "' in respect to pattern: '" + ptnContentType.pattern() + "'");
        } else {
            this.internetMediaType = mtcContentType.group(1);
            if(mtcContentType.groupCount() == 3) {
                this.charset = mtcContentType.group(3);
            } else {
                this.charset = null;
            }
        }
    }

    /**
     * @param internetMediaType The Internet Media Type to use in the Content-Type header
     * @param charset The Charset to use in the Content-Type header or null otherwise
     *
     * @throws IllegalArgumentException If the internetMediaType is not a valid value for an Internet Media Type
     */
    public ContentTypeHeader(final String internetMediaType, final String charset) {
        final Matcher mtcInternetMediaType = ptnInternetMediaType.matcher(internetMediaType);
        if(!mtcInternetMediaType.matches()) {
            throw new IllegalArgumentException("Invalid Internet Media Type value: '" + internetMediaType + "' in respect to pattern: '" + ptnInternetMediaType.pattern() + "'");
        } else {
            this.internetMediaType = internetMediaType;
            this.charset = charset;
        }
    }
    
    /**
     * Returns the Internet Media Type component of the ContentType header
     * 
     * @return The Internet Media Type
     */
    public String getInternetMediaType() {
        return internetMediaType;
    }

    /**
     * Returns the charset component of the ContentType header
     * 
     * @return The charset component, or null if the component is not present
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Returns a string representation as
     * would be used for the value of a HTTP
     * Content-Type Header
     *
     * @return value for a HTTP Content-Type header
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getInternetMediaType());
        if(charset != null && !charset.isEmpty()) {
            builder
                .append(CHARSET_SEPARATOR)
                .append(" ")
                .append(CHARSET_KEY).append(CHARSET_KEY_VALUE_SEPARATOR).append(charset);
        }
        return builder.toString();
    }
}
