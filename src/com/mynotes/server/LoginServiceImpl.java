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
import com.mynotes.client.services.LoginService;
import com.mynotes.client.services.loginservice.DeleteLoggedInUser;
import com.mynotes.client.services.loginservice.DeleteLoggedInUserResponse;
import com.mynotes.client.services.loginservice.GetLoggedInUser;
import com.mynotes.client.services.loginservice.GetLoggedInUserResponse;
import com.mynotes.server.domain.UserAccount;
import com.mynotes.shared.UserAccountDTO;
import com.mynotes.shared.exception.NotLoggedInException;

//~--- JDK imports ------------------------------------------------------------

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
    private static final Logger log = Logger.getLogger(LoginServiceImpl.class.getName());

    @Override
    public <T extends Response> T execute(Action<T> action) throws NotLoggedInException {
        log.info("Execute(" + action.getClass().getName() + ")");

        final UserService userService = UserServiceFactory.getUserService();

        if (!userService.isUserLoggedIn()) {
            log.info("not logged in");
            
            throw new NotLoggedInException();
        }

        final UserAccount userAccount = LoginHelper.findOrCreateUser(userService.getCurrentUser());
        T                 response    = null;

        if (action instanceof GetLoggedInUser) {
            response = handleGetLoggedInUser((GetLoggedInUser) action, userAccount);
        } else if (action instanceof DeleteLoggedInUser) {
            response = handleDeleteLoggedInUser((DeleteLoggedInUser) action, userAccount, userService);
        } else {
            log.warning("Unknow action requested -> Execute(" + action.getClass().getName() + ")");
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private <T extends Response> T handleGetLoggedInUser(GetLoggedInUser action, UserAccount userAccount) {
        UserAccountDTO userDTO = UserAccount.toDTO(userAccount);

        return (T) new GetLoggedInUserResponse(userDTO);
    }

    @SuppressWarnings("unchecked")
    private <T extends Response> T handleDeleteLoggedInUser(DeleteLoggedInUser action, UserAccount userAccount,
            UserService userService) {
        final String redirectUrl = userService.createLogoutURL(DevHelper.getHomeUrl(getThreadLocalRequest()));

        PersistenceManager pm = PMF.get().getPersistenceManager();
        userAccount = pm.makePersistent(userAccount);
        pm.deletePersistent(userAccount);
        
        return (T) new DeleteLoggedInUserResponse(redirectUrl);
    }
}
