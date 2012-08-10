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
package org.exquery.restxq.impl;

import java.net.URI;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.exquery.http.HttpMethod;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.RestXqService;
import org.exquery.restxq.RestXqServiceRegistryListener;

/**
 * Simple Thread-Safe Map that maintains the association between HttpMethods
 * and RestXqServices
 *
 * @author Adam Retter <adam.retter@googlemail.com>
 */
public class RestXqServicesMap {
        
    private final Map<HttpMethod, List<RestXqService>> orderedServices = new EnumMap<HttpMethod, List<RestXqService>>(HttpMethod.class);
    private final Map<HttpMethod, ReentrantReadWriteLock> methodLocks = new EnumMap<HttpMethod, ReentrantReadWriteLock>(HttpMethod.class);

    /**
     * Put a RESTXQ Service in the Map
     * 
     * @param method The HttpMethod with which to associate the Service
     * @param service The RESTXQ Service to register for the HttpMethod
     * 
     * @return The previous Service associated with the method,
     * or null if no previous service was registered
     */
    public RestXqService put(final HttpMethod method, final RestXqService service) {

        final ReentrantReadWriteLock lock = getOrCreateMethodLock(method);
        
        try {
            lock.writeLock().lock();

            List<RestXqService> list = orderedServices.get(method);
            if(list == null) {
                list = new ArrayList<RestXqService>();
            }

            RestXqService oldValue = null;
            int oldIndex = list.indexOf(service);
            if(oldIndex > -1) {
                oldValue = list.remove(oldIndex);
            }

            list.add(service);
            Collections.sort(list);

            orderedServices.put(method, list);

            return oldValue;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets the RESTXQ Service from the Map
     * 
     * @param method The HTTP Method to get the Service for
     * @param request The HTTP Request to get the Service for
     * 
     * @return The RESTXQ Service that matches the method and request
     */
    public RestXqService get(final HttpMethod method, final HttpRequest request) {
        final ReentrantReadWriteLock lock = getOrCreateMethodLock(method);
        
        try {
            lock.readLock().lock();
            
            final List<RestXqService> list = orderedServices.get(method);
            if(list == null) {
                return null;
            }
            
            for(final RestXqService value : list) {
                if(value.canService(request)) {
                    return value;
                }
            }
            
            return null;
            
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Removes all RESTXQ Services from the Map that come from the XQuery
     * located at the URI xqueryLocation
     * 
     * @param xqueryLocation The location of the XQuery
     * @param listeners Any Listseners that should be notified when a
     * Service is removed
     */
    public void removeAll(final URI xqueryLocation, final List<RestXqServiceRegistryListener> listeners) {
            
        for(final HttpMethod key : orderedServices.keySet()) {
            
            final ReentrantReadWriteLock lock = getOrCreateMethodLock(key);
            try {
                lock.writeLock().lock();
                

                final List<RestXqService> servicesToRemove = new ArrayList<RestXqService>();
                final List<RestXqService> serviceList = orderedServices.get(key);
                
                for(RestXqService service : serviceList) {
                    if(service.getResourceFunction().getXQueryLocation().equals(xqueryLocation)) {
                        //label the service to remove
                        servicesToRemove.add(service);
                    }
                }

                if(!servicesToRemove.isEmpty()) {
                    //remove the labelled service
                    for(final RestXqService serviceToRemove : servicesToRemove) {
                        serviceList.remove(serviceToRemove);
                    }

                    //update the service list
                    orderedServices.put(key, serviceList);
                    
                    //update the listeners
                    for(final RestXqServiceRegistryListener listener : listeners) {
                        for(final RestXqService service : servicesToRemove) {
                            listener.deregistered(service);
                        }
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * Gets of Creates a Lock for a specific HTTP Method
     * 
     * @param method The HTTP Method to Get a lock object for
     * 
     * @return The Lock to use for the HTTP Method
     */
    private ReentrantReadWriteLock getOrCreateMethodLock(final HttpMethod method) {
        synchronized(methodLocks) {
            ReentrantReadWriteLock lock = methodLocks.get(method);
            if(lock == null) {
                lock = new ReentrantReadWriteLock();
                methodLocks.put(method, lock);
            }
            return lock;
        }
    }
}