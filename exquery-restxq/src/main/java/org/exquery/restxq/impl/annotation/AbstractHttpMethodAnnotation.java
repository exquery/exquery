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

import javax.xml.namespace.QName;
import org.exquery.restxq.Namespace;
import org.exquery.restxq.RESTXQAnnotationException;
import org.exquery.restxq.RESTXQErrorCodes;
import org.exquery.restxq.annotation.HttpMethodAnnotation;
import org.exquery.xquery3.Annotation;

/**
 * Base class for RESTXQ Method Annotation Implementations
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public abstract class AbstractHttpMethodAnnotation extends AbstractRESTAnnotation implements HttpMethodAnnotation {

    @Override
    public void initialise() throws RESTXQAnnotationException {
        super.initialise();
        checkForPathAnnotation();
    }

    private void checkForPathAnnotation() throws RESTXQAnnotationException {
        for(Annotation annotation : getFunctionSignature().getAnnotations()) {
            if(annotation.getName().equals(new QName(Namespace.ANNOTATION_NS, "path"))) { //TODO having this hardcoded QName here is not ideal!
                return;
            }
        }
	        
        throw new RESTXQAnnotationException(RESTXQErrorCodes.RQST0009);
    }
}
