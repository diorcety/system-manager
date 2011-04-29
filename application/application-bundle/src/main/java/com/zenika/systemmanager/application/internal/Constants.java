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

/**
 * Application constants.
 *
 * @author Francois Fornaciari
 */
public class Constants {

    /**
     * Application's context.
     */
    public final static String APPLICATION_CONTEXT = "/system-manager";

    /**
     * Manifest's header to declare SWF-Modules.
     */
    public final static String SWF_MODULE_MANIFEST_HEADER = "SWF-Module";

    /**
     * Channel's ID for sending events.
     */
    public final static String DESTINATION = "events";

    /**
     * Granite properties
     */
    public final static String GRANITE_SERVICE = "SystemManager";
    public final static String GRANITE_CHANNEL = "SystemManagerChannel";
    public final static String GRANITE_CHANNEL_URI = "/WebContent/graniteamf/amf";

    public final static String GRAVITY_SERVICE = "SystemManagerGravity";
    public final static String GRAVITY_CHANNEL = "SystemManagerGravityChannel";
    public final static String GRAVITY_CHANNEL_URI = "/WebContent/graniteamf/gravity";
}
