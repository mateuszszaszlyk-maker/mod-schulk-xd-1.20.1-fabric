package dev.sculkxpcontrol;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * /sculkxp                          - pokaż aktualne ustawienia
 * /sculkxp multiplier <0-1000>      - mnożnik XP dla wszystkich bloków sculk
 * /sculkxp set <blok> <0-1000>      - bazowa ilość XP dla konkretnego bloku
 * /sculkxp reset                    - przywróć wartości z vanilli
 * /sculkxp reload                   - wczytaj ponownie plik konfiguracyjny
 */
public final class SculkXpCommand {
    private SculkXpCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("sculkxp")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ctx -> showStatus(ctx.getSource()));

        root.then(CommandManager.literal("status")
                .executes(ctx -> showStatus(ctx.getSource())));

        root.then(CommandManager.literal("multiplier")
                .then(CommandManager.argument("value",
                                DoubleArgumentType.doubleArg(0.0, SculkXpConfig.MAX_MULTIPLIER))
                        .executes(ctx -> setMultiplier(ctx.getSource(),
                                DoubleArgumentType.getDouble(ctx, "value")))));

        LiteralArgumentBuilder<ServerCommandSource> set = CommandManager.literal("set");
        for (SculkType type : SculkType.values()) {
            set.then(CommandManager.literal(type.id)
                    .then(CommandManager.argument("xp",
                                    IntegerArgumentType.integer(0, SculkXpConfig.MAX_BASE_XP))
                            .executes(ctx -> setBaseXp(ctx.getSource(), type,
                                    IntegerArgumentType.getInteger(ctx, "xp")))));
        }
        root.then(set);

        root.then(CommandManager.literal("reset")
                .executes(ctx -> reset(ctx.getSource())));

        root.then(CommandManager.literal("reload")
                .executes(ctx -> reload(ctx.getSource())));

        dispatcher.register(root);
    }

    private static int showStatus(ServerCommandSource source) {
        SculkXpConfig cfg = SculkXpConfig.get();

        StringBuilder sb = new StringBuilder();
        sb.append("Sculk XP Control - aktualne ustawienia\n");
        sb.append("Mnożnik: x").append(fmt(cfg.multiplier));
        for (SculkType type : SculkType.values()) {
            sb.append("\n - ").append(type.id).append(": ")
                    .append(cfg.getBaseXp(type)).append(" XP bazowo (vanilla: ").append(type.vanillaXp)
                    .append(") -> ").append(fmt(cfg.effectiveXp(type))).append(" XP");
        }

        Text message = Text.literal(sb.toString()).formatted(Formatting.AQUA);
        source.sendFeedback(() -> message, false);
        return 1;
    }

    private static int setMultiplier(ServerCommandSource source, double value) {
        SculkXpConfig cfg = SculkXpConfig.get();
        cfg.setMultiplier(value);
        SculkXpConfig.save();

        Text message = Text.literal("Mnożnik XP ze sculka ustawiony na x" + fmt(cfg.multiplier))
                .formatted(Formatting.GREEN);
        source.sendFeedback(() -> message, true);
        return 1;
    }

    private static int setBaseXp(ServerCommandSource source, SculkType type, int value) {
        SculkXpConfig cfg = SculkXpConfig.get();
        cfg.setBaseXp(type, value);
        SculkXpConfig.save();

        Text message = Text.literal("XP bazowe dla '" + type.id + "' ustawione na " + cfg.getBaseXp(type)
                + " (po mnożniku: " + fmt(cfg.effectiveXp(type)) + ")").formatted(Formatting.GREEN);
        source.sendFeedback(() -> message, true);
        return 1;
    }

    private static int reset(ServerCommandSource source) {
        SculkXpConfig.reset();

        Text message = Text.literal("Przywrócono domyślne wartości (vanilla).").formatted(Formatting.YELLOW);
        source.sendFeedback(() -> message, true);
        return 1;
    }

    private static int reload(ServerCommandSource source) {
        SculkXpConfig.load();

        Text message = Text.literal("Wczytano ponownie konfigurację.").formatted(Formatting.YELLOW);
        source.sendFeedback(() -> message, false);
        return 1;
    }

    private static String fmt(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }
}
