package lommie.dnacid;


import dev.architectury.platform.Platform;
import lommie.dnacid.blocks.ModBlocks;
import lommie.dnacid.items.ModItems;
import lommie.dnacid.items.ModTabs;
import lommie.dnacid.items.components.ModComponents;
import lommie.dnacid.mutation.ModMutations;
import lommie.dnacid.protein.ModProteins;
import lommie.dnacid.screens.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;


public final class Dnacid {
    public static final String MOD_ID = "dnacid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<Character> AMINO_ACID_CHARS = List.of('A','R','N','D','C','Q','E','G','H','I','L','K','M','F','P','S','T','W','Y','V');

    public static void init() {
        if (!Platform.isForgeLike()) {
            ModRegistries.register();
        }
        ModComponents.register();
        ModMutations.register();
        ModProteins.register();
        ModItems.register();
        ModBlocks.register();
        ModTabs.register();
        ModMenus.register();
    }
}