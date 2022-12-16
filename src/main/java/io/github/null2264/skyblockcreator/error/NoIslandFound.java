package io.github.null2264.skyblockcreator.error;

public class NoIslandFound extends Exception
{
    public NoIslandFound() {
        super("UUID doesn't have any island");
    }
}