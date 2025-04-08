package com.redcraft86.redpackutils.blocks;

import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class RainbowBeaconBlock extends ModBlock {

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    protected RainbowBeaconBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<String> getTooltipKeys() {
        return List.of("rainbow_beacon");
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, LevelReader level, BlockPos pos, BlockPos beaconPos) {
        if (level instanceof Level world) {
            float hue = (world.getGameTime() % 240) / 240.0f;
            int rgb = Mth.hsvToRgb(hue, 1.0f, 1.0f);
            return new float[] {
                    ((rgb >> 16) & 0xFF) / 255.0f, // R
                    ((rgb >> 8) & 0xFF) / 255.0f,  // G
                    (rgb & 0xFF) / 255.0f          // B
            };
        }
        return new float[]{1.0f, 1.0f, 1.0f};
    }
}
