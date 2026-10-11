package dev.sculkxpcontrol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Konfiguracja zapisywana w config/sculk_xp_control.json.
 *
 * Końcowa ilość XP = (XP bazowe danego bloku) x (mnożnik).
 * Domyślnie mnożnik = 1, a XP bazowe = wartości z vanilli, czyli mod nic nie zmienia,
 * dopóki sam tego nie ustawisz.
 */
public final class SculkXpConfig {
    public static final int MAX_BASE_XP = 1000;
    public static final double MAX_MULTIPLIER = 1000.0;
    private static final int MAX_FINAL_XP = 100_000;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile SculkXpConfig current = new SculkXpConfig();

    // --- pola zapisywane do JSON ---
    public double multiplier = 1.0;
    public Map<String, Integer> xp = new LinkedHashMap<>();

    public SculkXpConfig() {
        sanitize();
    }

    // ------------------------------------------------------------------ dostęp

    public static SculkXpConfig get() {
        return current;
    }

    public int getBaseXp(SculkType type) {
        Integer value = xp.get(type.id);
        return value == null ? type.vanillaXp : value;
    }

    public void setBaseXp(SculkType type, int value) {
        xp.put(type.id, Math.max(0, Math.min(MAX_BASE_XP, value)));
    }

    public void setMultiplier(double value) {
        multiplier = Math.max(0.0, Math.min(MAX_MULTIPLIER, value));
    }

    /** Średnia ilość XP z jednego bloku (bez losowego zaokrąglania). */
    public double effectiveXp(SculkType type) {
        return getBaseXp(type) * multiplier;
    }

    /**
     * Ilość XP, która faktycznie wypadnie. Ułamki są zaokrąglane losowo
     * (np. 2.5 XP = 2 lub 3 z szansą 50%), dzięki czemu średnia się zgadza.
     */
    public int rollXp(SculkType type) {
        double value = effectiveXp(type);
        int whole = (int) Math.floor(value);
        double fraction = value - whole;
        if (fraction > 0.0 && ThreadLocalRandom.current().nextDouble() < fraction) {
            whole++;
        }
        return Math.max(0, Math.min(MAX_FINAL_XP, whole));
    }

    // ------------------------------------------------------------ zapis / odczyt

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("sculk_xp_control.json");
    }

    public static void load() {
        Path file = file();
        SculkXpConfig loaded = null;

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                loaded = GSON.fromJson(reader, SculkXpConfig.class);
            } catch (IOException | JsonParseException e) {
                SculkXpControl.LOGGER.error("Nie udało się wczytać {} - używam ustawień domyślnych.", file, e);
                backupBrokenFile(file);
            }
        }

        boolean fileWasValid = loaded != null;
        if (loaded == null) {
            loaded = new SculkXpConfig();
        }
        loaded.sanitize();
        current = loaded;

        if (!fileWasValid) {
            save();
        }
    }

    public static void save() {
        Path file = file();
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(current, writer);
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            SculkXpControl.LOGGER.error("Nie udało się zapisać {}", file, e);
        }
    }

    public static void reset() {
        current = new SculkXpConfig();
        save();
    }

    private static void backupBrokenFile(Path file) {
        try {
            Files.move(file, file.resolveSibling(file.getFileName() + ".broken"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignored) {
            // nieudana kopia zapasowa nie jest krytyczna
        }
    }

    /** Naprawia brakujące / niepoprawne wartości (np. po ręcznej edycji pliku). */
    private void sanitize() {
        if (Double.isNaN(multiplier) || Double.isInfinite(multiplier)) {
            multiplier = 1.0;
        }
        multiplier = Math.max(0.0, Math.min(MAX_MULTIPLIER, multiplier));

        if (xp == null) {
            xp = new LinkedHashMap<>();
        }
        for (SculkType type : SculkType.values()) {
            Integer value = xp.get(type.id);
            xp.put(type.id, value == null ? type.vanillaXp : Math.max(0, Math.min(MAX_BASE_XP, value)));
        }
    }
}
