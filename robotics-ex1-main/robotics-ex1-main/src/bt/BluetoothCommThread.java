package bt;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import java.io.DataOutputStream;
import java.io.IOException;

import ex1.DebugNotes;

public class BluetoothCommThread extends Thread {
    private DataOutputStream dos;
    private volatile boolean running = true;
    private final Object lock = new Object();

    public void run() {
        BTConnection connection = Bluetooth.waitForConnection();

        if (connection == null) {
            DebugNotes.msg("Failed to connect to PC.");
            return;
        }

        dos = connection.openDataOutputStream();
        System.out.println("Connected to PC.");

        while (running) {
            synchronized (lock) {
                try {
                    lock.wait(); // Wait for data to be available
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Sending data
                try {
                    if (dos != null) {
                        String dataToSend = getDataToSend();
                        if (dataToSend != null) {
                            dos.writeUTF(dataToSend);
                            dos.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (dos != null) {
                dos.close();
            }
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) {
        synchronized (lock) {
            setDataToSend(data);
            lock.notify();
        }
    }

    public void stopRunning() {
        running = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    // Placeholder methods for storing and retrieving data to send
    private String dataToSend = null;

    private void setDataToSend(String data) {
        this.dataToSend = data;
    }

    private String getDataToSend() {
        return dataToSend;
    }
}
