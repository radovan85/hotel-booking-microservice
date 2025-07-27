package com.radovan.play.repositories.impl;

import com.radovan.play.entity.ReservationEntity;
import com.radovan.play.repositories.ReservationRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Singleton
public class ReservationRepositoryImpl implements ReservationRepository {

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
    public List<ReservationEntity> findAllByRoomId(Integer roomId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ReservationEntity> cq = cb.createQuery(ReservationEntity.class);
            Root<ReservationEntity> root = cq.from(ReservationEntity.class);

            Predicate byRoomId = cb.equal(root.get("roomId"), roomId);
            cq.where(byRoomId);
            cq.select(root);

            return session.createQuery(cq).getResultList();
        });
    }

    @Override
    public ReservationEntity save(ReservationEntity reservationEntity) {
        return withSession(session -> {
            if (reservationEntity.getReservationId() == null) {
                session.persist(reservationEntity);
            } else {
                session.merge(reservationEntity);
            }
            session.flush();
            return reservationEntity;
        });
    }

    @Override
    public List<ReservationEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ReservationEntity> cq = cb.createQuery(ReservationEntity.class);
            Root<ReservationEntity> root = cq.from(ReservationEntity.class);
            cq.select(root);
            return session.createQuery(cq).getResultList();
        });
    }

    @Override
    public List<ReservationEntity> findAllByGuestId(Integer guestId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ReservationEntity> cq = cb.createQuery(ReservationEntity.class);
            Root<ReservationEntity> root = cq.from(ReservationEntity.class);

            cq.select(root).where(cb.equal(root.get("guestId"), guestId));

            return session.createQuery(cq).getResultList();
        });
    }

    @Override
    public void deleteById(Integer reservationId) {
        withSession(session -> {
            ReservationEntity reservationEntity = session.get(ReservationEntity.class, reservationId);
            if (reservationEntity != null) {
                session.remove(reservationEntity);
            }
            return null;
        });
    }

    @Override
    public Optional<ReservationEntity> findById(Integer reservationId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ReservationEntity> query = cb.createQuery(ReservationEntity.class);
            Root<ReservationEntity> root = query.from(ReservationEntity.class);
            query.where(cb.equal(root.get("reservationId"), reservationId));
            List<ReservationEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }

    @Override
    public void deleteAllByGuestId(Integer guestId) {
        withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<ReservationEntity> delete = cb.createCriteriaDelete(ReservationEntity.class);
            Root<ReservationEntity> root = delete.from(ReservationEntity.class);

            delete.where(cb.equal(root.get("guestId"), guestId));
            session.createMutationQuery(delete).executeUpdate();

            return null;
        });
    }

    @Override
    public void deleteAllByRoomId(Integer roomId) {
        withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<ReservationEntity> delete = cb.createCriteriaDelete(ReservationEntity.class);
            Root<ReservationEntity> root = delete.from(ReservationEntity.class);

            delete.where(cb.equal(root.get("roomId"), roomId));
            session.createMutationQuery(delete).executeUpdate();

            return null;
        });
    }



}
