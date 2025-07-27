package com.radovan.play.repositories.impl;

import com.radovan.play.entity.RoomCategoryEntity;
import com.radovan.play.repositories.RoomCategoryRepository;
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
public class RoomCategoryRepositoryImpl implements RoomCategoryRepository {

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
    public Optional<RoomCategoryEntity> findById(Integer categoryId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomCategoryEntity> query = cb.createQuery(RoomCategoryEntity.class);
            Root<RoomCategoryEntity> root = query.from(RoomCategoryEntity.class);
            query.where(cb.equal(root.get("roomCategoryId"), categoryId));
            List<RoomCategoryEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }

    @Override
    public Optional<RoomCategoryEntity> findByName(String name) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomCategoryEntity> query = cb.createQuery(RoomCategoryEntity.class);
            Root<RoomCategoryEntity> root = query.from(RoomCategoryEntity.class);

            query.where(cb.equal(root.get("name"), name));

            query.select(root);

            List<RoomCategoryEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }

    @Override
    public RoomCategoryEntity save(RoomCategoryEntity categoryEntity) {
        return withSession(session -> {
            if (categoryEntity.getRoomCategoryId() == null) {
                session.persist(categoryEntity);
            } else {
                session.merge(categoryEntity);
            }
            session.flush();
            return categoryEntity;
        });
    }

    @Override
    public List<RoomCategoryEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<RoomCategoryEntity> query = cb.createQuery(RoomCategoryEntity.class);
            Root<RoomCategoryEntity> root = query.from(RoomCategoryEntity.class);
            query.select(root);
            return session.createQuery(query).getResultList();
        });
    }

    @Override
    public void deleteById(Integer categoryId) {
        withSession(session -> {
            RoomCategoryEntity categoryEntity = session.get(RoomCategoryEntity.class, categoryId);
            if (categoryEntity != null) {
                session.remove(categoryEntity);
            }
            return null;
        });
    }
}
