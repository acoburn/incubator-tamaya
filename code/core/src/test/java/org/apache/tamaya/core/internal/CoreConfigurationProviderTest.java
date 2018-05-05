/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Configuration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by atsticks on 11.09.16.
 */
public class CoreConfigurationProviderTest {

    @Test
    public void testInstantiation() throws Exception {
        new CoreConfigurationProvider();
    }

    @Test
    public void getConfiguration() throws Exception {
        assertThat(new CoreConfigurationProvider().getConfiguration()).isNotNull();
    }

    @Test
    public void createConfiguration() throws Exception {
        Configuration cfg = new CoreConfigurationBuilder().build();
        assertThat(new CoreConfigurationProvider().createConfiguration(cfg.getContext())).isNotNull();
        assertThat(cfg).isEqualTo(new CoreConfigurationProvider().createConfiguration(cfg.getContext()));
    }

    @Test
    public void getConfigurationContext() throws Exception {
        assertThat(new CoreConfigurationProvider().getConfigurationContext()).isNotNull();
        assertThat(new CoreConfigurationProvider().getConfigurationContext()).isEqualTo(new CoreConfigurationProvider().getConfiguration().getContext());
    }

    @Test
    public void getConfigurationContextBuilder() throws Exception {
        assertThat(new CoreConfigurationProvider().getConfigurationContextBuilder()).isNotNull();
    }

    @Test
    public void getConfigurationBuilder() throws Exception {
        assertThat(new CoreConfigurationProvider().getConfigurationBuilder()).isNotNull();
    }

    @SuppressWarnings("deprecation")
	@Test
    public void setConfigurationContext() throws Exception {
        new CoreConfigurationProvider()
                .setConfigurationContext(new CoreConfigurationProvider().getConfiguration().getContext());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void setConfiguration() throws Exception {
        new CoreConfigurationProvider()
                .setConfiguration(new CoreConfigurationProvider().getConfiguration());
    }

    @SuppressWarnings("deprecation")
	@Test
    public void isConfigurationContextSettable() throws Exception {
        assertThat(new CoreConfigurationProvider().isConfigurationContextSettable()).isTrue();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void isConfigurationSettable() throws Exception {
        assertThat(new CoreConfigurationProvider().isConfigurationSettable()).isTrue();
    }


}