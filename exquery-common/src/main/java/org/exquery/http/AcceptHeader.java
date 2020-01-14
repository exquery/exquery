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

import java.util.*;

/**
 * Representation of a HTTP Accept header
 * 
 * <a href="https://tools.ietf.org/html/rfc7231#section-5.3.2">RFC 7231</a>
 *
 * @author Adam Retter
 */
public class AcceptHeader {

    private final static char PARAMETER_SEPARATOR = ';';
    private final static char QUALITY_PARAMETER = 'q';
    private final static char PARAMETER_KEY_VALUE_SEPARATOR = '=';

    private final List<Accept> accepts;
    
    /**
     * @param headerValue The value of the HTTP Accept header
     * 
     * @throws IllegalArgumentException If the headerValue is not a valid value for an Accept header
     */
    public AcceptHeader(final String headerValue) {
        accepts = AcceptHeaderParser.parse(headerValue);

        //sort accepts by qualityFactor
        Collections.sort(accepts);
    }

    public List<Accept> getAccepts() {
        return accepts;
    }
    
    public static class Accept implements Comparable<Accept> {

        private final static float DEFAULT_QUALITY_FACTOR = 1;
        
        final String mediaRange;
        /* @Nullable */ final Parameter[] parameters;
        /* @Nullable */ final Weight weight;

        public Accept(final String mediaRange) {
            this(mediaRange, (Weight)null);
        }
        
        public Accept(final org.exquery.InternetMediaType internetMediaType) {
            this(internetMediaType.getMediaType());
        }

        public Accept(final org.exquery.InternetMediaType internetMediaType, final Weight weight) {
            this(internetMediaType.getMediaType(), weight);
        }

        public Accept(final org.exquery.InternetMediaType internetMediaType, final Parameter[] parameters) {
            this(internetMediaType.getMediaType(), parameters);
        }
        
        public Accept(final String mediaRange, final Weight weight) {
            this(mediaRange, null, weight);
        }

        public Accept(final String mediaRange, final Parameter[] parameters) {
            this(mediaRange, parameters, null);
        }
        
        public Accept(final org.exquery.InternetMediaType internetMediaType, final float qualityFactor) {
            this(internetMediaType.getMediaType(), new Weight(qualityFactor));
        }

        public Accept(final org.exquery.InternetMediaType internetMediaType, final int qualityFactor) {
            this(internetMediaType.getMediaType(), new Weight(qualityFactor));
        }

        public Accept(final org.exquery.InternetMediaType internetMediaType, final Parameter[] parameters, final Weight weight) {
            this(internetMediaType.getMediaType(), parameters, weight);
        }

        public Accept(final String mediaRange, final Parameter[] parameters, final Weight weight) {
            this.mediaRange = mediaRange;
            this.parameters = parameters;
            this.weight = weight;
        }
        
        @Override
        public int compareTo(final Accept other) {
            final float thisQf = weight == null ? DEFAULT_QUALITY_FACTOR : weight.qvalue;
            final float otherQf = other.weight == null ? DEFAULT_QUALITY_FACTOR : other.weight.qvalue;
            int c = Float.compare(otherQf, thisQf);
            if (c == 0) {
                c = mediaRange.compareTo(other.mediaRange);
            }
            return c;
//            return Math.round(otherQf * 10) - Math.round(thisQf * 10);
        }

        public String getMediaRange() {
            return mediaRange;
        }

        public float getQualityFactor() {
            return weight == null ? DEFAULT_QUALITY_FACTOR : weight.qvalue;
        }

        public Accept.Parameter[] getParameters() {
            return parameters;
        }

        public Accept.Weight getWeight() {
            return weight;
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(getMediaRange());
            
            if (getParameters() != null) {
                for (final Accept.Parameter parameter : parameters) {
                    builder.append(PARAMETER_SEPARATOR);
                    builder.append(parameter.toString());
                }
            }

            if (getWeight() != null) {
                builder.append(PARAMETER_SEPARATOR);
                builder.append(weight.toString());
            }
            
            return builder.toString();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            final Accept otherAccept = (Accept) other;
            return mediaRange.equals(otherAccept.mediaRange) &&
                    Arrays.equals(parameters, otherAccept.parameters) &&
                    (weight == otherAccept.weight || (weight != null && weight.equals(otherAccept.weight)));
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + this.mediaRange.hashCode();
            result = 31 * result + Arrays.hashCode(parameters);
            result = 31 * result + (weight != null ? weight.hashCode() : 0);
            return result;
        }

