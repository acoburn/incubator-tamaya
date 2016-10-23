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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.ConfigurationProviderSpi;

import javax.annotation.Priority;

/**
 * Test Configuration class, that is used to testdata the default methods provided by the API.
 */
@Priority(2)
public class TestConfigurationProvider implements ConfigurationProviderSpi {

    private static final Configuration config = new TestConfiguration();

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public Configuration createConfiguration(ConfigurationContext context) {
        return null;
    }

    @Override
    public ConfigurationContext getConfigurationContext() {
        return null;
    }

    @Override
    public void setConfigurationContext(ConfigurationContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConfigurationContextSettable() {
        return false;
    }

    @Override
    public ConfigurationContextBuilder getConfigurationContextBuilder() {
        return null;
    }

    @Override
    public void setConfiguration(Configuration config) {
    }

    @Override
    public boolean isConfigurationSettable() {
        return false;
    }
}
