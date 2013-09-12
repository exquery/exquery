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
import org.exquery.ErrorCodes;
import org.exquery.serialization.Namespace;

/**
 * Error Codes and descriptions for Serialization Annotation Errors
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class SerializationAnnotationErrorCodes extends ErrorCodes {

    //output method
    public static SerializationAnnotationErrorCode SEST0001 = new SerializationAnnotationErrorCode("SEST0001", "It is a static error if a Serialization Output method Annotation has more than one literal value");
    public static SerializationAnnotationErrorCode SEST0002 = new SerializationAnnotationErrorCode("SEST0002", "It is a static error if a Serialization Output method Annotation has an empty value");
    public static SerializationAnnotationErrorCode SEST0003 = new SerializationAnnotationErrorCode("SEST0003", "It is a static error if a Serialization Output method Annotation does not describe a valid output method");
    
    //indent
    public static SerializationAnnotationErrorCode SEST0004 = new SerializationAnnotationErrorCode("SEST0004", "It is a static error if a Serialization Output indent Annotation has more than one literal value");
    public static SerializationAnnotationErrorCode SEST0005 = new SerializationAnnotationErrorCode("SEST0005", "It is a static error if a Serialization Output indent Annotation has an empty value");
    public static SerializationAnnotationErrorCode SEST0006 = new SerializationAnnotationErrorCode("SEST0006", "It is a static error if a Serialization Output indent Annotation does not have a parameter value of 'yes' or 'no'");
    
    //omit-xml-declaration
    public static SerializationAnnotationErrorCode SEST0007 = new SerializationAnnotationErrorCode("SEST0007", "It is a static error if a Serialization Output omit-xml-declaration Annotation has more than one literal value");
    public static SerializationAnnotationErrorCode SEST0008 = new SerializationAnnotationErrorCode("SEST0008", "It is a static error if a Serialization Output omit-xml-declaration Annotation has an empty value");
    public static SerializationAnnotationErrorCode SEST0009 = new SerializationAnnotationErrorCode("SEST0009", "It is a static error if a Serialization Output omit-xml-declaration Annotation does not have a parameter value of 'yes' or 'no'");
    
    //media-type
    public static SerializationAnnotationErrorCode SEST0010 = new SerializationAnnotationErrorCode("SEST0010", "It is a static error if a Serialization Output media-type Annotation has more than one literal value");
    public static SerializationAnnotationErrorCode SEST0011 = new SerializationAnnotationErrorCode("SEST0011", "It is a static error if a Serialization Output media-type Annotation has an empty value");
    public static SerializationAnnotationErrorCode SEST0012 = new SerializationAnnotationErrorCode("SEST0012", "It is a static error if a Serialization Output media-type Annotation does not describe a valid Internet Media Type");
    
    //encoding
    public static SerializationAnnotationErrorCode SEST0013 = new SerializationAnnotationErrorCode("SEST0013", "It is a static error if a Serialization Output encoding Annotation has more than one literal value");
    public static SerializationAnnotationErrorCode SEST0014 = new SerializationAnnotationErrorCode("SEST0014", "It is a static error if a Serialization Output encoding Annotation has an empty value");
    public static SerializationAnnotationErrorCode SEST0015 = new SerializationAnnotationErrorCode("SEST0015", "It is a static error if a Serialization Output encoding Annotation does not describe a valid character encoding");
    
    public static class SerializationAnnotationErrorCode extends ErrorCode {

        private SerializationAnnotationErrorCode(String code, String description) {
            super(new QName(Namespace.ANNOTATION_ERROR_NS, code, Namespace.ANNOTATION_ERROR_PREFIX), description);
        }
    }
}