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

import java.net.URI;
import java.util.Iterator;
import org.exquery.http.HttpRequest;

/**
 * Registry of RESTXQ Services
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public interface RestXqServiceRegistry extends Iterable<RestXqService> {
    
    /**
     * Register a RESTXQ Service with the registry
     * 
     * @param service The Service to register
     */
    public void register(RestXqService service);
    
    /**
     * Register several RESTXQ Services with the registry
     *
     * @param services The Services to register with the registry
     */
    public void register(Iterable<RestXqService> services);
    
    /**
     * De-register RESTXQ Services from the registry
     * 
     * @param xqueryLocation The URI of the XQuery from which the Services came to deregister
     */
    public void deregister(URI xqueryLocation);
    
    /**
     * Iterate through the RESTXQ Services in the registry
     * 
     * @return Iterator over RestXqService(s)
     */
    @Override
    public Iterator<RestXqService> iterator();
    
    /**
     * Find the RESTXQ Service which can Service the HTTP Request
     * 
     * @param request The HTTP Request for which to try and find a matching RESTXQ Service
     * which can service the HTTP Request
     * 
     * @return The RESTXQ Service which can service the request, or null if no
     * suitable RESTXQ Service can be found
     */
    public RestXqService findService(HttpRequest request);
}