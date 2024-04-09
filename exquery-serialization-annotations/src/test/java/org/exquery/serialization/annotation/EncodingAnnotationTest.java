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
package org.exquery.serialization.annotation;

import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Adam Retter
 */
public class EncodingAnnotationTest {
    
    @Test
    public void utf8_encoding() throws SerializationAnnotationException {
        final String UTF8_ENCODING = "utf-8";
        
        final EncodingAnnotation encodingAnnotation = new EncodingAnnotation();
        final String encoding = encodingAnnotation.parseValue(new Literal() {

            @Override
            public Type getType() {
                return Type.STRING;
            }

            @Override
            public String getValue() {
                return UTF8_ENCODING;
            }
        });
        
        assertEquals(UTF8_ENCODING, encoding);
    }

    @Test
    public void UTF8_encoding() throws SerializationAnnotationException {
        final String UTF8_ENCODING = "UTF-8";

        final EncodingAnnotation encodingAnnotation = new EncodingAnnotation();
        final String encoding = encodingAnnotation.parseValue(new Literal() {

            @Override
            public Type getType() {
                return Type.STRING;
            }

            @Override
            public String getValue() {
                return UTF8_ENCODING;
            }
        });

        assertEquals(UTF8_ENCODING, encoding);
    }

    @Test
    public void utf16_encoding() throws SerializationAnnotationException {
        final String UTF8_ENCODING = "utf-16";

        final EncodingAnnotation encodingAnnotation = new EncodingAnnotation();
        final String encoding = encodingAnnotation.parseValue(new Literal() {

            @Override
            public Type getType() {
                return Type.STRING;
            }

            @Override
            public String getValue() {
                return UTF8_ENCODING;
            }
        });

        assertEquals(UTF8_ENCODING, encoding);
    }

    @Test
    public void UTF16_encoding() throws SerializationAnnotationException {
        final String UTF16_ENCODING = "UTF-16";

        final EncodingAnnotation encodingAnnotation = new EncodingAnnotation();
        final String encoding = encodingAnnotation.parseValue(new Literal() {

            @Override
            public Type getType() {
                return Type.STRING;
            }

            @Override
            public String getValue() {
                return UTF16_ENCODING;
            }
        });

        assertEquals(UTF16_ENCODING, encoding);
    }

    @Test
    public void invalid_encoding() throws SerializationAnnotationException {
        final String FAKE_ENCODING = "UTF12345";

        final EncodingAnnotation encodingAnnotation = new EncodingAnnotation();

        try {
            final String encoding = encodingAnnotation.parseValue(new Literal(){

                @Override
                public Type getType() {
                    return Type.STRING;
                }

                @Override
                public String getValue() {
                    return FAKE_ENCODING;
                }
            });
        } catch(final SerializationAnnotationException sae) {
            assertEquals(SerializationAnnotationErrorCodes.SEST0015, sae.getErrorCode());
        }
    }
}
