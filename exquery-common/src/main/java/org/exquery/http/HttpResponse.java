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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface of a HTTP Response
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface HttpResponse {
    
    /**
     * Gets the OutputStream for writing the body of the HTTP Response
     * 
     * @return The output stream for the response body
     *
     * @throws IOException if a problem occurs when writing the response body
     */
    public OutputStream getOutputStream() throws IOException;
    
    /**
     * Has the response already been sent to the client
     * 
     * @return true if the response has already been sent, false otherwise
     */
    public boolean isCommitted();
    
    public void setHeader(String httpHeaderName, String value);

    public void setStatus(HttpStatus status, String reason);

    public void setStatus(HttpStatus status);

    public boolean containsHeader(String httpHeaderName);

    public void setContentType(String contentType);
}
