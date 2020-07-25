package me.infinityz.minigame.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class IngameScoreboard extends IScoreboard {

    public IngameScoreboard(Player player) {
        super(player);
        this.updateTitle(ChatColor.BOLD + "UHC");
        
        this.updateLines(ChatColor.GRAY + "Game Time: " + ChatColor.WHITE + "00:00", "", 
                ChatColor.GRAY + "Kills: "+ ChatColor.WHITE + "0",
                ChatColor.GRAY + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size(),
                "",
                ChatColor.GRAY + "Border: " + ChatColor.WHITE + (Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2),
                "",
                ChatColor.WHITE + "noobsters.net");
    }

    @Override
    public void update() {
        runUpdates();
    }

}