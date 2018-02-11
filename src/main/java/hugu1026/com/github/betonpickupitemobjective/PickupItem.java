package hugu1026.com.github.betonpickupitemobjective;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class PickupItem extends Objective implements Listener {
    private final QuestItem item;
    private final int amount;

    public PickupItem(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = PickupData.class;
        item = instruction.getQuestItem();
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onCrafting(PlayerPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer() instanceof Player) {
            Player player = event.getPlayer();
            String playerID = PlayerConverter.getID(player);
            PickupData playerData = (PickupData) dataMap.get(playerID);
            if (containsPlayer(playerID) && item.compare(event.getItem().getItemStack()) && checkConditions(playerID)) {
                playerData.subtract(event.getItem().getItemStack().getAmount());
                if (playerData.isZero()) {
                    completeObjective(playerID);
                }
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(amount - ((PickupData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(((PickupData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class PickupData extends ObjectiveData {

        private int amount;

        public PickupData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void subtract(int amount) {
            this.amount -= amount;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }
}

