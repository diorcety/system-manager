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

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Default HttpContext implementation.
 *
 * @author Francois Fornaciari
 */
public class DefaultHttpContext implements HttpContext {

    /**
     * Bundle containing resources to register.
     */
    private Bundle bundle;

    public DefaultHttpContext(Bundle bundle) {
        this.bundle = bundle;
    }

    /* (non-Javadoc)
      * @see org.osgi.service.http.HttpContext#getMimeType(java.lang.String)
      */
    public String getMimeType(String name) {
        return null;
    }

    /* (non-Javadoc)
      * @see org.osgi.service.http.HttpContext#getResource(java.lang.String)
      */
    public URL getResource(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        return this.bundle.getResource(name);
    }

    /* (non-Javadoc)
      * @see org.osgi.service.http.HttpContext#handleSecurity(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
      */
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return true;
    }

}
