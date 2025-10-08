package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Wire extends WireBlock {
    public static final MapCodec<Wire> CODEC = Wire.createCodec(Wire::new);

    public Wire(Settings settings) {
        super(settings);
        resistance = 1.0f;

    }

    @Override
    public @Nullable WireBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        WireBlock mywire = (WireBlock) world.getBlockState(pos).getBlock();
        mywire.resistance = resistance;
    }
}
