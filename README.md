# spring-web-socket-chat
Sample chat application with spring web socket, Sock Js and Stomp protocol

This application demonstrates the usage of spring-websocket and stomp protocol to build a simple chat application. Shows examples for basic websocket messaging without stomp and sock js, group broadcasting message using stomp and sock js,user-to-user chat with spring security authentication, websocket java client implementation using spring-boot and java swing and also an out-of-topic example to push notifications using EventSources, SseEmitters.

*Refer end of the README file to get deployment info*

## Basic websocket chat

`spring.websocket.chat.config.WebSocketConfig`, and `webSocketChat.jsp` provides example for this.

Need to create `@Configuration` class with `@EnableWebSocket` annotation implementing interface `org.springframework.web.socket.config.annotation.WebSocketConfigurer`, a handler implementing `org.springframework.web.socket.WebSocketHandler` interface and register the end point with the handler. 

```Java
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
            super.afterConnectionEstablished(session);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            super.handleTextMessage(session, message);
            sessions.forEach(webSocketSession -> {
                try {
                    webSocketSession.sendMessage(message);
                } catch (IOException e) {
                    LOGGER.error("Error occurred.", e);
                }
            });
        }
```

## Group chat with Stomp and Sock Js

A sample group 'topic' chat using stomp protocol and [sock js](https://github.com/sockjs/sockjs-client/blob/master/README.md)  failover mechanisms.

Need to create a `@Configuration` class with `@EnableWebSocketMessageBroker` and implement `org.springframework.web.socket.config.annotation` register the end-points. Can handle messages using `@MessageMapping("/endpoint")` `@SendTo("/topic/messages")` annotated methods in the controller.
See `WebSocketSockJsBrokerConfig`, `MessageBroadcastController` and `sockJsGrpChat.jsp`.

```Java
    @MessageMapping("/grp-chat")
    @SendTo("/topic/messages")
    public OutputMessage send(ChatMessage chatMessage) throws Exception {
        String time = CommonUtils.getCurrentTimeStamp();
        return new OutputMessage(chatMessage.getFrom(), chatMessage.getText(), time, false);
    }
```
Jackson mapper library should be in the classpath to serialize return object into Json.

## User-to-user chat with Stomp, Sock Js and Spring security

A private chat application using stomp and sock js as previous and spring security for authentication.
See `WebSocketSockJsBrokerConfig`, `SecurityConfig` and `MessageForwardController`.

```Java
    @Autowired
    private SimpMessagingTemplate webSocket;

    @MessageMapping("/chat")
    public void send(Message<ChatMessage> message, @Payload ChatMessage chatMessage) throws Exception {
        String time = CommonUtils.getCurrentTimeStamp();

        webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages",
                new OutputMessage(chatMessage.getFrom(), chatMessage.getText(), time, false));

    }
```
Can send to specific authenticated user using `SimpMessagingTemplate#convertAndSendToUser`.

## Websocket Java client

Java-web-socket-client module implements this. Uses Java swing for the presentation and spring-boot for the server.
Exchanges messages using websocket stomp protocol from Java environment.
See `SockJsJavaClient`.

## Server push notifications using EventSource API

An additional example from websocket to demonstrate usage of EventSource API.
Spring provides `org.springframework.web.servlet.mvc.method.annotation.SseEmitter` for HTML 5 Javascript EventSource API.
See `PushNotificationController`.

```Java
    sseEmitter.send(SseEmitter.event().name("group1").data(data));
```
*Note: This is not categorized under spring websocket even though this sample implementation contained.*

## Deployment information

### Tested environment
1. Java version: JDK 8
2. Servlet container: Apache Tomcat 8.5.15
3. Build tool: Maven

### Deploying steps

#### Main project and server
1. `cd` into project.
2. Build project with `mvn install`
3. Copy server/target/sample-chat.war into tomcat server.
4. Run tomcat.
5. Visit http://localhost:{tomcat-port}/sample-chat/ from browser.

#### Java client
1. Build main project. (since server and java client both uses 'model' module)
2. cd into java-web-sock-client.
3. Run app using `mvn spring-boot:run`.
