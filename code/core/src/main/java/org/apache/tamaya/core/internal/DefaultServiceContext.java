/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ServiceContext;

import javax.annotation.Priority;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the (default) {@link org.apache.tamaya.spi.ServiceContext} interface and hereby uses the JDK
 * {@link java.util.ServiceLoader} to load the services required.
 */
public final class DefaultServiceContext implements ServiceContext {
    /**
     * List current services loaded, per class.
     */
    private final ConcurrentHashMap<Class<?>, List<Object>> servicesLoaded = new ConcurrentHashMap<>();
    /**
     * Singletons.
     */
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public <T> T getService(Class<T> serviceType) {
        Object cached = singletons.get(serviceType);
        if (cached == null) {
            Collection<T> services = getServices(serviceType);
            if (services.isEmpty()) {
                cached = null;
            } else {
                cached = getServiceWithHighestPriority(services, serviceType);
            }
            if(cached!=null) {
                singletons.put(serviceType, cached);
            }
        }
        return serviceType.cast(cached);
    }

    /**
     * Loads and registers services.
     *
     * @param <T>         the concrete type.
     * @param serviceType The service type.
     * @return the items found, never {@code null}.
     */
    @Override
    public <T> List<T> getServices(final Class<T> serviceType) {
        List<T> found = (List<T>) servicesLoaded.get(serviceType);
        if (found != null) {
            return found;
        }
        List<T> services = new ArrayList<>();
        try {
            for (T t : ServiceLoader.load(serviceType)) {
                services.add(t);
            }
            Collections.sort(services, PriorityServiceComparator.getInstance());
            services = Collections.unmodifiableList(services);
        } catch (ServiceConfigurationError e) {
            Logger.getLogger(DefaultServiceContext.class.getName()).log(Level.WARNING,
                    "Error loading services current type " + serviceType, e);
            if(services==null){
                services = Collections.emptyList();
            }
        }
        final List<T> previousServices = List.class.cast(servicesLoaded.putIfAbsent(serviceType, (List<Object>) services));
        return previousServices != null ? previousServices : services;
    }

    /**
     * Checks the given instance for a @Priority annotation. If present the annotation's value s evaluated. If no such
     * annotation is present, a default priority is returned (1);
     * @param o the instance, not null.
     * @return a priority, by default 1.
     */
    public static int getPriority(Object o){
        int prio = 1; //X TODO discuss default priority
        Priority priority = o.getClass().getAnnotation(Priority.class);
        if (priority != null) {
            prio = priority.value();
        }
        return prio;
    }

    /**
     * @param services to scan
     * @param <T>      type of the service
     *
     * @return the service with the highest {@link javax.annotation.Priority#value()}
     *
     * @throws ConfigException if there are multiple service implementations with the maximum priority
     */
    private <T> T getServiceWithHighestPriority(Collection<T> services, Class<T> serviceType) {

        // we do not need the priority stuff if the list contains only one element
        if (services.size() == 1) {
            return services.iterator().next();
        }

        Integer highestPriority = null;
        int highestPriorityServiceCount = 0;
        T highestService = null;

        for (T service : services) {
            int prio = getPriority(service);
            if (highestPriority == null || highestPriority < prio) {
                highestService = service;
                highestPriorityServiceCount = 1;
                highestPriority = prio;
            } else if (highestPriority == prio) {
                highestPriorityServiceCount++;
            }
        }

        if (highestPriorityServiceCount > 1) {
            throw new ConfigException(MessageFormat.format("Found {0} implementations for Service {1} with Priority {2}: {3}",
                                                           highestPriorityServiceCount,
                                                           serviceType.getName(),
                                                           highestPriority,
                                                           services));
        }

        return highestService;
    }

    @Override
    public int ordinal() {
        return 1;
    }


}
