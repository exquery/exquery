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
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.exquery.http.AcceptHeader;
import org.exquery.http.HttpHeader;
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
     * Visitor Interface for iterating over the RESTXQ Services Map
     */
    protected interface RestXqServiceMapVisitor {

        /**
         * Visits a HTTP Method in the RestXQ Services Map
         * 
         * @param method The HTTP Method for which the services are registered
         * @param restXqServices The services registered against the HTTP Method
         */
        public void visit(final HttpMethod method, final List<RestXqService> restXqServices);
    }
    
    /**
     * Iterate over the Services Map
     * 
     * @param visitor The visitor which visits the services map
     * @param eagerLockAll true if all methods should be locked before visiting anything, false to lock methods as they are visited
     * 
     */
    public void iterate(final RestXqServiceMapVisitor visitor, final boolean eagerLockAll) {
        
        try {
            
            if(eagerLockAll) {
                for(final HttpMethod method : HttpMethod.values()) {
                    final ReentrantReadWriteLock lock = getOrCreateMethodLock(method);
                    lock.readLock().lock();

                }
                
                for(final HttpMethod method : HttpMethod.values()) {
                    final List<RestXqService> services = orderedServices.get(method);
                    if(services != null) {
                        visitor.visit(method, services);
                    }
                }
            } else {
            
                for(final HttpMethod method : HttpMethod.values()) {
                    final ReentrantReadWriteLock lock = getOrCreateMethodLock(method);
                    
                    lock.readLock().lock();
                    
                    final List<RestXqService> services = orderedServices.get(method);
                    if(services != null) {
                        visitor.visit(method, services);
                    }
                }
            }
            
        } finally {
            for(final HttpMethod method : HttpMethod.values()) {
                final ReentrantReadWriteLock lock = getOrCreateMethodLock(method);
                lock.readLock().unlock();

            }
        }
    }
    
    
    /**
     * Gets the RESTXQ Service from the Map
     * 
     * @param method The HTTP Method to get the Service for
     * @param request The HTTP Request to get the Service for
     * 
     * @return The RESTXQ Service that matches the method and request
     * or null if there is no service that matches the request
     */
    public RestXqService get(final HttpMethod method, final HttpRequest request) {
        final ReentrantReadWriteLock lock = getOrCreateMethodLock(method);
        
        RestXqService result = null;
        try {
            lock.readLock().lock();
            
            final List<RestXqService> services = orderedServices.get(method);
            if(services != null) {
                
                final String acceptHeaderValue = request.getHeader(HttpHeader.ACCEPT.getHeaderName());
                final AcceptHeader acceptHeader = acceptHeaderValue != null ? new AcceptHeader(acceptHeaderValue) : null;
                
                for(final RestXqService service : services) {
                    if(service.canService(request)) {
                        if(acceptHeader != null && result != null) {
                            /* Does this service Produce an Internet Media Type
                             * which has a higher Quality Factor in the Accept header
                             * that the last result?
                             */
                            if(service.maxProducesQualityFactor(acceptHeader) > result.maxProducesQualityFactor(acceptHeader)) {
                                //yes, so this service has preference over the last result
                                result = service;
                            }
                        } else {
                            result = service;
                        }

                    }
                }
            }
            return result;
            
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Removes all RESTXQ Services from the Map that come from the XQuery
     * located at the URI xqueryLocation
     * 
     * @param xqueryLocation The location of the XQuery
     * @param listeners Any Listeners that should be notified when a
     * Service is removed
     */
    public void removeAll(final URI xqueryLocation, final List<RestXqServiceRegistryListener> listeners) {
            
        for(final HttpMethod key : orderedServices.keySet()) {
            
            final ReentrantReadWriteLock lock = getOrCreateMethodLock(key);
            try {
                lock.writeLock().lock();
                

                final List<RestXqService> servicesToRemove = new ArrayList<RestXqService>();
                final List<RestXqService> serviceList = orderedServices.get(key);
                
                for(final RestXqService service : serviceList) {
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
     * Removes the RESTXQ Service from the Map
     * 
     * @param service The RestXQ Service
     * @param listeners Any Listeners that should be notified when a
     * Service is removed
     */
    public void remove(final RestXqService service, final List<RestXqServiceRegistryListener> listeners) {
        for(final HttpMethod key : orderedServices.keySet()) {
            
            final ReentrantReadWriteLock lock = getOrCreateMethodLock(key);
            try {
                lock.writeLock().lock();
                

                RestXqService serviceToRemove = null;
                final List<RestXqService> serviceList = orderedServices.get(key);
                
                for(final RestXqService orderedService : serviceList) {
                    if(orderedService.equals(service)) {
                        //label the service to remove
                        serviceToRemove = orderedService;
                        break;
                    }
                }

                if(serviceToRemove != null) {
                    //remove the labelled service
                    serviceList.remove(serviceToRemove);

                    //update the service list
                    orderedServices.put(key, serviceList);
                    
                    //update the listeners
                    for(final RestXqServiceRegistryListener listener : listeners) {
                        listener.deregistered(serviceToRemove);
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