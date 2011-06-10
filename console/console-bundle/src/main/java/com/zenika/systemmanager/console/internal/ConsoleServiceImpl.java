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

package com.zenika.systemmanager.console.internal;

import com.zenika.systemmanager.console.service.BundleInformation;
import com.zenika.systemmanager.console.service.ConsoleService;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Francois Fornaciari
 * @{ConsoleService} implementation.
 */
@Component(name = "ConsoleService")
@Instantiate
@Provides
public class ConsoleServiceImpl implements ConsoleService, GraniteDestination {

    @Requires
    ConfigurationAdmin configurationAdmin;

    @Requires
    GraniteClassRegistry gcr;

    Configuration destination;

    /**
     * OSGi BundleContext.
     */
    private BundleContext bundleContext;

    public ConsoleServiceImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    @Validate
    void start() throws IOException {
        gcr.registerClasses(getId(), new Class[]{BundleInformation.class});

        {
            Dictionary properties = new Hashtable();
            properties.put("id", getId());
            properties.put("service", Constants.GRANITE_SERVICE);
            destination = configurationAdmin.createFactoryConfiguration("org.granite.config.flex.Destination", null);
            destination.update(properties);
        }
    }

    @Invalidate
    void stop() throws IOException {
        destination.delete();

        gcr.unregisterClasses(getId());
    }

    /* (non-Javadoc)
      * @see com.zenika.systemmanager.console.service.ConsoleService#getBundles()
      */
    public List<BundleInformation> getBundles() {
        List<BundleInformation> bundleInformationList = new LinkedList<BundleInformation>();

        Bundle[] bundles = bundleContext.getBundles();

        for (Bundle bundle : bundles) {
            BundleInformation bundleInformation = new BundleInformation();
            bundleInformation.setId(String.valueOf(bundle.getBundleId()));
            String name = (String) bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_NAME);
            bundleInformation.setName(name != null ? name : bundle.getSymbolicName());
            bundleInformation.setState(getState(bundle.getState()));
            bundleInformation.setVersion(bundle.getVersion().toString());
            bundleInformationList.add(bundleInformation);
        }
        return bundleInformationList;
    }

    /**
     * Return the literal bundle's state.
     *
     * @param state a state.
     * @return Literal bundle's state.
     */
    private String getState(int state) {
        switch (state) {
            case Bundle.ACTIVE:
                return "active";
            case Bundle.INSTALLED:
                return "installed";
            case Bundle.RESOLVED:
                return "resolved";
            case Bundle.STARTING:
                return "starting";
            case Bundle.UNINSTALLED:
                return "uninstalled";
            default:
                return "unknow";
        }
    }

    @Override
    public String getId() {
        return "com.zenika.systemmanager.console.service.ConsoleService";
    }
}
