package io.github.lucaargolo.structureworld;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import static io.github.lucaargolo.structureworld.Mod.MOD_ID;

public interface IEntityPersistentData {
    NbtCompound getPersistentData();
}