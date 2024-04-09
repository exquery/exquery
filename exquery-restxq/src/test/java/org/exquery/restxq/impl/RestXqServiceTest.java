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
package org.exquery.restxq.impl;

import org.exquery.http.HttpRequest;
import org.exquery.restxq.ResourceFunction;
import org.exquery.restxq.RestXqServiceException;
import org.exquery.restxq.annotation.PathAnnotation;
import org.exquery.xquery.Sequence;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Adam Retter
 */
public class RestXqServiceTest {
    
    @Test
    public void compareTo_resourceFunctions_path_before_no_path() {
        
        final PathAnnotation mockPathAnnotation = mock(PathAnnotation.class);
        final ResourceFunction mockResourceFunction = mock(ResourceFunction.class);
        final RestXqServiceMock restXqService = new RestXqServiceMock(mockResourceFunction);
        
        final ResourceFunction otherMockResourceFunction = mock(ResourceFunction.class);
        final RestXqServiceMock otherRestXqService = new RestXqServiceMock(otherMockResourceFunction);
        
        when(mockResourceFunction.getPathAnnotation()).thenReturn(mockPathAnnotation);
        when(mockPathAnnotation.getPathSpecificityMetric()).thenReturn(Long.valueOf(3));
        
        when(otherMockResourceFunction.getPathAnnotation()).thenReturn(null);
        
        final int result = restXqService.compareTo(otherRestXqService);
        
        assertEquals(-1, result);
    }
    
    @Test
    public void compareTo_resourceFunctions_no_path_after_path() {
        
        final ResourceFunction mockResourceFunction = mock(ResourceFunction.class);
        final RestXqServiceMock restXqService = new RestXqServiceMock(mockResourceFunction);
        
        final PathAnnotation otherMockPathAnnotation = mock(PathAnnotation.class);
        final ResourceFunction otherMockResourceFunction = mock(ResourceFunction.class);
        final RestXqServiceMock otherRestXqService = new RestXqServiceMock(otherMockResourceFunction);
        
        when(mockResourceFunction.getPathAnnotation()).thenReturn(null);
        
        when(otherMockResourceFunction.getPathAnnotation()).thenReturn(otherMockPathAnnotation);
        when(otherMockPathAnnotation.getPathSpecificityMetric()).thenReturn(Long.valueOf(3));
        
        final int result = restXqService.compareTo(otherRestXqService);
        
        assertEquals(1, result);
    }
    
    @Test
    public void compareTo_resourceFunctions_most_specific_path_first() {
        
        final PathAnnotation mockPathAnnotation = mock(PathAnnotation.class);
        final ResourceFunction mockResourceFunction = mock(ResourceFunction.class);
        final RestXqServiceMock restXqService = new RestXqServiceMock(mockResourceFunction);
        
        final PathAnnotation otherMockPathAnnotation = mock(PathAnnotation.class);
        final ResourceFunction otherMockResourceFunction = mock(ResourceFunction.class);
        final RestXqServiceMock otherRestXqService = new RestXqServiceMock(otherMockResourceFunction);
        
        when(mockResourceFunction.getPathAnnotation()).thenReturn(mockPathAnnotation);
        when(mockPathAnnotation.getPathSpecificityMetric()).thenReturn(Long.valueOf(3));
        
        when(otherMockResourceFunction.getPathAnnotation()).thenReturn(otherMockPathAnnotation);
        when(otherMockPathAnnotation.getPathSpecificityMetric()).thenReturn(Long.valueOf(2));
        
        final int result = restXqService.compareTo(otherRestXqService);
        
        assertEquals(-1, result);
    }
    
    private class RestXqServiceMock extends AbstractRestXqService {

        public RestXqServiceMock(final ResourceFunction resourceFunction) {
            super(resourceFunction);
        }
        
        @Override
        protected Sequence extractRequestBody(HttpRequest request) throws RestXqServiceException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
