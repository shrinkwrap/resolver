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
package org.jboss.shrinkwrap.resolver.api;

/**
 * {@link StrategyStage} extension providing support for the notion of transitivity, adding shorthand notation such that
 * the user does not have to manually specify {@link TransitiveResolutionStrategy} or
 * {@link NonTransitiveResolutionStrategy}
 *
 * @param <FORMATSTAGE>
 *            Next {@link FormatStage} in resolution
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
public interface TransitiveStrategyStage<COORDINATETYPE extends Coordinate, RESOLUTIONFILTERTYPE extends ResolutionFilter<COORDINATETYPE>, FORMATSTAGE extends FormatStage, RESOLUTIONSTRATEGYTYPE extends TransitiveResolutionStrategy<COORDINATETYPE, RESOLUTIONFILTERTYPE, RESOLUTIONSTRATEGYTYPE>>
    extends StrategyStage<COORDINATETYPE, RESOLUTIONFILTERTYPE, FORMATSTAGE, RESOLUTIONSTRATEGYTYPE> {

    /**
     * Alias to {@link StrategyStage#using(ResolutionStrategy)} with {@link TransitiveResolutionStrategy} as argument
     *
     * @return
     */
    FORMATSTAGE withTransitivity();

    /**
     * Alias to {@link StrategyStage#using(ResolutionStrategy)} with {@link NonTransitiveResolutionStrategy} as argument
     *
     * @return
     */
    FORMATSTAGE withoutTransitivity();
}
