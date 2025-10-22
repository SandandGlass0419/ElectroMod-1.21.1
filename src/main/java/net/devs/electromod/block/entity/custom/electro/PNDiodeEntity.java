package net.devs.electromod.block.entity.custom.electro;

import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class PNDiodeEntity extends BlockEntity {
    private int redstonePower = 0;

    public PNDiodeEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PN_BE, pos, state);
    }

    public int getRedstonePower() {
        return redstonePower;
    }

    public void setRedstonePower(int power) {
        this.redstonePower = power;
        markDirty(); // BlockEntity 변경 표시
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.redstonePower = nbt.getInt("RedstonePower");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("RedstonePower", redstonePower);
    }
}
