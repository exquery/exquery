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
package org.exquery.restxq;

import javax.xml.namespace.QName;
import org.exquery.ErrorCodes;

/**
 * Error Codes and descriptions for RESTXQ
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class RESTXQErrorCodes extends ErrorCodes {
    
    //path annotation errors
    public static RESTXQErrorCode RQST0001 = new RESTXQErrorCode("RQST0001", "It is a static error if a Path annotation refers to more than one path");
    public static RESTXQErrorCode RQST0002 = new RESTXQErrorCode("RQST0002", "It is a static error if a Path annotation is not a literal String");
    public static RESTXQErrorCode RQST0003 = new RESTXQErrorCode("RQST0003", "It is a static error if a Path annotation is empty");
    public static RESTXQErrorCode RQST0004 = new RESTXQErrorCode("RQST0004", "It is a static error if a Path annotation provides an invalid URI");
    public static RESTXQErrorCode RQST0009 = new RESTXQErrorCode("RQST0009", "It is a static error if a function has a REST method annotation, but does not contain a REST path annotation");
    
    
    //annotated function errors
    public static RESTXQErrorCode RQST0005 = new RESTXQErrorCode("RQST0005", "It is a static error if an annotated function parameter maps to a function parameter whoose cardinality is not Exactly One");
    public static RESTXQErrorCode RQST0006 = new RESTXQErrorCode("RQST0006", "It is a static error if an annotated function parameter maps to a function parameter whoose type is not a simple type");
    public static RESTXQErrorCode RQST0007 = new RESTXQErrorCode("RQST0007", "It is a static error if an annotated function parameter does not map onto a named function parameter");
    public static RESTXQErrorCode RQST0008 = new RESTXQErrorCode("RQST0008", "It is a static error if a function mapped by an annotated function parameter has parameters which are not mapped from the annotation and are not optional");
    
    
    //content (put/post) annotation errors
    public static RESTXQErrorCode RQST0010 = new RESTXQErrorCode("RQST0010", "It is a static error if a REST content method Annotation has more than one literal value");
    public static RESTXQErrorCode RQST0011 = new RESTXQErrorCode("RQST0011", "It is a static error if a REST content method Annotation is not a literal String");
    public static RESTXQErrorCode RQST0012 = new RESTXQErrorCode("RQST0012", "It is a static error if a REST content method Annotation has a value and it is empty");
    public static RESTXQErrorCode RQST0013 = new RESTXQErrorCode("RQST0013", "It is a static error if a REST content method Annotation does not describe a function parameter");
    
    //form-param annotation errors
    public static RESTXQErrorCode RQST0014 = new RESTXQErrorCode("RQST0014", "It is a static error if a REST form-param Annotation does not have two or three literal values");
    public static RESTXQErrorCode RQST0015 = new RESTXQErrorCode("RQST0015", "It is a static error if a REST form-param Annotations form field is not a literal String or is an empty literal String");
    public static RESTXQErrorCode RQST0016 = new RESTXQErrorCode("RQST0016", "It is a static error if a REST form-param Annotations function parameter is not a literal String or is an empty literal String");
    public static RESTXQErrorCode RQST0017 = new RESTXQErrorCode("RQST0017", "It is a static error if a REST form-param Annotations default value is present and is not a simple type");
    public static RESTXQErrorCode RQST0018 = new RESTXQErrorCode("RQST0018", "It is a static error if a REST form-param Annotations default value is present and is not a super-type of function parameter to which it maps");
    public static RESTXQErrorCode RQST0019 = new RESTXQErrorCode("RQST0019", "It is a static error if a REST form-param Annotation function parameter does not describe a function parameter");
    
    //query-param annotation errors
    public static RESTXQErrorCode RQST0020 = new RESTXQErrorCode("RQST0020", "It is a static error if a REST query-param Annotation does not have two or three literal values");
    public static RESTXQErrorCode RQST0021 = new RESTXQErrorCode("RQST0021", "It is a static error if a REST query-param Annotations form field is not a literal String or is an empty literal String");
    public static RESTXQErrorCode RQST0022 = new RESTXQErrorCode("RQST0022", "It is a static error if a REST query-param Annotations function parameter is not a literal String or is an empty literal String");
    public static RESTXQErrorCode RQST0023 = new RESTXQErrorCode("RQST0023", "It is a static error if a REST query-param Annotations default value is present and is not a simple type");
    public static RESTXQErrorCode RQST0024 = new RESTXQErrorCode("RQST0024", "It is a static error if a REST query-param Annotations default value is present and is not a super-type of function parameter to which it maps");
    public static RESTXQErrorCode RQST0025 = new RESTXQErrorCode("RQST0025", "It is a static error if a REST query-param Annotation function parameter does not describe a function parameter");
    
    
    public static class RESTXQErrorCode extends ErrorCode {

        private RESTXQErrorCode(final String code, final String description) {
            super(new QName(Namespace.ANNOTATION_ERROR_NS, code, Namespace.ANNOTATION_ERROR_PREFIX), description);
        }
    }
}