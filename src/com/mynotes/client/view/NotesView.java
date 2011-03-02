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



package com.mynotes.client.view;

//~--- non-JDK imports --------------------------------------------------------

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.user.client.ui.Widget;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

public interface NotesView {
    HasClickHandlers getDeleteAnchor();

    HasKeyPressHandlers getNoteKeyPressHandlers();

    HasFocusHandlers getNoteFocusHandlers();

    HasBlurHandlers getNoteBlurHandlers();

    void setNoteText(String text);

    void clearNoteText();

    void setNoteAdvise(String text);

    void setVisibleNoteAdvise(boolean visible);

    void setVisibleNotePressEnter(boolean visible);

    String getNoteText();

    void setData(List<String> data);

    int getClickedRow(ClickEvent event);

    List<Integer> getSelectedRows();

    Widget asWidget();
}