        public static class Parameter {
            final String name;
            final String value;

            public Parameter(final String name, final String value) {
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
                if (this == other) {
                    return true;
                }
                if (other == null || getClass() != other.getClass()) {
                    return false;
                }
                final Parameter otherParameter = (Parameter) other;
                return name.equals(otherParameter.name) &&
                        value.equals(otherParameter.value);
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 61 * hash + this.name.hashCode();
                hash = 61 * hash + this.value.hashCode();
                return hash;
            }
        }

        public static class Weight {
            final float qvalue;
            final boolean intQValue;
            final AcceptExt[] acceptExts;

            public Weight (final float qvalue) {
                this(qvalue, null);
            }

            public Weight (final int qvalue) {
                this(qvalue, null);
            }

            public Weight (final int qvalue, final AcceptExt[] acceptExts) {
                this(qvalue, true, acceptExts);
            }

            public Weight (final float qvalue, final AcceptExt[] acceptExts) {
                this(qvalue, false, acceptExts);
            }

            private Weight (final float qvalue, final boolean intQValue, final AcceptExt[] acceptExts) {
                this.qvalue = qvalue;
                this.intQValue = intQValue;
                this.acceptExts = acceptExts;
            }

            @Override
            public String toString() {
               if (acceptExts == null) {
                   if (intQValue) {
                       return "q=" + (int)qvalue;
                   } else {
                       return "q=" + qvalue;
                   }
               } else {
                   final StringBuilder builder = new StringBuilder();
                   builder.append("q=");
                   if (intQValue) {
                       builder.append((int) qvalue);
                   } else {
                       builder.append(qvalue);
                   }
                   for (final AcceptExt acceptExt : acceptExts) {
                       builder.append(PARAMETER_SEPARATOR);
                       builder.append(acceptExt.toString());
                   }
                   return builder.toString();
               }
            }

            @Override
            public boolean equals(final Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || getClass() != other.getClass()) {
                    return false;
                }
                final Weight otherWeight = (Weight) other;
                return Float.compare(otherWeight.qvalue, qvalue) == 0 &&
                        Arrays.equals(acceptExts, otherWeight.acceptExts);
            }

            @Override
            public int hashCode() {
                int result = 1;
                result = 31 * result + Float.floatToIntBits(qvalue);
                result = 31 * result + Arrays.hashCode(acceptExts);
                return result;
            }
        }

        public static class AcceptExt {
            final String name;
            /* @Nullable */ final String value;

            public AcceptExt(final String name) {
                this(name, null);
            }

            public AcceptExt(final String name, /* @Nullable */ final String value) {
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
                if (value == null) {
                    return name;
                } else {
                    return name + PARAMETER_KEY_VALUE_SEPARATOR + value;
                }
            }

