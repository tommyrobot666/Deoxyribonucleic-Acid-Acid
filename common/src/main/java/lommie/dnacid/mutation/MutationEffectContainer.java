package lommie.dnacid.mutation;

import java.util.ArrayList;

public interface MutationEffectContainer {
    void addMutationEffect(MutationEffect effect);

    void removeMutationEffectAt(int i);

    ArrayList<MutationEffect> getMutationEffects();

    creatureType getMutationCreatureType();

    enum creatureType {
        PLAYER,
        ENTITY,
        PLANT_BLOCK,
        BACTERIA
    }
}
