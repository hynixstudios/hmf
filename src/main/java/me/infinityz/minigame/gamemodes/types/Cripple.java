package me.infinityz.minigame.gamemodes.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.infinityz.minigame.UHC;
import me.infinityz.minigame.gamemodes.IGamemode;

public class Cripple extends IGamemode implements Listener {
    private UHC instance;

    public Cripple(UHC instance) {
        super("Cripple",
                "If a player take fall damage will receive slowness for 30 seconds.");
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
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player && e.getCause() == DamageCause.FALL){
            var player = (Player) e.getEntity();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 30, 2));
        }
    }

}