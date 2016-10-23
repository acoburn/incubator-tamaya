/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.ConfigException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Simple implementation of a {@link org.apache.tamaya.spi.PropertySource} for
 * simple property files and XML property files.
 */
public class SimplePropertySource extends BasePropertySource {

    private static final Logger LOG = Logger.getLogger(SimplePropertySource.class.getName());

    /**
     * The property source name.
     */
    private final String name;

    /**
     * The current properties.
     */
    private Map<String, String> properties;

    private SimplePropertySource(String name) {
        super(0);
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(File propertiesLocation) {
        this(propertiesLocation.toString());
        try {
            this.properties = load(propertiesLocation.toURI().toURL());
        } catch (IOException e) {
            throw new ConfigException("Failed to load properties from " + propertiesLocation, e);
        }
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(URL propertiesLocation) {
        this(propertiesLocation.toString());
        this.properties = load(propertiesLocation);
    }

    /**
     * Creates a new Properties based PropertySource based on the given properties map.
     *
     * @param name       the name, not null.
     * @param properties the properties, not null.
     */
    public SimplePropertySource(String name, Map<String, String> properties) {
        this(name);
        this.properties = new HashMap<>(properties);
    }

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param name               The property source name
     * @param propertiesLocation the URL encoded location, not null.
     */
    public SimplePropertySource(String name, URL propertiesLocation) {
        this(name);
        this.properties = load(propertiesLocation);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * loads the Properties from the given URL
     *
     * @param propertiesFile {@link java.net.URL} to load Properties from
     * @return loaded {@link java.util.Properties}
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    private Map<String, String> load(URL propertiesFile) {
        boolean isXML = isXMLPropertieFiles(propertiesFile);

        Map<String, String> properties = new HashMap<>();
        try (InputStream stream = propertiesFile.openStream()) {
            Properties props = new Properties();
            if (stream != null) {
                if (isXML) {
                    props.loadFromXML(stream);
                } else {
                    props.load(stream);
                }
            }

            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
                if (getName() == null){
                    LOG.warning("No property source name found for " + this +", ommitting source meta-entries.");
                } else {
                    properties.put("_" + key + ".source", getName());
                }
            }
        } catch (IOException e) {
            throw new ConfigException("Error loading properties from " + propertiesFile, e);
        }

        return properties;
    }

    private boolean isXMLPropertieFiles(URL url) {
        return url.getFile().endsWith(".xml");
    }

}
