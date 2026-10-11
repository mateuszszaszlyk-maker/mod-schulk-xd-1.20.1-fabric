package dev.sculkxpcontrol;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SculkXpControl implements ModInitializer {
    public static final String MOD_ID = "sculkxpcontrol";
    public static final Logger LOGGER = LoggerFactory.getLogger("Sculk XP Control");

    @Override
    public void onInitialize() {
        SculkXpConfig.load();

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> SculkXpCommand.register(dispatcher));

        LOGGER.info("Sculk XP Control załadowany. Użyj /sculkxp w grze, aby zmienić ilość XP ze sculka.");
    }
}
