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

import com.google.appengine.api.users.User;

import com.mynotes.server.domain.UserAccount;

//~--- JDK imports ------------------------------------------------------------

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

public class LoginHelper {
    private static Logger log = Logger.getLogger(LoginHelper.class.getName());

    private LoginHelper() {
        throw new UnsupportedOperationException();
    }

    public static UserAccount findOrCreateUser(User providerUser) {
        PersistenceManager pm           = PMF.get().getPersistenceManager();
        UserAccount        user         = null;
        UserAccount        detachedUser = null;
        Query              q            = pm.newQuery(UserAccount.class, "authId == :authId");
        Transaction 	   tx 			= pm.currentTransaction();
        
        q.setUnique(true);

        try {
            final String authId = providerUser.getEmail();
            final String name   = providerUser.getNickname();
            final String email  = providerUser.getEmail();

            user = (UserAccount) q.execute(authId);

            tx.begin();
            if (user != null) {
                log.info("User already exists, logged in ");
            } else {
                log.info("UserAccount " + authId + " is new, creating thes UserAccount");
                user = new UserAccount();
                user.setBasicInfo(authId, name, email);
                user = pm.makePersistent(user);
            }
            
            user.setLastActivity(new Date());
            
            tx.commit();
            
            detachedUser = pm.detachCopy(user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            q.closeAll();
            pm.close();
        }

        return detachedUser;
    }
}
