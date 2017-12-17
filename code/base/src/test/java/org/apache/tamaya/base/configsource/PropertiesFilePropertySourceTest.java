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
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.configsource.SimpleConfigSource;
import org.junit.Assert;
import org.junit.Test;

import javax.config.spi.ConfigSource;

public class PropertiesFilePropertySourceTest {

    private final SimpleConfigSource testfilePropertySource = new SimpleConfigSource(Thread.currentThread()
            .getContextClassLoader().getResource("testfile.properties"));
    private final SimpleConfigSource overrideOrdinalPropertySource = new SimpleConfigSource(
            Thread.currentThread().getContextClassLoader().getResource("overrideOrdinal.properties"));


    @Test
    public void testGetOrdinal() {
        Assert.assertEquals(0, testfilePropertySource.getOrdinal());
        Assert.assertEquals(Integer.parseInt(overrideOrdinalPropertySource.getValue(ConfigSource.CONFIG_ORDINAL)),
                overrideOrdinalPropertySource.getOrdinal());
    }


    @Test
    public void testGet() {
        Assert.assertEquals("val3", testfilePropertySource.getValue("key3"));
        Assert.assertEquals("myval5", overrideOrdinalPropertySource.getValue("mykey5"));
        Assert.assertNull(testfilePropertySource.getValue("nonpresentkey"));
    }


    @Test
    public void testGetProperties() throws Exception {
        Assert.assertEquals(5, testfilePropertySource.getProperties().size());
        Assert.assertTrue(testfilePropertySource.getProperties().containsKey("key1"));
        Assert.assertTrue(testfilePropertySource.getProperties().containsKey("key2"));
        Assert.assertTrue(testfilePropertySource.getProperties().containsKey("key3"));
        Assert.assertTrue(testfilePropertySource.getProperties().containsKey("key4"));
        Assert.assertTrue(testfilePropertySource.getProperties().containsKey("key5"));
    }
}
