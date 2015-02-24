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
package org.exquery.xquery;

import javax.xml.namespace.QName;
import org.exquery.Namespace;

/**
 * Error Codes caused by Static of Dynamic errors
 * when compiling or evaluating an XQuery 1.0
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class ErrorCodes {

    /**
     * Base class of either a static or dynamic error
     * caused during compilation or evaluation of an
     * XQuery
     */
    public static class ErrorCode {
        
        private final QName code;
        private final String description;
        
        /**
         * @param code A short code for error
         * @param description A description of the error
         */
        public ErrorCode(final String code, final String description) {
            this.code = new QName(Namespace.EXQUERY_ERROR_NS, code, Namespace.EXQUERY_ERROR_PREFIX);
            this.description = description;
        }

        /**
         * @param code The code of the error
         * @param description A description of the error
         */
        public ErrorCode(final QName code, final String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * Gets the code of the error
         * 
         * @return the error code
         */
        public QName getCode() {
            return code;
        }

        /**
         * Gets a string representation of the error
         * 
         * @return a string representation in the format (NAMESPACE#CODE):DESCRIPTION
         */
        @Override
        public String toString() {
            return "(" + code.getNamespaceURI() + "#" + code.getLocalPart() + "):" + description;
        }

        public String getDescription(){
            return description;
        }
    }
}