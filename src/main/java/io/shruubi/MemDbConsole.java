package io.shruubi;

import io.shruubi.memdb.MemDbInterface;
import io.shruubi.memdb.impl.MemDbImpl;
import picocli.CommandLine;

import java.io.Console;
import java.util.Scanner;
import java.util.concurrent.Callable;

@CommandLine.Command
public class MemDbConsole implements Callable<Integer> {
    private final MemDbInterface memDb;

    private MemDbConsole() {
        this.memDb = new MemDbImpl();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MemDbConsole()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        Console console = System.console();
        while (true) {
            String command = console.readLine("memdb> ");
            Scanner scanner = new Scanner(command);
            String cmd = scanner.next();

            switch (cmd.toLowerCase()) {
                case "set" -> {
                    String key = scanner.next();
                    StringBuilder valueBuilder = new StringBuilder();
                    while (scanner.hasNext()) {
                        valueBuilder.append(scanner.next());
                        valueBuilder.append(" ");
                    }
                    String value = valueBuilder.toString().trim();
                    memDb.set(key, value);
                }

                case "get" -> {
                    String key = scanner.next();
                    String value = memDb.get(key);
                    console.printf("%s\n", value);
                }

                case "delete" -> {
                    String key = scanner.next();
                    memDb.delete(key);
                }

                case "quit", "exit" -> {
                    return 0;
                }
            }
        }
    }
}
