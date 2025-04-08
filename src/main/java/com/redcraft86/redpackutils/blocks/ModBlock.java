package com.redcraft86.redpackutils.blocks;

import net.minecraft.world.level.block.Block;
import java.util.List;

public class ModBlock extends Block {
    public ModBlock(Properties pProperties) {
        super(pProperties);
    }

    public List<String> getTooltipKeys() {
        return List.of();
    }
}
