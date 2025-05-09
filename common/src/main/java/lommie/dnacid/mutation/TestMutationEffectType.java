package lommie.dnacid.mutation;

import lommie.dnacid.items.components.ModComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class TestMutationEffectType extends MutationEffectType {
    private final GameType gameType;

    @Override
    public MutationEffect decode(RegistryFriendlyByteBuf buf, MutationEffect effect) {
        effect.set(ModComponents.GAME_MODE_COMPONENT.get(),buf.readInt());
        return effect;
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeInt(gameType.getId());
    }

    public TestMutationEffectType(MutationEffectType.Settings settings, GameType gameType){
        super(settings);
        this.gameType = gameType;
    }

    @Override
    public boolean playerMutationTick(MutationEffect effect, ServerPlayer player) {
        player.setGameMode(effect.has(ModComponents.GAME_MODE_COMPONENT.get())?GameType.byId(effect.get(ModComponents.GAME_MODE_COMPONENT.get())):gameType);
        return false;
    }
}
