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

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handlers.event.publisher.Publisher;

import org.granite.osgi.GraniteClassRegistry;
import org.osgi.service.event.Event;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

@Component
@Instantiate
public class AsyncService {

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationFactory;

    @Requires
    GraniteClassRegistry gcr;

    @org.apache.felix.ipojo.handlers.event.Publisher(
            name = "AsyncPublisher",
            topics = "systemmanager"
    )

    private Publisher publisher;

    ComponentInstance gravity_destination;

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
                    prop.put("message.topic", "/discussion");
                    prop.put("message.destination", GRAVITY_DESTINATION);
                    prop.put("message.data", ce);
                    publisher.send(prop);
                    sleep(20000);
                } while (true);
            } catch (Exception e) {

            }
        }
    }

    TheadPublisher thread;

    @Validate
    void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {
        gcr.registerClasses(GRAVITY_DESTINATION, new Class[]{ChatEntry.class});

        {
            Dictionary properties = new Hashtable();
            properties.put("ID", GRAVITY_DESTINATION);
            properties.put("SERVICE", Constants.GRAVITY_SERVICE);
            properties.put("CHANNELS", new String[]{Constants.GRAVITY_CHANNEL});
            gravity_destination = destinationFactory.createComponentInstance(properties);
        }

        thread = new TheadPublisher(publisher);
        thread.start();
    }

    @Invalidate
    void stop() {
        try {
            thread.stop();
        } catch (Exception e) {
        }

        gravity_destination.dispose();

        gcr.unregisterClasses(GRAVITY_DESTINATION);
    }


    @org.apache.felix.ipojo.handlers.event.Subscriber(
            name = "AsyncPublisher",
            topics = "systemmanager"
    )
    public final void receive(final Event event) {
        try {
            String topic = (String) event.getProperty("message.topic");
            if (topic.equals("/discussion")) {
                ChatEntry data = (ChatEntry) event.getProperty("message.data");
                System.out.println(data.pseudo + ": " + data.message);
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }
}
