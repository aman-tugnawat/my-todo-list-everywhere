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
import com.mynotes.client.place.Todo;
import com.mynotes.client.services.NotesServiceAsync;
import com.mynotes.client.view.NotesView;

public class HeaderActivityMapper implements ActivityMapper {
    private NotesView         display;
    private PlaceController   placeController;
    private NotesServiceAsync rpcService;

    @Inject
    public HeaderActivityMapper(PlaceController placeController, NotesView display, NotesServiceAsync rpcService) {
        super();
        this.placeController = placeController;
        this.display         = display;
        this.rpcService      = rpcService;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof Todo) {
            return new NotesActivity((Todo) place, placeController, display, rpcService);
        }

        return null;
    }
}

