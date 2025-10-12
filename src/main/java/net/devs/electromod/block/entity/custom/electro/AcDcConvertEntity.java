package net.devs.electromod.block.entity.custom.electro;

import net.devs.electromod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class AcDcConvertEntity extends BlockEntity {
    //왜 기능도 없는 블록엔티티를 쓰냐고? 그야 틱이벤트를 쓰기 위함이지.
    //그럼 그냥 만들어둔 엔티티 상속하라고? 나도 그 생각을 했어. 이걸 다 만들고 나서 말이야. 젠장할
    public AcDcConvertEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACDC_BE, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
    {
        super.readNbt(nbt, registryLookup);
    }
}
