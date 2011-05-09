/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 *
 */

package com.zenika.systemmanager.scope.internal;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.service.GraniteDestination;
import org.granite.osgi.service.GraniteFactory;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component(name = "TestServiceFactory")
@Instantiate
@Provides
public class ScopeServiceFactory implements GraniteFactory {

    @Requires(from = "org.granite.config.flex.Factory")
    Factory factoryFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationFactory;

    ComponentInstance factory, destination1, destination2, destination3;

    @Validate
    void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", getId());
            factory = factoryFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", "com.zenika.systemmanager.test.service.ScopeServiceSession");
            properties.put("SERVICE", Constants.GRANITE_SERVICE);
            properties.put("CHANNELS", new String[]{Constants.GRANITE_CHANNEL});
            properties.put("FACTORY", getId());
            properties.put("SCOPE", GraniteDestination.SCOPE.SESSION);
            destination1 = destinationFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", "com.zenika.systemmanager.test.service.ScopeServiceRequest");
            properties.put("SERVICE", Constants.GRANITE_SERVICE);
            properties.put("CHANNELS", new String[]{Constants.GRANITE_CHANNEL});
            properties.put("FACTORY", getId());
            properties.put("SCOPE", GraniteDestination.SCOPE.REQUEST);
            destination2 = destinationFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", "com.zenika.systemmanager.test.service.ScopeServiceApplication");
            properties.put("SERVICE", Constants.GRANITE_SERVICE);
            properties.put("CHANNELS", new String[]{Constants.GRANITE_CHANNEL});
            properties.put("FACTORY", getId());
            properties.put("SCOPE", GraniteDestination.SCOPE.APPLICATION);
            destination3 = destinationFactory.createComponentInstance(properties);
        }
    }

    @Invalidate
    void stop() {
        destination1.dispose();
        destination2.dispose();
        destination3.dispose();
        factory.dispose();
    }

    @Override
    public String getId() {
        return "com.zenika.systemmanager.test.service.ScopeServiceFactory";
    }

    @Override
    public Object newInstance() {
        return new ScopeServiceImpl();
    }
}
