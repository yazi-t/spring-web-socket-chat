package spring.websocket.chat.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import spring.websocket.chat.util.ActiveSessionManager;

import java.util.List;

/**
 * Listener to spring security logout and session expire event.
 *
 * @author Yasitha Thilakaratne
 */
@Component
public class SessionExpireEventListener implements ApplicationListener<SessionDestroyedEvent> {

    @Autowired
    private ActiveSessionManager activeSessionManager;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent event) {
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext) {
            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
            activeSessionManager.remove(userDetails.getUsername());
        }
    }
}
