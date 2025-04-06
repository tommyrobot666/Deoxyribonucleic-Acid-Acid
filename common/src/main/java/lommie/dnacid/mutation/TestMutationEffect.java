package lommie.dnacid.mutation;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class TestMutationEffect extends MutationEffect {
    private final GameType gameType;

    public static final StreamCodec<RegistryFriendlyByteBuf, TestMutationEffect> STREAM_CODEC = StreamCodec.of(
            TestMutationEffect::encode,
            TestMutationEffect::decode
    );

    private static TestMutationEffect decode(RegistryFriendlyByteBuf buf) {
        return new TestMutationEffect(buf.readInt(),GameType.byId(buf.readInt()));
    }

    private static void encode(RegistryFriendlyByteBuf buf, TestMutationEffect effect) {
        buf.writeInt(effect.timeLeft);
        buf.writeInt(effect.gameType.getId());
    }

    public TestMutationEffect(int timeLeft,GameType gameType){
        super(timeLeft);
        this.gameType = gameType;
    }

    @Override
    public boolean playerMutationTick(ServerPlayer player) {
        player.setGameMode(gameType);
        return false;
    }
}
