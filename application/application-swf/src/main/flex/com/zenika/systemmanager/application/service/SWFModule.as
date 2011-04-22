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

package com.zenika.systemmanager.application.service {
/**
 * Model for an SWF Module.
 * @author Francois Fornaciari
 */
[RemoteClass(alias="com.zenika.systemmanager.application.service.SWFModule")]
[Bindable]
public class SWFModule {
    /**
     * Module's name.
     */
    public var name:String;

    /**
     * Module's alias.
     */
    public var alias:String;

    /**
     * Module's event type (load or unload).
     */
    public var eventType:String;

    /**
     * Load value.
     */
    public static const LOAD:String = "load";

    /**
     * Unload value.
     */
    public static const UNLOAD:String = "unload";

    /**
     * Shared suffix.
     */
    private static const SHARED:String = "Shared";

    /**
     * Empty constructor.
     */
    public function SWFModule() {

    }

    /**
     * Returns the module's path.
     * @return The module's path.
     *
     */
    public function getModulePath():String {
        return alias + "/" + name + ".swf";
    }

}
}