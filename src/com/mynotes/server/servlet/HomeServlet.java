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



package com.mynotes.server.servlet;

//~--- non-JDK imports --------------------------------------------------------

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.mynotes.server.DevHelper;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.PrintWriter;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(HomeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = UserServiceFactory.getUserService();

        if (userService.isUserLoggedIn()) {
            log.info("Redirect Logged user from Home page to App page");
            resp.sendRedirect(DevHelper.getAppUrl(req));
        } else {
            log.info("Serving Home page");
            writeHomePage(req, resp, userService);
        }
    }

    private void writeHomePage(HttpServletRequest req, HttpServletResponse resp, UserService userService)
            throws IOException {
        final String urlLogIn = userService.createLoginURL(DevHelper.getAppUrl(req));

        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        // TODO: Much better if using a template engine
        PrintWriter writer = resp.getWriter();

        writer.append("<html><head>");
        writer.append("<title>My todo list</title>");

        // Inlined CSS for less server request
        writer.append("<style type=\"text/css\">");
        writer.append("body {font-family: Arial, Helvetica, sans-serif; background-color:#F1F1F1; width: 100%; text-align: center}");
        writer.append(".container{ width: 900px; margin-left: auto ; margin-right: auto; display: block; text-align: left}");
        writer.append(".center{ margin-top:100px; background-color:#FFFFFF; border:1px solid #AAA; padding: 40px;}");
        writer.append(".title{font-size:2em; font-weight:bold;}");
        writer.append(".intro{line-height:1.5em; color:#888888;}");
        writer.append(".footer{margin-top:10px; color:#A7A7A7;text-align:center; font-size: 0.9em;}");
        writer.append("</style>");
        writer.append("</head><body>");
        writer.append("<div class=\"container\">");
        writer.append("<div class=\"center\">");
        writer.append("<span class=\"title\">Your Todo list everywhere</span>");
        writer.append("<p class=\"intro\">Are you always writing down what you must not forget ? Have you ever spend time to find your paper todo list ? Now this is done. With this online app you will manage your todo list from everywhere. No more time spending to seek your list, more todo loose. And it's FREE !</p>");
        writer.append("<a href=");
        writer.append(urlLogIn);
        writer.append(">Sign in with you Google Account</a>");
        writer.append("</div>");
        writer.append("<div class=\"footer\"><p>Source code available at: <a href=\"http://code.google.com/p/my-todo-list-everywhere\">http://code.google.com/p/my-todo-list-everywhere</a></p><div>");
        writer.append("</div>");
        writer.append("</body></html>");
    }
}