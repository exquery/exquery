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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.exquery.annotation.AbstractAnnotation;
import org.exquery.restxq.RestXqErrorCodes;
import org.exquery.restxq.RestXqErrorCodes.RestXqErrorCode;
import org.exquery.restxq.annotation.RestAnnotation;
import org.exquery.restxq.annotation.RestAnnotationException;
import org.exquery.xquery.Cardinality;
import org.exquery.xquery.FunctionArgument;
import org.exquery.xquery.Type;
import org.exquery.xquery3.FunctionSignature;

/**
 * Base class for RESTXQ Annotation Implementations
 * 
 * @author Adam Retter
 */
public abstract class AbstractRestAnnotation extends AbstractAnnotation<RestAnnotationException> implements RestAnnotation {
    
    /**
     * Matches a function parameter e.g. {$id}
     */
    protected final static String functionArgumentRegExp = "\\{\\$((?:[A-Za-z0-9_\\-]+:)?[A-Za-z0-9_\\-]+)\\}";  //TODO make sure this expression is correct - at the moment its too lax for function parameter syntax
    protected final static Pattern functionArgumentPattern = Pattern.compile(functionArgumentRegExp);
    
    /**
     * Checks that a function declares a named argument with a specific compatible type
     * 
     * 
     * @param functionSignature The function signature to check for the named argument with compatible type
     * @param fnArgumentName The name of the argument to check the function signature for
     * @param requiredArgumentType The required type of the argument to check the function signature for
     * @param errorCode An error code to return in the exception of the required type could not be found
     * 
     * @throws RestAnnotationException If the named argument with a compatible required type is not declared by the function signature
     */
    protected void checkFnDeclaresParameterWithType(final FunctionSignature functionSignature, final String fnArgumentName, final Type requiredArgumentType, final RestXqErrorCode errorCode) throws RestAnnotationException {
        
        final FunctionArgument[] fnArguments = functionSignature.getArguments();
        
        boolean found = false;
        for(final FunctionArgument fnArgument : fnArguments) {

            if(fnArgument.getName().equals(fnArgumentName)) {
                
                //if(!fnArgument.getType().isSubTypeOf(requiredArgumentType)) {
                if(!hasCompatibleType(fnArgument.getType(), requiredArgumentType)) {
                    throw new RestAnnotationException(errorCode);
                }

                found = true;
                break;
            }
        }
        
        if(!found) {
            throw new RestAnnotationException(RestXqErrorCodes.RQST0007);
        }
    }
    
    /**
     * Checks that a function declares a named argument
     *
     * @param functionSignature The function signature to check for the named argument
     * @param fnArgumentName The name of the argument to check the function signature for
     * 
     * @throws RestAnnotationException If the function arguments are not compatible with the function signature
     */
    protected void checkFnDeclaresParameter(final FunctionSignature functionSignature, final String fnArgumentName) throws RestAnnotationException {
        checkFnDeclaresParameter(functionSignature, fnArgumentName, getRequiredFunctionParameterCardinality());
    }
    
    //TODO remove above fn wrapper and remove abstract getRequiredFunctionParameterCardinality() method
    protected void checkFnDeclaresParameter(final FunctionSignature functionSignature, final String fnArgumentName, final Cardinality requiredCardinality) throws RestAnnotationException {
        final List<String> fnParamNames = new ArrayList<String>(1);
        fnParamNames.add(fnArgumentName);
        
        checkFnDeclaresParameters(functionSignature, fnParamNames, requiredCardinality);
    }
    
    /**
     * Checks that a function has parameters which are compatible
     * with those declared by an annotation on that function
     *
     * @param functionSignature The function signature to check for declared parameters
     * @param fnArgumentNames The Function arguments to check against the function signature
     * 
     * @throws RestAnnotationException If the function arguments are not compatible with the function signature
     */
    protected void checkFnDeclaresParameters(final FunctionSignature functionSignature, final List<String> fnArgumentNames) throws RestAnnotationException {
        checkFnDeclaresParameters(functionSignature, fnArgumentNames, getRequiredFunctionParameterCardinality());
    }
    
    //TODO rm above fn wrapper and remove abstract getRequiredFunctionParameterCardinality() method
    protected void checkFnDeclaresParameters(final FunctionSignature functionSignature, final List<String> fnArgumentNames, final Cardinality requiredCardinality) throws RestAnnotationException {
        
        final FunctionArgument[] fnArguments = functionSignature.getArguments();
        
        //1) make sure that each path parameter maps onto a function parameter
        for(final String fnArgumentName : fnArgumentNames) {
            boolean found = false;
            
            for(final FunctionArgument fnArgument : fnArguments) {
                
                if(fnArgument.getName().equals(fnArgumentName)) {
                    
                    if(!fnArgument.getCardinality().hasRequiredCardinality(requiredCardinality)) {
                        throw new RestAnnotationException(getInvalidFunctionParameterCardinalityErr());
                    }
                    
                    //if(!(fnArgument.getType().hasSubType(getRequiredFunctionParameterType()) | getRequiredFunctionParameterType().hasSubType(fnArgument.getType()))) {
                    if(!hasCompatibleType(fnArgument.getType(), getRequiredFunctionParameterType())) {
                        throw new RestAnnotationException(getInvalidFunctionParameterTypeErr());
                    }
                    
                    found = true;
                    break;
                }
            }
            
            if(!found) {
                throw new RestAnnotationException(RestXqErrorCodes.RQST0007);
            }
        }
    }

    private boolean hasCompatibleType(final Type actualType, final Type requiredType) {
        return actualType.hasSubType(requiredType) | requiredType.hasSubType(actualType);
    }
    
    /**
     * Get the Cardinality of Function Parameters required by parameters
     * in this Resource Function annotation
     * 
     * @return The required Cardinality
     */
    protected abstract Cardinality getRequiredFunctionParameterCardinality();

    /**
     * Get the Error Code for invalid Function Parameter Cardinality
     * 
     * @return The ErrorCode for invalid Function parameter cardinality
     */
    protected abstract RestXqErrorCode getInvalidFunctionParameterCardinalityErr();

    /**
     * Get the Type of Function Parameters required by parameters
     * in this Resource Function annotation
     * 
     * @return The required Type
     */
    protected abstract Type getRequiredFunctionParameterType();
    
    /**
     * Get the Error Code for invalid Function Parameter Type
     * 
     * @return The ErrorCode for invalid Function parameter type
     */
    protected abstract RestXqErrorCode getInvalidFunctionParameterTypeErr();
}