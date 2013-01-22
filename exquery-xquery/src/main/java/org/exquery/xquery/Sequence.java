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
package org.exquery.xquery;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Model of a Sequence from W3C XQuery 1.0 and XPath 2.0 Data Model (XDM)
 * 
 * @see <a href="http://www.w3.org/TR/xpath-datamodel/#types">XDM Types</a>
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface Sequence<T> extends Iterable<TypedValue<T>> {
    
    @Override
    public Iterator<TypedValue<T>> iterator();
    
    /**
     * Gets the tail of the Sequence
     * 
     * @return The Sequence without the first Item
     */
    public Sequence<T> tail();
    
    /**
     * The Empty Sequence
     */
    public static Sequence EMPTY_SEQUENCE = new Sequence<Void>(){
        
        private Iterator<TypedValue<Void>> EMPTY_ITERATOR = new Iterator<TypedValue<Void>>() {
            
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public TypedValue<Void> next() {
                throw new NoSuchElementException("This is an EMPTY Sequence with no elements!");
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("You cannot remove Items from an Empty Sequence.");
            }
        };
        
        @Override
        public Iterator<TypedValue<Void>> iterator() {
            return EMPTY_ITERATOR;
        }

        @Override
        public Sequence<Void> tail() {
            return EMPTY_SEQUENCE;
        }
    };
}