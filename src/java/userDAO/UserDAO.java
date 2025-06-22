package userDAO;

import jakarta.persistence.*;
import model.User;
import util.JPAUtil;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class UserDAO implements IUserDAO {
    private static final String LOGIN_JPQL =
        "SELECT NEW model.User(u.id, u.username, u.email, u.role) "
      + "FROM User u WHERE u.username = :u AND u.password = :p";

    /** Tương đương LOGIN1 JDBC */
    public User checkLogin(String username, String password) throws SQLException {
        if (username == null || password == null
         || username.isBlank() || password.isBlank()) {
            throw new SQLException("Tên đăng nhập hoặc mật khẩu không hợp lệ!");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> q = em.createQuery(LOGIN_JPQL, User.class);
            q.setParameter("u", username);
            q.setParameter("p", password);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (PersistenceException e) {
            throw new SQLException("Lỗi khi thực thi login", e);
        } finally {
            em.close();
        }
    }

    /** Tương đương INSERT_USER JDBC */
    @Override
    public void insertUser(User user) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw new SQLException("Lỗi khi thêm người dùng", ex);
        } finally {
            em.close();
        }
    }

    /** Tương đương SELECT_USER_BY_ID JDBC
     * @param id
     * @return 
     * @throws java.sql.SQLException */
    @Override
    public User selectUser(int id) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(User.class, id);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Lỗi khi tìm user theo ID", e);
        } finally {
            em.close();
        }
    }

    /** Tương đương SELECT_ALL_USERS JDBC
     * @throws java.sql.SQLException */
    @Override
    public List<User> selectAllUsers() throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM User", User.class)
                     .getResultList();
        } catch (PersistenceException e) {
            throw new SQLException("Lỗi khi lấy danh sách user", e);
        } finally {
            em.close();
        }
    }

    /** Tương đương UPDATE_USER JDBC */
    @Override
    public boolean updateUser(User user) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(user);
            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw new SQLException("Lỗi khi cập nhật người dùng", ex);
        } finally {
            em.close();
        }
    }

    /** Tương đương DELETE_USER JDBC */
    @Override
    public boolean deleteUser(int id) throws SQLException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            User u = em.find(User.class, id);
            if (u == null) return false;
            tx.begin();
            em.remove(u);
            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            throw new SQLException("Lỗi khi xóa người dùng", ex);
        } finally {
            em.close();
        }
    }

    /** Main test giống JDBC main() */
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        try {
            System.out.println("-- Test LOGIN --");
            User u = dao.checkLogin("a", "1");
            System.out.println(u == null ? "Login failed" : "Login success: " + u);

            System.out.println("-- Test INSERT --");
            User newUser = new User(0,
                "jpaUser", "jpa@mail.com", "VN",
                "user", true, "pwd123", LocalDate.parse("1995-07-15"));
            dao.insertUser(newUser);
            System.out.println("Inserted: " + newUser);

            System.out.println("-- Test SELECT ALL --");
            dao.selectAllUsers().forEach(System.out::println);

            System.out.println("-- Test UPDATE --");
            newUser.setRole("admin");
            dao.updateUser(newUser);
            System.out.println("Updated: " + dao.selectUser(newUser.getId()));

            System.out.println("-- Test DELETE --");
            dao.deleteUser(newUser.getId());
            System.out.println("After delete:");
            dao.selectAllUsers().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
