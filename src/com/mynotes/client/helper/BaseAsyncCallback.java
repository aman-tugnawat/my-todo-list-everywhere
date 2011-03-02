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



package com.mynotes.client.helper;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.mynotes.shared.exception.NotLoggedInException;

public abstract class BaseAsyncCallback<T> implements AsyncCallback<T> {
    abstract protected void onApplicationFailure(Throwable caught);

    abstract protected void got(T result);

    @Override
    public void onFailure(Throwable caught) {
        GWT.log(caught.toString(), caught);

        try {
            throw caught;
        } catch (InvocationException invocationException) {
            Window.alert("The server is unreacheable");
        } catch (IncompatibleRemoteServiceException remoteServiceException) {
            Window.alert("The app was updated. Reload the page to start using it.");
        } catch (SerializationException serializationException) {
            Window.alert("A serialization error occurred. Try again.");
        } catch (RequestTimeoutException e) {
            Window.alert("This is taking too long, try again");
        } catch (NotLoggedInException notLoggedInException){
        	Window.alert("You are no more logged, please refresh the page");
        } catch (Throwable e) {
            onApplicationFailure(caught);
        }
    }

    @Override
    public void onSuccess(T result) {
        got(result);
    }
}

