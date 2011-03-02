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



package com.mynotes.shared;

public class NoteVerifier {
    private final static int NOTE_CONTENT_MAX_LENGHT = 100;

    public static boolean isNoteValid(String text) {
        if (text == null) {
            return false;
        }

        if (text.isEmpty() || text.trim().equals("")) {
            return false;
        }

        return true;
    }

    // Test null before invoke method
    public static boolean isLenghtValid(String text) {
        return text.length() < NOTE_CONTENT_MAX_LENGHT;
    }

    // Test null before invoke method
    public static String prepareNote(String text) {
        String preparedText = text.trim();

        if (!isLenghtValid(preparedText)) {
            return preparedText.substring(0, NOTE_CONTENT_MAX_LENGHT);
        }

        return preparedText;
    }
}
