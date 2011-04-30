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

package com.zenika.systemmanager.application.internal;


import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component
@Instantiate
public class Activator {
    @Requires(from = "org.granite.config.flex.Service")
    Factory serviceFactory;

    @Requires(from = "org.granite.config.flex.Channel")
    Factory channelFactory;

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationFactory;

    @Requires(from = "org.granite.config.flex.Adapter")
    Factory adapterFactory;

    @Requires(from = "org.granite.gravity.osgi.adapters.EventAdmin")
    Factory osgiAdapter;

    ComponentInstance granite_service, granite_channel;
    ComponentInstance gravity_service, gravity_service_adapter, gravity_adapter, gravity_channel;

    @Validate
    void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {

        // Gravity
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", "default_gravity");
            gravity_adapter = adapterFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", Constants.GRAVITY_SERVICE);
            properties.put("MESSAGETYPES", "flex.messaging.messages.AsyncMessage");
            properties.put("DEFAULT_ADAPTER", "default_gravity");
            gravity_service = serviceFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", Constants.GRAVITY_CHANNEL);
            properties.put("CLASS", "org.granite.gravity.channels.GravityChannel");
            properties.put("ENDPOINT_URI", Constants.GRAVITY_CHANNEL_URI);
            gravity_channel = channelFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            Dictionary prop = new Hashtable();
            prop.put("GravitySubscriber", "systemmanager");
            prop.put("GravityPublisher", "systemmanager");
            properties.put("ID", "default_gravity");
            properties.put("event.topics", prop);
            gravity_service_adapter = osgiAdapter.createComponentInstance(properties);
        }

        // GraniteDS
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", Constants.GRANITE_SERVICE);
            granite_service = serviceFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", Constants.GRANITE_CHANNEL);
            properties.put("ENDPOINT_URI", Constants.GRANITE_CHANNEL_URI);
            granite_channel = channelFactory.createComponentInstance(properties);
        }
    }

    @Invalidate
    void stop() {
        granite_service.dispose();
        granite_channel.dispose();

        gravity_channel.dispose();
        gravity_adapter.dispose();
        gravity_service.dispose();
        gravity_service_adapter.dispose();
    }
}
