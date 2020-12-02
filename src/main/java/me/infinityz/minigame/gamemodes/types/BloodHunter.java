package me.infinityz.minigame.gamemodes.types;

import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class BloodHunter extends IGamemode implements Listener {
    private UHC instance;

    public BloodHunter(UHC instance) {
        super("BloodHunter", "Players get 1 extra heart for each kill.");
        this.instance = instance;
    }

    @Override
    public boolean enableScenario() {
        if (isEnabled())
            return false;
        instance.getListenerManager().registerListener(this);
        setEnabled(true);
        return true;
    }

    @Override
    public boolean disableScenario() {
        if (!isEnabled())
            return false;
        instance.getListenerManager().unregisterListener(this);
        setEnabled(false);
        return true;
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e){
        e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
            e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()+2);
    }

}