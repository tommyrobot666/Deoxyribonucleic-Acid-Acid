package lommie.dnacid.mutation;

import java.util.ArrayList;

public interface MutationEffectContainer {
    void addMutationEffect(MutationEffect effect);

    ArrayList<MutationEffect> getMutationEffects();

    creatureType getMutationCreatureType();

    enum creatureType {
        PLAYER,
        ENTITY,
        PLANT_BLOCK,
        BACTERIA
    }
}
