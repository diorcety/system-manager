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

package com.zenika.systemmanager.cpu.internal;

import com.sun.management.OperatingSystemMXBean;

import com.zenika.systemmanager.cpu.service.CpuService;

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

import java.lang.management.ManagementFactory;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * @author Francois Fornaciari
 * @{CpuService implementation.
 */
@Component(name = "CpuService")
@Instantiate
@Provides
public class CpuServiceImpl implements CpuService, GraniteDestination {

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationFactory;

    ComponentInstance destination;

    private long nanoBefore;
    private long cpuBefore;

    public CpuServiceImpl() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.nanoBefore = System.nanoTime();
        this.cpuBefore = operatingSystemMXBean.getProcessCpuTime();
    }

    @Validate
    void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {
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
    void stop() {
        destination.dispose();
    }

    /*
    * (non-Javadoc)
    *
    * @see com.zenika.systemmanager.cpu.service.CpuService#getCpuUsage()
    */
    public long getCpuUsage() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long cpuAfter = operatingSystemMXBean.getProcessCpuTime();
        long nanoAfter = System.nanoTime();
        long result = ((cpuAfter - cpuBefore) * 100L) / (nanoAfter - nanoBefore);
        this.nanoBefore = System.nanoTime();
        this.cpuBefore = operatingSystemMXBean.getProcessCpuTime();
        return result;
    }

    @Override
    public String getId() {
        return "com.zenika.systemmanager.cpu.service.CpuService";
    }
}
