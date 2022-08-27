package io.github.lucaargolo.structureworld.mixin;

import io.github.lucaargolo.structureworld.IEntityPersistentData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.lucaargolo.structureworld.Mod.MOD_ID;

@Mixin(Entity.class)
public abstract class EntityPersistentDataMixin implements IEntityPersistentData {
    private NbtCompound persistentData;
    private final String dataName = MOD_ID + ".persistent_data";

    @Override
    public NbtCompound getPersistentData() {
        if(this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable info) {
        if(persistentData != null) {
            nbt.put(dataName, persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains(dataName, 10)) {
            persistentData = nbt.getCompound(dataName);
        }
    }
}