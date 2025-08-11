package com.ddbrother.shootingstars.mixin;

import com.ddbrother.shootingstars.manager.CelebrationManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onDeathInject(DamageSource source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (CelebrationManager.INSTANCE.isMarked(self.getUuid())) {
            if (self.getWorld() instanceof ServerWorld) {
                Text customMessage = Text.empty()
                        .append(self.getDisplayName())
                        .append(Text.of(" was confettified"));

                self.getServer().getPlayerManager().broadcast(customMessage, false);
            }
            ci.cancel();
        }
    }
}