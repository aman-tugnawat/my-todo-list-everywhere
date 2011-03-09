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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.mynotes.client.event.NoteAddedEvent;
import com.mynotes.client.event.NotesDeletedEvent;
import com.mynotes.client.place.Todo;
import com.mynotes.client.services.NotesServiceAsync;
import com.mynotes.client.services.notesservice.AddNote;
import com.mynotes.client.services.notesservice.AddNoteCallback;
import com.mynotes.client.services.notesservice.AddNoteResponse;
import com.mynotes.client.services.notesservice.DeleteNotes;
import com.mynotes.client.services.notesservice.DeleteNotesCallback;
import com.mynotes.client.services.notesservice.DeleteNotesResponse;
import com.mynotes.client.services.notesservice.GetUserNotes;
import com.mynotes.client.services.notesservice.GetUserNotesCallback;
import com.mynotes.client.services.notesservice.GetUserNotesResponse;
import com.mynotes.client.view.NotesView;
import com.mynotes.shared.NoteDTO;
import com.mynotes.shared.NoteVerifier;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/**
 * Display the list of todo list of the user.
 * The user can add and delete todos
 */
public class NotesActivity extends AbstractActivity {
    private HandlerRegistration deleteAnchorClickHandlerRegistration;
    private NotesView           display;
    private EventBus            eventBus;
    private HandlerRegistration noteBlurHandlerRegistration;
    private HandlerRegistration noteFocusHandlerRegistration;
    private HandlerRegistration noteKeyPressHandlerRegistration;
    private ArrayList<NoteDTO>  notes;
    private PlaceController     placeController;
    private NotesServiceAsync   rpcService;

    @Inject
    public NotesActivity(Todo place, PlaceController placeController, NotesView display, NotesServiceAsync rpcService) {
        this.display         = display;
        this.rpcService      = rpcService;
        this.placeController = placeController;
    }

    /**
     * Invoked by the ActivityManager to start a new Activity
     */
    @Override
    public void start(final AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        bind();
        initView();
        containerWidget.setWidget(display.asWidget());
        //TODO We can embedded NoteDTOs on the webpage JS to enhance loading
        //And having less server request
        fetchNotes();
    }

    @Override
    public void onStop() {
        unbind();
    }

    private void fetchNotes() {
        rpcService.execute(new GetUserNotes(), new GetUserNotesCallback() {
            @Override
            protected void got(GetUserNotesResponse result) {
                populateNotesMap(result.getNotes());
                sortNotes();
                populateView();
            }
        });
    }

    private void initView() {
        display.setVisibleNotePressEnter(false);
        display.setVisibleDeleteButton(false);
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

    private void populateView() {
        List<String> data = new ArrayList<String>();

        if (!notes.isEmpty()) {
            for (NoteDTO note : notes) {
                data.add(note.getContent());
            }
        }

        display.setVisibleDeleteButton(!notes.isEmpty());
        display.setData(data);
    }

    public void bind() {
        noteKeyPressHandlerRegistration = display.getNoteKeyDownHandlers().addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    addNote();
                }
			}
		});
        
        noteFocusHandlerRegistration = display.getNoteFocusHandlers().addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                display.setVisibleNoteAdvise(false);
                display.setVisibleNotePressEnter(false);
            }
        });
        noteBlurHandlerRegistration = display.getNoteBlurHandlers().addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                String text = display.getNoteText();

                if (text.trim().isEmpty()) {
                    display.setVisibleNoteAdvise(true);
                } else {
                    display.setVisibleNotePressEnter(true);
                }
            }
        });
        deleteAnchorClickHandlerRegistration = display.getDeleteAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                deleteSelectedNotes();
            }
        });
    }

    private void unbind() {
        noteKeyPressHandlerRegistration.removeHandler();
        noteFocusHandlerRegistration.removeHandler();
        noteBlurHandlerRegistration.removeHandler();
        deleteAnchorClickHandlerRegistration.removeHandler();
    }

    private void addNote() {
        String text = NoteVerifier.prepareNote(display.getNoteText());

        display.setNoteText("");
        rpcService.execute(new AddNote(text), new AddNoteCallback() {
            @Override
            protected void got(AddNoteResponse result) {
                eventBus.fireEvent(new NoteAddedEvent());

                NoteDTO note = result.getNoteDTO();

                notes.add(note);
                sortNotes();
                populateView();
            }
        });
    }

    public void sortNotes() {

        // TODO: Improve the sort algorithm (quickSort maybe)
        for (int i = 0; i < notes.size(); ++i) {
            for (int j = 0; j < notes.size() - 1; ++j) {
                if (notes.get(j).getContent().compareTo((notes.get(j + 1).getContent())) > 0) {
                    NoteDTO tmp = notes.get(j);

                    notes.set(j, notes.get(j + 1));
                    notes.set(j + 1, tmp);
                }
            }
        }
    }

    public void setNotes(List<NoteDTO> notes) {
        populateNotesMap(notes);
    }

    public NoteDTO getNotes(int index) {
        return notes.get(index);
    }

    private void populateNotesMap(List<NoteDTO> notesDTO) {
        this.notes = new ArrayList<NoteDTO>();

        if (!notesDTO.isEmpty()) {
            for (NoteDTO note : notesDTO) {
                this.notes.add(note);
            }
        }
    }

    private void deleteSelectedNotes() {
        List<Integer> selectedRows = display.getSelectedRows();

        // Prevent useless requests
        if (selectedRows.size() == 0) {
            return;
        }

        ArrayList<String> ids = new ArrayList<String>();

        for (int i = 0; i < selectedRows.size(); ++i) {
            ids.add(notes.get(selectedRows.get(i)).getId());
        }

        rpcService.execute(new DeleteNotes(ids), new DeleteNotesCallback() {
            @Override
            protected void got(DeleteNotesResponse result) {
                List<String> ids = result.getNotesIds();

                eventBus.fireEvent(new NotesDeletedEvent());

                // TODO: Improve the way we delete notes
                for (String deletedId : ids) {
                    for (int i = 0; i < notes.size(); ++i) {
                        NoteDTO note = notes.get(i);

                        if (note.getId().equals(deletedId)) {
                            notes.remove(i);
                        }
                    }
                }

                populateView();
            }
        });
    }
}
