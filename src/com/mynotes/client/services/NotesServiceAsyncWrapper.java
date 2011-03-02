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



package com.mynotes.client.services;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.mynotes.client.event.RPCInEvent;
import com.mynotes.client.event.RPCOutEvent;
import com.mynotes.client.rpc.Action;
import com.mynotes.client.rpc.Response;

/**
 * NotesService wrapper to inject RPC in and out events
 */
public class NotesServiceAsyncWrapper implements NotesServiceAsync {
    private NotesServiceAsync AsyncService;
    private EventBus          eventBus;

    public NotesServiceAsyncWrapper(NotesServiceAsync rpcService, EventBus eventBus) {
        this.AsyncService = rpcService;
        this.eventBus     = eventBus;
    }

    @Override
    public <T extends Response> void execute(Action<T> action, final AsyncCallback<T> callback) {
        onRpcOut();
        AsyncService.execute(action, new AsyncCallback<T>() {
            @Override
            public void onFailure(Throwable caught) {
                onRpcIn();
                callback.onFailure(caught);
            }
            @Override
            public void onSuccess(T result) {
                onRpcIn();
                callback.onSuccess(result);
            }
        });
    }

    private void onRpcOut() {
        eventBus.fireEvent(new RPCOutEvent());
    }

    protected void onRpcIn() {
        eventBus.fireEvent(new RPCInEvent());
    }
}

