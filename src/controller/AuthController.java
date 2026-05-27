package controller;

import model.User;
import model.UserDAO;

/**
 * AuthController - Controller untuk autentikasi dan register
 */
public class AuthController {
    private UserDAO userDAO;
    private User loggedInUser;

    public AuthController() { this.userDAO = new UserDAO(); }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) return null;
        loggedInUser = userDAO.login(username.trim(), password);
        return loggedInUser;
    }

    public boolean register(String username, String password, String fullName, String email) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()) return false;
        if (userDAO.findByUsername(username.trim()) != null) return false;

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setFullName(fullName.trim());
        user.setEmail(email != null ? email.trim() : "");
        return userDAO.create(user);
    }

    public void logout() { loggedInUser = null; }
    public User getLoggedInUser() { return loggedInUser; }
    public boolean isLoggedIn() { return loggedInUser != null; }
}
