package lommie.dnacid.mutation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class TestMutationEffect implements MutationEffect{
    @Override
    public boolean mutationTick(MutationEffectContainer mutationEffectContainer) {
        switch (mutationEffectContainer.getMutationCreatureType()){
            case PLAYER -> {
                ServerPlayer player = (ServerPlayer) mutationEffectContainer;
                player.setGameMode(GameType.ADVENTURE);
                break;
            }
        }

        return false;
    }
}
