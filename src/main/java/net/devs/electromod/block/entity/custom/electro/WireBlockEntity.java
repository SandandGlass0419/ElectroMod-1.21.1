package net.devs.electromod.block.entity.custom.electro;

import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class WireBlockEntity extends BlockEntity {

    private float Electrocity = 0; // 여기에 큰 값 저장 가능
    private float voltage = 0;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BE, pos, state);
    }


    // 값 읽기/쓰기
    public void setElectrocity(float value) {
        this.Electrocity = value;
        markDirty(); // 값 변경 후 월드에 반영
    }

    // 월드 저장용 NBT
    public float getElectrocity() {
        return Electrocity;
    }

    public void addVoltage(float value) {
        this.voltage += value;
        markDirty(); // 값 변경 후 월드에 반영
    }

    // 월드 저장용 NBT
    public float getVoltage() {
        return voltage;
    }





    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.Electrocity = nbt.getInt("StoredValue");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat("StoredValue", Electrocity);
    }
}
