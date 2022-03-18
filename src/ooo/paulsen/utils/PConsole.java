package ooo.paulsen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class PConsole {

    // class is static
    private PConsole() {
    }

    /**
     * Runs a given Command in the Terminal/Console (Linux/Windows) and returns the response
     * @param command Command that is executed (Do <b>NOT</b> use sudo on Linux with this function)
     * @return the response of the console.<br>The Error-Stream will be put at the end with "ERROR>  " at the beginning of every line.
     */
    public static String run(String command) {
        return run(null, command);
    }

    /**
     * Runs a given Command in the Terminal/Console (Linux/Windows) and returns the response
     * @param workingDir Folder in which the command should be executed in.<br>If Null or path doesn't point to a Folder: The workingDir will be ignored.
     * @param command Command that is executed (Do <b>NOT</b> use sudo on Linux with this function)
     * @return the response of the console.<br>The Error-Stream will be put at the end with "ERROR>  " at the beginning of every line.
     */
    public static String run(File workingDir, String command) {
        ProcessBuilder pb = new ProcessBuilder();
        String out = "";

        try {

            // set Command
            if (PSystem.getOSType() == PSystem.OSType.WINDOWS) {
                pb.command("cmd.exe", "/c", command);
            } else {
                pb.command("sh", "-c", command);
            }

            // set workingDir
            if (workingDir != null && workingDir.exists()) {
                pb.directory(workingDir);
            }

            // start process
            Process proc = pb.start();

            // Reader
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String line = "";
            while (true) { // normal input
                try {
                    if (!((line = inputReader.readLine()) != null))
                        break;

                    out += line + "\n";
                } catch (IOException e) {
                }
            }
            while (true) { // eror input
                try {
                    if (!((line = errorReader.readLine()) != null))
                        break;

                    out += "ERROR>  " + line + "\n";
                } catch (IOException e) {
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

}
