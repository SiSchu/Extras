package net.eloxad.extras.managers;

import net.eloxad.extras.utils.InventorySession;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventorySessionManager {
    // Map target UUID to InventorySession
    private static final Map<UUID, InventorySession> sessions = new HashMap<>();

    // Get or create a session for the given target.
    public static InventorySession getOrCreateSession(OfflinePlayer target) {
        UUID targetId = target.getUniqueId();
        InventorySession session = sessions.get(targetId);
        if (session == null) {
            session = new InventorySession(target);
            sessions.put(targetId, session);
        }
        return session;
    }

    public static InventorySession getSession(UUID targetId) {
        return sessions.get(targetId);
    }

    public static void removeSession(UUID targetId) {
        sessions.remove(targetId);
    }
}
