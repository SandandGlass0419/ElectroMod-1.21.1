package net.devs.electromod.block.custom.magnetic;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.custom.magnetic.MagneticForce.AbstractMagneticBlock;
import net.devs.electromod.block.entity.custom.magnetic.MagnetBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class MagnetBlock extends AbstractMagneticBlock
{
    public static final MapCodec<MagnetBlock> CODEC = MagnetBlock.createCodec(MagnetBlock::new);

    public MagnetBlock(Settings settings)
    {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.UP));
    }

    // register properties
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(FACING); }

    // block entity settings
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() { return CODEC; }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)  { return new MagnetBlockEntity(pos, state); }

    @Override
    protected BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    // blockstates - direction
    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx)
    { return this.getDefaultState().with(FACING, ctx.getSide().getOpposite()); }

}
