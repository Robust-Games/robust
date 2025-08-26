/**
 * @author Ersin Yesiltas
 */
package com.robustgames.robustserver;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.net.Connection;

/**
 * Handles a single client connection in a game session.
 * <p>
 * Listens for incoming messages (bundles) from the client and
 * delegates processing to the parent {@link GameSession}.
 */
public class PlayerConnectionHandler {

    private final Connection<Bundle> conn;
    private final String playerId;
    private final GameSession session;

    /**
     * Constructs a new handler for a connected player.
     *
     * @param conn     The TCP connection to the client.
     * @param playerId The assigned player ID (e.g., "PLAYER1" or "PLAYER2").
     * @param session  The parent game session that manages the game.
     */
    public PlayerConnectionHandler(Connection<Bundle> conn, String playerId, GameSession session) {
        this.conn = conn;
        this.playerId = playerId;
        this.session = session;

        init();
    }

    /**
     * Initializes the message listener for this player.
     * Incoming bundles are passed to the {@link GameSession}.
     */
    private void init() {
        conn.addMessageHandler((c, bundle) -> {
            session.handleBundle(playerId, bundle);
        });
    }

    /**
     * Sends a bundle to the connected client.
     *
     * @param bundle The message to send.
     */
    public void send(Bundle bundle) {
        conn.send(bundle);
    }
}
