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
import com.mynotes.client.services.LoginServiceAsync;
import com.mynotes.client.view.HeaderView;
import com.mynotes.shared.UserAccountDTO;

public class HeaderActivity extends AbstractActivity {
    private HandlerRegistration SignOutAnchorClickHandlerRegistration;
    private HandlerRegistration UserNameAnchorClickHandlerRegistration;
    private HeaderView          display;
    private EventBus            eventBus;
    private String              logOutUrl;
    private PlaceController     placeController;
    private UserAccountDTO      userAccount;

    @Inject
    public HeaderActivity(PlaceController placeController, HeaderView display) {
        this.display         = display;
        this.placeController = placeController;
    }

    /*
     * TODO Integrate this in the constructor and GIN Must be call before
     * start(..);
     */
    public void setUserAccountDTO(UserAccountDTO userAccout) {
        this.userAccount = userAccout;
    }

    /*
     * TODO Integrate this in the constructor and GIN Must be call before
     * start(..);
     */
    public void setLogOutURL(String logOutUrl) {
        this.logOutUrl = logOutUrl;
    }

    /**
     * Invoked by the ActivityManager to start a new Activity
     */
    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        bind();
        populateView();
        containerWidget.setWidget(display.asWidget());
    }

    @Override
    public void onStop() {
        unbind();
    }

    private void populateView() {
        if (userAccount != null) {
            display.setUserName(userAccount.getEmail());
        } else {
            display.setUserName("?");
        }
    }

    /**
     * Ask user before stopping this activity
     */
    @Override
    public String mayStop() {
        return "";
    }

    /**
     * Navigate to a new Place in the browser
     */
    public void goTo(Place place) {
        placeController.goTo(place);
    }

    public void bind() {
        UserNameAnchorClickHandlerRegistration = display.getSettingsAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                goTo(new Setting());
            }
        });
        SignOutAnchorClickHandlerRegistration = display.getSignOutAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                logOut();
            }
        });
    }

    private void unbind() {
        UserNameAnchorClickHandlerRegistration.removeHandler();
        SignOutAnchorClickHandlerRegistration.removeHandler();
    }

    private void logOut() {
        if (logOutUrl != null) {
            Window.Location.assign(logOutUrl);
        }
    }
}
