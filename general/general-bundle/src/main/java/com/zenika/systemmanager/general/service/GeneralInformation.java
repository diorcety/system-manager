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

package com.zenika.systemmanager.general.service;

import java.io.Serializable;

/**
 * General system information POJO.
 *
 * @author François Fornaciari
 */
public class GeneralInformation implements Serializable {

    /**
     * The number of processors available to the JVM.
     */
    private int availableProcessors;

    /**
     * The name of the operating system.
     */
    private String name;

    /**
     * The operating system's architecture.
     */
    private String arch;

    /**
     * The operating system's version.
     */
    private String version;

    /**
     * Empty contructor.
     */
    public GeneralInformation() {

    }

    /**
     * @return the availableProcessors
     */
    public int getAvailableProcessors() {
        return availableProcessors;
    }

    /**
     * @param availableProcessors the availableProcessors to set
     */
    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the arch
     */
    public String getArch() {
        return arch;
    }

    /**
     * @param arch the arch to set
     */
    public void setArch(String arch) {
        this.arch = arch;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
