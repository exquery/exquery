package org.exquery.restxq.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.exquery.http.HttpMethod;
import org.exquery.http.HttpRequest;
import org.exquery.restxq.RestXqServiceRegistryListener;
import org.exquery.restxq.RestXqService;
import org.exquery.restxq.RestXqServiceRegistry;

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
            for(HttpMethod servicedMethod : servicedMethods) {
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
    public RestXqService findService(final HttpRequest request) {
        return getServices().get(request.getMethod(), request);
        
        //TODO future work - else consult the ANY METHOD bucket?
    }
    
    @Override
    public void deregister(final URI xqueryLocation) {
        getServices().removeAll(xqueryLocation, listeners);
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