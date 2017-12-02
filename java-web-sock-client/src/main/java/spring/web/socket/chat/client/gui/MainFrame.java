package spring.web.socket.chat.client.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.web.socket.chat.client.sender.SockJsJavaClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.*;

/**
 * Sample swing UI to provide user send/receive message functionality.
 *
 * @author Yasitha Thilakaratne
 */
@Component
public class MainFrame extends JFrame {

    @Autowired
    SockJsJavaClient sockJsJavaClient;

     DefaultListModel<String> listModel;

    public MainFrame() throws HeadlessException {
        this.setSize(500, 500);
        this.setResizable(false);
        initUI();
        setFrameCloseEvent();
    }

    @PostConstruct
    private void init() {
        sockJsJavaClient.connectClient();
        sockJsJavaClient.subscribeClient();
    }

    @PreDestroy
    private void destroy() {
        sockJsJavaClient.disconnectClient();
    }

    public void addToList(String from, String time, String msg) {
        listModel.addElement(from + " (" + time + ")" + " -> " + msg);
    }

    public void addToList(String msg) {
        listModel.addElement(msg);
    }

    private void initUI() {
        this.setLayout(null);
        listModel = new DefaultListModel<>();

        JList<String> list = new JList<>(listModel);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBounds(0, 0, 480, 430);

        this.add(scroll);

        JTextField textField = new JTextField();
        textField.setBounds(0, 430, 350, 50);
        this.add(textField);

        JButton jButton = new JButton("Send");
        jButton.setBounds(350, 430, 130, 50);
        this.add(jButton);

        jButton.addActionListener(e -> {
            String text = textField.getText();
            if (text == null || text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your message");
                textField.requestFocus();
                return;
            }
            sockJsJavaClient.send("Java swing sender", textField.getText());
        });
    }

    private void setFrameCloseEvent() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(-1);
            }
        });
    }
}
