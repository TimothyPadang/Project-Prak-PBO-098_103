package controller;

import model.User;
import model.UserDAO;

/**
 * AuthController - Controller untuk autentikasi
 * MVC: menangani logika login/logout
 */
public class AuthController {
    private UserDAO userDAO;
    private User loggedInUser;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }
        loggedInUser = userDAO.login(username, password);
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
