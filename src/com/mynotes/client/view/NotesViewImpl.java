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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

public class NotesViewImpl extends Composite implements NotesView {
    private static final int           CHECKBOX_COLUMN = 0;
    private static final int           TEXT_COLUMN     = 1;
    private static NotesViewUiUiBinder uiBinder        = GWT.create(NotesViewUiUiBinder.class);
    @UiField
    Label                              advise;
    @UiField
    Anchor                             deleteLink;
    @UiField
    TextBox                            newNote;
    @UiField
    FlexTable                          notesTable;
    @UiField
    Label                              pressEnter;

    public NotesViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        notesTable.getColumnFormatter().setWidth(CHECKBOX_COLUMN, "15px");
        notesTable.setCellPadding(0);
        notesTable.setCellSpacing(0);
    }

    @Override
    public HasKeyDownHandlers getNoteKeyDownHandlers() {
        return newNote;
    }

    @Override
    public void setData(List<String> data) {
        notesTable.removeAllRows();

        HTMLTable.CellFormatter cf = notesTable.getCellFormatter();

        for (int i = 0; i < data.size(); ++i) {

            // Set data
            notesTable.setWidget(i, CHECKBOX_COLUMN, new CheckBox());
            notesTable.setText(i, TEXT_COLUMN, data.get(i));

            // Set style
            cf.addStyleName(i, CHECKBOX_COLUMN, "rowFContainer");
            cf.addStyleName(i, TEXT_COLUMN, "rowContainer");
        }
    }

    @Override
    public int getClickedRow(ClickEvent event) {
        int            selectedRow = -1;
        HTMLTable.Cell cell        = notesTable.getCellForEvent(event);

        if (cell != null) {

            // Suppress clicks if the user is actually selecting the
            // check box
            //
            if (cell.getCellIndex() > 0) {
                selectedRow = cell.getRowIndex();
            }
        }

        return selectedRow;
    }

    @Override
    public List<Integer> getSelectedRows() {
        List<Integer> selectedRows = new ArrayList<Integer>();

        for (int i = 0; i < notesTable.getRowCount(); ++i) {
            CheckBox checkBox = (CheckBox) notesTable.getWidget(i, 0);

            if (checkBox.getValue()) {
                selectedRows.add(i);
            }
        }

        return selectedRows;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setNoteText(String text) {
        newNote.setText(text);
    }

    @Override
    public String getNoteText() {
        return newNote.getText();
    }

    @Override
    public HasClickHandlers getDeleteAnchor() {
        return deleteLink;
    }

    @Override
    public HasBlurHandlers getNoteBlurHandlers() {
        return newNote;
    }

    @Override
    public HasFocusHandlers getNoteFocusHandlers() {
        return newNote;
    }

    @Override
    public void clearNoteText() {
        newNote.setText("");
    }

    @Override
    public void setNoteAdvise(String text) {
        advise.setText(text);
    }

    @Override
    public void setVisibleNoteAdvise(boolean visible) {
        advise.setVisible(visible);
    }

    @Override
    public void setVisibleNotePressEnter(boolean visible) {
        pressEnter.setVisible(visible);
    }

    interface NotesViewUiUiBinder extends UiBinder<Widget, NotesViewImpl> {}
}
