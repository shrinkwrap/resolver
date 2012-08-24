package org.jboss.shrinkwrap.resolver.api.maven;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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

import org.junit.Assert;
import org.junit.Test;

/**
 * Ensures that the {@link ScopeType#toString()} contracts are as expected
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ScopeTypeTestCase {

    @Test
    public void compile() {
        Assert.assertEquals("compile", ScopeType.COMPILE.toString());
    }

    @Test
    public void provided() {
        Assert.assertEquals("provided", ScopeType.PROVIDED.toString());
    }

    @Test
    public void runtime() {
        Assert.assertEquals("runtime", ScopeType.RUNTIME.toString());
    }

    @Test
    public void test() {
        Assert.assertEquals("test", ScopeType.TEST.toString());
    }

    @Test
    public void system() {
        Assert.assertEquals("system", ScopeType.SYSTEM.toString());
    }

    @Test
    public void importTest() {
        Assert.assertEquals("import", ScopeType.IMPORT.toString());
    }

}
