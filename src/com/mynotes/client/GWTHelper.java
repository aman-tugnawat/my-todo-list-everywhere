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

package com.mynotes.client;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;

import com.mynotes.client.services.LoginService;

public class GWTHelper {
	private GWTHelper() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create the text to declare a JavaScript variable
	 * 
	 * @param varName
	 *            JavaScript variable name
	 * @param varValue
	 *            JavaScript variable value ( must be escaped by the user )
	 * @return JavaScript variable string like var varName='varValue';
	 */
	public static String createJavascriptVar(String varName, String varValue) {
		StringBuffer strBuffer = new StringBuffer("var ");

		strBuffer.append(varName);
		strBuffer.append("='");
		strBuffer.append(varValue);
		strBuffer.append("'; ");

		return strBuffer.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSerializedObject(String name)
			throws SerializationException {
		String serialized = getString(name);
		// Need one service class, not using GIN (not important)
		SerializationStreamFactory ssf = GWT.create(LoginService.class);

		return (T) ssf.createStreamReader(serialized).readObject();
	}

	public static native String getString(String name)
	/*-{
		return eval("$wnd." + name);
	}-*/;

	/**
	 * Escape a variable value in order to create a safe JavaScript value
	 * 
	 * @param s
	 *            string to escape
	 * 
	 * @return string escaped
	 */
	public static String escapeForSingleQuotedJavaScriptString(String s) {
		s = escapeScriptTags(s);
		s = escapeBackslash(s);
		s = escapeSingleQuotes(s);

		return s;
	}

	private static String escapeScriptTags(String s) {
		return s.replaceAll("(?si)<\\s*script.*?>", "<xxxscript>").replaceAll(
				"(?si)</\\s*script\\s*>", "</xxxscript>");
	}

	private static String escapeBackslash(String s) {
		return s.replaceAll("\\\\", "\\\\\\\\");
	}

	private static String escapeSingleQuotes(String s) {
		return s.replaceAll("'", "\\\\'");
	}
}
