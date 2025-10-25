package net.devs.electromod.block.custom.electro;

import com.mojang.serialization.MapCodec;
import net.devs.electromod.block.entity.custom.electro.AcDcConvertEntity;
import net.devs.electromod.block.entity.custom.electro.WireBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AcDcConverter extends BlockWithEntity implements BlockEntityProvider {
    public static final IntProperty POWER = IntProperty.of("power", 0, 15);

    public AcDcConverter(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AcDcConvertEntity(pos, state);
    }


    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL; // uses block model for entity
    }


    //솔직히 ㅅㅂ 나도 왜 이 전류알고리즘이 작동하는지 모르겠다..
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {

        return (w, pos, s, entity) -> {
            if (!(entity instanceof AcDcConvertEntity)) return;

            int maxElectro = 0; // 주변 WireBlock 중 최대 전류 값을 사용

            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.offset(dir);
                BlockState neighborState = w.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof WireBlock) {
                    BlockEntity neighborBE = w.getBlockEntity(neighborPos);
                    if (neighborBE instanceof WireBlockEntity wireBE) {
                        maxElectro = Math.max(maxElectro, MathHelper.clamp((int)wireBE.getElectricity(),0,15));
                    }
                }
            }

            BlockState currentState = w.getBlockState(pos);
            if (currentState.get(POWER) != maxElectro) {
                w.setBlockState(pos, currentState.with(POWER, maxElectro), 3);
            }
        };

    }



    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

}
