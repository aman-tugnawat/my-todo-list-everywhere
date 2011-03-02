/*******************************************************************************
 *
 * My todo list everywhere
 *
 * Copyright (C) 2011 Davy Leggieri
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Tou can try the demo at http://todo-list-everywhere.appspot.com
 *
 ******************************************************************************/



package com.mynotes.server;

//~--- non-JDK imports --------------------------------------------------------

import com.google.appengine.api.utils.SystemProperty;

//~--- JDK imports ------------------------------------------------------------

import javax.servlet.http.HttpServletRequest;

public class DevHelper {
    private DevHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean isDevelopment() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
    }

    public static String getBaseUrl(HttpServletRequest req) {
        if (isDevelopment()) {
            return "http://127.0.0.1:8888/?gwt.codesvr=127.0.0.1:9997";
        } else if ((req.getServerPort() == 80) || (req.getServerPort() == 443)) {
            return req.getScheme() + "://" + req.getServerName();
        } else {
            return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        }
    }

    public static String getHomeUrl(HttpServletRequest req) {
        if (isDevelopment()) {
            return "http://127.0.0.1:8888/?gwt.codesvr=127.0.0.1:9997";
        } else if ((req.getServerPort() == 80) || (req.getServerPort() == 443)) {
            return req.getScheme() + "://" + req.getServerName();
        } else {
            return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        }
    }

    public static String getAppUrl(HttpServletRequest req) {
        if (isDevelopment()) {
            return "http://127.0.0.1:8888/app/?gwt.codesvr=127.0.0.1:9997";
        } else if ((req.getServerPort() == 80) || (req.getServerPort() == 443)) {
            return req.getScheme() + "://" + req.getServerName() + "/app";
        } else {
            return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/app";
        }
    }
}
