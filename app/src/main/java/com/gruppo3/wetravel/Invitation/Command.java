package com.gruppo3.wetravel.Invitation;

/**
 * Basic structure of a Command to be executed by the CommandExecutor.
 * Created following the
 * <a href="https://refactoring.guru/design-patterns/command">Command Design Pattern</a>
 *
 * @author Edoardo Raimondi, idea by Marco Cognolato, Enrico Cestaro, Giovanni Velludo
 */
public abstract class Command {

    /**
     * Execute the specific class command
     */
    protected abstract void execute();
}