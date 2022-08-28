package io.github.lucaargolo.structureworld.error;

public class InvalidChunkGenerator extends Exception {
    public InvalidChunkGenerator() {
        super("Not structure world");
    }
}