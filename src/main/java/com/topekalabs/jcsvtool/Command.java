package com.topekalabs.jcsvtool;

/**
 * This is an interface which is used for processing and executing commands passed to the utility.
 * @author tfarkas
 */
public interface Command
{
    /**
     * This method executes the command.
     * @return True if the method was executed successfully.
     */
    public void execute();
}
