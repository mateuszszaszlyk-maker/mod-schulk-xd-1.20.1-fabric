package dev.sculkxpcontrol.mixin;

import dev.sculkxpcontrol.SculkType;
import dev.sculkxpcontrol.SculkXpConfig;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Jedyny punkt ingerencji moda.
 *
 * Block#dropExperience(ServerWorld, BlockPos, int) to metoda, przez którą każdy blok
 * (rudy, sculk, spawner...) tworzy kulki XP po zniszczeniu. Podmieniamy tylko argument
 * "size", i to wyłącznie gdy blok jest częścią rodziny sculk. Dla wszystkich innych
 * bloków nic się nie zmienia. Silk Touch, gamerule doTileDrops itd. nadal działają
 * tak jak w vanilli, bo ta logika jest wykonywana PRZED wywołaniem tej metody / po niej.
 */
@Mixin(Block.class)
public abstract class BlockMixin {

    @ModifyVariable(method = "dropExperience", at = @At("HEAD"), argsOnly = true)
    private int sculkxpcontrol$modifySculkXp(int size) {
        Object self = this;
        SculkType type = SculkType.fromBlock(self);
        if (type == null) {
            return size;
        }
        return SculkXpConfig.get().rollXp(type);
    }
}
