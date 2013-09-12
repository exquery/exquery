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
package org.exquery.restxq.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.exquery.http.HttpMethod;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.RestXqService;
import org.exquery.restxq.RestXqServiceRegistry;
import org.exquery.restxq.RestXqServiceRegistryListener;
import org.exquery.restxq.impl.RestXqServicesMap.RestXqServiceMapVisitor;

/**
 * Simple Implementation of a Registry of RESTXQ Services
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class RestXqServiceRegistryImpl implements RestXqServiceRegistry {

    private final RestXqServicesMap services = new RestXqServicesMap();
    private final List<RestXqServiceRegistryListener> listeners = new ArrayList<RestXqServiceRegistryListener>();
    
    private RestXqServicesMap getServices() {
        return services;
    }
    
    @Override
    public void register(final RestXqService service) {
        final EnumSet<HttpMethod> servicedMethods = service.getServicedMethods();

        if(servicedMethods.isEmpty()) {
            
            //TODO future work - if no method annotation, this service could apply to ANY method!

        } else {
            for(final HttpMethod servicedMethod : servicedMethods) {
                getServices().put(servicedMethod, service);
                for(final RestXqServiceRegistryListener listener : listeners) {
                    listener.registered(service);
                }
            }
        }
    }

    @Override
    public void register(final Iterable<RestXqService> services) {
        for(final RestXqService service : services) {
            register(service);
        }
    }

    @Override
    public Iterator<RestXqService> iterator() {
        
        final Set<RestXqService> uniqueServices = new HashSet<RestXqService>();
        final RestXqServiceMapVisitor visitor = new RestXqServiceMapVisitor() {
                @Override
                public void visit(final HttpMethod method, final List<RestXqService> restXqServices) {
                    uniqueServices.addAll(restXqServices);
                }
            };
        getServices().iterate(visitor, true);
        
        return uniqueServices.iterator();
    }
    
    @Override
    public RestXqService findService(final HttpRequest request) {
        return getServices().get(request.getMethod(), request);
        
        //TODO future work - else consult the ANY METHOD bucket?
    }
    
    @Override
    public void deregister(final URI xqueryLocation) {
        getServices().removeAll(xqueryLocation, listeners);
    }
    
    @Override
    public void deregister(final RestXqService service) {
        getServices().remove(service, listeners);
    }
    
    /**
     * Add a Registry Listener to this Registry to receive event notification
     * 
     * @param listener The Registry Listener to receive notifications
     */
    public void addListener(final RestXqServiceRegistryListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a Registry Listener from this Registry
     * 
     * @param listener The Registry Listener which should no longer receive
     * notifications from this registry
     */
    public boolean removeListener(final RestXqServiceRegistryListener listener) {
        return listeners.remove(listener);
    }
    
    /**
     * Remove all Registry Listeners from receiving notifications from this Registry
     */
    public void clearListeners() {
        listeners.clear();
    }
}