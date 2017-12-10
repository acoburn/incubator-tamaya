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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;

/**
 * Converter, converting from String to tge given enum type.
 */
public class EnumConverter<T extends Enum> implements PropertyConverter<T>{

    private org.apache.tamaya.base.convert.EnumConverter<T> converter;

    public EnumConverter(Class<T> enumType){
        converter = new org.apache.tamaya.base.convert.EnumConverter<>(enumType);
    }

    @Override
    public T convert(String value, ConversionContext context) {
        return converter.convert(value);
    }
}