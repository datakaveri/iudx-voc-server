/**
 * <h1>Proc.java</h1>
 * Exec a system process
 */

package iudx.vocserver.utils;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.concurrent.Executors;

import iudx.vocserver.utils.StreamGobbler;



public final class Proc {

    public static int execCommand(String command) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sh", "-c", command);
        try {
            Process process = builder.start();
            StreamGobbler streamGobbler = 
                new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            return exitCode;
        } catch (Exception e) {
            return -1;
        }
    }

}


