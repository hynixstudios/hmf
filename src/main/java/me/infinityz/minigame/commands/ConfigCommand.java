package me.infinityz.minigame.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.infinityz.minigame.UHC;
import net.md_5.bungee.api.ChatColor;

/**
 *Config COMMAND
 */

@CommandAlias("config")
public @RequiredArgsConstructor class ConfigCommand extends BaseCommand {
    private @NonNull UHC instance;
    
    @Default
    public void configCMD(CommandSender sender){
        var color = ChatColor.of("#5EA95F");
        var color2 = ChatColor.of("#776FC4");
        var color3 = ChatColor.of("#A40A0A") + "" + ChatColor.BOLD;
        var color4 = ChatColor.of("#BCBCBC");
        sender.sendMessage(color4 +"---------------------------------\n"
                                            + color3 + "        Noobsters\n"
                                            + color4 +"---------------------------------\n"
                                            + color + "Config: " + ChatColor.WHITE + "UHC " + getGameType() + "\n" 
                                            + color + "Scenarios: " +  ChatColor.WHITE + instance.getGamemodeManager().getEnabledGamemodesToString() +"\n"
                                            + color2 + "PvP Enabled: " + ChatColor.WHITE + (instance.getGame().getPvpTime() / 60)+ "m\n"
                                            + color2 + "Border Time: " + ChatColor.WHITE + (instance.getGame().getBorderTime() / 60) + "m\n"
                        + color4 +"---------------------------------\n");    
    }

    private String getGameType(){ 
        final int teamSize = instance.getTeamManger().getTeamSize();
        return teamSize > 1 ? "Teams of " + teamSize : "FFA";
    }

    @CommandPermission("uhc.config.pvp")
    @Subcommand("pvptime")
    @CommandAlias("pvptime")
    public void changePvpTime(CommandSender sender, Integer newPvpTime) {
        instance.getGame().setPvpTime(newPvpTime);
    }

    @CommandPermission("uhc.config.heal")
    @Subcommand("healtime")
    @CommandAlias("healtime")
    public void changeHealTime(CommandSender sender, Integer newHealTime) {
        instance.getGame().setHealTime(newHealTime);
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordertime")
    @CommandAlias("bordertime")
    public void changeBorderTime(CommandSender sender, Integer newBorderTime) {
        instance.getGame().setBorderTime(newBorderTime);
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordercentertime")
    @CommandAlias("bordercentertime")
    public void changeBorderCenterTime(CommandSender sender, Integer newBorderCenterTime) {
        instance.getGame().setBorderCenterTime(newBorderCenterTime);
    }

    @CommandPermission("uhc.config.border")
    @Subcommand("bordersize")
    @CommandAlias("bordersize")
    public void changeBorderSize(CommandSender sender, Integer newBorderSize) {
        instance.getGame().setBorderSize(newBorderSize);
    }
    

}