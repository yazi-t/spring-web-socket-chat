package spring.websocket.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import spring.web.socket.chat.dto.ChatMessage;
import spring.web.socket.chat.dto.OutputMessage;
import spring.websocket.chat.util.ActiveSessionManager;
import spring.websocket.chat.util.CommonUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.Principal;
import java.util.*;

/**
 * Implements user-to-user sock js based message sending
 * controller methods.
 *
 * @author Yasitha Thilakaratne
 */
@Controller
@RequestMapping("msg-forward")
public class MessageForwardController extends BaseSecurityController implements ActiveSessionManager.ActiveUserChangeListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageForwardController.class);

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveSessionManager activeSessionManager;

    @PostConstruct
    private void init() {
        activeSessionManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeSessionManager.removeListener(this);
    }

    @RequestMapping("/chatbot")
    public String getChatBot(ModelMap modelMap) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            modelMap.addAttribute("username", getCurrentUserName());
            modelMap.addAttribute("onlineUsers", activeSessionManager.getAllExceptCurrentUser(getCurrentUserName()));
            return "sockJsEndToEndChat";
        }
        return "login";
    }

    @MessageMapping("/chat")
    public void send(Message<ChatMessage> message, @Payload ChatMessage chatMessage) throws Exception {
        Principal principal = message.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class);
        if (principal == null) {
            LOGGER.error("Principal is null");
            return;
        }
        String authenticatedSender = principal.getName();
        String time = CommonUtils.getCurrentTimeStamp();

        if (!authenticatedSender.equals(chatMessage.getRecipient())) {
            webSocket.convertAndSendToUser(authenticatedSender, "/queue/messages",
                    new OutputMessage(chatMessage.getFrom(), chatMessage.getText(), time, true));
        }

        webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages",
                new OutputMessage(chatMessage.getFrom(), chatMessage.getText(), time, false));

    }

    /**
     * This method will get called when Observable's internal state
     * is changed.
     */
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeSessionManager.getAll();
        webSocket.convertAndSend("/topic/active", activeUsers);
    }
}
