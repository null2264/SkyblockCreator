package io.github.null2264.skyblockcreator.error;

public class AlreadyHaveIsland extends Exception
{
    public AlreadyHaveIsland() {
        super("UUID already have an island");
    }
}