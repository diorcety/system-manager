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


import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.gravity.osgi.adapters.ea.EAConstants;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;


@Component
@Instantiate
public class Activator {
    @Requires
    ConfigurationAdmin configurationAdmin;

    @Requires
    HttpService httpService;

    Configuration granite_service, granite_channel;
    Configuration gravity_service, gravity_channel;
    ServiceRegistration httpContext;

    BundleContext bundleContext;

    Activator(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    @Validate
    void start() throws IOException {

        // New Context
        Dictionary dictionary = new Hashtable();
        dictionary.put("ID", "SystemManagerContext");
        httpContext = bundleContext.registerService(HttpContext.class.getName(), httpService.createDefaultHttpContext(), dictionary);

        // Gravity
        {
            Dictionary properties = new Hashtable();
            properties.put("id", Constants.GRAVITY_SERVICE);
            properties.put("messageTypes", "flex.messaging.messages.AsyncMessage");
            properties.put("defaultAdapter", EAConstants.ADAPTER_ID);
            gravity_service = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Service", null);
            gravity_service.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", Constants.GRAVITY_CHANNEL);
            properties.put("uri", Constants.GRAVITY_CHANNEL_URI);
            properties.put("context", "SystemManagerContext");
            properties.put("gravity", "true");
            gravity_channel = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Channel", null);
            gravity_channel.update(properties);
        }

        // GraniteDS
        {
            Dictionary properties = new Hashtable();
            properties.put("id", Constants.GRANITE_SERVICE);
            granite_service = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Service", null);
            granite_service.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("id", Constants.GRANITE_CHANNEL);
            properties.put("uri", Constants.GRANITE_CHANNEL_URI);
            properties.put("context", "SystemManagerContext");
            granite_channel = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Channel", null);
            granite_channel.update(properties);
        }
    }

    @Invalidate
    void stop() throws IOException {
        granite_service.delete();
        granite_channel.delete();

        gravity_channel.delete();
        gravity_service.delete();

        httpContext.unregister();
    }
}
