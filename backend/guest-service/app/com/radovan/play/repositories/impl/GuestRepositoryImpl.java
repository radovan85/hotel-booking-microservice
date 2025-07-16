package com.radovan.play.repositories.impl;

import com.radovan.play.entity.GuestEntity;
import com.radovan.play.repositories.GuestRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Singleton
public class GuestRepositoryImpl implements GuestRepository {

    private SessionFactory sessionFactory;

    @Inject
    private void initialize(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private <T> T withSession(Function<Session, T> function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = function.apply(session);
                tx.commit();
                return result;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }


    @Override
    public Optional<GuestEntity> findById(Integer guestId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<GuestEntity> cq = cb.createQuery(GuestEntity.class);
            Root<GuestEntity> root = cq.from(GuestEntity.class);
            cq.where(cb.equal(root.get("guestId"), guestId));
            List<GuestEntity> results = session.createQuery(cq).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }

    @Override
    public Optional<GuestEntity> findByUserId(Integer userId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<GuestEntity> cq = cb.createQuery(GuestEntity.class);
            Root<GuestEntity> root = cq.from(GuestEntity.class);
            cq.where(cb.equal(root.get("userId"), userId));
            List<GuestEntity> result = session.createQuery(cq).getResultList();
            return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
        });
    }


    @Override
    public GuestEntity save(GuestEntity guestEntity) {
        return withSession(session -> {
           if(guestEntity.getGuestId() == null){
               session.persist(guestEntity);
           }else {
               session.merge(guestEntity);
           }

           session.flush();
           return guestEntity;
        });
    }

    @Override
    public void deleteById(Integer guestId) {
        withSession(session -> {
           GuestEntity guestEntity = session.get(GuestEntity.class,guestId);
           if(guestEntity!=null){
               session.remove(guestEntity);
           }
           return null;
        });
    }

    @Override
    public List<GuestEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<GuestEntity> cq = cb.createQuery(GuestEntity.class);
            Root<GuestEntity> root = cq.from(GuestEntity.class);
            cq.select(root);
            return session.createQuery(cq).getResultList();
        });
    }
}
