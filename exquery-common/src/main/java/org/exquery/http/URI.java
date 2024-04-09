/*
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

/**
 * URI concepts from RFC 3986
 * 
 * Not intended as a replacement for java.net.URI,
 * rather it just contains declarations to assist
 * in processing URIs
 *
 * @see <a href="http://tools.ietf.org/html/rfc3986">RFC 3986: Uniform Resource Identifier (URI): Generic Syntax</a>
 * 
 * @author Adam Retter
 */
public interface URI {
    
    
    public final static char PATH_SEGMENT_DELIMITER = '/';
    
    /**
     * URI path segment valid characters from RFC 3986
     * 
     * pchar         = unreserved / pct-encoded / sub-delims / ":" / "@"
     * unreserved    = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * pct-encoded   = "%" HEXDIG HEXDIG
     * sub-delims    = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * 
     * @see <a href="http://tools.ietf.org/html/rfc3986#appendix-A">RFC 3986: Appendix A. Collected ABNF for URI</a>
     */
    
    /**
     * Regular Expression for matching 'unreserved' URI Path segment characters
     */
    public final static String unreserved_regExp = "[A-Za-z0-9\\-\\._~]";
    
    /**
     * Regular Expression for matching 'HEXDIG' URI Path segment characters
     */
    public final static String HEXDIG_regExp = "[A-F0-9]";
    
    /**
     * Regular Expression for matching 'pct-encoded' URI Path segment characters
     */
    public final static String pctEncoded_regExp = "%" + HEXDIG_regExp + "{2}";
    
    /**
     * Regular Expression for matching 'sub-delims' URI Path segment characters
     */
    public final static String subDelims_regExp = "[!\\$&'\\(\\)\\*\\+,;=]";
    
    /**
     * Regular Expression for matching and capturing 'pchar' URI Path segment characters
     */
    public final static String pchar_capturingRegExp = "(" + unreserved_regExp + "|" + pctEncoded_regExp + "|" + subDelims_regExp + "|[\\:@]" + ")+";
    
    /**
     * Regular Expression for matching 'pchar' URI Path segment characters
     */
    public final static String pchar_regExp = "(?:" + unreserved_regExp + "|" + pctEncoded_regExp + "|" + subDelims_regExp + "|[\\:@]" + ")+";
}
