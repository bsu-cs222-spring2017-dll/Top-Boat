package cs222.topboat.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public  class Log {
    private static Log chatLog;
    private static Log gameLog;

    public ObservableList<Message> messages;
    private MessageReceivedListener messageReceivedListener;
    public boolean newMessages;

    private Log() {
        messages = FXCollections.observableArrayList();
    }

    public static Log chatLog() {
        if(chatLog == null) {
            chatLog = new Log();
        }
        return chatLog;
    }

    public static Log gameLog() {
        if(gameLog == null) {
            gameLog = new Log();
        }
        return gameLog;
    }

    public void addMessage(Message message) {
        messages.add(message);
        messageReceivedListener.onMessageReceived(message);
    }

    public void addMessageReceivedListener(MessageReceivedListener listener) {
        this.messageReceivedListener = listener;
    }

    public static class Message {
        String contents;
        Type type;

        public Message(String contents, Type type) {
            this.contents = contents;
            this.type = type;
        }

        public String getContents() {
            return contents;
        }

        public Color getColor() {
            return type.color;
        }

        public enum Type {
            ERROR(Color.RED),
            SUCCESS(Color.GREEN),
            PLAYER_MESSAGE(Color.BLUE),
            OPPONENT_MESSAGE(Color.ORANGE);

            Type(Color c) {
                color = c;
            }
            Color color;
        }
    }

    public interface MessageReceivedListener {
        void onMessageReceived(Message newMessage);
    }
}
