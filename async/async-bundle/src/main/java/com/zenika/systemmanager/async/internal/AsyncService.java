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

package com.zenika.systemmanager.async.internal;

import com.zenika.systemmanager.async.service.ChatEntry;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handlers.event.publisher.Publisher;

import org.granite.gravity.osgi.adapters.ea.EAConstants;
import org.granite.osgi.GraniteClassRegistry;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.event.Event;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

@Component
@Instantiate
public class AsyncService {
    @Requires
    ConfigurationAdmin configurationAdmin;

    @Requires
    GraniteClassRegistry gcr;

    @org.apache.felix.ipojo.handlers.event.Publisher(
            name = "AsyncPublisher",
            topics = "discussion"
    )

    private Publisher publisher;

    Configuration gravity_destination, ea_config;

    private final static String GRAVITY_DESTINATION = "SystemManagerAsync";

    class TheadPublisher extends Thread {
        Publisher publisher;

        TheadPublisher(Publisher publisher) {
            this.publisher = publisher;
        }

        @Override
        public void run() {
            try {
                boolean astop = false;
                do {
                    Dictionary<String, Object> prop = new Hashtable<String, Object>();
                    ChatEntry ce = new ChatEntry("System", "Alive!");
                    publisher.sendData(ce);
                    sleep(20000);
                } while (true);
            } catch (Exception e) {

            }
        }
    }

    TheadPublisher thread;

    @Validate
    void start() throws IOException {
        gcr.registerClasses(GRAVITY_DESTINATION, new Class[]{ChatEntry.class});

        {
            Dictionary properties = new Hashtable();
            properties.put("id", GRAVITY_DESTINATION);
            properties.put("service", Constants.GRAVITY_SERVICE);
            gravity_destination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            gravity_destination.update(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", GRAVITY_DESTINATION);
            ea_config = configurationAdmin.createFactoryConfiguration("org.granite.gravity.osgi.adapters.ea.configuration", null);
            ea_config.update(properties);
        }
        thread = new TheadPublisher(publisher);
        thread.start();
    }

    @Invalidate
    void stop() throws IOException {
        try {
            thread.stop();
        } catch (Exception e) {
        }

        gravity_destination.delete();
        ea_config.delete();

        gcr.unregisterClasses(GRAVITY_DESTINATION);
    }

    @org.apache.felix.ipojo.handlers.event.Subscriber(
            name = "AsyncPublisher",
            topics = "discussion"
    )
    public final void receive(final Event event) {
        try {
            ChatEntry data = (ChatEntry) event.getProperty(EAConstants.DATA);
            System.out.println(data.pseudo + ": " + data.message);
        } catch (Exception e) {
            System.out.println("error");
        }
    }
}
