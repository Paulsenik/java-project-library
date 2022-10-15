package ooo.paulsen.utils;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Used for singe-instance-Programms <br>
 * <a href="https://stackoverflow.com/questions/920386/how-to-allow-running-only-one-instance-of-a-java-program-at-a-time">Source</a>
 */
public class PInstance {

    private final int PORT;
    private final Runnable connectAction;
    private ServerSocket serverSocket;

    /**
     * Creates/reserves a local Port to indicate that this Program is running.
     *
     * @param port          has to be in Range and not be allocated by <a href="https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xhtml">another program</a>
     * @param connectAction is the Function that is called when some program connects to it
     * @throws IOException if the socket failed or Another Instance already exists
     */
    public PInstance(int port, Runnable connectAction) throws IOException {
        PORT = port;
        this.connectAction = connectAction;

        callServer();
        initServer();
    }

    /**
     * Looks for a Server/Process that waits for a connection
     */
    private void callServer() {
        try {
            Socket s = new Socket("127.0.0.1", PORT);
        } catch (IOException e) {
            // No Other Instance found (or other error)
        }
    }

    /**
     * Inits Server
     *
     * @throws IOException if the socket failed or Another Instance already exists
     */
    private void initServer() throws IOException {

        //Bind to localhost adapter with a zero connection queue
        serverSocket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        Thread t = new Thread(() -> {
            while (true) {
                try {

                    // look for connection
                    serverSocket.accept();

                    // execute Function if some other program connected (to e.g. focus the older UI-Instance)
                    if (connectAction != null)
                        connectAction.run();

                    // close and reopen server
                    serverSocket.close();
                    serverSocket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
                } catch (IOException e) {
                    // Address already in Use
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
    }

    /**
     * @return if a local Program, which reserves/uses a Port, is running.
     */
    public static boolean isInstanceRunning(int port) {
        try {
            //Bind to localhost adapter with a zero connection queue
            ServerSocket serverSocket = new ServerSocket(port, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
        } catch (BindException e) {
            return true;
        } catch (IOException e) {
            return false;
        }
        return false;
    }

}
