package net.devs.electromod.block.entity.custom.electro;

import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.devs.electromod.block.custom.electro.WireBlock;

import static net.devs.electromod.block.custom.electro.WireBlock.ELECTRIFIED;

public class WireBlockEntity extends BlockEntity {

    public float Electrocity = 0; // 전류 값
    public  int tickCounter = 0;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BE, pos, state);
    }

    // ⚡ 전류 설정 함수
    public void setElectrocity(float value, World w, BlockPos pos, BlockState state, WireBlockEntity wireBE) {
        this.Electrocity = value;
        if (w != null) {
            Block block = state.getBlock();
            if (block instanceof WireBlock wireBlock) {
                if(!wireBlock.IsDidElectrified(state))
                {
                    w.setBlockState(pos, state.with(ELECTRIFIED, true), Block.NOTIFY_ALL);
                    wireBlock.onBlockElectrocityUpdated(w, pos, state, wireBE);
                }
            }
        }

        markDirty(); // 데이터 저장 (NBT)
    }

    // ⚡ 전류 값 읽기
    public float getElectrocity() {
        return Electrocity;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.Electrocity = nbt.getFloat("Electrocity");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat("Electrocity", Electrocity);
    }
}
