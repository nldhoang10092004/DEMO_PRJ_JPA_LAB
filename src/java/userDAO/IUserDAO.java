package userDAO;

import model.User;
import java.sql.SQLException;
import java.util.List;

public interface IUserDAO {
    User checkLogin(String username, String password) throws SQLException;
    void insertUser(User user) throws SQLException;
    User selectUser(int id) throws SQLException ;
    List<User> selectAllUsers() throws SQLException ;
    boolean deleteUser(int id) throws SQLException ;
    boolean updateUser(User user) throws SQLException;
}