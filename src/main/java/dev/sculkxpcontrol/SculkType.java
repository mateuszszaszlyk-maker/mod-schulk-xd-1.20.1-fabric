package dev.sculkxpcontrol;

import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.block.SculkBlock;
import net.minecraft.block.SculkCatalystBlock;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.SculkShriekerBlock;

/**
 * Bloki z rodziny sculk, które po zniszczeniu wypuszczają XP,
 * wraz z domyślnymi (vanilla) wartościami z Minecrafta 1.20.1.
 */
public enum SculkType {
    SCULK("sculk", 1),
    CATALYST("catalyst", 5),
    SENSOR("sensor", 5),
    CALIBRATED_SENSOR("calibrated_sensor", 5),
    SHRIEKER("shrieker", 5);

    /** Nazwa używana w komendzie i w pliku konfiguracyjnym. */
    public final String id;
    public final int vanillaXp;

    SculkType(String id, int vanillaXp) {
        this.id = id;
        this.vanillaXp = vanillaXp;
    }

    /** Zwraca typ sculk dla danego bloku albo null, jeśli to nie jest blok sculk. */
    public static SculkType fromBlock(Object block) {
        if (block instanceof SculkBlock) return SCULK;
        if (block instanceof SculkCatalystBlock) return CATALYST;
        // CalibratedSculkSensorBlock dziedziczy po SculkSensorBlock, więc musi być sprawdzony wcześniej
        if (block instanceof CalibratedSculkSensorBlock) return CALIBRATED_SENSOR;
        if (block instanceof SculkSensorBlock) return SENSOR;
        if (block instanceof SculkShriekerBlock) return SHRIEKER;
        return null;
    }
}
