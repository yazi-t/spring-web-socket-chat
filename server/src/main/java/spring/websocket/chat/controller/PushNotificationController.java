package spring.websocket.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements spring MVC based server sent event controller
 * methods. EventSources are considered as a part of
 * HTML 5 javascript API.
 *
 * <a href="https://developer.mozilla.org/en-US/docs/Web/API/EventSource">EventSource documentation</a>
 *
 * @author Yasitha Thilakaratne
 */
@Controller
@RequestMapping("sse")
public class PushNotificationController {

    List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @RequestMapping("subscribe")
    public SseEmitter subscribeSSE() {
        SseEmitter sseEmitter = new SseEmitter();

        emitters.add(sseEmitter);

        sseEmitter.onCompletion(() -> {
            System.out.println("SseEmitter.onCompletion called.");
            emitters.remove(sseEmitter);
        });
        return sseEmitter;
    }

    @RequestMapping(value = "add-event", method = RequestMethod.GET)
    public String postNotification(@RequestParam("data") String data) {
        emitters.forEach(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().name("group1").data(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return "redirect:/sse/add-notification";
    }

    @RequestMapping("add-notification")
    public String getNotification() {
        return "addNotification";
    }

    @RequestMapping("get-notification")
    public String pushNotification() {
        return "pushNotification";
    }
}
