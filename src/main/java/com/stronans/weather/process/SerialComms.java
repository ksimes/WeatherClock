package com.stronans.weather.process;

import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.StopBits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Serial communications.
 */
@Slf4j
@Scope("prototype")
@Component
public class SerialComms {

    private Serial serial;
    private String comPort = "COM1";    // Default serial port
    private int speed = 9600;           // Default com speed
    private final DeviceContext context;
    private static int counter = 1;

    private static final String terminator = "\r";      // Message terminator

    private static final ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(2000);
    private static final List<MessageListener> listeners = new ArrayList<>();

    public SerialComms(DeviceContext context) {
        this.context = context;
    }

    public void configure(String port, int speed) {
        if (port != null) {
            comPort = port;
        }

        if (speed != 0) {
            this.speed = speed;
        }

        log.info("comPost = {}", comPort);
//        log.info("speed = {}", this.speed);

        // create an instance of the serial communications class
        serial = context.getContext().create(Serial.newConfigBuilder(context.getContext())
                .use_115200_N81()
                .dataBits_8()
                .id("comz" + counter++)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
                .device(comPort)
                .provider("pigpio-serial")
                .build());

// Start a thread to handle the incoming data from the serial port
        SerialReader serialReader = new SerialReader(serial);
        Thread serialReaderThread = new Thread(serialReader, "SerialReader");
        serialReaderThread.setDaemon(true);
        serialReaderThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
                                                 @Override
                                                 public void run() {
                                                     if (!context.getContext().isShutdown()) {
                                                         context.getContext().shutdown();
                                                         log.info("Shutdown Context with Serial hook.");
                                                     } else {
                                                         log.info("Context already Shutdown in Serial hook.");
                                                     }
                                                 }
                                             }
        );
    }

    public void configure(String port) {
        this.configure(port, 0);
    }

    public void configure(int speed) {
        this.configure(null, speed);
    }

    public String getPort() {
        return comPort;
    }

    public int getSpeed() {
        return speed;
    }

    public ArrayBlockingQueue<String> messages() {
        return messages;
    }

    public synchronized String getMessage() throws InterruptedException {
        return messages().take();
    }

    public synchronized boolean messagesAvailable() {
        return !messages().isEmpty();
    }

    public synchronized void sendMessage(String message) {
        String finalMsg = message + terminator;
        log.info("Message Sent : [{}] to port :{}", finalMsg, comPort);
        serial.write(finalMsg);
    }

    public void startComms() {
        try {
            serial.open();

            // Wait till the serial port is open
            while (!serial.isOpen()) {
                Thread.sleep(250);
            }
            log.info("Serial MetaData = {}", serial.getMetadata().all());

        } catch (InterruptedException ex) {
            log.error(" ==>> SERIAL SETUP FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
        }
    }

    public void endComms() {
        // Check if open the serial port provided on the GPIO header
        if (serial.isOpen()) {
            serial.close();
        }
    }

    public boolean addListener(MessageListener listener) {
        boolean result = false;

        listeners.add(listener);

        return result;
    }

    private static void notifyListeners() {
        if (!listeners.isEmpty()) {
            for (MessageListener listener : listeners) {
                listener.messageReceived();
            }
        }
    }

    public static class SerialReader implements Runnable {

        private final Serial serial;

        private boolean continueReading = true;

        public SerialReader(Serial serial) {
            this.serial = serial;
        }

        public void stopReading() {
            continueReading = false;
        }

        @Override
        public void run() {
            // We use a buffered reader to handle the data received from the serial port
            BufferedReader br = new BufferedReader(new InputStreamReader(serial.getInputStream()));

            try {
                // Data from the GPS is recieved in lines
                String line = "";

                // Read data until the flag is false
                while (continueReading) {
                    // First we need to check if there is data available to read.
                    // The read() command for pigio-serial is a NON-BLOCKING call,
                    // in contrast to typical java input streams.
                    var available = serial.available();
                    if (available > 0) {
                        for (int i = 0; i < available; i++) {
                            byte b = (byte) br.read();
                            if (b < 32) {
                                // All non-string bytes are handled as line breaks
                                if (!line.isEmpty()) {
                                    // Here we should add code to parse the data to a GPS data object
                                    log.info("from port {}", line);
                                    messages.add(line);
                                    line = "";
                                }
                            } else {
                                line += (char) b;
                            }
                        }
                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (Exception e) {
                log.error("Exception = {}", e.getMessage(), e);
            }
        }
    }
}
