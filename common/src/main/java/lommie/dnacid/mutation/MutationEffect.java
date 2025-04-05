package lommie.dnacid.mutation;

import net.minecraft.server.level.ServerPlayer;

public class MutationEffect {
    int timeLeft;

    MutationEffect(int timeLeft){
        this.timeLeft = timeLeft;
    }

    MutationEffect(){
        this(-1);
    }

    /**
     * @return returns whether effect should be removed
     * */
    boolean mutationTick(MutationEffectContainer mutationEffectContainer){
        if (timeLeft == 0){
            return false;
        }else if (this.timeLeft > 0) {
            this.timeLeft--;
        }
        switch (mutationEffectContainer.getMutationCreatureType()){
            case PLAYER -> {
                ServerPlayer player = (ServerPlayer) mutationEffectContainer;
                return playerMutationTick(player);
            }
            case ENTITY, BLOCK -> {
                return false;
            }
        }

        return false;
    }

    /**
     * @return returns whether effect should be removed
     * */
    boolean playerMutationTick(ServerPlayer player){
        throw new IllegalStateException("Mutation Effect \""+this+"\" does nothing when applyed to a player");
    };
}
