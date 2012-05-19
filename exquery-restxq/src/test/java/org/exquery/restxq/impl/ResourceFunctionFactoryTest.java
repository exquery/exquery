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
import org.exquery.EXQueryException;
import org.exquery.http.HttpMethod;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.ResourceFunction;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.xquery.Literal;
import org.exquery.xquery3.Annotation;
import org.exquery.xquery3.FunctionSignature;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * Tests for ResourceFunctionFactory
 * 
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ResourceFunctionFactoryTest {
 
    @Test(expected=EXQueryException.class)
    public void create_failsForNonRESTXQAnnotation() throws URISyntaxException {
        
        final Set<Annotation> annotations = new HashSet<Annotation>();
        annotations.add(new Annotation(){
            public QName getName() {
                return new QName("http://fake", "fake");
            }

            public Literal[] getLiterals() {
                return new Literal[0];
            }

            public FunctionSignature getFunctionSignature() {
                return null;
            }
        });
                
        ResourceFunctionFactory.create(new URI("/fake.xquery"), annotations);
    }
    
    public void create_succeedsForRESTXQAnnotation() throws URISyntaxException {
        
        final Annotation getHttpMethodAnnotation = new HttpMethodAnnotation(){

            public HttpMethod getHttpMethod() {
                return HttpMethod.GET;
            }

            public QName getName() {
                return new QName(Namespace.ANNOTATION_NS, "GET");
            }

            public Literal[] getLiterals() {
                return new Literal[0];
            }

            public FunctionSignature getFunctionSignature() {
                return null;
            }
        };
        
        final Set<Annotation> annotations = new HashSet<Annotation>();
        annotations.add(getHttpMethodAnnotation);
                
        final ResourceFunction resourceFunction = ResourceFunctionFactory.create(new URI("/some.xquery"), annotations);
        assertNotNull(resourceFunction);
    }
}