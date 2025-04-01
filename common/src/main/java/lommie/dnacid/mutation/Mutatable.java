package lommie.dnacid.mutation;

import java.util.ArrayList;

public interface Mutatable {
    void addMutationEffect(MutationEffect effect);

    ArrayList<MutationEffect> getMutationEffects();

    creatureType getMutationCreatureType();

    enum creatureType {
        PLAYER,
        ENTITY,
        BLOCK
    }
}
