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

package com.zenika.systemmanager.test.internal;

import com.zenika.systemmanager.test.service.TestClass;
import com.zenika.systemmanager.test.service.Test2Service;

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

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component(name = "Test2Service")
@Instantiate
@Provides
public class Test2ServiceImpl implements Test2Service, GraniteDestination {
    ComponentInstance destination;

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationFactory;

    @Requires
    GraniteClassRegistry gcr;

    @Validate
    void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {
        gcr.registerClass(getId(), new Class[]{TestClass.class});

        {
            Collection<String> channels = new LinkedList<String>();
            channels.add(Constants.GRANITE_CHANNEL);
            Dictionary properties = new Hashtable();
            properties.put("ID", getId());
            properties.put("SERVICE", Constants.GRANITE_SERVICE);
            properties.put("CHANNELS", channels);
            destination = destinationFactory.createComponentInstance(properties);
        }
    }

    @Invalidate
    public void stop() {
        destination.dispose();

        gcr.unregisterClass(getId());
    }

    @Override
    public String setObject(TestClass cls) {
        String ret = "";
        for (String str : cls.strs) {
            ret += str + " ";
        }
        ret += "\n";
        return ret;
    }

    @Override
    public String getId() {
        return "com.zenika.systemmanager.test.service.Test2Service";
    }

}
