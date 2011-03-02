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

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.mynotes.client.activity.HeaderActivity;
import com.mynotes.client.gin.MyNotesGinjector;
import com.mynotes.shared.UserAccountDTO;

//~--- JDK imports ------------------------------------------------------------

import java.util.logging.Logger;

public class MyNotesApp implements EntryPoint {
	interface MyNotesAppUiBinder extends UiBinder<HTMLPanel, MyNotesApp> {}
	
    public static final String        EMBEDDED_SIGN_OUT_URL = "signOutUrl";
    public static final String        EMBEDDED_USER_ACCOUNT = "userAccount";
    private static final Logger       log                   = Logger.getLogger(MyNotesApp.class.getName());
    private static MyNotesAppUiBinder mUiBinder             = GWT.create(MyNotesAppUiBinder.class);
    private EventBus                  eventBus;
    @UiField
    SimplePanel                       headerPanel;
    @UiField
    SimplePanel                       loadingPopup;
    @UiField
    SimplePanel                       mainPanel;
    private RootLayoutPanel           root;
    private String                    signOutUrl;
    private UserAccountDTO            userAccountDTO;

    @Override
    public void onModuleLoad() {
        final boolean retrieved = loadJSEmbeddedData();

        if (!retrieved) {

            // A possible solution
            // (1) or/then (2)
            // (1) (degraded mode) try to retrieve missing data by requesting
            // server
            // If errors occurs again, go to (2)
            // (2) (out of service) redirect to error page
        	
            // Here I'm doing a bad thing in order to not loose time
        	// I will simply display a popup (worst thing for the user =0) )
            Window.alert("Sorry, but your account have a problem. Our team will solve this issue as soon as possible.");

            return;
        }

        createUI();

        // We can put more in GIN and have less work here
        MyNotesGinjector factory = GWT.create(MyNotesGinjector.class);

        eventBus = factory.getEventBus();

        // Setting the loading popup & headerActivity (no need ActivityMapper,
        // only one activity in this example)
        factory.getLoadingPopupActivity().start(loadingPopup, eventBus);

        HeaderActivity headerActivity = factory.getHeaderActivity();
        headerActivity.setUserAccountDTO(userAccountDTO);
        headerActivity.setLogOutURL(signOutUrl);
        headerActivity.start(headerPanel, eventBus);

        // Every ActivityManager listen the same eventBus
        setDisplayToActivityMapper(factory.getCenterActivityMapper(), eventBus, mainPanel);

        // Goes to place represented on URL or default place
        factory.getPlaceHistoryHandler().handleCurrentHistory();
    }

    /**
     * Load webpage embedded data (reduce the number of server requests to display the first screen)
     * Populate the field userAccountDTO & signOutUrl
     * 
     * @return true if userAccountDTO & signOutUrl loaded from JS, false otherwise
     */
    private boolean loadJSEmbeddedData() {
        try {
            userAccountDTO = GWTHelper.getSerializedObject(EMBEDDED_USER_ACCOUNT);
            signOutUrl     = GWTHelper.getString(EMBEDDED_SIGN_OUT_URL);
        } catch (Exception e) {
            log.severe("Javascript embedded variable ");
            e.printStackTrace();

            return false;
        }

        // Coherence check
        if ((userAccountDTO == null) || (signOutUrl == null) || signOutUrl.isEmpty()) {
            log.severe("Javascript embedded variable lakes or errors");

            return false;
        }

        return true;
    }

    /**
     * Create the UI from XML and add the content to rootpanel
     */
    private void createUI() {
        HTMLPanel outer = mUiBinder.createAndBindUi(this);

        root = RootLayoutPanel.get();
        root.add(outer);
    }

    /**
     * Create an ActivityManager for the ActivityMapper in argument, and set the ActivityManager display
     * 
     * @param mapper The ActivityMapper that will listen eventBus to manager widget
     * @param eventBus EventBus that the ActivityMapper will listen (used for Place mechanism)
     * @param widget Widget managed by the ActivityManager
     */
    private static void setDisplayToActivityMapper(ActivityMapper mapper, EventBus eventBus, AcceptsOneWidget widget) {
        ActivityManager manager = new ActivityManager(mapper, eventBus);

        manager.setDisplay(widget);
    }

    
}
