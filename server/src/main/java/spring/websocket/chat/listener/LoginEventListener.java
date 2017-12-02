package spring.websocket.chat.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import spring.websocket.chat.util.ActiveSessionManager;

/**
 * Listener for spring security login success event.
 *
 * @author Yasitha Thilakaratne
 */
@Component
public class LoginEventListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    @Autowired
    private ActiveSessionManager activeSessionManager;

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        activeSessionManager.add(event.getAuthentication().getName());
    }
}
