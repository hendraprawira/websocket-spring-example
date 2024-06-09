package com.example.demo;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/ws-example")
public class WSExample {
    /** set list session for multiple client/connection */
    private static final Set<Session> sessions = new HashSet<>();

    /**
     * On Open WebSocket
     * @param session session
     * @param config endpoint config
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

        System.out.println("WebSocket connection opened");
        sessions.add(session);
        int i;
        for (i=0; i<100; i++){
            broadcastMessage("EXAMPLE");
        }
    }

    /**
     * On Close WebSocket
     * @param session session
     * @param closeReason close reason
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + closeReason.getReasonPhrase());
        sessions.remove(session);
    }

    /**
     * On Message WebSocket
     * @param message message
     * @param session session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        // Send Reply to Sender
        sendMessage(session, "Response to: " + message);
        // Send Message Broadcast
        broadcastMessage("Broadcast: " + message);
    }

    /**
     * Method to send message to sender
     * @param session session
     * @param message message
     */
    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to send message to all connected clients
     * @param path /v1/simulation-track
     * @param message message
     */
    public static void broadcastMessage(String message) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                // Handle exceptions, such as closed sessions
                e.printStackTrace();
            }
        }
    }
}
