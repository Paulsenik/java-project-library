package ooo.paulsen.io.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * References to this class should be disconnected before setting null!!!
 */
public class PSerialConnection {

    /**
     * @return list of systemPortNames of available serialports
     */
    public static String[] getSerialPorts() {
        SerialPort[] temp = SerialPort.getCommPorts();
        String s[] = new String[temp.length];
        for (int i = 0; i < temp.length; i++)
            s[i] = temp[i].getSystemPortName();
        return s;
    }

    // Private Constructor
    public PSerialConnection(SerialPort port) {
        if (port == null)
            throw new SerialPortInvalidPortException("Port can not be null!");
        initSerial(port);
    }

    // Private Constructor
    public PSerialConnection(String systemPortName) throws SerialPortInvalidPortException {
        initSerial(SerialPort.getCommPort(systemPortName));
    }

    private CopyOnWriteArrayList<PSerialListener> listeners = new CopyOnWriteArrayList<>();

    private SerialPort port;
    private OutputStream out;

    private Thread listenerThread;
    private boolean isConnected = false;

    private void initSerial(SerialPort port) {
        this.port = port;
        port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        // Disconnect-Event
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (serialPortEvent.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED)
                    disConnect();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (isConnected)
                    disConnect();
            }
        }));
    }

    // should only be called from connect()
    private void initThread() {
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scnIn = new Scanner(port.getInputStream());

                while (port.isOpen() && isConnected && !Thread.currentThread().isInterrupted()) {
                    try {
                        String s = scnIn.nextLine();
                        for (PSerialListener l : listeners) {
                            if (l != null)
                                l.readLine(s);
                        }
                    } catch (Exception e) {
                    }
                }

                if (!port.isOpen()) {
                    disConnect();
                }
                scnIn.close();

            }
        });
        listenerThread.start();
    }

    /**
     * Sends Data in bytes to the connected device
     *
     * @param data as String
     * @return true when transmission was successful
     * @throws IOException
     */
    public boolean write(String data) throws IOException {
        if (isConnected && out != null) {
            out.write((data.getBytes()));
            out.flush();
            return true;
        }
        return false;
    }

    public boolean connect() {
        if (!isConnected) {
            if (port.openPort()) {
                initThread();
                out = port.getOutputStream();
                isConnected = true;
                return true;
            } else
                System.err.println("--port could not be opened");
        } else
            System.err.println("--already connected to something");
        return false;
    }

    public boolean disConnect() {
        if (isConnected) {
            port.closePort();
            listenerThread.stop();
            listenerThread = null;
            isConnected = false;
            return true;
        }

        return false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public synchronized boolean addListener(PSerialListener listener) {
        if (listener != null)
            return listeners.add(listener);
        return false;
    }

    public synchronized boolean removeListener(PSerialListener listener) {
        return listeners.remove(listener);
    }

    public ArrayList<PSerialListener> getListeners() {
        return new ArrayList<>(listeners);
    }

    public String getPortName() {
        if (port != null) {
            return port.getSystemPortName();
        }
        return null;
    }

    @Override
    public void finalize() {
        if (isConnected)
            disConnect();
    }

}
