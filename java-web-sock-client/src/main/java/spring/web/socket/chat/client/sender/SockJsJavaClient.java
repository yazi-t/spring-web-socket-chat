package spring.web.socket.chat.client.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import spring.web.socket.chat.client.gui.MainFrame;
import spring.web.socket.chat.dto.ChatMessage;
import spring.web.socket.chat.dto.OutputMessage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Java client to connect and send messages to web socket
 * using stomp protocol.
 *
 * @author Yasitha Thilakaratne
 */
@Component
public class SockJsJavaClient {

    @Value("${web.socket.connect.url}")
    private String connectUrl;

    @Value("${web.socket.subscribe.url}")
    private String subscribeUrl;

    @Value("${web.socket.send.url}")
    private String sendUrl;

    @Autowired
    private MainFrame mainFrame;

    private StompSessionHandler stompSessionHandler;

    private static final Logger LOGGER = LoggerFactory.getLogger(SockJsJavaClient.class);

    private StompSession session;

    /**
     * Connects to the web socket
     */
    public void connectClient() {
        try {
            List<Transport> transports = new ArrayList<>(2);
            transports.add(new WebSocketTransport(new StandardWebSocketClient()));
            transports.add(new RestTemplateXhrTransport());

            SockJsClient sockJsClient = new SockJsClient(transports);

            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            try {
                session = stompClient.connect(connectUrl, getStompSessionHandler()).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.error("Error in callable while connecting to the web socket. ", e);
            }
        } catch (Exception e) {
            LOGGER.error("Error connecting to the web socket.", e);
        }
    }

    /**
     * Subscribe client to an end point
     *
     * @return StompSession.Subscription to unsubscribe
     *         if wants
     */
    public StompSession.Subscription subscribeClient() {
        return session.subscribe(subscribeUrl, getStompSessionHandler());
    }

    public void send(String from, String message) {
        try {
            StompSession.Receiptable receiptable = session.send(sendUrl, new ChatMessage(from, message, null));
            LOGGER.info("Message sent, [id: {}]", receiptable.getReceiptId());
        } catch (Exception e) {
            LOGGER.error("Error while sending message", e);
        }
    }

    private StompSessionHandler getStompSessionHandler() {
        if (stompSessionHandler == null) {
            stompSessionHandler = new StompSessionHandler() {
                @Override
                public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
                    LOGGER.info("Connected to the server. [session id : {}]", stompSession.getSessionId());
                }

                @Override
                public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
                    LOGGER.error("Exception in session handler. [session id : {}, exception : {}]", stompSession.getSessionId(), throwable);
                }

                @Override
                public void handleTransportError(StompSession stompSession, Throwable throwable) {
                    LOGGER.error("Transport error in th session handler. [session id : {}, exception : {}]", stompSession.getSessionId(), throwable);
                }

                @Override
                public Type getPayloadType(StompHeaders stompHeaders) {
                    return OutputMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders stompHeaders, Object o) {
                    OutputMessage outputMessage = (OutputMessage) o;
                    mainFrame.addToList(outputMessage.getFrom(), outputMessage.getTime(), outputMessage.getMessage());
                }

            };
        }
            return stompSessionHandler;
    }

    /**
     * Disconnects from web socket
      */
    public void disconnectClient() {
        try {
            session.disconnect();
            LOGGER.info("Disconnected from web socket. [url: {}]", connectUrl);
        } catch (Exception e) {
            LOGGER.error("Error while disconnecting from the server. ", e);
        }
    }
}
