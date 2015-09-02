package meet.lunch.lunchmeet.dao;

import meet.lunch.lunchmeet.dto.User;

/**
 * Created by Aaron on 6/16/2015.
 */
public interface IUserDAO {

    /**
     * Registers a user with firebase authentication.
     * @param email the user's email
     * @param password the user's password
     * @throws Exception new user not registered.
     */
    public String registerUser(String email, String password) throws Exception;

    /**
     * Creates a user on the database.
     * @param user the user to save
     * @throws Exception new user is not created.
     */
    public String createUser(User user) throws Exception;

    /**
     * Fetches a user from the database.
     * @param userID the userId of the user to retrieve
     * @throws Exception user could not be retrieved from the database.
     */
    public User fetchUser(String userID) throws Exception;

    /**
     * Updates a user in the database.
     * @param userId the userId of the user to update
     * @param user the updated user object to overwrite the current user object
     * @throws Exception user could not be updated in the database.
     */
    public String updateUser(String userId, User user) throws Exception;

    /**
     * Deletes a user from the database.
     * @param userId the userId of the user to delete
     * @throws Exception user could not be deleted from the database.
     */
    public String deleteUser(String userId) throws Exception;
}
