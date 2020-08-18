package me.infinityz.minigame.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.mrmicky.fastinv.FastInv;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.game.UpdatableInventory;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public class SpectatorListener implements Listener {
    private @NonNull UHC instance;

    private static Class<?> packetClass;
    private static Constructor<?> packetConstructor;
    private static Method sendPacket;

    /*
     * Spectators chat
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpecChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR && !e.getPlayer().hasPermission("staff.perm")) {
            e.setCancelled(true);

            e.getRecipients().stream()
                    .filter(it -> it.getGameMode() == GameMode.SPECTATOR || it.hasPermission("staff.perm"))
                    .forEach(specs -> {
                        specs.sendMessage(ChatColor.GRAY + "[SPEC] " + e.getFormat()
                                .replace("%1$s", e.getPlayer().getName()).replace("%2$s", e.getMessage()));
                    });
        }
    }

    @EventHandler
    public void onMoveSpecNoClip(PlayerMoveEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.SPECTATOR)
            return;
        if (e.getTo().getBlock().getType() != Material.AIR) {
            e.setCancelled(true);
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 1000));
        }
    }

    /*
     * Spec hider from no spectators
     */
    @EventHandler
    public void onJoinHide(PlayerJoinEvent e) {
        var player = e.getPlayer();
        // If gamemode is Spectator, then hide him from all other non spectators
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().stream().filter(all -> all.getGameMode() != GameMode.SPECTATOR)
                    .forEach(all -> all.hidePlayer(instance, player));
        } else {
            // If gamemode isn't Spectator, then hide all spectators for him.
            Bukkit.getOnlinePlayers().stream().filter(it -> it.getGameMode() == GameMode.SPECTATOR)
                    .forEach(all -> player.hidePlayer(instance, all.getPlayer()));
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        var player = e.getPlayer();
        // If gamemode to change is spectator
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            // If player has no perms hide f3
            if (!player.hasPermission("spec.see.coords"))
                disableF3(player);

            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                // If players are not specs, hide them the player
                if (all.getGameMode() != GameMode.SPECTATOR) {
                    all.hidePlayer(instance, player);
                } else {
                    // If players are specs, then show them to the player
                    player.showPlayer(instance, all);
                }
            });
        } else {
            enableF3(player);
            Bukkit.getOnlinePlayers().stream().forEach(all -> {
                // When switching to other gamemodes, show them if not visible to player
                if (!all.canSee(player)) {
                    all.showPlayer(instance, player);
                }
                // If one of the players is a spec, hide them from the player
                if (all.getGameMode() == GameMode.SPECTATOR) {
                    player.hidePlayer(instance, all);
                }
            });
        }

    }

    /*
     * Spectator disable F3 Codigo
     */
    public static void disableF3(Player player) {
        try {
            if (packetClass == null)
                packetClass = getNMSClass("PacketPlayOutEntityStatus");
            if (packetConstructor == null)
                packetConstructor = packetClass.getConstructor(new Class[] { getNMSClass("Entity"), byte.class });
            if (sendPacket == null)
                sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket",
                        new Class[] { getNMSClass("Packet") });
            Object packet = packetConstructor.newInstance(new Object[] { getHandle(player), Byte.valueOf((byte) 22) });
            sendPacket.invoke(getConnection(player), new Object[] { packet });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static void enableF3(Player player) {
        try {
            if (packetClass == null)
                packetClass = getNMSClass("PacketPlayOutEntityStatus");
            if (packetConstructor == null)
                packetConstructor = packetClass.getConstructor(new Class[] { getNMSClass("Entity"), byte.class });
            if (sendPacket == null)
                sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket",
                        new Class[] { getNMSClass("Packet") });
            Object packet = packetConstructor.newInstance(new Object[] { getHandle(player), Byte.valueOf((byte) 23) });
            sendPacket.invoke(getConnection(player), new Object[] { packet });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = "net.minecraft.server." + version + nmsClassString;
        return Class.forName(name);
    }

    private static Object getConnection(Player player) throws SecurityException, NoSuchMethodException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Field conField = getHandle(player).getClass().getField("playerConnection");
        return conField.get(getHandle(player));
    }

    private static Object getHandle(Player player) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method getHandle = player.getClass().getMethod("getHandle", new Class[0]);
        return getHandle.invoke(player, new Object[0]);
    }

    /**
     * Spectator cancel any possible damage
     */
    @EventHandler
    public void onDamageSpec(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER && ((Player) (e.getEntity())).getGameMode() == GameMode.SPECTATOR)
            e.setCancelled(true);
    }

    @EventHandler
    public void onStopSpectating(PlayerStopSpectatingEntityEvent e) {
        // TODO: Right now this causes the client to glitch up.
        if (!e.getPlayer().hasPermission("uhc.spectator.unmount")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onStopSpectating(PlayerStartSpectatingEntityEvent e) {
        // TODO: Right now this causes the client to glitch up.
        if (!e.getPlayer().hasPermission("uhc.spectator.mount")) {
            e.setCancelled(true);
        }
    }

    /*
     * Spec Inventory with right click.
     */
    @EventHandler
    public void invSpecEvent(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() == null || e.getRightClicked().getType() != EntityType.PLAYER)
            return;
        if (!e.getPlayer().hasPermission("staff.perm") || e.getPlayer().getGameMode() != GameMode.SPECTATOR)
            return;
        var player = e.getPlayer();
        var clicked = (Player) e.getRightClicked();
        // TODO: Add a specInv manager to share inventories and not open one viewer.

        var fastInv = new UpdatableInventory(5 * 9, clicked.getName() + "'s inventory'");
        fastInv.addUpdateTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (isCancelled()) {
                    cancel();
                    return;
                }
                updateInventory(fastInv, clicked);
            }

        }, instance, 0, 20, true);
        fastInv.open(player);
    }

    private void updateInventory(FastInv fastInv, Player target) {
        var count = 0;
        for (var itemStack : target.getInventory()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                fastInv.setItem(count, new ItemStack(Material.AIR));
            } else {
                fastInv.setItem(count, itemStack);
            }
            count++;
        }
        // Obtain a list of all the active potion effects as strings
        var effects = target
                .getActivePotionEffects().stream().map(it -> ChatColor.GRAY + it.getType().getName() + " "
                        + (1 + it.getAmplifier()) + ": " + ChatColor.WHITE + (it.getDuration() / 20) + "s")
                .collect(Collectors.toList());
        // Create a new Item Stack
        var potionEffectsItem = new ItemStack(Material.GLASS_BOTTLE);
        // Obtain the meta
        var potionEffectsItemMeta = potionEffectsItem.getItemMeta();
        // Change the meta
        potionEffectsItemMeta.setDisplayName(ChatColor.GOLD + "Active Potion Effects:");
        potionEffectsItemMeta.setLore(effects);
        potionEffectsItem.setItemMeta(potionEffectsItemMeta);
        // Add the item to the inventory 41 is the one next to the offhand item.
        fastInv.setItem(41, potionEffectsItem);
        // Repeat for Health
        var healthItem = new ItemStack(Material.RED_BANNER);
        var healthItemMeta = healthItem.getItemMeta();
        healthItemMeta.setDisplayName(ChatColor.GOLD + "Health:");
        healthItemMeta.setLore(List.of(ChatColor.WHITE + "Hearts: " + (int) target.getHealth(),
                ChatColor.WHITE + "Absorption: " + (int) target.getAbsorptionAmount()));
        healthItem.setItemMeta(healthItemMeta);
        fastInv.setItem(42, healthItem);
        // Repeat for EXP values
        var experienceItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        var experienceItemMeta = experienceItem.getItemMeta();
        experienceItemMeta.setDisplayName(ChatColor.GOLD + "Experience:");
        experienceItemMeta.setLore(List.of(ChatColor.WHITE + "Levels: " + target.getLevel(),
                ChatColor.WHITE + "Percent to next level: " + String.format("%.2f", target.getExp() * 100)));
        experienceItem.setItemMeta(experienceItemMeta);
        fastInv.setItem(43, experienceItem);

    }

}