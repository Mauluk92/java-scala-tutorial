package it.mauluk92.tutorial.scala.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ScalaFacade {

    private final static Logger logger = Logger.getLogger(ScalaFacade.class.getName());


    public static int compile(String outputDir, String classPath, String sourcePath, String modulePath, String... files) {
        try {
            ProcessBuilder compileBuilder = new ProcessBuilder();
            compileBuilder.directory(new File(sourcePath));
            List<String> commands = new ArrayList<>();
            commands.add("scalac");
            commands.add("-p");
            commands.add(modulePath);
            commands.add("-cp");
            commands.add(classPath);
            commands.add("-d");
            commands.add(outputDir);
            commands.addAll(Arrays.stream(files).toList());
            compileBuilder.command(commands);
            Process compileProcess = compileBuilder.start();
            BufferedReader compileErrorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
            StringBuilder compileErrors = new StringBuilder();
            String line;
            while ((line = compileErrorReader.readLine()) != null) {
                compileErrors.append(line).append("\n");
            }
            int compileExitCode = compileProcess.waitFor();
            if (compileExitCode != 0) {
                System.out.println("Error during compilation (exit code " + compileExitCode + "):" + "files: " + Arrays.toString(files));
                System.out.println(compileErrors);
            } else {
                System.out.println("Compiled successfully files: " + Arrays.toString(files));
            }
            return compileExitCode;
        } catch (IOException | InterruptedException e) {
            logger.severe(e.getMessage());
            return -1;
        }

    }


    public static int run(List<String> args, String compiledPath, String classPath, String mainClass, String modulePath, String moduleName) {
        try {
            ProcessBuilder runBuilder = new ProcessBuilder();
            runBuilder.directory(new File(compiledPath));
            List<String> commands = new ArrayList<>();
            if(modulePath != null && !modulePath.isEmpty() && moduleName != null && !moduleName.isEmpty()){
                commands.add("scala");
                commands.add("-p");
                commands.add(modulePath);
                commands.add("-m");
                commands.add("-ea");
                commands.add(moduleName);
            }else {
                commands.add("scala");
                commands.add("-cp");
                commands.add(classPath);
                commands.add("-ea");
                commands.add(mainClass);

                commands.addAll(args);
            }

            runBuilder.command(commands);

            Process runProcess = runBuilder.start();

            BufferedReader runOutputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder runOutput = new StringBuilder();
            String line;
            while ((line = runOutputReader.readLine()) != null) {
                runOutput.append(line).append("\n");
            }

            BufferedReader runErrorReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
            StringBuilder runErrors = new StringBuilder();
            while ((line = runErrorReader.readLine()) != null) {
                runErrors.append(line).append("\n");
            }

            int runExitCode = runProcess.waitFor();
            if (runExitCode != 0) {
                System.out.printf("Error during execution  of %s (exit code " + runExitCode + "):", mainClass);
                System.out.println(runErrors);
            } else {
                System.out.printf("Executed successfully file %s. Output:%n", mainClass);
                System.out.println(runOutput);
            }
            return runExitCode;

        } catch (Exception e) {
            logger.severe(e.getMessage());
            return -1;
        }
    }
}
