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

package com.zenika.systemmanager.general.internal;

import com.zenika.systemmanager.general.service.GeneralInformation;
import com.zenika.systemmanager.general.service.GeneralService;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * @author Francois Fornaciari
 * @{MemoryService implementation.
 */
@Component(name = "GeneralService")
@Instantiate
@Provides
public class GeneralServiceImpl implements GeneralService, GraniteDestination {

    @Requires
    ConfigurationAdmin configurationAdmin;

    @Requires
    GraniteClassRegistry gcr;

    Configuration destination;

    @Validate
    void start() throws IOException {
        gcr.registerClasses(getId(), new Class[]{GeneralInformation.class});

        {
            Dictionary properties = new Hashtable();
            properties.put("id", getId());
            properties.put("service", Constants.GRANITE_SERVICE);
            destination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            destination.update(properties);
        }
    }

    @Invalidate
    void stop() throws IOException{
        destination.delete();

        gcr.unregisterClasses(getId());
    }

    /* (non-Javadoc)
    * @see com.zenika.systemmanager.general.service.GeneralService#getGeneralInformations()
    */
    public GeneralInformation getGeneralInformation() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        GeneralInformation generalInformation = new GeneralInformation();
        generalInformation.setArch(operatingSystemMXBean.getArch());
        generalInformation.setAvailableProcessors(operatingSystemMXBean.getAvailableProcessors());
        generalInformation.setName(operatingSystemMXBean.getName());
        generalInformation.setVersion(operatingSystemMXBean.getVersion());
        return generalInformation;
    }

    @Override
    public String getId() {
        return "com.zenika.systemmanager.general.service.GeneralService";
    }
}
