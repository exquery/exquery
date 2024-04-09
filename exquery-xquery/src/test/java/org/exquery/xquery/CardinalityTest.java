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
package org.exquery.xquery;

import static org.exquery.xquery.Cardinality.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Adam Retter
 */
public class CardinalityTest {

    @Test
    public void hasRequired_have_ZERO_OR_ONE_require_ONE() {
        //pass
        assertTrue(ZERO_OR_ONE.hasRequiredCardinality(ONE));
    }
    
    @Test
    public void hasRequired_have_ZERO_OR_ONE_require_ZERO_OR_ONE() {
        //pass
        assertTrue(ZERO_OR_ONE.hasRequiredCardinality(ZERO_OR_ONE));
    }
    
    @Test
    public void hasRequired_have_ZERO_OR_ONE_require_ONE_OR_MORE() {
        //fail
        assertFalse(ZERO_OR_ONE.hasRequiredCardinality(ONE_OR_MORE));
    }
    
    @Test
    public void hasRequired_have_ZERO_OR_MORE_require_ONE() {
        //pass
        assertTrue(ZERO_OR_MORE.hasRequiredCardinality(ONE));
    }
    
    @Test
    public void hasRequired_have_ZERO_OR_MORE_require_ZERO_OR_MORE() {
        //pass
        assertTrue(ZERO_OR_MORE.hasRequiredCardinality(ZERO_OR_MORE));
    }
    
    @Test
    public void hasRequired_have_ZERO_OR_MORE_require_ZERO_OR_ONE() {
        //pass
        assertTrue(ZERO_OR_MORE.hasRequiredCardinality(ZERO_OR_ONE));
    }
    
    @Test
    public void hasRequired_have_ONE_require_ONE() {
        //pass
        assertTrue(ONE.hasRequiredCardinality(ONE));
    }
    
    @Test
    public void hasRequired_have_ONE_require_ZERO_OR_ONE() {
        //fail
        assertFalse(ONE.hasRequiredCardinality(ZERO_OR_ONE));
    }
    
    @Test
    public void hasRequired_have_ONE_require_ONE_OR_MORE() {
        //fail
        assertFalse(ONE.hasRequiredCardinality(ONE_OR_MORE));
    }
}
