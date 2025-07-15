package com.radovan.play.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.inject.Singleton;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

@Singleton
public class HibernateUtil {

    private final SessionFactory sessionFactory;

    public HibernateUtil() {
        this.sessionFactory = buildSessionFactory();
    }

    private SessionFactory buildSessionFactory() {
        try {
            // 🔧 Hikari konfiguracija
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mariadb://localhost:3307/hotel-db");
            hikariConfig.setUsername("root");
            hikariConfig.setPassword("1111");
            hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            hikariConfig.setMaximumPoolSize(10);
            hikariConfig.setMinimumIdle(2);
            hikariConfig.setIdleTimeout(600000);
            hikariConfig.setConnectionTimeout(30000);
            hikariConfig.setMaxLifetime(1800000);

            HikariDataSource dataSource = new HikariDataSource(hikariConfig);

            // 🧠 Hibernate konfiguracija
            Configuration configuration = new Configuration();
            configuration.getProperties().put("hibernate.connection.datasource", dataSource);
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", "false");
            configuration.setProperty("hibernate.format_sql", "false");

            // ➕ Dodaj sve entity klase
            configuration.addAnnotatedClass(com.radovan.play.entity.RoomCategoryEntity.class);
            configuration.addAnnotatedClass(com.radovan.play.entity.RoomEntity.class);
            // Dodaj još po potrebi...

            // 🧱 SessionFactory setup
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
