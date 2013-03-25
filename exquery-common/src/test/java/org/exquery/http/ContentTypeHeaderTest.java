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

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.exquery.InternetMediaType.APPLICATION_XML;

/**
 * Tests for HTTP ContentType header representation
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ContentTypeHeaderTest {
    
    @Test
    public void extracts_internetMediaType_and_charsetIsNullWhenNotPresent() {
        final String headerValue = APPLICATION_XML.getMediaType();
        
        final ContentTypeHeader header = new ContentTypeHeader(headerValue);
        
        assertEquals(APPLICATION_XML.getMediaType(), header.getInternetMediaType());
        assertNull(header.getCharset());
    }
    
    @Test
    public void extracts_internetMediaType_and_charset() {
        final String headerValue = APPLICATION_XML.getMediaType() + "; charset=UTF-8";
        
        final ContentTypeHeader header = new ContentTypeHeader(headerValue);
        
        assertEquals(APPLICATION_XML.getMediaType(), header.getInternetMediaType());
        assertEquals("UTF-8", header.getCharset());
    }
    
    @Test
    public void extracts_internetMediaType_and_charset2() {
        final String headerValue = APPLICATION_XML.getMediaType() + ";charset=UTF-8";
        
        final ContentTypeHeader header = new ContentTypeHeader(headerValue);
        
        assertEquals(APPLICATION_XML.getMediaType(), header.getInternetMediaType());
        assertEquals("UTF-8", header.getCharset());
    }
}
