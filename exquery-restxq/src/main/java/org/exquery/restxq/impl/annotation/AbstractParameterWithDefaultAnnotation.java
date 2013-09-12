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
package org.exquery.restxq.impl.annotation;

import java.util.Arrays;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xdm.type.SequenceImpl;
import org.exquery.xdm.type.StringTypedValue;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Sequence;
import org.exquery.xquery.Type;

/**
 * Base class for RESTXQ Parameter Annotation Implementations
 * which can have an optional default value(s) specified
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractParameterWithDefaultAnnotation extends AbstractParameterAnnotation {
    
    /**
     * @see AbstractParameterAnnotation#parseAnnotationValue()
     */
    @Override
    protected ParameterAnnotationMapping parseAnnotationValue() throws RestAnnotationException {
        final Literal[] annotationLiterals = getLiterals();
        
        if(annotationLiterals.length < 2) {
            throw new RestAnnotationException(getInvalidAnnotationParamsErr());
        }
        final Literal[] defaultValueLiterals = Arrays.copyOfRange(annotationLiterals, 2, annotationLiterals.length);
        
        //check that the decalred literals are themselves valid
        //TODO probably not needed enforced by the XQuery parser/grammar
        for(final Literal defaultValueLiteral : defaultValueLiterals) {
            if(!defaultValueLiteral.getType().isSubTypeOf(Type.ANY_SIMPLE_TYPE)){
                throw new RestAnnotationException(getInvalidDefaultValueErr());
            }
        }
        
        return parseAnnotationLiterals(annotationLiterals[0], annotationLiterals[1], defaultValueLiterals);
    }
    
    
    private ParameterAnnotationMapping parseAnnotationLiterals(final Literal parameterName, final Literal functionArgumentName, final Literal[] defaultValueLiterals) throws RestAnnotationException {
        //check the function that has this annotation, has parameters as declared by the annotation
        final Cardinality requiredCardinality;
        if(defaultValueLiterals.length == 0) {
            requiredCardinality = Cardinality.ZERO_OR_MORE;
        } else {
            //defaultValueLiterals.length > 0
            requiredCardinality = Cardinality.ONE_OR_MORE;
        }
        
        //check the cardinality and existence of the parameter
        final ParameterAnnotationMapping mapping = super.parseAnnotationLiterals(parameterName, functionArgumentName, requiredCardinality);
        
        //check the type of the parameter, if default values are present
        if(defaultValueLiterals.length > 0) {
            final Type requiredType;
            if(haveConsistentType(defaultValueLiterals)) {
                requiredType = defaultValueLiterals[0].getType();
            } else {
                requiredType = Type.ANY_SIMPLE_TYPE;
            }
            
            //if a default value(s) is provided make sure it matches the type of the function parameter declared
            checkFnDeclaresParameterWithType(getFunctionSignature(), mapping.getFunctionArgumentName(), requiredType, getInvalidDefaultValueTypeErr());
        }
        
        return new ParameterAnnotationMapping(mapping.getParameterName(), mapping.getFunctionArgumentName(), defaultValueLiterals);
    }
    
    /**
     * Determines if an array of literals all have the same type     
     * 
     * @param literals An array of literals to examine
     * 
     * @return true if the literals all share the same type, false otherwise
     * Note we return true if 'literals' is null or empty
     */
    private boolean haveConsistentType(final Literal[] literals) {
        if(literals == null || literals.length == 0) {
            return true;
        }
        
        final Type type = literals[0].getType();
        for(final Literal literal : literals) {
            if(!literal.getType().equals(type)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Produces a Sequence from an Array of Literals
     * 
     * @param literals The literals
     * 
     * @return The equivalent Sequence
     */
    protected Sequence literalsToSequence(final Literal[] literals) {
        //TODO cope with non-string literal types
        
        final SequenceImpl<String> sequence = new SequenceImpl<String>();
        for(final Literal literal : literals) {
            sequence.add(new StringTypedValue(literal.getValue()));
        }
        
        return sequence;
    }
}
