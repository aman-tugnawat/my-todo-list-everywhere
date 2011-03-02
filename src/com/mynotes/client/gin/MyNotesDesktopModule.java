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



package com.mynotes.client.gin;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.mynotes.client.activity.HeaderActivity;
import com.mynotes.client.activity.LoadingPopupActivity;
import com.mynotes.client.mvp.AppPlaceHistoryMapper;
import com.mynotes.client.mvp.CenterActivityMapper;
import com.mynotes.client.place.Todo;
import com.mynotes.client.services.LoginService;
import com.mynotes.client.services.LoginServiceAsync;
import com.mynotes.client.services.NotesService;
import com.mynotes.client.services.NotesServiceAsync;
import com.mynotes.client.services.NotesServiceAsyncWrapper;
import com.mynotes.client.view.HeaderView;
import com.mynotes.client.view.HeaderViewImpl;
import com.mynotes.client.view.LoadingPopupView;
import com.mynotes.client.view.LoadingPopupViewImpl;
import com.mynotes.client.view.NotesView;
import com.mynotes.client.view.NotesViewImpl;
import com.mynotes.client.view.SettingsView;
import com.mynotes.client.view.SettingsViewImpl;

public class MyNotesDesktopModule extends AbstractGinModule {
    private static final String LOGIN_SERVICES = "/mynotes/loginService";
    private static final String NOTES_SERVICE  = "/mynotes/notesService";
    private final Place         startingPlace  = new Todo();

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(LoadingPopupActivity.class);
        bind(HeaderActivity.class);

        // Center views
        bind(HeaderView.class).to(HeaderViewImpl.class).in(Singleton.class);
        bind(SettingsView.class).to(SettingsViewImpl.class).in(Singleton.class);
        bind(NotesView.class).to(NotesViewImpl.class).in(Singleton.class);
        bind(LoadingPopupView.class).to(LoadingPopupViewImpl.class).in(Singleton.class);
        bind(CenterActivityMapper.class);
    }

    @Provides
    @Singleton
    public NotesServiceAsync getNotesService(EventBus eventBus) {

        // Hack for Development Mode (Eclipse)
        NotesServiceAsync service  = GWT.create(NotesService.class);
        ServiceDefTarget  endpoint = (ServiceDefTarget) service;

        endpoint.setServiceEntryPoint(NOTES_SERVICE);

        // Inject events to monitor in/out traffic
        NotesServiceAsync wrapper = new NotesServiceAsyncWrapper(service, eventBus);

        return wrapper;
    }

    @Provides
    @Singleton
    public LoginServiceAsync getLoginService(EventBus eventBus) {

        // Hack for Development Mode (Eclipse)
        LoginServiceAsync service  = GWT.create(LoginService.class);
        ServiceDefTarget  endpoint = (ServiceDefTarget) service;

        endpoint.setServiceEntryPoint(LOGIN_SERVICES);

        return service;
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(EventBus eventBus) {
        PlaceController placeController = new PlaceController(eventBus);

        return placeController;
    }

    @Provides
    @Singleton
    public PlaceHistoryHandler getPlaceHistoryHandler(PlaceController placeController, EventBus eventBus) {
        AppPlaceHistoryMapper historyMapper  = GWT.create(AppPlaceHistoryMapper.class);
        PlaceHistoryHandler   historyHandler = new PlaceHistoryHandler(historyMapper);

        historyHandler.register(placeController, eventBus, startingPlace);

        return historyHandler;
    }
}
