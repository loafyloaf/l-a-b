package com.ibm.codey.loyalty.accounts.deletion;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.ibm.codey.loyalty.accounts.dao.UserDao;
import com.ibm.codey.loyalty.accounts.models.User;

@Singleton
@Startup
public class AccountDeletionStartup {

    @Inject
    private UserDao userDAO;

    @Inject
    private AccountDeletionProcessor accountDeletionProcessor;

    /**
     * Process any pending user deletion requests when the application starts.
     */
    @PostConstruct
    public void init() {
        List<User> pendingUserDeletes = userDAO.findPendingDeletionRequests();
        for (User user : pendingUserDeletes) {
            try {
                accountDeletionProcessor.delete(user.getSubject(), true);
            } catch (EJBException ejbex) {  // cannot schedule asynchronous method right now (pool full)
                accountDeletionProcessor.addRetry(user.getSubject());
            }
        }
    }

}