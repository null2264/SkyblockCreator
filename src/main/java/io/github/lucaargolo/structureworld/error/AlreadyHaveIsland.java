package io.github.lucaargolo.structureworld.error;

public class AlreadyHaveIsland extends Exception {
    public AlreadyHaveIsland() {
        super("UUID already have an island");
    }
}