/*
Copyright (c) 2013, Adam Retter
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
package org.exquery.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of a HTTP Accept header
 * 
 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class AcceptHeader {
    
    //accept-params components
    private final static char PARAMETER_SEPARATOR = ';';
    private final static char QUALITY_PARAMETER = 'q';
    private final static char PARAMETER_KEY_VALUE_SEPARATOR = '=';
    private final static String QUALITY_FACTOR_REGEX = "(?:0(?:\\.[0-9]{1,3})?)|(?:1(?:\\.[0]{1,3})?)";
    private final static String QUALITY_PARAM_REGEX = PARAMETER_SEPARATOR + "\\s?" + QUALITY_PARAMETER + PARAMETER_KEY_VALUE_SEPARATOR + QUALITY_FACTOR_REGEX;
    
    //accept-extension components
    private final static String TOKEN_REGEX = "[a-z]+";
    private final static String QUOTED_STRING = "\"[a-z0-9]+\"";
    private final static String EXTENSION_PARAM_REGEX = PARAMETER_SEPARATOR + "\\s?" + TOKEN_REGEX + PARAMETER_KEY_VALUE_SEPARATOR + TOKEN_REGEX + "|(?:" + QUOTED_STRING + ")";
    
    private final static String acceptParams_regExp = "(?:" + QUALITY_PARAM_REGEX + ")?\\s?(?:" + EXTENSION_PARAM_REGEX + ")?";
    
    public final static char WILDCARD = '*';
    
    public final static String anyRange_regExp = "\\" + WILDCARD + "\\" + InternetMediaType.subtypeSeparator + "\\" + WILDCARD;
    public final static String anySubtype_regExp = "\\" + WILDCARD + "\\" + InternetMediaType.subtypeSeparator + "\\" + InternetMediaType.subtypeName_regExp;
    
    public final static String accept_regExp = "(?:(?:" + anyRange_regExp + ")|(?:" + anySubtype_regExp + ")|(?:" + InternetMediaType.mediaType_regExp + "))" + acceptParams_regExp;
    public final static Pattern ptnAccept = Pattern.compile(accept_regExp);
    
    public final static String accepts_regExp = "(" + accept_regExp + ")(,\\s?" + accept_regExp + ")*";
    public final static Pattern ptnAccepts = Pattern.compile(accepts_regExp);
    
    private final List<Accept> accepts = new ArrayList<Accept>();
    
    /**
     * @param headerValue The value of the HTTP Accept header
     * 
     * @throws IllegalArgumentException If the headerValue is not a valid value for an Accept header
     */
    public AcceptHeader(final String headerValue) {
        final Matcher mtcAccepts = ptnAccepts.matcher(headerValue);
        if(!mtcAccepts.matches()) {
            throw new IllegalArgumentException("Invalid Accept Header Value: '" + headerValue + "' in respect to pattern: '" + ptnAccepts.pattern() + "'");
        } else {
            final Matcher mtcAccept = ptnAccept.matcher(headerValue);
            while(mtcAccept.find()) {
               final String acceptStr = mtcAccept.group();
               if(acceptStr != null) {
                    final Accept accept;
                    if(acceptStr.indexOf(PARAMETER_SEPARATOR) == -1) {
                        accept = new Accept(acceptStr.trim());
                    } else {
                        //break an accept group in the value of the accept header into parts
                        final String parts[] = acceptStr.split(String.valueOf(PARAMETER_SEPARATOR));
                        final String mediaRange = parts[0];

                        //1st part will be the mediaRange
                        //2nd part maybe the qualityFactor or extension
                        if(parts[1].trim().startsWith(String.valueOf(QUALITY_PARAMETER) + PARAMETER_KEY_VALUE_SEPARATOR)) {
                            //2nd part is qualityFactor
                            final float qualityFactor = Float.parseFloat(parts[1].trim().substring(parts[1].trim().indexOf(PARAMETER_KEY_VALUE_SEPARATOR) + 1));

                            //if 2nd part is qualityFactor, 3rd part maybe extension
                            if(parts.length == 3) {
                                final String extension[] = parts[2].trim().split(String.valueOf(PARAMETER_KEY_VALUE_SEPARATOR));
                                accept = new Accept(mediaRange, qualityFactor, new Accept.Extension(extension[0], extension[1]));
                            } else {
                                accept = new Accept(mediaRange, qualityFactor);
                            }

                        } else {
                            //2nd part is extension
                            final String extension[] = parts[1].trim().split(String.valueOf(PARAMETER_KEY_VALUE_SEPARATOR));
                            accept = new Accept(mediaRange, new Accept.Extension(extension[0], extension[1]));
                        }
                    }
                    accepts.add(accept);
               }
           }
        }
        
        //sort accepts by qualityFactor
        Collections.sort(accepts);
    }

    public List<Accept> getAccepts() {
        return accepts;
    }
    
    public static class Accept implements Comparable<Accept> {
        
        private final static float DEFAULT_QUALITY_FACTOR = 1;
        
        final String mediaRange;
        final float qualityFactor;
        final Accept.Extension extension;

        public Accept(final String mediaRange) {
            this(mediaRange, DEFAULT_QUALITY_FACTOR);
        }
        
        public Accept(final org.exquery.InternetMediaType internetMediaType) {
            this(internetMediaType.getMediaType());
        }
        
        public Accept(final String mediaRange, final float qualityFactor) {
            this(mediaRange, qualityFactor, null);
        }
        
        public Accept(final org.exquery.InternetMediaType internetMediaType, final float qualityFactor) {
            this(internetMediaType.getMediaType(), qualityFactor);
        }
        
        public Accept(final String mediaRange, final Accept.Extension extension) {
            this(mediaRange, DEFAULT_QUALITY_FACTOR, extension);
        }
        
        public Accept(final String mediaRange, final float qualityFactor, final Accept.Extension extension) {
            this.mediaRange = mediaRange;
            this.qualityFactor = qualityFactor;
            this.extension = extension;
        }
        
        @Override
        public int compareTo(final Accept other) {
            return Math.round(other.qualityFactor * 10) - Math.round(qualityFactor * 10);
        }

        public String getMediaRange() {
            return mediaRange;
        }

        public float getQualityFactor() {
            return qualityFactor;
        }

        public Accept.Extension getExtension() {
            return extension;
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(getMediaRange());
            
            if(getQualityFactor() != DEFAULT_QUALITY_FACTOR) {
                builder.append(PARAMETER_SEPARATOR);
                builder.append(QUALITY_PARAMETER);
                builder.append(PARAMETER_KEY_VALUE_SEPARATOR);
                builder.append(getQualityFactor());
            }
            
            if(getExtension() != null) {
                builder.append(PARAMETER_SEPARATOR);
                builder.append(getExtension().toString());
            }
            
            return builder.toString();
        }
        
        @Override
        public boolean equals(final Object other) {
            final boolean equals;
            if(other != null && other instanceof Accept) {
                final Accept otherAccept = (Accept)other;
                equals = otherAccept.getMediaRange().equals(getMediaRange()) &&
                        otherAccept.getQualityFactor() == getQualityFactor() &&
                        otherAccept.getExtension() == getExtension();
            } else {
                equals = false;
            }
            
            return equals;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 23 * hash + (this.mediaRange != null ? this.mediaRange.hashCode() : 0);
            hash = 23 * hash + Float.floatToIntBits(this.qualityFactor);
            hash = 23 * hash + (this.extension != null ? this.extension.hashCode() : 0);
            return hash;
        }
        
        
        public static class Extension {
            final String name;
            final String value;

            public Extension(final String name, final String value) {
                this.name = name;
                this.value = value;
            }

            public String getName() {
                return name;
            }

            public String getValue() {
                return value;
            }
            
            @Override
            public String toString() {
                return name + PARAMETER_KEY_VALUE_SEPARATOR + value;
            }
            
            @Override
            public boolean equals(final Object other) {
                final boolean equals;
                if(other != null && other instanceof Extension) {
                    final Extension otherExtension = ((Extension)other);
                    equals = otherExtension.getName().equals(getName()) &&
                            otherExtension.getValue().equals(getValue());
                } else {
                    equals = false;
                }
                return equals;
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
                hash = 61 * hash + (this.value != null ? this.value.hashCode() : 0);
                return hash;
            }
        }
    }
}
