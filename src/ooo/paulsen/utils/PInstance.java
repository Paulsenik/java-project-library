package ooo.paulsen.utils;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Used for singe-instance-Programms <br>
 * <a href="https://stackoverflow.com/questions/920386/how-to-allow-running-only-one-instance-of-a-java-program-at-a-time">Source</a>
 */
public class PInstance {

    private static int PORT;
    private static ServerSocket socket;

    /**
     * Creates/reserves a local Port to indicate that this Program is running.
     * @param port has to be in Range and not be allocated by <a href="https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml">another program</a>
     * @throws BindException if Another Instance already exists
     * <br>IOException if the socket failed
     */
    public PInstance(int port) throws IOException, BindException {
        PORT = port;

        //Bind to localhost adapter with a zero connection queue
        socket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
    }

    /**
     *
     * @param port
     * @return if a local Program, which reserves/uses a Port, is running.
     */
    public static boolean isInstanceRunning(int port) {
        try {
            //Bind to localhost adapter with a zero connection queue
            socket = new ServerSocket(port, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        } catch (BindException e) {
            return true;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

}
