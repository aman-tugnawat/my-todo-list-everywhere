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

import com.mynotes.client.GWTHelper;
import com.mynotes.client.MyNotesApp;
import com.mynotes.server.DevHelper;
import com.mynotes.server.LoginHelper;
import com.mynotes.server.domain.UserAccount;
import com.mynotes.shared.UserAccountDTO;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.PrintWriter;

import java.lang.reflect.Method;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AppServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(AppServlet.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		final UserService userService = UserServiceFactory.getUserService();

		if (userService.isUserLoggedIn()) {
			log.info("Serving App page");

			final UserAccount userAccount = LoginHelper
					.findOrCreateUser(userService.getCurrentUser());

			// Process data for JavaScript Variable
			final String serializedUserAccount = serializeUserAccount(userAccount);
			final String logOutUrl = userService.createLogoutURL(DevHelper
					.getHomeUrl(req));

			writeAppWebPage(req, resp, serializedUserAccount, logOutUrl);
		} else {
			log.info("Redirect unlogged user from App page to Home page");
			resp.sendRedirect(DevHelper.getHomeUrl(req));
		}
	}

	private void writeAppWebPage(HttpServletRequest req,
			HttpServletResponse resp, String serializedUserAccountDTO,
			String serializedLogOutUrl) throws IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");

		PrintWriter writer = resp.getWriter();

		writer.append("<!doctype html>");
		writer.append("<html>");
		writer.append("<head>");
		writer.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
		writer.append("<style type=\"text/css\">");
		writer.append(".centerContainer {padding: 15px 11px 15px 11px;}");
		writer.append(".rowFContainer {padding: 5px 11px 5px 11px;border-top: 2px solid #dbd9d5;border-right: 2px solid #dbd9d5;}");
		writer.append(".rowContainer {padding: 5px 11px 5px 11px;border-top: 2px solid #dbd9d5;}");
		writer.append("</style>");
		writer.append("<title>My todo list</title>");
		writer.append("<script type=\"text/javascript\" language=\"javascript\" src=\"../mynotes/mynotes.nocache.js\"></script>");
		writer.append("<script type=\"text/javascript\" language=\"javascript\">");

		// Example of Javascript variable
		// writer.append("var user='<serialized user data>'");
		// writer.append("var info = { ");
		// writer.append("\"email\" : \"" +
		// userService.getCurrentUser().getEmail() + "\"");
		// writer.append(" };");
		writer.append(GWTHelper.createJavascriptVar(
				MyNotesApp.EMBEDDED_USER_ACCOUNT, serializedUserAccountDTO));
		writer.append(GWTHelper.createJavascriptVar(
				MyNotesApp.EMBEDDED_SIGN_OUT_URL, serializedLogOutUrl));
		writer.append("</script>");
		writer.append("</head>");
		writer.append("<body>");
		writer.append("<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe>");
		writer.append("<noscript>");
		writer.append("<div style=\"width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif\">");
		writer.append("Your web browser must have JavaScript enabled in order for this application to display correctly.");
		writer.append("</div>");
		writer.append("</noscript>");
		writer.append("</body>");
		writer.append("</html>");
	}

	private String serializeUserAccount(UserAccount userAccount) {
		String serializedUserAccountDTO = null;
		final UserAccountDTO userAccountDTO = UserAccount.toDTO(userAccount);

		try {

			// TODO Find an elegant way to refer to the name of the method toDTO
			Method userAccountMethod = UserAccount.class.getMethod("toDTO",
					UserAccount.class);

			serializedUserAccountDTO = ServletHelper.serialize(
					userAccountMethod, userAccountDTO);

			if (serializedUserAccountDTO == null) {
				log.severe("UserAccountDTO not serialized");
			}
		} catch (SecurityException e) {
			log.severe("UserAccountDTO not serialized");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			log.severe("UserAccountDTO not serialized");
			e.printStackTrace();
		}

		return serializedUserAccountDTO;
	}
}
