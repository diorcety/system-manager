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

import com.zenika.systemmanager.application.service.SWFModule;
import com.zenika.systemmanager.application.service.SWFModulesService;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.MissingHandlerException;
import org.apache.felix.ipojo.UnacceptableConfiguration;
import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;
import org.apache.felix.ipojo.handlers.event.publisher.Publisher;

import org.granite.osgi.GraniteClassRegistry;
import org.granite.osgi.service.GraniteDestination;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.BundleTracker;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Francois Fornaciari
 * @{FlexModulesService} implementation.
 * Listen to registered SWF modules with the help of the extender model pattern.
 */
@Component(name = "SWFModulesService")
@Instantiate
@Provides
public class SWFModulesServiceImpl implements SWFModulesService, GraniteDestination {

    @Requires(from = "org.granite.config.flex.Destination")
    Factory destinationFactory;

    @Requires(from = "org.granite.gravity.osgi.adapters.ea.configuration")
    Factory eaFactory;

    @Requires
    GraniteClassRegistry gcr;

    ComponentInstance granite_destination, gravity_destination, ea_config;

    @org.apache.felix.ipojo.handlers.event.Publisher(
            name = "AsyncPublisher",
            topics = "events"
    )

    private Publisher publisher;

    /**
     * OSGi BundleContext.
     */
    private BundleContext bundleContext;

    /**
     * OSGi BundleTracker.
     */
    private BundleTracker bundleTracker;

    /**
     * OSGi HttpService.
     */
    private HttpService httpService;

    /**
     * List of registered SWF modules.
     */
    private List<SWFModule> registeredSWFModules = new LinkedList<SWFModule>();


    private final static String GRAVITY_DESTINATION = "SystemManagerApplication";

    /**
     * Constructor.
     *
     * @param bundleContext OSGi BundleContext
     */
    public SWFModulesServiceImpl(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Register resources for SWF modules and listen to resources arrival/departures
     */
    @Validate
    public void start() throws MissingHandlerException, ConfigurationException, UnacceptableConfiguration {
        gcr.registerClasses(GRAVITY_DESTINATION, new Class[]{SWFModule.class});
        gcr.registerClasses(getId(), new Class[]{SWFModule.class});
        {
            Dictionary properties = new Hashtable();
            properties.put("destination", GRAVITY_DESTINATION);
            ea_config = eaFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", getId());
            properties.put("SERVICE", Constants.GRANITE_SERVICE);
            properties.put("CHANNELS", new String[]{Constants.GRANITE_CHANNEL});
            granite_destination = destinationFactory.createComponentInstance(properties);
        }
        {
            Dictionary properties = new Hashtable();
            properties.put("ID", GRAVITY_DESTINATION);
            properties.put("SERVICE", Constants.GRAVITY_SERVICE);
            properties.put("CHANNELS", new String[]{Constants.GRAVITY_CHANNEL});
            gravity_destination = destinationFactory.createComponentInstance(properties);
        }

        bundleTracker = new BundleTracker(bundleContext, Bundle.ACTIVE, null) {
            public Object addingBundle(Bundle bundle, BundleEvent event) {
                // Registering SWF modules for this bundle if any
                return registerSWFModule(bundle);
            }

            public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
                // Unregistering tracked aliases
                SWFModule swfModule = (SWFModule) object;
                unregister(swfModule);
            }
        };

        // Starts tracking SWF Modules
        bundleTracker.open();
    }

    @Invalidate
    public void stop() {
        granite_destination.dispose();
        gravity_destination.dispose();
        ea_config.dispose();

        // Stops tracking SWF Modules
        bundleTracker.close();

        gcr.unregisterClasses(GRAVITY_DESTINATION);
        gcr.unregisterClasses(getId());
    }

    /**
     * Method called when the HttpService is registered.
     *
     * @param httpService HttpService instance
     */
    @Bind
    public void bindHttpService(HttpService httpService) {
        this.httpService = httpService;

        System.out.println("Registering resources at context /system-manager");
        try {
            this.httpService.registerResources(Constants.APPLICATION_CONTEXT, "", null);
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called before the HttpService is unregistered.
     *
     * @param httpService HttpService instance
     */
    @Unbind
    public void unbindHttpService(HttpService httpService) {
        // Stops tracking SWF Modules
        bundleTracker.close();

        System.out.println("Unregistering resources at context /system-manager");
        this.httpService.unregister(Constants.APPLICATION_CONTEXT);

        this.httpService = null;
    }

    /* (non-Javadoc)
      * @see com.zenika.systemmanager.application.service.FlexModulesService#getModules()
      */
    public List<SWFModule> getModules() {
        return registeredSWFModules;
    }

    /**
     * Registers SWF Modules if any.
     *
     * @param bundle Bundle to analyze
     */
    private SWFModule registerSWFModule(Bundle bundle) {
        SWFModule swfModule = null;

        try {
            // Get the SWF module from the MANIFEST headers
            swfModule = getSWFModuleFromHeaders(bundle);

            // If an SWF module is declared
            if (swfModule != null) {
                swfModule.setEventType(SWFModule.LOAD);

                // Register bundle's resources with the module's alias
                registerResources(bundle, swfModule.getAlias());

                // Post event
                publisher.sendData(swfModule);

                // Save registered SWF modules
                registeredSWFModules.add(swfModule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return swfModule;
    }

    /**
     * Registers resources.
     *
     * @param bundle Bundle used to load the SWF Module
     * @throws Exception If the registration fails
     */
    private void registerResources(Bundle bundle, String alias) throws Exception {
        // Resources registration
        System.out.println("Registering resources at context " + alias);
        HttpContext httpContext = new DefaultHttpContext(bundle);
        httpService.registerResources(alias, "", httpContext);

    }

    /**
     * Unregisters alias for a given SWF module.
     *
     * @param swfModule SWF module to unregister
     */
    private void unregister(SWFModule swfModule) {
        System.out.println("Unregistering resources at context " + swfModule.getAlias());
        swfModule.setEventType(SWFModule.UNLOAD);
        publisher.sendData(swfModule);
        registeredSWFModules.remove(swfModule);
        httpService.unregister(swfModule.getAlias());
    }

    /**
     * Gets the couple (key,value) of the SWF-Module MANIFEST headers.
     *
     * @param bundle Bundle to analyze
     * @return The couple (key,value) of the SWF-Module MANIFEST headers
     */
    private SWFModule getSWFModuleFromHeaders(Bundle bundle) {
        SWFModule swfModule = null;
        String swfModuleHeader = (String) bundle.getHeaders().get(Constants.SWF_MODULE_MANIFEST_HEADER);
        if (swfModuleHeader != null) {
            String parts[] = swfModuleHeader.trim().split("=");
            String moduleContext = parts[0];
            String moduleName = parts[1];

            // Build a SWF module POJO
            swfModule = new SWFModule();
            swfModule.setAlias(Constants.APPLICATION_CONTEXT + moduleContext);
            swfModule.setName(moduleName);
        }
        return swfModule;
    }

    @Override
    public String getId() {
        return "com.zenika.systemmanager.application.service.SWFModulesService";
    }
}
