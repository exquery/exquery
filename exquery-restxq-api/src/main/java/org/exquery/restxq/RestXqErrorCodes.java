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
 * Error Codes and descriptions for RESTXQ errors
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class RestXqErrorCodes extends ErrorCodes {
    
    //TODO renumber the below so that they are sequentially ordered
    
    //path annotation errors
    public static RestXqErrorCode RQST0001 = new RestXqErrorCode("RQST0001", "It is a static error if a Path annotation refers to more than one path");
    public static RestXqErrorCode RQST0002 = new RestXqErrorCode("RQST0002", "It is a static error if a Path annotation is not a literal String");
    public static RestXqErrorCode RQST0003 = new RestXqErrorCode("RQST0003", "It is a static error if a Path annotation is empty");
    public static RestXqErrorCode RQST0004 = new RestXqErrorCode("RQST0004", "It is a static error if a Path annotation provides an invalid URI");
    //public static RestXqErrorCode RQST0009 = new RestXqErrorCode("RQST0009", "It is a static error if a function has a REST method annotation, but does not contain a REST path annotation");
    
    //annotated function errors
    public static RestXqErrorCode RQST0005 = new RestXqErrorCode("RQST0005", "It is a static error if an annotated function parameter maps to a function parameter whoose cardinality is not Exactly One");
    public static RestXqErrorCode RQST0006 = new RestXqErrorCode("RQST0006", "It is a static error if an annotated function parameter maps to a function parameter whoose type is not an atomic type");
    public static RestXqErrorCode RQST0007 = new RestXqErrorCode("RQST0007", "It is a static error if an annotated function parameter does not map onto a named function parameter");
    public static RestXqErrorCode RQST0008 = new RestXqErrorCode("RQST0008", "It is a static error if a function mapped by an annotated function parameter has parameters which are not mapped from the annotation and are not optional");
    public static RestXqErrorCode RQST0034 = new RestXqErrorCode("RQST0034", "It is a static error if an annotated function parameter maps to a function parameter whoose cardinality is not Zero or More");
    
    
    //content (put/post) annotation errors
    public static RestXqErrorCode RQST0010 = new RestXqErrorCode("RQST0010", "It is a static error if a REST content method Annotation has more than one literal value");
    public static RestXqErrorCode RQST0011 = new RestXqErrorCode("RQST0011", "It is a static error if a REST content method Annotation is not a literal String");
    public static RestXqErrorCode RQST0012 = new RestXqErrorCode("RQST0012", "It is a static error if a REST content method Annotation has a value and it is empty");
    public static RestXqErrorCode RQST0013 = new RestXqErrorCode("RQST0013", "It is a static error if a REST content method Annotation does not describe a function parameter");
    public static RestXqErrorCode RQDY0014 = new RestXqErrorCode("RQDY0014", "It is a dynamic error if a REST content method Annotation cannot extract the Request body of which the annotation describes");
    public static RestXqErrorCode RQST0033 = new RestXqErrorCode("RQST0033", "It is a static error if a REST content method Annotation maps to a function parameter whoose type is not an item");
    
    //form-param annotation errors
    public static RestXqErrorCode RQST0015 = new RestXqErrorCode("RQST0015", "It is a static error if a REST form-param Annotation does not have two or three literal values");
    public static RestXqErrorCode RQST0016 = new RestXqErrorCode("RQST0016", "It is a static error if a REST form-param Annotations query field is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0017 = new RestXqErrorCode("RQST0017", "It is a static error if a REST form-param Annotations function parameter is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0018 = new RestXqErrorCode("RQST0018", "It is a static error if a REST form-param Annotations default value is present and is not a simple type");
    public static RestXqErrorCode RQST0019 = new RestXqErrorCode("RQST0019", "It is a static error if a REST form-param Annotations default value is present and is not a super-type of function parameter to which it maps");
    public static RestXqErrorCode RQST0020 = new RestXqErrorCode("RQST0020", "It is a static error if a REST form-param Annotation function parameter does not describe a function parameter");
    
    //query-param annotation errors
    public static RestXqErrorCode RQST0021 = new RestXqErrorCode("RQST0021", "It is a static error if a REST query-param Annotation does not have two or three literal values");
    public static RestXqErrorCode RQST0022 = new RestXqErrorCode("RQST0022", "It is a static error if a REST query-param Annotations form field is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0023 = new RestXqErrorCode("RQST0023", "It is a static error if a REST query-param Annotations function parameter is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0024 = new RestXqErrorCode("RQST0024", "It is a static error if a REST query-param Annotations default value is present and is not a simple type");
    public static RestXqErrorCode RQST0025 = new RestXqErrorCode("RQST0025", "It is a static error if a REST query-param Annotations default value is present and is not a super-type of function parameter to which it maps");
    public static RestXqErrorCode RQST0026 = new RestXqErrorCode("RQST0026", "It is a static error if a REST query-param Annotation function parameter does not describe a function parameter");
    
    //header-param annotation errors
    public static RestXqErrorCode RQST0035 = new RestXqErrorCode("RQST0035", "It is a static error if a REST header-param Annotation does not have two or three literal values");
    public static RestXqErrorCode RQST0036 = new RestXqErrorCode("RQST0036", "It is a static error if a REST header-param Annotations header field is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0037 = new RestXqErrorCode("RQST0037", "It is a static error if a REST header-param Annotations function parameter is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0038 = new RestXqErrorCode("RQST0038", "It is a static error if a REST header-param Annotations default value is present and is not a simple type");
    public static RestXqErrorCode RQST0039 = new RestXqErrorCode("RQST0039", "It is a static error if a REST header-param Annotations default value is present and is not a super-type of function parameter to which it maps");
    public static RestXqErrorCode RQST0040 = new RestXqErrorCode("RQST0040", "It is a static error if a REST header-param Annotation function parameter does not describe a function parameter");
    
    //cookie-param annotation errors
    public static RestXqErrorCode RQST0041 = new RestXqErrorCode("RQST0041", "It is a static error if a REST cookie-param Annotation does not have two or three literal values");
    public static RestXqErrorCode RQST0042 = new RestXqErrorCode("RQST0042", "It is a static error if a REST cookie-param Annotations cookie field is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0043 = new RestXqErrorCode("RQST0043", "It is a static error if a REST cookie-param Annotations function parameter is not a literal String or is an empty literal String");
    public static RestXqErrorCode RQST0044 = new RestXqErrorCode("RQST0044", "It is a static error if a REST cookie-param Annotations default value is present and is not a simple type");
    public static RestXqErrorCode RQST0045 = new RestXqErrorCode("RQST0045", "It is a static error if a REST cookie-param Annotations default value is present and is not a super-type of function parameter to which it maps");
    public static RestXqErrorCode RQST0046 = new RestXqErrorCode("RQST0046", "It is a static error if a REST cookie-param Annotation function parameter does not describe a function parameter");
    
    //consumes annotation errors
    public static RestXqErrorCode RQST0027 = new RestXqErrorCode("RQST0027", "It is a static error if a REST consumes Annotation is empty");
    public static RestXqErrorCode RQST0028 = new RestXqErrorCode("RQST0028", "It is a static error if a REST consumes Annotation contains literals which are not strings");
    public static RestXqErrorCode RQST0029 = new RestXqErrorCode("RQST0029", "It is a static error if a REST consumes Annotation contains string literals which are not valid Internet Media Types");
    
    //produces annotation errors
    public static RestXqErrorCode RQST0030 = new RestXqErrorCode("RQST0030", "It is a static error if a REST produces Annotation is empty");
    public static RestXqErrorCode RQST0031 = new RestXqErrorCode("RQST0031", "It is a static error if a REST produces Annotation contains literals which are not strings");
    public static RestXqErrorCode RQST0032 = new RestXqErrorCode("RQST0032", "It is a static error if a REST produces Annotation contains string literals which are not valid Internet Media Types");
    
    public static class RestXqErrorCode extends ErrorCode {

        private RestXqErrorCode(final String code, final String description) {
            super(new QName(Namespace.ANNOTATION_ERROR_NS, code, Namespace.ANNOTATION_ERROR_PREFIX), description);
        }
    }
}