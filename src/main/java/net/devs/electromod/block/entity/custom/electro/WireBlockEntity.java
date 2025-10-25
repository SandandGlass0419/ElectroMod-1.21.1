package net.devs.electromod.block.entity.custom.electro;

import net.devs.electromod.block.custom.electro.WireBlock;
import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import static net.devs.electromod.block.custom.electro.WireBlock.ELECTRIFIED;

public class WireBlockEntity extends BlockEntity {

    private float Electricity = 0; // 전류 값
    private int tickCounter = 0;

    // ⚡ 전류 값 읽기
    public float getElectricity() {
        return this.Electricity;
    }
    public void setElectricity(float electricity)
    {
        if (this.Electricity != electricity)
        {
            this.Electricity = electricity;
            markDirty();
        }
    }

    public int getTickCounter() { return this.tickCounter; }
    public void setTickCounter(int tickCounter) { this.tickCounter = tickCounter; }
    public void incrementTickCounter(int increment) { this.tickCounter += increment; }

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BE, pos, state);
    }

    // ⚡ 전류 설정 함수
    public void updateElectricity(float value) {
        if (this.world == null) return;

        setElectricity(value);

        BlockState state = this.world.getBlockState(this.pos);
        if (!(state.getBlock() instanceof WireBlock wireBlock)) return;

        if (!wireBlock.IsElectrified(state))
        {
            this.world.setBlockState(pos, state.with(ELECTRIFIED, true), Block.NOTIFY_ALL);
            wireBlock.onBlockElectricityUpdated(this.world, this.pos, this);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.Electricity = nbt.getFloat("electricity");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat("electricity", this.Electricity);
    }
}
