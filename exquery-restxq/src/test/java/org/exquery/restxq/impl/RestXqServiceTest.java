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
 * @author Adam Retter <adam.retter@googlemail.com>
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
