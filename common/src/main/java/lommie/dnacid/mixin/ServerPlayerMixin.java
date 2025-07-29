package lommie.dnacid.mixin;

import com.mojang.serialization.Dynamic;
import lommie.dnacid.mutation.MutationEffectContainer;
import lommie.dnacid.mutation.MutationEffect;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(value = ServerPlayer.class)
public abstract class ServerPlayerMixin implements MutationEffectContainer {
    @Unique
    ArrayList<MutationEffect> mutationEffects = new ArrayList<>();

    @Override
    public void addMutationEffect(MutationEffect effect) {
        mutationEffects.add(effect);
    }

    @Override
    public void removeMutationEffectAt(int i){
        mutationEffects.remove(i);
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
            if (mutationEffects.get(i).getType().mutationTick(mutationEffects.get(i),this)) {
                mutationEffects.remove(i);
            } else {
                i++; // Increment index if no removal occurs.
            }
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    public void restoreMutationEffects(ServerPlayer player, boolean bl, CallbackInfo ci){
        this.mutationEffects = ((MutationEffectContainer) player).getMutationEffects();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void load(CompoundTag compoundTag, CallbackInfo ci){
        if (compoundTag.contains("mutationEffects")){
            var result = MutationEffect.CODEC.listOf().parse(new Dynamic<>(NbtOps.INSTANCE, compoundTag.get("mutationEffects")));
            result.result().ifPresent(list -> this.mutationEffects = new ArrayList<>(list));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void save(CompoundTag compoundTag, CallbackInfo ci){
        MutationEffect.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE,this.mutationEffects)
                .result()
                .ifPresent(t -> compoundTag.put("mutationEffects", t));
    }
}
