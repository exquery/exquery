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

/**
 * Representation of the Cardinality of a type in XQuery 1.0
 *
 * @author Adam Retter
 */
public enum Cardinality {
    
    ZERO(1),
    ONE(2),
    MANY(4),
    
    ZERO_OR_ONE(ZERO, ONE),
    ZERO_OR_MORE(ZERO, ONE, MANY),
    ONE_OR_MORE(ONE, MANY);

    private int value;
    
    /**
     * Cardinality
     * 
     * @param value integer representation of the cardinality (must be distinct in base two)
     */
    Cardinality(final int value) {
        this.value = value;
    }
    
    /**
     * Composite Cardinality
     * 
     * @param cardinalities Cardinalities when combined make up this cardinality
     */
    Cardinality(final Cardinality... cardinalities) {
        for(Cardinality cardinality : cardinalities) {
            value |= cardinality.value;
        }
    }
    
    /**
     * Checks if this Cardinality satisfies the required Cardinality
     * 
     * @param requiredCardinality The cardinality that is required
     *
     * @return true if the required cardinality is met
     */
    public boolean hasRequiredCardinality(final Cardinality requiredCardinality) {
        return ((requiredCardinality.value & value) == requiredCardinality.value);
    }

    /**
     * Gets the numeric value of the cardinality.
     * Perhaps useful for calculations by external functions
     *
     * @return The internal numeric value of the cardinality
     */
    public int getNumericValue() {
        return value;
    }
}