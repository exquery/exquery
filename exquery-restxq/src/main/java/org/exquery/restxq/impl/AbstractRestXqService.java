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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import org.exquery.http.HttpMethod;
import org.exquery.http.HttpRequest;
import org.exquery.http.HttpResponse;
import org.exquery.restxq.ResourceFunction;
import org.exquery.restxq.ResourceFunctionExecuter;
import org.exquery.restxq.RestXqService;
import org.exquery.restxq.RestXqServiceException;
import org.exquery.restxq.RestXqServiceSerializer;
import org.exquery.restxq.annotation.ConsumesAnnotation;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.restxq.annotation.HttpMethodWithBodyAnnotation;
import org.exquery.restxq.annotation.ParameterAnnotation;
import org.exquery.restxq.annotation.ProducesAnnotation;
import org.exquery.xdm.type.SequenceImpl;
import org.exquery.xdm.type.StringTypedValue;
import org.exquery.xquery.FunctionSignature;
import org.exquery.xquery.Sequence;
import org.exquery.xquery.TypedArgumentValue;

/**
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractRestXqService implements RestXqService {

    private final ResourceFunction resourceFunction;

    public AbstractRestXqService(final ResourceFunction resourceFunction) {
        this.resourceFunction = resourceFunction;
    }
    
    /**
     * @see org.exquery.restxq.RestXqService#getResourceFunction() 
     */
    @Override
    public ResourceFunction getResourceFunction() {
        return resourceFunction;
    }
    
    /**
     * @see org.exquery.restxq.RestXqService#getServicedMethods() 
     */
    @Override
    public EnumSet<HttpMethod> getServicedMethods() {
        final EnumSet<HttpMethod> servicedMethods = EnumSet.noneOf(HttpMethod.class);
        for(final HttpMethodAnnotation httpMethodAnnotation : getResourceFunction().getHttpMethodAnnotations()) {
            servicedMethods.add(httpMethodAnnotation.getHttpMethod());
        }
        return servicedMethods;
    }

    /**
     * Determines if this RESTXQ Service can service the request
     * 
     * Rules are (must all apply):
     * 1) Can this Service service the HTTP Method of the request
     * 2) Does the ResourceFunction of this RESTXQ Service apply to the Request Path
     * 
     * @see org.exquery.restxq.RestXqService#canService(org.exquery.http.HttpRequest)
     */
    @Override
    public boolean canService(final HttpRequest request) {
        
        //1) check the method matches
        if(getServicedMethods().contains(request.getMethod())) {
            
            //2) check the path matches
            if(getResourceFunction().getPathAnnotation().matchesPath(request.getPath())) {
                
                if(!canServiceConsume(request)) {
                    //TODO HTTP 415 - in RESTXQServiceRegistry.findService, if no service matches we should be able to return a HTTP reason!
                    return false;
                }
                
                if(!canServiceProduce(request)) {
                    //TODO HTTP 406 - in RESTXQServiceRegistry.findService, if no service matches we should be able to return a HTTP reason!
                    return false;
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    private boolean canServiceConsume(final HttpRequest request) {
        if(getResourceFunction().getConsumesAnnotations().isEmpty()) {
            
            //if there are no constraints we can consume anything
            return true;
        } else {
            for(final ConsumesAnnotation consumesAnnotation : getResourceFunction().getConsumesAnnotations()) {
                if(consumesAnnotation.matchesMediaType(request)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean canServiceProduce(final HttpRequest request) {
        if(getResourceFunction().getProducesAnnotations().isEmpty()) {
            
            //if there are no constraints we can produce anything
            return true;
        } else {
            for(final ProducesAnnotation producesAnnotation : getResourceFunction().getProducesAnnotations()) {
                if(producesAnnotation.matchesMediaType(request)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Service the request and send the response
     * 
     * @see org.exquery.restxq.RestXqService#service(org.exquery.http.HttpRequest, org.exquery.http.HttpResponse, org.exquery.restxq.ResourceFunctionExecuter, org.exquery.restxq.RestXqServiceSerializer)
     */
    @Override
    public void service(final HttpRequest request, final HttpResponse response, final ResourceFunctionExecuter resourceFunctionExecuter, final RestXqServiceSerializer restXqServiceSerializer) throws RestXqServiceException {
        
        final Set<TypedArgumentValue> typedArgumentValues = extractParameters(request);
        
        final Sequence result = resourceFunctionExecuter.execute(getResourceFunction(), typedArgumentValues);
        
        restXqServiceSerializer.serialize(result, getResourceFunction().getSerializationAnnotations(), response);
    }

    /**
     * Gets the HTTP Method Annotations which potentially have Body Content
     * 
     * @return The HTTP Method Annotations with a potential body parameter
     */
    protected Set<HttpMethodWithBodyAnnotation> getBodyContentAnnotations() {
        final Set<HttpMethodWithBodyAnnotation> bodyContentAnnotations = new HashSet<HttpMethodWithBodyAnnotation>();
        for(final HttpMethodAnnotation methodAnnotation : getResourceFunction().getHttpMethodAnnotations()) {
            if(methodAnnotation instanceof HttpMethodWithBodyAnnotation) {
                bodyContentAnnotations.add((HttpMethodWithBodyAnnotation)methodAnnotation);
            }
        }
        return bodyContentAnnotations;
    }
    
    
    /**
     * Extract Annotated Parameters from the Request
     * 
     * @param request The HTTP Request to process
     *
     * @return The Map of Parameters to values, the key is the parameter
     * name and the value is the sequence of values extracted from the request
     *
     * @throws RestXqServiceException If an error occured whilst processing the request
     */
    protected Set<TypedArgumentValue> extractParameters(final HttpRequest request) throws RestXqServiceException {
        
        final Set<TypedArgumentValue> paramNameValues = new HashSet<TypedArgumentValue>();
        
        //extract the param mappings for the Path Annotation
        for(final Entry<String, String> pathParameter : getResourceFunction().getPathAnnotation().extractPathParameters(request.getPath()).entrySet()) {
            
            paramNameValues.add(new TypedArgumentValue<String>(){
                @Override
                public String getArgumentName() {
                    return pathParameter.getKey();
                }

                @Override
                public Sequence<String> getTypedValue() {
                    return new SequenceImpl<String>(new StringTypedValue(pathParameter.getValue()));
                }
            });
        }
        
        //extract the param mappings for the Body Content Annotations
        if(!getBodyContentAnnotations().isEmpty()) {
            final Sequence requestBody = extractRequestBody(request);
            for(final HttpMethodWithBodyAnnotation bodyContentAnnotation : getBodyContentAnnotations()) {
                paramNameValues.add(new TypedArgumentValue(){
                    @Override
                    public String getArgumentName() {
                        return bodyContentAnnotation.getBodyParameterName();
                    }

                    @Override
                    public Sequence getTypedValue() {
                        return requestBody;
                    }
                });
            }
        }
        
        //extract the param mappings for Param Annotations
        for(final ParameterAnnotation parameterAnnotation : getResourceFunction().getParameterAnnotations()) {
            final TypedArgumentValue typedArgumentValue = parameterAnnotation.extractParameter(request);
            paramNameValues.add(new TypedArgumentValue(){

                @Override
                public String getArgumentName() {
                    return typedArgumentValue.getArgumentName();
                }

                @Override
                public Sequence getTypedValue() {
                    return typedArgumentValue.getTypedValue();
                }
            });
        }
        
        return paramNameValues;
    }
    
    /**
     * Extract the HTTP Request Body
     * 
     * Implementations are free to return
     * a proxy which lazily extracts the request body
     * if desired.
     * 
     * @param request The HTTP Request to extract the request body from
     * 
     * @return The Sequence of values extracted from the request body,
     * typically a single item but possibly more for a multi-part request
     * 
     * @throws RestXqServiceException If an error occurred whilst processing the Request Body
     */
    protected abstract Sequence extractRequestBody(final HttpRequest request) throws RestXqServiceException;
    
    /**
     * Generates a Hash Code for the RestXqService Object
     * 
     * XORs together:
     *  1) the URI to the XQuery containing the Resource Function
     *  2) the name of the Resource Function
     *  3) the arity of the Resource Function
     */
    @Override
    public int hashCode() {
        final FunctionSignature fnSignature = getResourceFunction().getFunctionSignature();
        return getResourceFunction().getXQueryLocation().hashCode()
        ^ fnSignature.getName().hashCode()
        ^ fnSignature.getArgumentCount() * 32;
    }

    /**
     * Determines if this Service is equal to another Service
     * 
     * Compares:
     *  1) the URI to the XQuery containing the Resource Function
     *  2) the name of the Resource Function
     *  3) the arity of the Resource Function
     */
    @Override
    public boolean equals(final Object obj) {
        final FunctionSignature fnSignature = getResourceFunction().getFunctionSignature();
        
        if(obj == null) {
            return false;
        }
        
        if(!(obj instanceof RestXqService)) {
            return false;
        }
        
        final RestXqService other = ((RestXqService)obj);
        
        return
            other.getResourceFunction().getXQueryLocation().equals(getResourceFunction().getXQueryLocation())
            && other.getResourceFunction().getFunctionSignature().getName().equals(fnSignature.getName())
            && other.getResourceFunction().getFunctionSignature().getArgumentCount() == fnSignature.getArgumentCount();
    }

    /**
     * Sorts the Services into Path Specificity descending order
     * That is to say that the resultant sorted list should have the most specific URI's at the top! 
     *
     * @param other Another Service
     */
    @Override
    public int compareTo(final RestXqService other) {
        if(other == null || !(other instanceof RestXqService)) {
            return 1;
        }
        
        long result = getResourceFunction().getPathAnnotation().getPathSpecificityMetric() - ((RestXqService)other).getResourceFunction().getPathAnnotation().getPathSpecificityMetric();
        if(result > 0) {
            return 1;
        } else if(result < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}