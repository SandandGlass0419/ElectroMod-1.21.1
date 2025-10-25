package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GoldWire extends WireBlock {
    public static final MapCodec<GoldWire> CODEC = GoldWire.createCodec(GoldWire::new);
    public static final float goldResistance = 1.5f;

    public GoldWire(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable WireBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WireBlockEntity(pos, state);
    }

    @Override
    public float getElectricResistance() {
        return goldResistance;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }
}
