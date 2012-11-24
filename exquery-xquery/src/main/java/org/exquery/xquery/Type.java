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

/**
 * Model of W3C XQuery 1.0 and XPath 2.0 XDM Types
 * 
 * @see http://www.w3.org/TR/xpath-datamodel/#types
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public enum Type {

    YEAR_MONTH_DURATION,
    DAY_TIME_DURATION,
    
    NEGATIVE_INTEGER,
    
    NON_POSITIVE_INTEGER(NEGATIVE_INTEGER),
    
    INT,
    SHORT,
    BYTE,
    
    LONG(INT, SHORT, BYTE),
    
    UNSIGNED_INT,
    UNSIGNED_SHORT,
    UNSIGNED_BYTE,
    
    UNSIGNED_LONG(UNSIGNED_INT, UNSIGNED_SHORT, UNSIGNED_BYTE),
    
    POSITIVE_INTEGER,
    
    NON_NEGATIVE_INTEGER(UNSIGNED_LONG, POSITIVE_INTEGER),
    
    INTEGER(NON_POSITIVE_INTEGER, LONG, NON_NEGATIVE_INTEGER),
    
    ID,
    ID_REF,
    ENTITY,
    
    NC_NAME(ID, ID_REF, ENTITY),
    
    LANGUAGE,
    NM_TOKEN,
    NAME(NC_NAME),
    
    TOKEN(LANGUAGE, NM_TOKEN, NAME),
    
    NORMALIZED_STRING(TOKEN),
    
    UNTYPED_ATOMIC,
    DATE_TIME,
    DATE,
    TIME,
    DURATION(YEAR_MONTH_DURATION, DAY_TIME_DURATION),
    FLOAT,
    DOUBLE,
    DECIMAL(INTEGER),
    G_YEAR_MONTH,
    G_YEAR,
    G_MONTH_DAY,
    G_DAY,
    G_MONTH,
    BOOLEAN,
    BASE64_BINARY,
    HEX_BINARY,
    ANY_URI,
    QNAME,
    NOTATION,
    STRING(NORMALIZED_STRING, TOKEN),
    
    ATTRIBUTE,
    COMMENT,
    DOCUMENT,
    ELEMENT,
    PROCESSING_INSTRUCTION,
    TEXT,
    
    ANY_ATOMIC_TYPE(UNTYPED_ATOMIC, DATE_TIME, DATE, TIME, DURATION, FLOAT, DOUBLE, DECIMAL, G_YEAR_MONTH, G_YEAR, G_MONTH_DAY, G_DAY, G_MONTH, BOOLEAN, BASE64_BINARY, HEX_BINARY, ANY_URI, QNAME, NOTATION, STRING), 
    NODE(ATTRIBUTE, COMMENT, DOCUMENT, ELEMENT, PROCESSING_INSTRUCTION, TEXT),
    IDREFS,
    NMTOKENS,
    ENTITIES,
    
    ANY_SIMPLE_TYPE(ANY_ATOMIC_TYPE, NODE, IDREFS, NMTOKENS, ENTITIES), //NODE is in this list because its a Union Type
    UNTYPED,
    
    ITEM(NODE, ANY_ATOMIC_TYPE),
    
    ANY_TYPE(ANY_SIMPLE_TYPE, UNTYPED);
    
    
    final Type subTypes[];
    
    /**
     * Type
     */
    Type() {
        subTypes = null;
    }
    
    /**
     * Type
     * 
     * @param subTypes The sub-types of this type
     */
    Type(final Type... subTypes) {
        this.subTypes = subTypes;
    }
    
    /**
     * Determines if this type is a sub-type of the other type
     * 
     * @param other another Type
     * 
     * @return true if this type is a sub-type of other, false otherwise
     */
    public boolean isSubTypeOf(final Type other) {
        if(this == other) {
            return true;
        }
        
        return other.hasSubType(this);
    }
    
    /**
     * Determines if this type has a sub-type of other
     * 
     * @param other another Type
     * 
     * @return true if other is a sub-type of this type, false otherwise
     */
    public boolean hasSubType(final Type other) {
        return hasSubType(subTypes, other);
    }

    /**
     * Determines if the type to match is a sub-type of the provides types
     * by recursively examining all sub-types
     * 
     * @param types
     * @param toMatch 
     * 
     * @return true if the type to match is a sub-type of the types, false otherwise
     */
    private boolean hasSubType(final Type types[], final Type toMatch) {
        if(types != null) {
            for(final Type type : types) {
                if(type.equals(toMatch)) {
                    return true;
                }
                
                if(hasSubType(type.subTypes, toMatch)) {
                    return true;
                }
            }
        }
        return false;
    }
}