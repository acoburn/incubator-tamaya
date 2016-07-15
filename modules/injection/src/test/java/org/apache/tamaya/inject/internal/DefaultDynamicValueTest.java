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
package org.apache.tamaya.inject.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.builder.ConfigurationBuilder;
import org.apache.tamaya.inject.api.ConfiguredItemSupplier;
import org.apache.tamaya.inject.api.DynamicValue;
import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.UpdatePolicy;
import org.apache.tamaya.builder.spi.ConversionContext;
import org.apache.tamaya.builder.spi.PropertyConverter;
import org.apache.tamaya.builder.spi.PropertySource;
import org.apache.tamaya.builder.spi.PropertyValue;
import org.junit.Test;

import org.apache.tamaya.Configuration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link org.apache.tamaya.inject.internal.DefaultDynamicValue}.
 */
public class DefaultDynamicValueTest {

    @Config("a")
    String myValue;

    @Config("a")
    String myValue2;

    @Config("a")
    void setterMethod(String value){

    }

    private PropertyChangeEvent event;

    private PropertyChangeListener consumer = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            event = evt;
        }
    };

    private Map<String,String> properties = new HashMap<>();
    private Configuration config = new ConfigurationBuilder().addPropertySources(
            new PropertySource() {
                @Override
                public int getOrdinal() {
                    return 0;
                }

                @Override
                public String getName() {
                    return "test";
                }

                @Override
                public PropertyValue get(String key) {
                    return PropertyValue.of(key,properties.get(key),getName());
                }

                @Override
                public Map<String, String> getProperties() {
                    return properties;
                }

                @Override
                public boolean isScannable() {
                    return false;
                }
            }
    ).build();

    @Test
    public void testOf_Field() throws Exception {
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                ConfigurationProvider.getConfiguration());
        assertNotNull(val);
    }

    @Test
    public void testOf_Method() throws Exception {
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredMethod("setterMethod", String.class),
                config);
        assertNotNull(val);
    }

    @Test
    public void testCommitAndGet() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        assertNotNull(val);
        assertEquals("aValue",val.evaluateValue());
    }

    @Test
    public void testCommitAndGets() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.EXPLCIT);
        assertNotNull(val);
        assertEquals("aValue",val.evaluateValue());
        // change config
        val.get();
        this.properties.put("a", "aValue2");
        assertTrue(val.updateValue());
        assertEquals("aValue2", val.commitAndGet());
    }

    @Test
    public void testCommit() throws Exception {
        properties.put("a", "aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.EXPLCIT);
        assertNotNull(val);
        assertEquals("aValue", val.evaluateValue());
        // change config
        val.get();
        this.properties.put("a", "aValue2");
        assertEquals("aValue2", val.evaluateValue());
        assertTrue(val.updateValue());
        val.commit();
        assertEquals("aValue2", val.get());
    }

    @Test
    public void testGetSetUpdatePolicy() throws Exception {
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        for(UpdatePolicy pol: UpdatePolicy.values()) {
            val.setUpdatePolicy(pol);
            assertEquals(pol, val.getUpdatePolicy());
        }
    }

    @Test
    public void testAddRemoveListener() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.IMMEDEATE);
        val.addListener(consumer);
        // change config
        val.get();
        this.properties.put("a", "aValue2");
        val.get();
        assertNotNull(event);
        event = null;
        val.removeListener(consumer);
        this.properties.put("a", "aValue3");
        val.updateValue();
        assertNull(event);
    }

    @Test
    public void testGet() throws Exception {
        properties.put("a", "aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.IMMEDEATE);
        properties.put("a", "aValue2");
        val.updateValue();
        assertEquals("aValue2", val.get());
    }

    @Test
    public void testUpdateValue() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.EXPLCIT);
        assertNotNull(val.get());
        assertEquals("aValue", val.get());
        val.updateValue();
        assertEquals("aValue", val.get());
        val.setUpdatePolicy(UpdatePolicy.IMMEDEATE);
        val.updateValue();
        assertEquals("aValue",val.get());
    }

    @Test
    public void testEvaluateValue() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.EXPLCIT);
        assertNotNull(val.get());
        assertEquals("aValue",val.evaluateValue());
        properties.put("a", "aValue2");
        assertEquals("aValue2", val.evaluateValue());
    }

    @Test
    public void testGetNewValue() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.EXPLCIT);
        val.get();
        assertNull(val.getNewValue());
        properties.put("a", "aValue2");
        val.get();
        assertNotNull(val.getNewValue());
        assertEquals("aValue2", val.getNewValue());
        val.commit();
        assertNull(val.getNewValue());
    }

    @Test
    public void testIsPresent() throws Exception {

    }

    @Test
    public void testIfPresent() throws Exception {
        properties.put("a","aValue");
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.IMMEDEATE);
        assertTrue(val.isPresent());
        properties.remove("a");
        val.updateValue();
        assertFalse(val.isPresent());
    }

    @Test
    public void testOrElse() throws Exception {
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.IMMEDEATE);
        assertEquals("bla", val.orElse("bla"));
        properties.put("a","aValue");
        val.updateValue();
        assertEquals("aValue", val.orElse("bla"));
    }

    @Test
    public void testOrElseGet() throws Exception {
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.IMMEDEATE);
        assertEquals("bla", val.orElseGet(new ConfiguredItemSupplier() {
            @Override
            public Object get() {
                return "bla";
            }
        }));
        properties.put("a", "aValue");
        val.updateValue();
        assertEquals("aValue", val.orElseGet(new ConfiguredItemSupplier() {
            @Override
            public Object get() {
                return "bla";
            }
        }));
    }

    @Test(expected = ConfigException.class)
    public void testOrElseThrow() throws Throwable {
        DynamicValue val = DefaultDynamicValue.of(getClass().getDeclaredField("myValue"),
                config);
        val.setUpdatePolicy(UpdatePolicy.EXPLCIT);
        val.get();
        properties.put("a", "aValue");
        assertEquals("aValue", val.orElseThrow(new ConfiguredItemSupplier() {
            @Override
            public ConfigException get() {
                return new ConfigException("bla");
            }
        }));
        properties.remove("a");
        val.updateValue();
        assertEquals("aValue", val.orElseThrow(new ConfiguredItemSupplier() {
            @Override
            public ConfigException get() {
                return new ConfigException("bla");
            }
        }));
    }

    private static final class DoublicatingConverter implements PropertyConverter<String>{

        @Override
        public String convert(String value, ConversionContext context) {
            return value + value;
        }
    }
}