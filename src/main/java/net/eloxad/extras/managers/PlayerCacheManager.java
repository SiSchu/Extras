package net.eloxad.extras.managers;

import com.google.inject.Singleton;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
@Singleton
public class PlayerCacheManager {

    private final Map<UUID, Map<String, Object>> cache = new ConcurrentHashMap<>();


    public <T> void setValue(UUID uuid, String key, T value) {
        cache.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>()).put(key, value);
    }


    public <T> T getValue(UUID uuid, String key, Class<T> type) {
        Map<String, Object> playerData = cache.get(uuid);
        if (playerData != null) {
            Object value = playerData.get(key);
            if (type.isInstance(value)) {
                return (T) value;
            }
        }
        return null;
    }

    public void removeValue(UUID uuid, String key) {
        Map<String, Object> playerData = cache.get(uuid);
        if (playerData != null) {
            playerData.remove(key);
            if (playerData.isEmpty()) {
                cache.remove(uuid);
            }
        }
    }

    public boolean hasValue(UUID uuid, String key) {
        return cache.containsKey(uuid) && cache.get(uuid).containsKey(key);
    }


    public void removePlayer(UUID uuid) {
        cache.remove(uuid);
    }
}
