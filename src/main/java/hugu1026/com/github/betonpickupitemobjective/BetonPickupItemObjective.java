package hugu1026.com.github.betonpickupitemobjective;

import org.bukkit.plugin.java.JavaPlugin;
import pl.betoncraft.betonquest.BetonQuest;

public final class BetonPickupItemObjective
        extends JavaPlugin
{
    public void onEnable()
    {
        BetonQuest.getInstance().registerObjectives("pickup", PickupItem.class);
    }

    public void onDisable() {}
}

