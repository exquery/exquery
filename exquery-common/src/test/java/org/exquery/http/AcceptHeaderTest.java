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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.exquery.InternetMediaType.*;
import org.exquery.http.AcceptHeader.Accept;
import org.exquery.http.AcceptHeader.Accept.Extension;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

/**
 * Tests for HTTP Accept header representation
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class AcceptHeaderTest {

    @Test
    public void singleMediaRange() {
        final String appXml = "application/xml";
        final AcceptHeader acceptHeader = new AcceptHeader(appXml);
        
        assertEquals(1, acceptHeader.getAccepts().size());
        assertEquals(appXml, acceptHeader.getAccepts().get(0).getMediaRange());
    }
    
    @Test
    public void singleWildcardMediaRange() {
        final String any = ANY.getMediaType();
        final AcceptHeader acceptHeader = new AcceptHeader(any);
        
        assertEquals(1, acceptHeader.getAccepts().size());
        assertEquals(any, acceptHeader.getAccepts().get(0).getMediaRange());
    }
    
    @Test
    public void singleSubtypeWildcardMediaRange() {
        final String anySubtype = APPLICATION_ANY.getMediaType();
        final AcceptHeader acceptHeader = new AcceptHeader(anySubtype);
        
        assertEquals(1, acceptHeader.getAccepts().size());
        assertEquals(anySubtype, acceptHeader.getAccepts().get(0).getMediaRange());
    }
    
    @Test
    public void multipleMediaRanges_withoutWhiteSpace() {
        final String appXml = APPLICATION_XML.getMediaType();
        final String appXhtml = APPLICATION_XHTML_XML.getMediaType();
        final String appPdf = APPLICATION_PDF.getMediaType();
        final AcceptHeader acceptHeader = new AcceptHeader(appXml + "," + appXhtml + "," + appPdf);
        
        assertEquals(3, acceptHeader.getAccepts().size());
        assertEquals(appXml, acceptHeader.getAccepts().get(0).getMediaRange());
        assertEquals(appXhtml, acceptHeader.getAccepts().get(1).getMediaRange());
        assertEquals(appPdf, acceptHeader.getAccepts().get(2).getMediaRange());
    }
    
    @Test
    public void multipleMediaRanges_withWhiteSpace() {
        final String appXml = APPLICATION_XML.getMediaType();
        final String appXhtml = APPLICATION_XHTML_XML.getMediaType();
        final String appPdf = APPLICATION_PDF.getMediaType();
        final AcceptHeader acceptHeader = new AcceptHeader(appXml + ", " + appXhtml + ", " + appPdf);
        
        assertEquals(3, acceptHeader.getAccepts().size());
        assertEquals(appXml, acceptHeader.getAccepts().get(0).getMediaRange());
        assertEquals(appXhtml, acceptHeader.getAccepts().get(1).getMediaRange());
        assertEquals(appPdf, acceptHeader.getAccepts().get(2).getMediaRange());
    }
    
    @Test
    public void multipleMediaRangesWith_withQualityFactors() {
        final Accept appXml = new Accept(APPLICATION_XML);
        final Accept appXhtml = new Accept(APPLICATION_XHTML_XML, 0.9f);
        final Accept appPdf = new Accept(APPLICATION_PDF, 0.4f);
        final AcceptHeader acceptHeader = new AcceptHeader(appXml.toString() + "," + appXhtml.toString() + "," + appPdf.toString());
        
        assertEquals(3, acceptHeader.getAccepts().size());
        assertEquals(appXml, acceptHeader.getAccepts().get(0));
        assertEquals(appXhtml, acceptHeader.getAccepts().get(1));
        assertEquals(appPdf, acceptHeader.getAccepts().get(2));
    }
    
    @Test
    public void qualityFactorOrderingOfAccepts() {
        
        final Accept txtHtml = new Accept(TEXT_HTML);
        final Accept appXhtml = new Accept(APPLICATION_XHTML_XML);
        final Accept appXml = new Accept(APPLICATION_XML, 0.8f);
        final Accept appPdf = new Accept(APPLICATION_PDF, 0.6f);
        final Accept any = new Accept(ANY, 0.1f);
        
        //expected result
        final List<Accept> expectedAccepts = new ArrayList<Accept>(){{
            add(txtHtml);
            add(appXhtml);
            add(appXml);
            add(appPdf);
            add(any);
        }};
        
        //test
        final List<Accept> providedAccepts = new ArrayList<Accept>(){{
            add(appXml);
            add(txtHtml);
            add(appXhtml);
            add(any);
            add(appPdf);
        }};
        Collections.sort(providedAccepts);
        
        //assert
        assertThat(providedAccepts, is(expectedAccepts));
    }
    
    @Test
    public void acceptToString_mediaRange() {
        final String appXmlStr = APPLICATION_XML.getMediaType();
        final Accept appXml = new Accept(appXmlStr);
        
        assertEquals(appXmlStr, appXml.toString());
    }
    
    @Test
    public void acceptToString_mediaRangeAndQualityFactor() {
        final String appXmlStr = APPLICATION_XML.getMediaType();
        final float qualityFactor = 0.6f;
        final Accept appXml = new Accept(appXmlStr, qualityFactor);
        
        assertEquals(appXmlStr + ";q=" + Float.toString(qualityFactor), appXml.toString());
    }
    
    @Test
    public void acceptToString_mediaRangeAndExtension() {
        final String appXmlStr = APPLICATION_XML.getMediaType();
        final Extension extension = new Extension("x", "y");
        final Accept appXml = new Accept(appXmlStr, extension);
        
        assertEquals(appXmlStr + ";x=y", appXml.toString());
    }
    
    @Test
    public void acceptToString_mediaRangeQualityFactorAndExtension() {
        final String appXmlStr = APPLICATION_XML.getMediaType();
        final float qualityFactor = 0.6f;
        final Extension extension = new Extension("x", "y");
        final Accept appXml = new Accept(appXmlStr, qualityFactor, extension);
        
        assertEquals(appXmlStr + ";q=" + Float.toString(qualityFactor) + ";x=y", appXml.toString());
    }
}