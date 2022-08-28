package io.github.lucaargolo.structureworld;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StructureChunkGenerator extends ChunkGenerator {

    public static final Codec<StructureChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    RegistryOps.createRegistryCodec(Registry.STRUCTURE_SET_KEY).forGetter((chunkGenerator) -> chunkGenerator.field_37053),
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter((generator) -> generator.biomeSource),
                    Codec.STRING.stable().fieldOf("structure").forGetter((generator) -> generator.structure),
                    BlockPos.CODEC.fieldOf("structureOffset").forGetter((generator) -> generator.structureOffset),
                    BlockPos.CODEC.fieldOf("playerSpawnOffset").forGetter((generator) -> generator.playerSpawnOffset),
                    BlockState.CODEC.optionalFieldOf("fillmentBlock", Blocks.AIR.getDefaultState()).stable().forGetter((generator) -> generator.fillmentBlock),
                    Codec.BOOL.optionalFieldOf("enableTopBedrock", false).stable().forGetter((generator) -> generator.enableTopBedrock),
                    Codec.BOOL.optionalFieldOf("enableBottomBedrock", false).stable().forGetter((generator) -> generator.enableBottomBedrock),
                    Codec.BOOL.optionalFieldOf("isBedrockFlat", false).stable().forGetter((generator) -> generator.isBedrockFlat)
            ).apply(instance, instance.stable(StructureChunkGenerator::new))
    );
    private final String structure;
    private final BlockPos structureOffset;
    private final BlockPos playerSpawnOffset;
    private final BlockState fillmentBlock;
    private final boolean enableTopBedrock;
    private final boolean enableBottomBedrock;
    private final boolean isBedrockFlat;

    public StructureChunkGenerator(Registry<StructureSet> registry, BiomeSource biomeSource, String structure, BlockPos structureOffset, BlockPos playerSpawnOffset, BlockState fillmentBlock, boolean enableTopBedrock, boolean enableBottomBedrock, boolean isBedrockFlat) {
        super(registry, Optional.of(RegistryEntryList.of(Collections.emptyList())), biomeSource);
        this.structure = structure;
        this.structureOffset = structureOffset;
        this.playerSpawnOffset = playerSpawnOffset;
        this.fillmentBlock = fillmentBlock;
        this.enableTopBedrock = enableTopBedrock;
        this.enableBottomBedrock = enableBottomBedrock;
        this.isBedrockFlat = isBedrockFlat;
    }

    public String getStructure() {
        return structure;
    }

    public BlockPos getStructureOffset() {
        return structureOffset;
    }

    public BlockPos getPlayerSpawnOffset() {
        return playerSpawnOffset;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return this;
    }

    @Override
    public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler() {
        return MultiNoiseUtil.method_40443();
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
        if (!fillmentBlock.isAir()) {
            int startX = chunk.getPos().getStartX();
            int startZ = chunk.getPos().getStartZ();

            BlockPos.iterate(startX, 0, startZ, startX + 15, getWorldHeight(), startZ + 15).forEach(blockPos -> chunk.setBlockState(blockPos, fillmentBlock, false));
        }
        if (enableTopBedrock || enableBottomBedrock) {
            ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
            buildBedrock(chunk, chunkRandom);
        }
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public int getWorldHeight() {
        return 320;
    }

    @Override
    public int getSeaLevel() {
        return 64;
    }

    @Override
    public int getMinimumY() {
        return -64;
    }

    @SuppressWarnings("ConstantConditions")
    private void buildBedrock(Chunk chunk, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();

        int bottomBedrockY = 0;
        int topBedrockY = this.getWorldHeight() - 1;

        boolean shouldGenerateTopBedrock = topBedrockY + 4 >= 0 && topBedrockY < this.getWorldHeight() && this.enableTopBedrock;
        boolean shouldGenerateBottomBedrock = bottomBedrockY + 4 >= 0 && bottomBedrockY < this.getWorldHeight() && this.enableBottomBedrock;
        if (shouldGenerateTopBedrock || shouldGenerateBottomBedrock) {
            BlockPos.iterate(startX, 0, startZ, startX + 15, 0, startZ + 15).forEach(blockPos -> {
                if (shouldGenerateTopBedrock) {
                    if (isBedrockFlat) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), topBedrockY, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                    } else {
                        for (int o = 0; o < 5; ++o) {
                            if (o <= random.nextInt(5)) {
                                chunk.setBlockState(mutable.set(blockPos.getX(), topBedrockY - o, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                            }
                        }
                    }
                }
                if (shouldGenerateBottomBedrock) {
                    if (isBedrockFlat) {
                        chunk.setBlockState(mutable.set(blockPos.getX(), bottomBedrockY, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                    } else {
                        for (int o = 4; o >= 0; --o) {
                            if (o <= random.nextInt(5)) {
                                chunk.setBlockState(mutable.set(blockPos.getX(), bottomBedrockY + o, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType, HeightLimitView world) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) {
        return new VerticalBlockSample(0, new BlockState[0]);
    }

    @Override
    public void getDebugHudText(List<String> text, BlockPos pos) {

    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver generationStep) {
    }
}