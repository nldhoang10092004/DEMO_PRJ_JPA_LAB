package service;

import model.User;
import userDAO.IUserDAO;
import userDAO.UserDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService implements IUserService {
    private IUserDAO userDao = new UserDAO();
    private static final DateTimeFormatter fmt =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void createUser(User user) throws SQLException {
        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên người dùng không được để trống!");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email không hợp lệ!");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự!");
        }
        // Validate and normalize dob
        LocalDate dob = user.getDob();
        if (dob != null) {
            if (dob.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày sinh không được là tương lai!");
            }
        }
        // Persist
        userDao.insertUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDao.selectAllUsers();
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public User getUserById(int id) {
        try {
            return userDao.selectUser(id);
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void updateUser(User user) throws SQLException {
        userDao.updateUser(user);
    }

    @Override
    public void deleteUser(int id) throws SQLException {
        userDao.deleteUser(id);
    }
}