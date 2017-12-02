package spring.web.socket.chat.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import spring.web.socket.chat.client.gui.MainFrame;

/**
 * Spring boot application initiator
 *
 * @author Yasitha Thilakaratne
 */
@EnableAutoConfiguration
@SpringBootApplication
public class Program {

    public static void main(String [] args) {
        SpringApplication app = new SpringApplication(Program.class);
        app.setHeadless(false);
        ConfigurableApplicationContext context = app.run(args);

        MainFrame appFrame = context.getBean(MainFrame.class);

        appFrame.setVisible(true);
    }
}
