/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.resolver.api;

/**
 * Represent a part of a fluent API which was spawned from parent type T. Allows user to return back to the previous API
 * and call upper methods on the object.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <T>
 *            The type of the parent.
 */
public interface Child<T> {

    /**
     * Returns back in API structure to a parent which spawned this child. Side effects called on child are preserved.
     *
     * @return The parent of the child
     */
    T up();
}
