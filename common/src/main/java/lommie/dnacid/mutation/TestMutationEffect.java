package lommie.dnacid.mutation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class TestMutationEffect extends MutationEffect{
    @Override
    public boolean playerMutationTick(ServerPlayer player) {
        player.setGameMode(GameType.ADVENTURE);
        return false;
    }
}
