package lommie.dnacid.mutation;

public interface MutationEffect {
    /**
     * @return returns whether effect should be removed
     * */
    boolean mutationTick(Mutatable mutatable);
}
