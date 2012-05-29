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
package org.exquery.restxq.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import org.exquery.ExQueryException;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.ResourceFunction;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.xquery3.Annotation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for ResourceFunctionFactory
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ResourceFunctionFactoryTest {
 
    @Test(expected=ExQueryException.class)
    public void create_failsForNonRESTXQAnnotation() throws URISyntaxException, ExQueryException {
        
        final QName badName = new QName("http://fake", "fake");
        
        final Annotation mckAnnotation = mock(Annotation.class);
        when(mckAnnotation.getName()).thenReturn(badName);
        
        final Set<Annotation> annotations = new HashSet<Annotation>();
        annotations.add(mckAnnotation);
                
        ResourceFunctionFactory.create(new URI("/fake.xquery"), annotations);
    }
    
    @Test(expected=ExQueryException.class)
    public void create_failsForRESTXQAnnotations_Without_PathAnnotation() throws URISyntaxException, ExQueryException {
        final QName qnHttpMethodAnnotation = new QName(Namespace.ANNOTATION_NS, "GET");
        final HttpMethodAnnotation mckHttpMethodAnnotation = mock(HttpMethodAnnotation.class);
        
        final Set<Annotation> annotations = new HashSet<Annotation>();
        annotations.add(mckHttpMethodAnnotation);
        
        when(mckHttpMethodAnnotation.getName()).thenReturn(qnHttpMethodAnnotation);
        
        final ResourceFunction resourceFunction = ResourceFunctionFactory.create(new URI("/some.xquery"), annotations);
    }
    
    @Test
    public void create_succeedsForRESTXQAnnotations_With_PathAnnotation() throws URISyntaxException, ExQueryException {
        
        final QName qnPathAnnotation = new QName(Namespace.ANNOTATION_NS, "path");
        final PathAnnotation mckPathAnnotation = mock(PathAnnotation.class);
        
        final Set<Annotation> annotations = new HashSet<Annotation>();
        annotations.add(mckPathAnnotation);
        
        when(mckPathAnnotation.getName()).thenReturn(qnPathAnnotation);
        
        final ResourceFunction resourceFunction = ResourceFunctionFactory.create(new URI("/some.xquery"), annotations);
        assertNotNull(resourceFunction);
        assertEquals(mckPathAnnotation, resourceFunction.getPathAnnotation());
    }
}