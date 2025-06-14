package com.joshuadev.worldTour;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public final class WorldTour extends JavaPlugin {

    public static WorldTour pluginInstance;
    public static final NamespacedKey JOINED_KEY = new NamespacedKey("worldtour", "joined");

    @Override
    public void onEnable() {
        pluginInstance = this;
        PaperCommandManager<CommandSourceStack> commandManager =
                PaperCommandManager.builder()
                        .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                        .buildOnEnable(this);
        AnnotationParser<CommandSourceStack> annotationParser =
                new AnnotationParser<>(commandManager, CommandSourceStack.class);
        try {
            annotationParser.parseContainers();
        } catch (Exception e) {
            getLogger().severe("Failed to parse command annotations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
