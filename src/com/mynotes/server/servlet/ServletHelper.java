package com.mynotes.server.servlet;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.mynotes.client.GWTHelper;

public class ServletHelper {

	private static final Logger log = Logger.getLogger(ServletHelper.class
			.getName());

	private ServletHelper() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Serialize an object to Javascript string
	 * 
	 * @param method method producing the object to serialize
	 * @param obj object to serialize
	 * 
	 * @return Javascript string which is the object serialized
	 */
	static public String serialize(Method method, Object obj) {
		String serialized = null;
		try {
			serialized = RPC.encodeResponseForSuccess(method, obj);
			serialized = GWTHelper
					.escapeForSingleQuotedJavaScriptString(serialized);
		} catch (SerializationException e) {
			log.severe("SerializationException for object class ->"
					+ obj.getClass().getName());
			e.printStackTrace();
		}
		return serialized;
	}
}
