package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class MagnetBlock extends FacingBlock
{
    public static final MapCodec<MagnetBlock> CODEC = IronCoilBlock.createCodec(MagnetBlock::new);

    public MagnetBlock(Settings settings)
    {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    @Override protected MapCodec<? extends FacingBlock> getCodec() { return CODEC; }

    @Override protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(FACING); }

    @Override public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
}
