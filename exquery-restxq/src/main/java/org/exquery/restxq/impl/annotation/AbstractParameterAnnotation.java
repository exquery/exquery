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
package org.exquery.restxq.impl.annotation;

import java.util.regex.Matcher;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.ParameterAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.Literal;
import org.exquery.xquery.Type;

/**
 * Base class for RESTXQ Parameter Annotation Implementations
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractParameterAnnotation extends AbstractRestAnnotation implements ParameterAnnotation {
    
    private ParameterAnnotationMapping parameterAnnotationMapping;
    
    /**
     * Determines whether a particular Parameter Annotation
     * can provide a default value if no value is present
     * 
     * @return true if the Parameter Annotation can
     * provide a default value
     */
    protected abstract boolean canProvideDefaultValue();

    /**
     * Checks that the Parameter Annotation is compatible
     * with the function which it annotates
     *
     * @throws RestAnnotationException if the Parameter could not be parsed
     */
    @Override
    public void initialise() throws RestAnnotationException {
        super.initialise();
        this.parameterAnnotationMapping = parseAnnotationValue();
    }    
    
    /**
     * Get the Parameter Annotation Mapping
     * 
     * @return The Parameter Annotation Mapping
     */
    protected ParameterAnnotationMapping getParameterAnnotationMapping() {
        return parameterAnnotationMapping;
    }
    
    /**
     * Parses the Parameter Annotation Value
     * 
     * @return The Parameter to Argument Mapping described by the Parameter Annotation
     * @throws RestAnnotationException if the Parameter annotations mapping is invalid
     */
    protected ParameterAnnotationMapping parseAnnotationValue() throws RestAnnotationException {
        final Literal[] annotationLiterals = getLiterals();
        
        if(canProvideDefaultValue()) {
            if(annotationLiterals.length < 2 || annotationLiterals.length > 3) {
                throw new RestAnnotationException(getInvalidAnnotationParamsErr());
            }
        } else {
            if(annotationLiterals.length != 2) {
                throw new RestAnnotationException(getInvalidAnnotationParamsErr());
            }
        }
        
        return parseAnnotationLiterals(annotationLiterals[0], annotationLiterals[1], annotationLiterals.length == 3 ? annotationLiterals[2] : null);
    }
    
    /**
     * Parses the Parameter Annotations Literal Values
     * 
     * @param parameterName The name of the Parameter
     * @param functionArgumentName The name of the Function Argument
     * @param defaultValue The default value if provided, or null otherwise
     * 
     * @return A description of the mapping between the Parameter and the Function Argument
     * 
     * @throws RestAnnotationException if the mapping is invalid
     */
    protected ParameterAnnotationMapping parseAnnotationLiterals(final Literal parameterName, final Literal functionArgumentName, final Literal defaultValue) throws RestAnnotationException {
        if(parameterName.getType() != Type.STRING) {
            throw new RestAnnotationException(getInvalidParameterNameErr());
        }
        
        if(functionArgumentName.getType() != Type.STRING) {
            throw new RestAnnotationException(getInvalidFunctionArgumentNameErr());
        }
        
        if(defaultValue != null && !defaultValue.getType().isSubTypeOf(Type.ANY_SIMPLE_TYPE)) {
            throw new RestAnnotationException(getInvalidDefaultValueErr());
        }
        
        final String keyStr = parameterName.getValue();
        final String varStr = functionArgumentName.getValue();
        if(keyStr.isEmpty()) {
            throw new RestAnnotationException(getInvalidParameterNameErr());
        }

        if(varStr.isEmpty()) {
            throw new RestAnnotationException(getInvalidFunctionArgumentNameErr());
        }

        //validate the varStr
        final Matcher mtcFnParameter = functionArgumentPattern.matcher(varStr);
        if(!mtcFnParameter.matches()) {
            throw new RestAnnotationException(getInvalidAnnotationParametersSyntaxErr());
        }

        final String varName = mtcFnParameter.group(1);

        if(defaultValue == null) {
            //check the function that has this annotation has parameters as declared by the annotation
            checkFnDeclaresParameter(getFunctionSignature(), varName);
        } else {
            //if a default value is provided make sure it matches the type of the function parameter declared
            checkFnDeclaresParameterWithType(getFunctionSignature(), varName, defaultValue.getType(), getInvalidDefaultValueTypeErr());
        }

        return new ParameterAnnotationMapping(keyStr, varName, defaultValue != null ? defaultValue : null);
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getRequiredFunctionParameterCardinality()
     */
    @Override
    protected Cardinality getRequiredFunctionParameterCardinality() {
        return Cardinality.ZERO_OR_MORE;
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getInvalidFunctionParameterCardinalityErr()
     */
    @Override
    protected RestXqErrorCode getInvalidFunctionParameterCardinalityErr() {
        return RestXqErrorCodes.RQST0034;
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getRequiredFunctionParameterType()
     */
    @Override
    protected Type getRequiredFunctionParameterType() {
        return Type.ANY_ATOMIC_TYPE;
    }

    /**
     * @see org.exquery.restxq.annotation.AbstractRestAnnotation#getInvalidFunctionParameterTypeErr()
     */
    @Override
    protected RestXqErrorCode getInvalidFunctionParameterTypeErr() {
        return RestXqErrorCodes.RQST0006;
    }
    
    //<editor-fold desc="Error Codes">
    
    /**
     * Get the Error Code for invalid Annotation Parameters
     * 
     * @return The ErrorCode for invalid Annotation Parameters
     */
    protected abstract RestXqErrorCode getInvalidAnnotationParamsErr();
    
    /**
     * Get the Error Code for an invalid Parameter name
     * 
     * @return The ErrorCode for an invalid Parameter name
     */
    protected abstract RestXqErrorCode getInvalidParameterNameErr();
    
    /**
     * Get the Error Code for an invalid Function Argument name
     * 
     * @return The ErrorCode for an invalid Function Argument name
     */
    protected abstract RestXqErrorCode getInvalidFunctionArgumentNameErr();
    
    /**
     * Get the Error Code for an invalid Default Value
     * 
     * @return The ErrorCode for an invalid Default Value
     */
    protected abstract RestXqErrorCode getInvalidDefaultValueErr();
    
    /**
     * Get the Error Code for an invalidly typed Default Value
     * 
     * @return The ErrorCode for an invalidly typed Default Value
     */
    protected abstract RestXqErrorCode getInvalidDefaultValueTypeErr();
    
    /**
     * Get the Error Code for invalid Annotation parameters syntax
     * 
     * @return The ErrorCode for invalid Annotation parameters syntax
     */
    protected abstract RestXqErrorCode getInvalidAnnotationParametersSyntaxErr();
    
    //</editor-fold>
}