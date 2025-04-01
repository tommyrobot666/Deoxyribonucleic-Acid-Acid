package lommie.dnacid.mixin;

import lommie.dnacid.mutation.Mutatable;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerMixin implements Mutatable {
    @Unique
    ArrayList<MutationEffect> mutationEffects = new ArrayList<>();

    @Override
    public void addMutationEffect(MutationEffect effect) {
        mutationEffects.add(effect);
    }

    @Override
    public ArrayList<MutationEffect> getMutationEffects() {
        return mutationEffects;
    }

    @Override
    public creatureType getMutationCreatureType() {
        return creatureType.PLAYER;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tickMutationEffects(CallbackInfo ci) {
        int i = 0;
        while (i < mutationEffects.size()) {
            if (mutationEffects.get(i).mutationTick(this)) {
                mutationEffects.remove(i);
            } else {
                i++; // Increment index if no removal occurs.
            }
        }
    }
}
