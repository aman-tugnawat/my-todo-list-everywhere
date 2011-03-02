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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.mynotes.client.place.Setting;
import com.mynotes.client.place.Todo;
import com.mynotes.client.services.LoginServiceAsync;
import com.mynotes.client.services.loginservice.DeleteLoggedInUser;
import com.mynotes.client.services.loginservice.DeleteLoggedInUserCallback;
import com.mynotes.client.services.loginservice.DeleteLoggedInUserResponse;
import com.mynotes.client.view.SettingsView;

/**
 * Display user settings
 *
 */
public class SettingsActivity extends AbstractActivity {
    private HandlerRegistration backAnchorClickHandlerRegistration;
    private HandlerRegistration deleteAccountAnchorClickHandlerRegistration;
    private SettingsView            display;
    private EventBus            eventBus;
    private LoginServiceAsync   loginService;
    private PlaceController     placeController;

    @Inject
    public SettingsActivity(Setting place, PlaceController placeController, SettingsView display,
                        LoginServiceAsync loginService) {
        this.display         = display;
        this.placeController = placeController;
        this.loginService    = loginService;
    }

    /**
     * Invoked by the ActivityManager to start a new Activity
     */
    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        bind();
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
        backAnchorClickHandlerRegistration = display.getBackAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                goTo(new Todo());
            }
        });
        deleteAccountAnchorClickHandlerRegistration =
            display.getDeleteAccountAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                deleteUserAccount();
            }
        });
    }

    private void unbind() {
        backAnchorClickHandlerRegistration.removeHandler();
        deleteAccountAnchorClickHandlerRegistration.removeHandler();
    }

    protected void deleteUserAccount() {
        loginService.execute(new DeleteLoggedInUser(), new DeleteLoggedInUserCallback() {
            @Override
            protected void got(DeleteLoggedInUserResponse result) {
                final String redirectUrl = result.getUserRedirectUrl();

                Window.alert("Thank you to gave us a try.");
                Window.Location.replace(redirectUrl);
            }
        });
    }
}
