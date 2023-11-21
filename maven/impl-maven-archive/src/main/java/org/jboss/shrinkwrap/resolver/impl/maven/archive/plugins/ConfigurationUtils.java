/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.resolver.impl.maven.archive.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.shrinkwrap.resolver.impl.maven.util.Validate;

/**
 * An utility class that allows type safe conversions from XPP3 configuration provided by Maven into Java objects
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ConfigurationUtils {

    /**
     * Fetches a value specified by key
     *
     * @param map XPP3 map equivalent
     * @param key navigation key
     * @param defaultValue Default value if no such key exists
     * @return String representation of the value
     */
    static String valueAsString(Map<String, Object> map, Key key, String defaultValue) {
        Validate.notNullOrEmpty(key.key, "Key for plugin configuration must be set");
        if (map.containsKey(key.key)) {
            return map.get(key.key).toString().isEmpty() ? defaultValue : map.get(key.key).toString();
        }
        return defaultValue;
    }

    /**
     * Fetches a value specified by key
     *
     * @param map XPP3 map equivalent
     * @param key navigation key
     * @param defaultValue Default value if no such key exists
     * @return Boolean representation of the value
     */
    static boolean valueAsBoolean(Map<String, Object> map, Key key, boolean defaultValue) {
        return Boolean.parseBoolean(valueAsString(map, key, String.valueOf(defaultValue)));
    }

    /**
     * Fetches a value specified by key
     *
     * @param map XPP3 map equivalent
     * @param key navigation key
     * @param basedir basedir can be different from current basedir
     * @param defaultValue Default value if no such key exists
     * @return File representation of the value
     */
    static File valueAsFile(Map<String, Object> map, Key key, File basedir, File defaultValue) {
        String value = valueAsString(map, key, null);
        if (Validate.isNullOrEmpty(value)) {
            return defaultValue;
        }

        File candidate = new File(value);
        if (!candidate.isAbsolute() && (basedir != null && basedir.exists())) {
            return new File(basedir, candidate.getPath());
        }
        return candidate;
    }

    /**
     * Fetches a value specified by key
     *
     * @param map XPP3 map equivalent
     * @param key navigation key
     * @param defaultValue Default value if no such key exists
     * @return Map representation of the value
     */
    static Map<String, Object> valueAsMap(Map<String, Object> map, Key key, Map<String, Object> defaultValue) {
        if (map.containsKey(key.key)) {
            Object rawOrMap = map.get(key.key);
            if (rawOrMap instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> submap = (Map<String, Object>) rawOrMap;
                return submap;
            } else {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Fetches a value specified by key
     *
     * @param map XPP3 map equivalent
     * @param key navigation key
     * @param defaultValue Default value if no such key exists
     * @return Map representation of the values mapped into Strings
     */
    static Map<String, String> valueAsMapOfStrings(Map<String, Object> map, Key key, Map<String, String> defaultValue) {
        Map<String, Object> castedDefaultValue = new HashMap<>(defaultValue);

        Map<String, Object> uncastedResult = valueAsMap(map, key, castedDefaultValue);
        Map<String, String> castedResult = new HashMap<>();
        for (Map.Entry<String, Object> entry : uncastedResult.entrySet()) {
            castedResult.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
        }
        return castedResult;
    }

    /**
     * Fetches a value specified by key
     *
     * @param map XPP3 map equivalent
     * @param key navigation key
     * @param defaultValue Default value if no such key exists
     * @return List representation of the value
     */
    static List<String> valueAsStringList(Map<String, Object> map, Key key, List<String> defaultValue) {
        Validate.notNullOrEmpty(key.key, "Key for plugin configuration must be set");
        if (map.containsKey(key.key)) {
            Object rawMapOrObject = map.get(key.key);

            // handles non-nested content
            if (key.subKey == null) {
                if (rawMapOrObject == null) {
                    return defaultValue;
                } else {
                    return tokenize(rawMapOrObject, key.delimiter);
                }
            }

            // go for nested content
            if (rawMapOrObject == null) {
                return defaultValue;

            } else if (!(rawMapOrObject instanceof Map)) {
                return Collections.singletonList(rawMapOrObject.toString());
            }

            // 1/ we can either have <excludes>foo,bar</excludes>
            // 2/ or <excludes><exclude>foo</exclude><exclude>bar</exclude></excludes>
            @SuppressWarnings("unchecked")
            Map<String, Object> subMap = (Map<String, Object>) rawMapOrObject;
            Object nestedRaw = subMap.get(key.subKey);
            if (nestedRaw == null) {
                return defaultValue;
            }
            // format 2/
            else if (nestedRaw instanceof Iterable<?>) {
                List<String> list = new ArrayList<>();
                for (Object nested : (Iterable<?>) nestedRaw) {
                    list.addAll(tokenize(nested, key.delimiter));
                }
                return list;
            }
            // format 1/
            else {
                return tokenize(nestedRaw, key.delimiter);
            }
        }
        return defaultValue;
    }

    private static List<String> tokenize(Object object, String delimiter) {
        List<String> list = new ArrayList<>();
        final StringTokenizer tokenizer = new StringTokenizer(object.toString(), delimiter);
        while (tokenizer.hasMoreElements()) {
            list.add(tokenizer.nextToken());
        }
        return list;
    }

    /**
     * Represents a key that can be used to fetch values from XPP3 Configuration returned by Maven
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    static final class Key {
        static final String DEFAULT_DELIMITER = ",";
        final String key;
        String subKey;
        final String delimiter;

        /**
         * Constructs a simple key. It equals xpath {@code /key}.
         *
         * @param key name of the child to be fetched
         */
        Key(String key) {
            this.key = key;
            this.delimiter = DEFAULT_DELIMITER;
        }

        /**
         * Constructs a composed key. It equals either {@code /key} split by delimiter or all {@code /key/subKey} values
         *
         * @param key name of the child to be fetched
         * @param subKey name of the grand children to be fetched
         */
        Key(String key, String subKey) {
            this.key = key;
            this.subKey = subKey;
            this.delimiter = DEFAULT_DELIMITER;
        }
    }
}
