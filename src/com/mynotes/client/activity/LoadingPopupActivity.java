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



package com.mynotes.client.activity;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.mynotes.client.event.RPCInEvent;
import com.mynotes.client.event.RPCInEventHandler;
import com.mynotes.client.event.RPCOutEvent;
import com.mynotes.client.event.RPCOutEventHandler;
import com.mynotes.client.view.LoadingPopupView;

/**
 * Display a loading message when RPC messages are pending
 */
public class LoadingPopupActivity extends AbstractActivity {
    private int                 rpcCount = 0;
    private LoadingPopupView    display;
    private EventBus            eventBus;
    private PlaceController     placeController;
    private HandlerRegistration rcpInEventHandlerRegistration;
    private HandlerRegistration rcpOutEventHandlerRegistration;

    @Inject
    public LoadingPopupActivity(PlaceController placeController, LoadingPopupView display) {
        this.display         = display;
        this.placeController = placeController;
    }

    /**
     * Invoked by the ActivityManager to start a new Activity
     */
    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        bind();
        display.setVisible(false);
        containerWidget.setWidget(display.asWidget());
    }

    @Override
    public void onStop() {
        unbind();
    }

    /**
     * Ask user before stopping this activity
     */
    @Override
    public String mayStop() {
        return null;
    }

    /**
     * Navigate to a new Place in the browser
     */
    public void goTo(Place place) {
        placeController.goTo(place);
    }

    public void bind() {
        rcpOutEventHandlerRegistration = eventBus.addHandler(RPCOutEvent.TYPE, new RPCOutEventHandler() {
            @Override
            public void onRPCOut(RPCOutEvent event) {
                RPCout();
            }
        });
        rcpInEventHandlerRegistration = eventBus.addHandler(RPCInEvent.TYPE, new RPCInEventHandler() {
            @Override
            public void onRPCIn(RPCInEvent event) {
                RPCin();
            }
        });
    }

    private void unbind() {
        rcpOutEventHandlerRegistration.removeHandler();
        rcpInEventHandlerRegistration.removeHandler();
    }

    protected void RPCout() {
        synchronized (this) {
            rpcCount++;

            if (rpcCount == 1) {
                display.setVisible(true);
            }
        }
    }

    protected void RPCin() {
        synchronized (this) {
            rpcCount--;

            if (rpcCount <= 0) {
                rpcCount = 0;
                display.setVisible(false);
            }
        }
    }
}
