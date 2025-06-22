package service;

import model.User;
import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for user-related operations.
 */
public interface IUserService {

    /**
     * Create a new user from form input values.
     *
     * @param username user name
     * @param email user email
     * @param country user country
     * @param role user role
     * @param status active status
     * @param password user password
     * @param dobStr date of birth string in yyyy-MM-dd or null
     * @throws SQLException on persistence errors
     */
    void createUser(
            User user
    ) throws SQLException;

    /**
     * Retrieve all users.
     */
    List<User> getAllUsers();

    /**
     * Retrieve a user by primary key.
     */
    User getUserById(int id);

    /**
     * Update an existing user.
     */
    void updateUser(User user) throws SQLException;

    /**
     * Delete a user.
     */
    void deleteUser(int id) throws SQLException;
}
