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

package com.mynotes.server;

//~--- non-JDK imports --------------------------------------------------------

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.mynotes.client.rpc.Action;
import com.mynotes.client.rpc.Response;
import com.mynotes.client.services.NotesService;
import com.mynotes.client.services.notesservice.AddNote;
import com.mynotes.client.services.notesservice.AddNoteResponse;
import com.mynotes.client.services.notesservice.DeleteNotes;
import com.mynotes.client.services.notesservice.DeleteNotesResponse;
import com.mynotes.client.services.notesservice.GetUserNotes;
import com.mynotes.client.services.notesservice.GetUserNotesResponse;
import com.mynotes.server.domain.Note;
import com.mynotes.server.domain.UserAccount;
import com.mynotes.shared.NoteDTO;
import com.mynotes.shared.NoteVerifier;
import com.mynotes.shared.exception.NotLoggedInException;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@SuppressWarnings("serial")
public class NotesServiceImpl extends RemoteServiceServlet implements
		NotesService {
	private static final Logger log = Logger.getLogger(NotesServiceImpl.class
			.getName());

	@Override
	public <T extends Response> T execute(Action<T> action)
			throws NotLoggedInException {
		log.info("Execute(" + action.getClass().getName() + ")");

		final UserService userService = UserServiceFactory.getUserService();

		if (!userService.isUserLoggedIn()) {
			log.info("not logged in");

			throw new NotLoggedInException();
		}

		final UserAccount userAccount = LoginHelper
				.findOrCreateUser(userService.getCurrentUser());
		T response = null;

		if (action instanceof GetUserNotes) {
			response = handleGetUserNotes((GetUserNotes) action, userAccount);
		} else if (action instanceof AddNote) {
			response = handleAddNote((AddNote) action, userAccount);
		} else if (action instanceof DeleteNotes) {
			response = handleDeleteNotes((DeleteNotes) action, userAccount);
		} else {
			log.warning("Unknow action requested -> Execute("
					+ action.getClass().getName() + ")");
		}

		return response;
	}

	@SuppressWarnings("unchecked")
	private <T extends Response> T handleGetUserNotes(GetUserNotes action,
			UserAccount user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// Attach user
		user = pm.makePersistent(user);

		ArrayList<NoteDTO> notesDTO = new ArrayList<NoteDTO>();
		Set<Note> notes = user.getNotes();
		if (!notes.isEmpty()) {
			for (Note note : notes) {
				notesDTO.add(Note.toDTO(note));
			}
		}

		return (T) new GetUserNotesResponse(notesDTO);
	}

	@SuppressWarnings("unchecked")
	private <T extends Response> T handleAddNote(AddNote action,
			UserAccount user) {
		String text = NoteVerifier.prepareNote(action.getNote());
		Note note = new Note();

		note.setBasicInfo(text, new Date(), new Date());

		PersistenceManager pm = PMF.get().getPersistenceManager();

		// Attach user
		user = pm.makePersistent(user);
		user.addNote(note);
		pm.close();

		return (T) new AddNoteResponse(Note.toDTO(note));
	}

	@SuppressWarnings("unchecked")
	private <T extends Response> T handleDeleteNotes(DeleteNotes action,
			UserAccount user) {
		ArrayList<String> notesIds = action.getNotesIds();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			// Attach user
			user = pm.makePersistent(user);

			Query q = pm.newQuery("SELECT FROM " + Note.class.getName()
					+ " WHERE :id.contains(id)");
			List<Note> results = (List<Note>) q.execute(notesIds);

			if (!results.isEmpty()) {
				for (Note note : results) {
					user.removeNote(note);
				}
			}

			q.closeAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}

		return (T) new DeleteNotesResponse(notesIds);
	}
}
