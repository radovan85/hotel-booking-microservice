package com.radovan.play.repositories.impl;

import com.radovan.play.entity.NoteEntity;
import com.radovan.play.repositories.NoteRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Singleton
public class NoteRepositoryImpl implements NoteRepository {

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
    public NoteEntity save(NoteEntity noteEntity) {
        return withSession(session -> {
            if (noteEntity.getNoteId() == null) {
                session.persist(noteEntity);
            } else {
                session.merge(noteEntity);
            }
            session.flush();
            return noteEntity;
        });
    }

    @Override
    public Optional<NoteEntity> findById(Integer noteId) {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<NoteEntity> query = cb.createQuery(NoteEntity.class);
            Root<NoteEntity> root = query.from(NoteEntity.class);
            query.where(cb.equal(root.get("noteId"), noteId));
            List<NoteEntity> results = session.createQuery(query).getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        });
    }

    @Override
    public void deleteById(Integer noteId) {
        withSession(session -> {
            NoteEntity noteEntity = session.get(NoteEntity.class, noteId);
            if (noteEntity != null) {
                session.remove(noteEntity);
            }
            return null;
        });
    }

    @Override
    public List<NoteEntity> findAll() {
        return withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<NoteEntity> cq = cb.createQuery(NoteEntity.class);
            Root<NoteEntity> root = cq.from(NoteEntity.class);
            cq.select(root);
            cq.orderBy(cb.desc(root.get("createTime")));  // 👈 Sort by createTime DESC
            return session.createQuery(cq).getResultList();
        });
    }


    @Override
    public void deleteAll() {
        withSession(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<NoteEntity> criteriaDelete = cb.createCriteriaDelete(NoteEntity.class);
            criteriaDelete.from(NoteEntity.class);

            MutationQuery deleteQuery = session.createMutationQuery(criteriaDelete);
            deleteQuery.executeUpdate();
            return null;
        });
    }


}
