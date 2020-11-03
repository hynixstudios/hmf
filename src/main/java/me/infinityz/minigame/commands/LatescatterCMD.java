package me.infinityz.minigame.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import me.infinityz.minigame.chunks.ChunksManager;
import me.infinityz.minigame.events.PlayerJoinedLateEvent;
import me.infinityz.minigame.players.UHCPlayer;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@CommandAlias("latescatter|ls|play")
public class LatescatterCMD extends BaseCommand {

    private @NonNull UHC instance;

    @Default
    @Conditions("ingame")
    public void lateScatter(@Conditions("hasNotDied|spec") UHCPlayer uhcPlayer) {
        if (instance.getGame().getGameTime() >= 1200) {
            // If greater than minute 20, change to variable or differntial.
            return;
        }
        var player = uhcPlayer.getPlayer();
        var world = Bukkit.getWorlds().get(0);
        var worldBorderSizeHaved = (int) world.getWorldBorder().getSize() / 2;

        player.sendMessage((ChatColor.of("#7ab83c") + "Loading a location..."));
        player.setGameMode(GameMode.SURVIVAL);

        player.teleportAsync(ChunksManager.findScatterLocation(world, worldBorderSizeHaved))
                .thenAccept(result -> player
                        .sendMessage((ChatColor.of("#7ab83c") + (result ? "You have been scattered into the world."
                                : "Coudn't scatter your, ask for help."))));

        uhcPlayer.setAlive(true);
        Bukkit.getPluginManager().callEvent(PlayerJoinedLateEvent.of(player));

    }

}