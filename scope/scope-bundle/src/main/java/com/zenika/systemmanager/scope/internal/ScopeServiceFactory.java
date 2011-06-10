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

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.service.GraniteDestination;
import org.granite.osgi.service.GraniteFactory;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component(name = "TestServiceFactory")
@Instantiate
@Provides
public class ScopeServiceFactory implements GraniteFactory {

    @Requires
    ConfigurationAdmin configurationAdmin;

    Configuration factory, destination1, destination2, destination3;

    @Validate
    void start() throws IOException {
        {
            Dictionary properties = new Hashtable();
            properties.put("id", getId());
            factory = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Factory", null);
            factory.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", "com.zenika.systemmanager.test.service.ScopeServiceSession");
            properties.put("service", Constants.GRANITE_SERVICE);
            properties.put("factory", getId());
            properties.put("scope", "session");
            destination1 = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            destination1.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", "com.zenika.systemmanager.test.service.ScopeServiceRequest");
            properties.put("service", Constants.GRANITE_SERVICE);
            properties.put("factory", getId());
            properties.put("scope", "request");
            destination2 = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            destination2.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", "com.zenika.systemmanager.test.service.ScopeServiceApplication");
            properties.put("service", Constants.GRANITE_SERVICE);
            properties.put("factory", getId());
            properties.put("scope", "application");
            destination3 = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            destination3.update(properties);
        }
    }

    @Invalidate
    void stop() throws IOException {
        destination1.delete();
        destination2.delete();
        destination3.delete();
        factory.delete();
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
