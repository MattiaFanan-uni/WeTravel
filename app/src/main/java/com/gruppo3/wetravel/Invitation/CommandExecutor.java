package com.gruppo3.wetravel.Invitation;


/**
 * Class that manages all the sent commands.
 * It just lets them executing.
 *
 * @author Edoardo Raimondi, idea by Marco Cognolato, Enrico cestaro
 * @link https://refactoring.guru/design-patterns/command
 */
public class CommandExecutor {

    /**
     * Calls the given command {@link Command#execute()} method
     *
     * @param command to be performed
     */
    public static void execute(Command command) {
        command.execute();
    }
}