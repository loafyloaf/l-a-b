package com.ibm.codey.loyalty.accounts.dao;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.ibm.codey.loyalty.accounts.models.User;

@RequestScoped
public class UserDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public void createUser(User user) {
        em.persist(user);
    }

    public void updateUser(User user) {
        em.merge(user);
    }

    public void lockUser(User user) {
        em.find(User.class, user.getUserId(), LockModeType.PESSIMISTIC_WRITE);
    }

    public User findUserByRegistryId(String subject) {
        try {
            return em.createNamedQuery("User.findUserByRegistryId", User.class)
                .setParameter("subject", subject).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }

    public List<User> findPendingDeletionRequests() {
        return em.createNamedQuery("User.findPendingDeletionRequests", User.class).getResultList();
    }

}