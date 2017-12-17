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
package org.apache.tamaya.base.convert;

import org.junit.Test;

import javax.config.spi.Converter;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class ConverterManagerTest {

    private final ConversionContext DUMMY_CONTEXT = new ConversionContext.Builder(
            "someKey", Object.class).build();

    @Test
    public void customTypeWithFactoryMethodOfIsRecognizedAsSupported() {
        ConverterManager manager = new ConverterManager();

        assertThat(manager.isTargetTypeSupported(MyType.class),
                   is(true));
    }

    @Test
    public void factoryMethodOfIsUsedAsConverter() {
        ConverterManager manager = new ConverterManager();

        List<Converter> converters = manager.getConverters(
                MyType.class);

        assertThat(converters, hasSize(1));

        Converter<MyType> converter = converters.get(0);

        Object result = converter.convert("IN");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(MyType.class));
        assertThat(((MyType)result).getValue(), equalTo("IN"));
    }

    @Test
    public void testDirectConverterMapping(){
        ConverterManager manager = new ConverterManager();
        List<Converter<C>> converters = List.class.cast(manager.getConverters(C.class));
        assertThat(converters, hasSize(1));

        Converter<C> converter = converters.get(0);
        C result = converter.convert("testDirectConverterMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat((result).getInValue(), equalTo("testDirectConverterMapping"));
    }

    @Test
    public void testDirectSuperclassConverterMapping(){
        ConverterManager manager = new ConverterManager();
        manager.addDiscoveredConverters();
        List<Converter<B>> converters = List.class.cast(manager.getConverters(B.class));
        assertThat(converters, hasSize(1));
        converters = List.class.cast(manager.getConverters(B.class));
        assertThat(converters, hasSize(1));

        Converter<B> converter = converters.get(0);
        B result = converter.convert("testDirectSuperclassConverterMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testDirectSuperclassConverterMapping"));
    }

    @Test
    public void testMultipleConverterLoad(){
        ConverterManager manager = new ConverterManager();
        manager.addDiscoveredConverters();
        List<Converter<B>> converters = List.class.cast(manager.getConverters(B.class));
        assertThat(converters, hasSize(1));
        manager = new ConverterManager();
        converters = List.class.cast(manager.getConverters(B.class));
        assertThat(converters, hasSize(0));
        manager.addDiscoveredConverters();
        converters = List.class.cast(manager.getConverters(B.class));
        assertThat(converters, hasSize(1));
    }

    @Test
    public void testTransitiveSuperclassConverterMapping(){
        ConverterManager manager = new ConverterManager();
        manager.addDiscoveredConverters();
        List<Converter<A>> converters = List.class.cast(manager.getConverters(A.class));
        assertThat(converters, hasSize(1));

        Converter<A> converter = converters.get(0);
        A result = converter.convert("testTransitiveSuperclassConverterMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testTransitiveSuperclassConverterMapping"));
    }

    @Test
    public void testDirectInterfaceMapping(){
        ConverterManager manager = new ConverterManager();
        manager.addDiscoveredConverters();
        List<Converter<Readable>> converters = List.class.cast(manager.getConverters(Readable.class));
        assertThat(converters, hasSize(1));

        Converter<Readable> converter = converters.get(0);
        Readable result = converter.convert("testDirectInterfaceMapping");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testDirectInterfaceMapping"));
    }

    @Test
    public void testTransitiveInterfaceMapping1(){
        ConverterManager manager = new ConverterManager();
        manager.addDiscoveredConverters();
        List<Converter<Runnable>> converters = List.class.cast(manager.getConverters(Runnable.class));
        assertThat(converters, hasSize(1));

        Converter<Runnable> converter = converters.get(0);
        Runnable result = converter.convert("testTransitiveInterfaceMapping1");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testTransitiveInterfaceMapping1"));
    }

    @Test
    public void testTransitiveInterfaceMapping2(){
        ConverterManager manager = new ConverterManager();
        manager.addDiscoveredConverters();
        List<Converter<AutoCloseable>> converters = List.class.cast(manager.getConverters(AutoCloseable.class));
        assertThat(converters, hasSize(1));

        Converter<AutoCloseable> converter = converters.get(0);
        AutoCloseable result = converter.convert("testTransitiveInterfaceMapping2");

        assertThat(result, notNullValue());
        assertThat(result, instanceOf(C.class));
        assertThat(((C)result).getInValue(), equalTo("testTransitiveInterfaceMapping2"));
    }

    public static class MyType {
        private final String typeValue;

        private MyType(String value) {
            typeValue = value;
        }

        public static MyType of(String source) {
            return new MyType(source);
        }

        public String getValue() {
            return typeValue;
        }

    }

}