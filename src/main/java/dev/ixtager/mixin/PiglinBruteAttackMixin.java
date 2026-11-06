package dev.ixtager.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PiglinBrute.class)
public class PiglinBruteAttackMixin {

    @Inject(
            method = "customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V",
            target = @Desc(value = "customServerAiStep", args = { ServerLevel.class }),
            at = @At("HEAD")
    )
    private void onCustomServerAiStep(ServerLevel level, CallbackInfo ci) {
        PiglinBrute brute = (PiglinBrute) (Object) this;
        Brain<?> brain = brute.getBrain();

        Optional targetOpt = brain.getMemory(MemoryModuleType.ATTACK_TARGET);

        if (targetOpt.isPresent() && targetOpt.get() instanceof Player player) {

            Inventory playerInventory = ((PlayerInventoryAccessor) player).getInventoryInstance();

            for (int slot = 36; slot <= 39; slot++) {
                ItemStack armorPiece = ((InventoryItemInvoker) playerInventory).invokeGetItem(slot);

                if (armorPiece != null && !armorPiece.isEmpty()) {
                    if (armorPiece.is(Items.GOLDEN_HELMET) || armorPiece.is(Items.GOLDEN_CHESTPLATE) ||
                        armorPiece.is(Items.GOLDEN_LEGGINGS) ||
                        armorPiece.is(Items.GOLDEN_BOOTS)) {

                            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                            brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                            brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
                            return;
                    }
                }
            }
        }
    }


    
}