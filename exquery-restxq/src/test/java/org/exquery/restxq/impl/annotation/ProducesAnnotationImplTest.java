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
package org.exquery.restxq.impl.annotation;

import static org.exquery.InternetMediaType.*;
import org.exquery.http.HttpHeaderName;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Literal;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ProducesAnnotationImplTest {
    
    @Test
    public void matchesMediaType_simple_match() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(APPLICATION_XML.getMediaType());
        
        assertTrue(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
    
    @Test
    public void matchesMediaType_simple_non_match() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(TEXT_HTML.getMediaType());
        
        assertFalse(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
    
    @Test
    public void matchesMediaType_multiple_match() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType()),
            new StringLiteral(TEXT_HTML.getMediaType()),
            new StringLiteral(APPLICATION_JSON.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(APPLICATION_XML.getMediaType());
        
        assertTrue(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
    
    @Test
    public void matchesMediaType_multiple_non_match() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType()),
            new StringLiteral(TEXT_HTML.getMediaType()),
            new StringLiteral(APPLICATION_JSON.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(APPLICATION_OCTET_STREAM.getMediaType());
        
        assertFalse(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
    
    @Test
    public void matchesMediaType_multiple_match_2() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType()),
            new StringLiteral(TEXT_HTML.getMediaType()),
            new StringLiteral(APPLICATION_JSON.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(TEXT_HTML.getMediaType());
        
        assertTrue(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
    
    @Test
    public void matchesMediaType_single_match_wildcard() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(ANY.getMediaType());
        
        assertTrue(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
    
    @Test
    public void matchesMediaType_single_match_wildSubType() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
            new StringLiteral(APPLICATION_XML.getMediaType())
        });
        mediaTypeAnnotation.initialise();
        
        final HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getHeader(HttpHeaderName.Accept.toString())).thenReturn(APPLICATION_ANY.getMediaType());
        
        assertTrue(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }

    @Test
    public void matchesMediaType_no_accept_header() throws RestAnnotationException {
        final ProducesAnnotationImpl mediaTypeAnnotation = new ProducesAnnotationImpl();
        mediaTypeAnnotation.setLiterals(new Literal[] {
                new StringLiteral(APPLICATION_XML.getMediaType())
        });
        mediaTypeAnnotation.initialise();

        final HttpRequest httpRequest = mock(HttpRequest.class);

        assertTrue(mediaTypeAnnotation.matchesMediaType(httpRequest));
    }
}
