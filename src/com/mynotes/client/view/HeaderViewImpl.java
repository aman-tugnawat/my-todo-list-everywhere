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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HeaderViewImpl extends Composite implements HeaderView {
    private static HeaderPanelUiBinder uiBinder = GWT.create(HeaderPanelUiBinder.class);
    @UiField
    Anchor                             settings;
    @UiField
    Anchor                             signOut;
    @UiField
    Label                              userName;

    public HeaderViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public HasClickHandlers getSignOutAnchor() {
        return signOut;
    }

    @Override
    public HasClickHandlers getSettingsAnchor() {

        // TODO Auto-generated method stub
        return settings;
    }

    @Override
    public void setUserName(String name) {
        userName.setText(name);
    }

    @Override
    public void setLogOutUrl(String url) {
        signOut.setHref(url);
    }

    interface HeaderPanelUiBinder extends UiBinder<Widget, HeaderViewImpl> {}
}
