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
package org.exquery.serialization.annotation;

import javax.xml.namespace.QName;
import org.exquery.annotation.AnnotationException;
import org.exquery.serialization.Namespace;
import org.exquery.xquery3.Annotation;

/**
 * Factory for instantiating and configuring Serialization Annotations
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class SerializationAnnotationFactory {
    
    /**
     * Determines if the Annotation Name is a valid Serialization Annotation
     * 
     * @param annotationName The name of the annotation to test
     * 
     * @return true if the Annotation Name is a valid Serialization Annotation, false otherwise
     */
    public static boolean isSerializationAnnotation(final QName annotationName) {
        return annotationName.getNamespaceURI().equals(Namespace.ANNOTATION_NS);
    }
    
    /**
     * Gets a Serialization Annotation from an XQuery 3.0 Annotation
     * 
     * @param annotation The XQuery 3.0 Annotation
     * 
     * @return The SerializationAnnotation
     * 
     * @throws AnnotationException If the provided annotation is not a valid Serialization Annotation
     */
    public static SerializationAnnotation getAnnotation(final Annotation annotation) throws AnnotationException {
        if(isSerializationAnnotation(annotation.getName())) {
            return getSerializationAnnotation(annotation);
        } else {
            throw new IllegalArgumentException("Unknown annotation: " + annotation.getName().toString());
        }
    }

    private static SerializationAnnotation getSerializationAnnotation(final Annotation annotation) throws SerializationAnnotationException {
        final SerializationAnnotationName an = SerializationAnnotationName.valueOf(annotation.getName());
        
        final AbstractSerializationAnnotation serializationAnnotation;
        switch(an) {
            case method:
                serializationAnnotation = new MethodAnnotation();
                break;
            
            case indent:
                serializationAnnotation = new IndentAnnotation();
                break;
                
            case omitxmldeclaration:
                serializationAnnotation = new OmitXmlDeclarationAnnotation();
                break;
                
            case mediatype:
                serializationAnnotation = new MediaTypeAnnotation();
                break;
                
            default:
                throw new IllegalArgumentException("Unknown annotation: " + annotation.getName().toString());
        }
        
        serializationAnnotation.setName(annotation.getName());
        serializationAnnotation.setFunctionSignature(annotation.getFunctionSignature());
        serializationAnnotation.setLiterals(annotation.getLiterals());
        serializationAnnotation.initialise();
        
        return serializationAnnotation;
    }
}