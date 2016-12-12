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
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link PropertySource} and {@link PropertyFilter}
 * instance to evaluate the current Configuration.
 */
public class DefaultConfiguration implements Configuration {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultConfiguration.class.getName());

    /**
     * The current {@link ConfigurationContext} of the current instance.
     */
    private final ConfigurationContext configurationContext;


    /**
     * Constructor.
     * @param configurationContext The configuration Context to be used.
     */
    public DefaultConfiguration(ConfigurationContext configurationContext){
        this.configurationContext = Objects.requireNonNull(configurationContext);
    }


    public String get(String key) {
        Map<String,String> value = evaluteRawValue(key);
        if(value==null || value.get(key)==null){
            return null;
        }
        return PropertyFiltering.applyFilter(key, value, configurationContext);
    }

    protected Map<String,String> evaluteRawValue(String key) {
        List<PropertySource> propertySources = configurationContext.getPropertySources();
        Map<String,String> unfilteredValue = null;
        PropertyValueCombinationPolicy combinationPolicy = this.configurationContext
                .getPropertyValueCombinationPolicy();
        for (PropertySource propertySource : propertySources) {
            unfilteredValue = combinationPolicy.collect(unfilteredValue, key, propertySource);
        }
        return unfilteredValue;
    }


    @Override
    public String getOrDefault(String key, String defaultValue) {
        String val = get(key);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    /**
     * Get the current properties, composed by the loaded {@link PropertySource} and filtered
     * by registered {@link PropertyFilter}.
     *
     * @return the final properties.
     */
    @Override
    public Map<String, String> getProperties() {
        return PropertyFiltering.applyFilters(evaluateUnfilteredMap(), configurationContext);
    }

    protected Map<String, String> evaluateUnfilteredMap() {
        List<PropertySource> propertySources = new ArrayList<>(configurationContext.getPropertySources());
        Collections.reverse(propertySources);
        Map<String, String> result = new HashMap<>();
        for (PropertySource propertySource : propertySources) {
            try {
                int origSize = result.size();
                Map<String, String> otherMap = propertySource.getProperties();
                LOG.log(Level.FINEST, null, "Overriding with properties from " + propertySource.getName());
                result.putAll(otherMap);
                LOG.log(Level.FINEST, null, "Handled properties from " + propertySource.getName() + "(new: " +
                        (result.size() - origSize) + ", overrides: " + origSize + ", total: " + result.size());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error adding properties from PropertySource: " + propertySource + ", ignoring PropertySource.", e);
            }
        }
        return result;
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not null.
     * @param <T>  the value type
     * @return the converted value, never null.
     */
    @Override
    public <T> T get(String key, Class<T> type) {
        return get(key, (TypeLiteral<T>)TypeLiteral.of(type));
    }

    /**
     * Accesses the current String value for the given key and tries to convert it
     * using the {@link PropertyConverter} instances provided by the current
     * {@link ConfigurationContext}.
     *
     * @param key  the property's absolute, or relative path, e.g. @code
     *             a/b/c/d.myProperty}.
     * @param type The target type required, not null.
     * @param <T>  the value type
     * @return the converted value, never null.
     */
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        return convertValue(key, get(key), type);
    }

    protected <T> T convertValue(String key, String value, TypeLiteral<T> type) {
        if (value != null) {
            List<PropertyConverter<T>> converters = configurationContext.getPropertyConverters(type);
            ConversionContext context = new ConversionContext.Builder(this, this.configurationContext, key, type)
                    .build();
            for (PropertyConverter<T> converter : converters) {
                try {
                    T t = converter.convert(value, context);
                    if (t != null) {
                        return t;
                    }
                } catch (Exception e) {
                    LOG.log(Level.INFO, "PropertyConverter: " + converter + " failed to convert value: " + value, e);
                }
            }
            // if the target type is a String, we can return the value, no conversion required.
            if(type.equals(TypeLiteral.of(String.class))){
                return (T)value;
            }
            // unsupported type, throw an exception
            throw new ConfigException("Unparseable config value for type: " + type.getRawType().getName() + ": " + key +
                    ", supported formats: " + context.getSupportedFormats());
        }
        return null;
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        T val = get(key, type);
        if(val==null){
            return defaultValue;
        }
        return val;
    }

    @Override
    public Configuration with(ConfigOperator operator) {
        return operator.operate(this);
    }

    @Override
    public <T> T query(ConfigQuery<T> query) {
        return query.query(this);
    }

    @Override
    public ConfigurationContext getContext() {
        return this.configurationContext;
    }

    /**
     * Access the configuration's context.
     * @return the configurastion context-
     */
    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }

    @Override
    public String toString() {
        return "Configuration{\n " +
                configurationContext +
                '}';
    }
}
