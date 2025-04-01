package lommie.dnacid.mutation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class TestMutationEffect implements MutationEffect{
    @Override
    public boolean mutationTick(Mutatable mutatable) {
        switch (mutatable.getMutationCreatureType()){
            case PLAYER -> {
                ServerPlayer player = (ServerPlayer) mutatable;
                player.setGameMode(GameType.ADVENTURE);
                break;
            }
        }

        return false;
    }
}
