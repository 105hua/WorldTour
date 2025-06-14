package com.joshuadev.worldTour.commands;

import com.joshuadev.worldTour.WorldTour;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;

import java.util.List;

@SuppressWarnings("unused")
@CommandContainer
public class Wt {
    private static final Component JOIN_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<green>✓ You've successfully joined the World Tour!</green>"
    );
    private static final Component LEAVE_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<red>✓ You've successfully left the World Tour!</red>"
    );
    private static final Component ALREADY_JOINED_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<red>✗ You are already in the World Tour!</red>"
    );
    private static final Component NOT_JOINED_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<red>✗ You are not in the World Tour!</red>"
    );
    private static final Component TOUR_STARTED_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<green>✓ The World Tour has started!</green>"
    );
    private static final Component UNKNOWN_ACTION_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<red>✗ Unknown action. Please use /worldtour <join|leave|lead|tpall></red>"
    );
    private static final Component TOUR_LEADER_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<green>✓ You are now the leader of the World Tour!</green>"
    );
    private static final Component TOUR_LEADER_REMOVED_MESSAGE = MiniMessage.miniMessage().deserialize(
            "<red>✓ You have been removed as the leader of the World Tour.</red>"
    );

    @Suggestions("action")
    public List<String> suggestions (CommandContext<CommandSourceStack> context, CommandInput input) {
        return List.of("join", "leave", "lead", "tpall");
    }

    Player currentLeader = null;

    @Command("worldtour <action>")
    @CommandDescription("The main World Tour command.")
    @Permission("worldtour.command")
    @SuppressWarnings("unused")
    public void worldTourCommand(
            CommandSourceStack stack,
            @Argument(value = "action", suggestions = "action") String action
    ) {
        CommandSender sender = stack.getSender();
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("<red>This command can only be used by players.</red>"));
            return;
        }
        switch (action.toLowerCase()) {
            case "join" -> {
                PersistentDataContainer playerContainer = player.getPersistentDataContainer();
                if (playerContainer.has(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN)) {
                    if (Boolean.TRUE.equals(playerContainer.get(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN))) {
                        player.sendMessage(ALREADY_JOINED_MESSAGE);
                    } else {
                        playerContainer.set(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN, true);
                        player.sendMessage(JOIN_MESSAGE);
                    }
                }else{
                    playerContainer.set(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN, true);
                    player.sendMessage(JOIN_MESSAGE);
                }
            }
            case "leave" -> {
                PersistentDataContainer playerContainer = player.getPersistentDataContainer();
                if (playerContainer.has(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN)) {
                    if (Boolean.TRUE.equals(playerContainer.get(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN))) {
                        playerContainer.set(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN, false);
                        player.sendMessage(LEAVE_MESSAGE);
                    } else {
                        player.sendMessage(NOT_JOINED_MESSAGE);
                    }
                } else {
                    player.sendMessage(NOT_JOINED_MESSAGE);
                }
            }
            case "lead" -> {
                if(!player.hasPermission("worldtour.lead")) {
                    player.sendMessage(UNKNOWN_ACTION_MESSAGE);
                    return;
                }
                if(player.hasPotionEffect(PotionEffectType.GLOWING)){
                    player.removePotionEffect(PotionEffectType.GLOWING);
                    player.sendMessage(TOUR_LEADER_REMOVED_MESSAGE);
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, false, false));
                    player.sendMessage(TOUR_LEADER_MESSAGE);
                }
            }
            case "tpall" -> {
                if(!player.hasPermission("worldtour.tpall")) {
                    player.sendMessage(UNKNOWN_ACTION_MESSAGE);
                    return;
                }
                WorldTour.pluginInstance.getServer().getOnlinePlayers().forEach(p -> {
                    PersistentDataContainer playerContainer = p.getPersistentDataContainer();
                    if (playerContainer.has(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN) &&
                            Boolean.TRUE.equals(playerContainer.get(WorldTour.JOINED_KEY, PersistentDataType.BOOLEAN))) {
                        p.teleport(player.getLocation());
                        p.sendMessage(MiniMessage.miniMessage().deserialize("<green>✓ You have been teleported to the World Tour leader.</green>"));
                    }
                });
                player.sendMessage(TOUR_STARTED_MESSAGE);
            }
            default -> player.sendMessage(NOT_JOINED_MESSAGE);
        }
    }
}
