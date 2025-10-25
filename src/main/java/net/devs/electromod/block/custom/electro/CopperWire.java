package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class CopperWire extends WireBlock {
    public static final MapCodec<CopperWire> CODEC = CopperWire.createCodec(CopperWire::new);
    public static final float copperResistance = 1f;

    public CopperWire(Settings settings) {
        super(settings);
}

    @Override
    public @Nullable WireBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Override
    public float getElectricResistance() {
        return copperResistance;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
