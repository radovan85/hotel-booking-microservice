package com.radovan.play.repositories.impl;

import com.radovan.play.entity.RoomEntity;
import com.radovan.play.repositories.RoomRepository;
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
public class RoomRepositoryImpl implements RoomRepository {

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
    public Optional<RoomEntity> findById(Integer roomId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomEntity> query = cb.createQuery(RoomEntity.class);
            Root<RoomEntity> root = query.from(RoomEntity.class);
            query.where(cb.equal(root.get("roomId"), roomId));
            List<RoomEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }

    @Override
    public RoomEntity save(RoomEntity roomEntity) {
        return withSession(session -> {
            if (roomEntity.getRoomId() == null) {
                session.persist(roomEntity);
            } else {
                session.merge(roomEntity);
            }
            session.flush();
            return roomEntity;
        });
    }

    @Override
    public void deleteById(Integer roomId) {
        withSession(session -> {
            RoomEntity roomEntity = session.get(RoomEntity.class, roomId);
            if (roomEntity != null) {
                session.remove(roomEntity);
            }
            return null;
        });
    }

    @Override
    public List<RoomEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomEntity> cq = cb.createQuery(RoomEntity.class);
            Root<RoomEntity> root = cq.from(RoomEntity.class);
            cq.select(root);
            return session.createQuery(cq).getResultList();
        });
    }

    @Override
    public List<RoomEntity> findAllByCategoryId(Integer categoryId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomEntity> cq = cb.createQuery(RoomEntity.class);
            Root<RoomEntity> root = cq.from(RoomEntity.class);

            root.join("roomCategory");

            cq.where(cb.equal(root.get("roomCategory").get("roomCategoryId"), categoryId));

            cq.select(root);
            return session.createQuery(cq).getResultList();
        });
    }

    @Override
    public Optional<RoomEntity> findByRoomNumber(Integer roomNumber) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomEntity> query = cb.createQuery(RoomEntity.class);
            Root<RoomEntity> root = query.from(RoomEntity.class);

            query.where(cb.equal(root.get("roomNumber"), roomNumber));

            query.select(root);

            List<RoomEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }
}
