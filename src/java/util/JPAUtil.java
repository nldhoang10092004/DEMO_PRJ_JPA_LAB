package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/** Tạo duy nhất 1 EntityManagerFactory cho toàn ứng dụng */
public class JPAUtil {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("TestDBPU"); // tên persistence-unit trong persistence.xml

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
