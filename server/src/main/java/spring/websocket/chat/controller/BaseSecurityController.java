package spring.websocket.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements common security utility methods.
 * Extend controller classes from this class to use common security
 * methods.
 *
 * @author Yasitha Thilakaratne
 */
public abstract class BaseSecurityController {

    @Autowired
    private SessionRegistry sessionRegistry;

    /**
     * checks whether the current user is authenticated.
     * @return true if logged in
     */
    protected boolean isAuthenticated() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    private User getSecurityContextHeldUserObject() {
        if (isAuthenticated()) {
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }

    /**
     * @return username of the current user
     */
    protected String getCurrentUserName() {
        User authUser = getSecurityContextHeldUserObject();
        if (authUser != null) {
            return authUser.getUsername();
        }
        return null;
    }

    /**
     * Note: Regularly calling on this method while large number of users logged in
     * may have performance issue. In that sort of situations it's better to use
     * separate online user store and add/remove users by listeners implementing,
     * {@link org.springframework.security.web.authentication.AuthenticationSuccessHandler}
     * {@link org.springframework.security.web.authentication.logout.LogoutSuccessHandler}
     * interfaces.
     *
     * @return list of usernames of all logged in users.
     */
    public List<String> getAllActiveUsers() {
        String currentUsername = getCurrentUserName();
        return sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty()
                        && !((User) u).getUsername().equals(currentUsername))
                .map(o -> ((User) o).getUsername())
                .collect(Collectors.toList());
    }
}
