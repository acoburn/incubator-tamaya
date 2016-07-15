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
package org.apache.tamaya.builder.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.builder.spi.*;

/**
 * Implementation of the Configuration API. This class uses the current {@link ConfigurationContext} to evaluate the
 * chain of {@link PropertySource} and {@link PropertyFilter}
 * instance to evaluate the current Configuration.
 */
public class DefaultConfigurationProvider implements ConfigurationProviderSpi {

    private ConfigurationContext context = new DefaultConfigurationContext();
    private Configuration config = new DefaultConfiguration(context);

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    @Override
    public ConfigurationContext getConfigurationContext() {
        return context;
    }

    @Override
    public ConfigurationContextBuilder getConfigurationContextBuilder() {
        return ServiceContextManager.getServiceContext().getService(ConfigurationContextBuilder.class);
    }

    @Override
    public void setConfigurationContext(ConfigurationContext context){
        // TODO think on a SPI or move event part into API...
        this.config = new DefaultConfiguration(context);
        this.context = context;
    }


    @Override
    public boolean isConfigurationContextSettable() {
        return true;
    }

}
