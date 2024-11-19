import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Frame frame;
    private TextArea textArea;
    private TextField textField;

    public static void main(String[] args) {
        new ChatClient().start();
    }

    public void start() {
        frame = new Frame("Chat Client");

        // Layout setup
        textArea = new TextArea();
        textArea.setEditable(false);
        textField = new TextField();

        // Panel for input
        Panel panel = new Panel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);

        // Send button
        Button sendButton = new Button("Send");
        panel.add(sendButton, BorderLayout.EAST);

        // Add components to frame
        frame.add(textArea, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        // Set frame properties
        frame.setSize(400, 300);
        frame.setVisible(true);

        // Send button action
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Connect to server
        connectToServer("127.0.0.1", 12345);
    }

    private void connectToServer(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Read messages from the server in a new thread
            new Thread(new IncomingMessageHandler()).start();
        } catch (IOException e) {
            textArea.append("Unable to connect to server.\n");
        }
    }

    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            textArea.append("You: " + message + "\n");
            textField.setText("");  // Clear the input field
        }
    }

    // This thread listens for incoming messages from the server
    private class IncomingMessageHandler implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    textArea.append("Server: " + message + "\n");
                }
            } catch (IOException e) {
                textArea.append("Connection to server lost.\n");
            }
        }
    }
}