            @Override
            public boolean equals(final Object other) {
                if (this == other) {
                    return true;
                }
                if (other == null || getClass() != other.getClass()) {
                    return false;
                }
                final AcceptExt otherAcceptExt = (AcceptExt) other;
                return name.equals(otherAcceptExt.name) &&
                        (value == otherAcceptExt.value
                                || (value != null && value.equals(otherAcceptExt.value)));
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 61 * hash + this.name.hashCode();
                hash = 61 * hash + (this.value != null ? this.value.hashCode() : 0);
                return hash;
            }
        }
    }

    public static class AcceptHeaderParser {
        private enum ParserState {
            INIT,
            TYPE,
            SUBTYPE,
            FINISHING_SUBTYPE,
            STARTING_PARAMETER,
            PARAMETER_NAME,
            PARAMETER_VALUE,
            FINISHING_PARAMETER_VALUE,
            MAYBE_WEIGHT_PARAMETER_NAME,
            WEIGHT_PARAMETER_VALUE_INT,
            WEIGHT_PARAMETER_VALUE_MAYBE_DECIMAL,
            WEIGHT_PARAMETER_VALUE_ZERO_DECIMAL,
            WEIGHT_PARAMETER_VALUE_ONE_DECIMAL,

            QUOTED_STRING,
            QUOTED_PAIR_2,

            NEXT
        }

        private final static char SYMBOL_ACCEPT_SEPARATOR = ',';
        private final static char SYMBOL_TYPE_SUBTYPE_SEP = '/';
        private final static char SYMBOL_PARAMETER_SEP = ';';
        private final static char SYMBOL_WEIGHT_PARAM_NAME = 'q';
        private final static char SYMBOL_PARAM_NAME_VALUE_SEP = '=';
        private final static char SYMBOL_DQUOTE = '"';
        private final static char SYMBOL_BACKSLASH = '\\';

        /**
         * Parses the value of a HTTP Accept header
         *
         * @param headerValue the value of the HTTP Accept header
         *
         * @return the list of things that are acceptable
         */
        public static List<Accept> parse(final String headerValue) throws IllegalArgumentException {
            ParserState state = ParserState.INIT;
            ParserState prevState = null;
            String type = null;
            String subType = null;
            String parameterName = null;
            String parameterValue = null;
            final StringBuilder buf = new StringBuilder();
            boolean isAcceptExt = false;
            Accept.Parameter[] parameters = null;
            Accept.Weight weight = null;
            final List<Accept> accepts = new ArrayList<Accept>();

            for (int idx = 0; idx < headerValue.length(); idx++) {
                final char c = headerValue.charAt(idx);

                switch (state) {
                    case NEXT:
                        if (isOWS(c)) {
                            continue;
                        }
                        // NOTE: intentional fall-through
                    case INIT:
                        if (isTokenChar(c)) {
                            buf.append(c);
                            state = ParserState.TYPE;
                        } else {
                            throw new IllegalArgumentException("Non-Token character at index " + idx + " whilst looking for media-type at start of Accept Header: '" + c + "'");
                        }
                        break;

                    case TYPE:
                        if (c == SYMBOL_TYPE_SUBTYPE_SEP) {
                            type = buf.toString();
                            buf.setLength(0);
                            state = ParserState.SUBTYPE;
                        } else if (isTokenChar(c)) {
                            buf.append(c);
                        } else {
                            throw new IllegalArgumentException("Non-Token character at index " + idx + " whilst parsing type component of media-type: '" + c + "'");
                        }
                        break;

                    case FINISHING_SUBTYPE:
                        if (isOWS(c)) {
                            continue;
                        } else if(isTokenChar(c)) {
                            throw new IllegalArgumentException("Token character at index " + idx + " whilst parsing end of sub-type component of media-type: '" + c + "'");
                        }
                        // NOTE: intentional fall-through
                    case SUBTYPE:
                        if (c == SYMBOL_PARAMETER_SEP) {
                            subType = buf.toString();
                            buf.setLength(0);
                            state = ParserState.STARTING_PARAMETER;
                        } else if (c == SYMBOL_ACCEPT_SEPARATOR) {
                            subType = buf.toString();
                            buf.setLength(0);
                            state = ParserState.NEXT;
                            accepts.add(new Accept(type + '/' + subType, copyOf(parameters), weight));
                            type = null;
                            subType = null;
                            isAcceptExt = false;
                            parameters = null;
                            weight = null;
                        } else if (isOWS(c)) {
                            subType = buf.toString();
                            buf.setLength(0);
                            state = ParserState.FINISHING_SUBTYPE;
                        } else if(isTokenChar(c)) {
                            buf.appendCodePoint(c);
                        } else {
                            throw new IllegalArgumentException("Non-Token character at index " + idx + " whilst parsing sub-type component of media-type: '" + c + "'");
                        }
                        break;

                    case STARTING_PARAMETER:
                        if (isOWS(c)) {
                            continue;
                        } else {
                            state = ParserState.PARAMETER_NAME;
                        }
                        // NOTE: intentional fall-through
                    case PARAMETER_NAME:
                        if (c == SYMBOL_WEIGHT_PARAM_NAME) {
                            buf.append(c);
                            state = ParserState.MAYBE_WEIGHT_PARAMETER_NAME;
                        } else if (isTokenChar(c)) {
                            buf.append(c);
                        } else if (c == SYMBOL_PARAM_NAME_VALUE_SEP) {
                            parameterName = buf.toString();
                            buf.setLength(0);
                            state = ParserState.PARAMETER_VALUE;
                        } else if (isAcceptExt && (isOWS(c) || c == SYMBOL_PARAMETER_SEP)) {
                            parameterName = buf.toString();
                            buf.setLength(0);
                            state = ParserState.STARTING_PARAMETER;
                            weight = addAcceptExt(weight, parameterName, null);
                            parameterName = null;
                            parameterValue = null;
                        } else {
                            throw new IllegalArgumentException("Non-Token character at index " + idx + " whilst parsing parameter: '" + c + "'");
                        }
                        break;

                    case MAYBE_WEIGHT_PARAMETER_NAME:
                        if (c == SYMBOL_PARAM_NAME_VALUE_SEP) {
                            parameterName = buf.toString();
                            buf.setLength(0);
                            state = ParserState.WEIGHT_PARAMETER_VALUE_INT;
                        } else if (isTokenChar(c)) {
                            buf.append(c);
                            state = ParserState.PARAMETER_NAME;
                        } else {
                            throw new IllegalArgumentException("Non-Token character at index " + idx + " whilst parsing parameter: '" + c + "'");
                        }
                        break;

                    case FINISHING_PARAMETER_VALUE:
                        if (isOWS(c)) {
                            continue;
                        } else if(c == SYMBOL_DQUOTE) {
                            throw new IllegalArgumentException("Double-quote character at index " + idx + " whilst parsing end of parameter value: '" + c + "'");
                        } else if (isTokenChar(c)) {
                            throw new IllegalArgumentException("Token character at index " + idx + " whilst parsing end of parameter value: '" + c + "'");
                        }
                        // NOTE: intentional fall-through
                    case PARAMETER_VALUE:
                        if (c == SYMBOL_DQUOTE) {
                            prevState = state;  // save the state, so we can come back when we finish the quoted string
                            state = ParserState.QUOTED_STRING;
                        } else if (isTokenChar(c)) {
                            buf.append(c);
                        } else if (c == SYMBOL_PARAMETER_SEP) {
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            if (isAcceptExt) {
                                weight = addAcceptExt(weight, parameterName, parameterValue);
                            } else {
                                parameters = addParameter(parameters, parameterName, parameterValue);
                            }
                            state = ParserState.STARTING_PARAMETER;
                            parameterName = null;
                            parameterValue = null;
                        } else if (c == SYMBOL_ACCEPT_SEPARATOR) {
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            if (isAcceptExt) {
                                weight = addAcceptExt(weight, parameterName, parameterValue);
                            } else {
                                parameters = addParameter(parameters, parameterName, parameterValue);
                            }
                            state = ParserState.NEXT;
                            accepts.add(new Accept(type + '/' + subType, copyOf(parameters), weight));
                            type = null;
                            subType = null;
                            isAcceptExt = false;
                            parameterName = null;
                            parameterValue = null;
                            parameters = null;
                            weight = null;
                        } else if (isOWS(c)) {
                            parameterValue = buf.toString();
                            buf.setLength(0);
                            if (isAcceptExt) {
                                weight = addAcceptExt(weight, parameterName, parameterValue);
                            } else {
                                parameters = addParameter(parameters, parameterName, parameterValue);
                            }
                            state = ParserState.FINISHING_PARAMETER_VALUE;
                            parameterName = null;
                            parameterValue = null;
                        }
                        break;

                    case WEIGHT_PARAMETER_VALUE_INT:
                        if (c == '0') {
                            buf.append(c);
                            prevState = ParserState.WEIGHT_PARAMETER_VALUE_ZERO_DECIMAL;  // actually the next state ;-)
                            state = ParserState.WEIGHT_PARAMETER_VALUE_MAYBE_DECIMAL;
                        } else if (c == '1') {
                            buf.append(c);
                            prevState = ParserState.WEIGHT_PARAMETER_VALUE_ONE_DECIMAL;   // actually the next state ;-)
                            state = ParserState.WEIGHT_PARAMETER_VALUE_MAYBE_DECIMAL;
                        } else {
                            throw new IllegalArgumentException("Illegal character at index " + idx + " whilst parsing weight accept-parameter value: '" + c + "'");
                        }
                        break;

                    case WEIGHT_PARAMETER_VALUE_MAYBE_DECIMAL:
                        if (c == '.') {
                            buf.append(c);
                            state = prevState;
                        } else if (isOWS(c) || c == SYMBOL_PARAMETER_SEP) {
                            // next must be an accept-ext
                            isAcceptExt = true;
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            weight = new Accept.Weight(Integer.parseInt(parameterValue));
                            state = ParserState.PARAMETER_NAME;
                            parameterName = null;
                            parameterValue = null;
                        } else if (c == SYMBOL_ACCEPT_SEPARATOR) {
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            weight = new Accept.Weight(Integer.parseInt(parameterValue));
                            state = ParserState.NEXT;
                            accepts.add(new Accept(type + '/' + subType, copyOf(parameters), weight));
                            type = null;
                            subType = null;
                            isAcceptExt = false;
                            parameterName = null;
                            parameterValue = null;
                            parameters = null;
                            weight = null;
                        } else {
                            throw new IllegalArgumentException("Illegal character at index " + idx + " whilst parsing weight accept-parameter value: '" + c + "'");
                        }
                        break;

                    case WEIGHT_PARAMETER_VALUE_ZERO_DECIMAL:
                        if (isDigit(c) && buf.length() < 6) {
                            buf.append(c);
                        } else if (isOWS(c) || c == SYMBOL_PARAMETER_SEP) {
                            // next must be an accept-ext
                            isAcceptExt = true;
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            weight = new Accept.Weight(Float.parseFloat(parameterValue));
                            state = ParserState.PARAMETER_NAME;
                            parameterName = null;
                            parameterValue = null;
                        } else if (c == SYMBOL_ACCEPT_SEPARATOR) {
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            weight = new Accept.Weight(Float.parseFloat(parameterValue));
                            state = ParserState.NEXT;
                            accepts.add(new Accept(type + '/' + subType, copyOf(parameters), weight));
                            type = null;
                            subType = null;
                            isAcceptExt = false;
                            parameterName = null;
                            parameterValue = null;
                            parameters = null;
                            weight = null;
                        } else {
                            throw new IllegalArgumentException("Illegal character at index " + idx + " whilst parsing weight accept-parameter value: '" + c + "'");
                        }
                        break;

                   case WEIGHT_PARAMETER_VALUE_ONE_DECIMAL:
                        if (c == '0' && buf.length() < 6) {
                            buf.append(c);
                        } else if (isOWS(c) || c == SYMBOL_PARAMETER_SEP) {
                            // next must be an accept-ext
                            isAcceptExt = true;
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            weight = new Accept.Weight(Float.parseFloat(parameterValue));
                            state = ParserState.PARAMETER_NAME;
                            parameterName = null;
                            parameterValue = null;
                        } else if (c == SYMBOL_ACCEPT_SEPARATOR) {
                            if (buf.length() > 0) {
                                parameterValue = buf.toString();
                            }
                            buf.setLength(0);
                            weight = new Accept.Weight(Float.parseFloat(parameterValue));
                            state = ParserState.NEXT;
                            accepts.add(new Accept(type + '/' + subType, copyOf(parameters), weight));
                            type = null;
                            subType = null;
                            isAcceptExt = false;
                            parameterName = null;
                            parameterValue = null;
                            parameters = null;
                            weight = null;
                        } else {
                            throw new IllegalArgumentException("Illegal character at index " + idx + " whilst parsing weight accept-parameter value: '" + c + "'");
                        }
                       break;

                    case QUOTED_PAIR_2:
                        if (isQuotedPair2(c)) {
                            buf.append(c);
                            state = ParserState.QUOTED_STRING;
                        } else {
                            throw new IllegalArgumentException("Illegal character at index " + idx + " whilst parsing quoted pair: '" + c + "'");
                        }
                        // NOTE: intentional fall-through
                    case QUOTED_STRING:
                        if (c == SYMBOL_DQUOTE) {
                            parameterValue = buf.toString();
                            buf.setLength(0);
                            if (isAcceptExt) {
                                weight = addAcceptExt(weight, parameterName, parameterValue);
                            } else {
                                parameters = addParameter(parameters, parameterName, parameterValue);
                            }
                            state = prevState;
                            parameterName = null;
                            parameterValue = null;
                        } else if (isQdText(c)) {
                            buf.append(c);
                        } else if (c == SYMBOL_BACKSLASH) {
                            buf.append(c);
                            state = ParserState.QUOTED_PAIR_2;
                        } else {
                            throw new IllegalArgumentException("Illegal character at index " + idx + " whilst parsing quoted string: '" + c + "'");
                        }
                        break;
                }
            }

            if (type != null) {
                if (subType == null) {
                    subType = buf.toString();
                } else if (parameterValue == null) {
                    parameterValue = buf.toString();
                    if (parameterName.equals("" + QUALITY_PARAMETER)) {
                        if (weight == null) {
                            if (parameterValue.indexOf('.') != -1) {
                                weight = new Accept.Weight(Float.parseFloat(parameterValue));
                            } else {
                                weight = new Accept.Weight(Integer.parseInt(parameterValue));
                            }
                        }
                    } else {
                        if (isAcceptExt) {
                            weight = addAcceptExt(weight, parameterName, parameterValue);
                        } else {
                            parameters = addParameter(parameters, parameterName, parameterValue);
                        }
                    }
                }

                accepts.add(new Accept(type + '/' + subType, copyOf(parameters), weight));

                type = null;
                subType = null;
                isAcceptExt = false;
                parameterName = null;
                parameterValue = null;
                parameters = null;
                weight = null;
            }

            return accepts;
        }

        private static boolean isTokenChar(final char c) {
            return
                    isDigit(c)
                    || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')  // ALPHA
                    || c == '!'
                    || c == '#'
                    || c == '$'
                    || c == '%'
                    || c == '&'
                    || c == '\''
                    || c == '*'
                    || c == '+'
                    || c == '-'
                    || c == '.'
                    || c == '^'
                    || c == '_'
                    || c == '`'
                    || c == '|'
                    || c == '~';
        }

        private static boolean isDigit(final char c) {
            return c >= '0' && c <= '9';  // DIGIT
        }

        private static boolean isOWS(final char c) {
            return c == ' ' || c == '\t';
        }

        private static boolean isQdText(final char c) {
            return
                    c == '\t'
                    || c == ' '
                    || c == 0x21
                    || (c >= 0x23 && c <= 0x5B)
                    || (c >= 0x5D && c <= 0x7E)
                    || isObsText(c);  // obs-text
        }

        private static boolean isObsText(final char c) {
            return c >= 0x80 && c <= 0xFF;
        }

        private static boolean isQuotedPair2(final char c) {
            return
                    c == '\t'
                    || c == ' '
                    || (c >= 0x20 && c <= 0x7F)  // VCHAR
                    || isObsText(c);  // obs-text
        }

        private static Accept.Parameter[] addParameter(/* @Nullable */ Accept.Parameter[] parameters,
            final String parameterName, final String parameterValue) {
            if (parameters == null) {
                parameters = new Accept.Parameter[1];
            } else {
                parameters = Arrays.copyOf(parameters, parameters.length + 1);
            }
            parameters[parameters.length - 1] = new Accept.Parameter(parameterName, parameterValue);
            return parameters;
        }

        private static Accept.Weight addAcceptExt(final Accept.Weight weight,
                    final String name, /* @Nullable */ final String value) {
            final Accept.AcceptExt[] acceptExts;
            if (weight.acceptExts == null) {
                acceptExts = new Accept.AcceptExt[1];
            } else {
                acceptExts = Arrays.copyOf(weight.acceptExts, weight.acceptExts.length + 1);
            }

            acceptExts[acceptExts.length - 1] = new Accept.AcceptExt(name, value);

            return new Accept.Weight(weight.qvalue, weight.intQValue, acceptExts);
        }

        private static <T> /* @Nullable */ T[] copyOf(/* @Nullable */ final T[] array) {
            if (array == null) {
                return null;
            }
            return Arrays.copyOf(array, array.length);
        }
    }
}
