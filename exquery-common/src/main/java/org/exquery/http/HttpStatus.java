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
package org.exquery.http;

/**
 * HTTP 1.1 Status Codes from RFC 2616
 * 
 * @see <a href="http://tools.ietf.org/html/rfc2616">RFC 2616: Hypertext Transfer Protocol -- HTTP/1.1</a>
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public enum HttpStatus {
    
    Continue(100),
    Switching_Protocols(101),
    
    OK(200),
    Created(201),
    Accepted(202),
    Non_Authorative_Information(203),
    No_Content(204),
    Reset_Content(205),
    Partial_Content(206),
    
    Multiple_Choices(300),
    Moved_Permanently(301),
    Found(302),
    See_Other(303),
    Not_Modified(304),
    Use_Proxy(305),
    _Unused_(306),
    Temporary_Redirect(307),
    
    Bad_Request(400),
    Unauthorized(401),
    Payment_Required(402),
    Forbidden(403),
    Not_Found(404),
    Method_Not_Allowed(405),
    Not_Acceptable(406),
    Proxy_Authentication_Required(407),
    Request_Timeout(408),
    Conflict(409),
    Gone(410),
    Length_Required(411),
    Precondition_Failed(412),
    Request_Entity_Too_Large(413),
    Request_URI_Too_Long(414),
    Unsupported_Media_Type(415),
    Request_Range_Not_Satisfiable(416),
    Expectation_Failed(417),
    
    Internal_Server_Error(500),
    Not_Implemented(501),
    Bad_Gateway(502),
    Service_Unavailable(503),
    Gateway_Timeout(504),
    Http_Version_Not_Supported(505);
    
    
    final int status;
    HttpStatus(final int status) {
        this.status = status;
    }
    
    /**
     * Gets the Enumerated value from the HTTP Status code
     * 
     * @param code the HTTP Status code
     * 
     * @return The matching HttpStatus Enumerated value
     * 
     * @throws IllegalArgumentException if the HTTP Status is not a valid HTTP
     * 1.1 status
     */
    public static HttpStatus fromStatus(final int code) {
        for(final HttpStatus httpStatus : HttpStatus.values()) {
            if(httpStatus.getStatus() == code) {
                return httpStatus;
            }
        }
        throw new IllegalArgumentException("HTTP Status code: " + code + " is not recognised.");
    }
    
    public int getStatus() {
        return status;
    }
}
