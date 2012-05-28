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
    public static SerializationAnnotationErrorCode SEST0001 = new SerializationAnnotationErrorCode("SEST0001", "It is a static error if a Serialization Output Method Annotation has more than one literal value");
    public static SerializationAnnotationErrorCode SEST0002 = new SerializationAnnotationErrorCode("SEST0002", "It is a static error if a Serialization Output Method Annotation has an empty value");
    public static SerializationAnnotationErrorCode SEST0003 = new SerializationAnnotationErrorCode("SEST0003", "It is a static error if a Serialization Output Method Annotation does not describe a valid output method");
    
    public static class SerializationAnnotationErrorCode extends ErrorCode {

        private SerializationAnnotationErrorCode(String code, String description) {
            super(new QName(Namespace.ANNOTATION_ERROR_NS, code, Namespace.ANNOTATION_ERROR_PREFIX), description);
        }
    }
}