package com.doublejump;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.UUID;
import java.util.HashMap;

class DoubleJumpListener implements Listener {
    private final HashSet<UUID> jumpedPlayers = new HashSet<>();
    private final HashSet<UUID> canDoubleJump = new HashSet<>();
    private final HashMap<UUID, Long> lastNotifyTime = new HashMap<>();
    private final JavaPlugin plugin;
    private final double launchPower = 1.3;
    private final double launchPowerY = 1.0;
    private final long notifyCooldown = 5000; // 5 секунд

    public DoubleJumpListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!player.hasPotionEffect(PotionEffectType.WIND_CHARGED)) {
            return;
        }

        UUID playerId = player.getUniqueId();

        if (!jumpedPlayers.contains(playerId)) {
            jumpedPlayers.add(playerId);
            canDoubleJump.add(playerId);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (jumpedPlayers.contains(playerId) && !player.isOnGround()) {
            if (canDoubleJump.contains(playerId) && player.getVelocity().getY() < 0) {
                if (player.isSneaking()) { // Проверка нажатия клавиши прыжка
                    if (player.getLevel() >= 1) { // Проверка уровня опыта
                        player.setLevel(player.getLevel() - 1); // Снятие одного уровня опыта
                        Vector jumpVelocity = player.getLocation().getDirection().multiply(launchPower).setY(launchPowerY); // Толчок вперед и вверх
                        player.setVelocity(jumpVelocity); // Второй прыжок
                        player.playSound(player.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0f, 1.0f);
                        player.getWorld().spawnParticle(Particle.GUST, player.getLocation(), 3, 0.5, 0.5, 0.5, 0.1);
                        jumpedPlayers.remove(playerId);
                        canDoubleJump.remove(playerId);
                    } else {
                        long currentTime = System.currentTimeMillis();
                        if (!lastNotifyTime.containsKey(playerId) || (currentTime - lastNotifyTime.get(playerId)) >= notifyCooldown) {
                            player.sendMessage("Недостаточно опыта для двойного прыжка!");
                            lastNotifyTime.put(playerId, currentTime);
                        }
                    }
                }
            }
        } else if (player.isOnGround()) {
            jumpedPlayers.remove(playerId);
            canDoubleJump.remove(playerId);
            lastNotifyTime.remove(playerId);
        }
    }
}