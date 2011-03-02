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



package com.mynotes.client.mvp;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;

import com.mynotes.client.activity.NotesActivity;
import com.mynotes.client.activity.SettingsActivity;
import com.mynotes.client.place.Setting;
import com.mynotes.client.place.Todo;
import com.mynotes.client.services.LoginServiceAsync;
import com.mynotes.client.services.NotesServiceAsync;
import com.mynotes.client.view.NotesView;
import com.mynotes.client.view.SettingsView;

public class CenterActivityMapper implements ActivityMapper {
    private LoginServiceAsync loginService;
    private NotesView         notesDisplay;
    private NotesServiceAsync notesService;
    private PlaceController   placeController;
    private SettingsView          userDisplay;

    @Inject
    public CenterActivityMapper(PlaceController placeController, NotesView notesDisplay, SettingsView UserView,
                                NotesServiceAsync notesService, LoginServiceAsync loginService) {
        super();
        this.placeController = placeController;
        this.notesDisplay    = notesDisplay;
        this.userDisplay     = UserView;
        this.loginService    = loginService;
        this.notesService    = notesService;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof Todo) {
            return new NotesActivity((Todo) place, placeController, notesDisplay, notesService);
        }

        if (place instanceof Setting) {
            return new SettingsActivity((Setting) place, placeController, userDisplay, loginService);
        }

        return null;
    }
}

