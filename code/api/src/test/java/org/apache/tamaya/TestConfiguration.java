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

import java.util.HashMap;
import java.util.Map;

/**
 * Test Configuration class, that is used to testdata the default methods provided by the API.
 */
public class TestConfiguration implements Configuration{

    private static final Map<String, String> VALUES;
    static {
        VALUES = new HashMap<>();
        VALUES.put("long", String.valueOf(Long.MAX_VALUE));
        VALUES.put("int", String.valueOf(Integer.MAX_VALUE));
        VALUES.put("double", String.valueOf(Double.MAX_VALUE));
        VALUES.put("float", String.valueOf(Float.MAX_VALUE));
        VALUES.put("short", String.valueOf(Short.MAX_VALUE));
        VALUES.put("byte", String.valueOf(Byte.MAX_VALUE));
        VALUES.put("booleanTrue", "true");
        VALUES.put("booleanFalse", "false");
        VALUES.put("String", "aStringValue");
    }

    @Override
    public String get(String key) {
        return VALUES.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        throw new RuntimeException("Method not implemented yet.");
    }
}
