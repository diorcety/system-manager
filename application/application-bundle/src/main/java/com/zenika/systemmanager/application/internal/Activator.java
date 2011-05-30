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

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.osgi.ConfigurationHelper;

import java.util.Dictionary;
import java.util.Hashtable;


@Component
@Instantiate
public class Activator {

    @Requires
    ConfigurationHelper confHelper;

    ComponentInstance granite_service, granite_channel;
    ComponentInstance gravity_service, gravity_channel;

    @Validate
    void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {

        // Gravity
        gravity_service = confHelper.newGravityService(Constants.GRAVITY_SERVICE, EAConstants.ADAPTER_ID);
        gravity_channel = confHelper.newGravityChannel(Constants.GRAVITY_CHANNEL, Constants.GRAVITY_CHANNEL_URI);

        // GraniteDS
        granite_service = confHelper.newGraniteService(Constants.GRANITE_SERVICE);
        granite_channel = confHelper.newGraniteChannel(Constants.GRANITE_CHANNEL, Constants.GRANITE_CHANNEL_URI);
    }

    @Invalidate
    void stop() {
        granite_service.dispose();
        granite_channel.dispose();

        gravity_channel.dispose();
        gravity_service.dispose();
    }
}
