package io.github.lucaargolo.structureworld.error;

public class NoIslandFound extends Exception {
    public NoIslandFound() {
        super("UUID doesn't have any island");
    }
}