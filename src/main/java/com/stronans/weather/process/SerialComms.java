package com.stronans.weather.process;

import com.pi4j.io.serial.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
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
    private String comPort = "COM1";
    private int speed = 9600;     // Default com speed

    private static final String terminator = "\r";
    private static final char terminatorchar = '\r';

    private String message;

    private static final ArrayBlockingQueue<String> messages = new ArrayBlockingQueue<>(20);
    private static final List<MessageListener> listeners = new ArrayList<>();

    public SerialComms() {
    }

    public void configure(String port, int speed) {
        if (port != null) {
            comPort = port;
        }

        if (speed != 0) {
            this.speed = speed;
        }

        log.info("comPost = {}", comPort);
        log.info("speed = {}", this.speed);

        // create an instance of the serial communications class
        serial = SerialFactory.createInstance();

        // create and register the serial data listener
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                try {
                    message += event.getString(Charset.defaultCharset());
                } catch (IOException e) {
                    log.error("Cannot convert text from : " + comPort + " : " + e.getMessage(), e);
                }
//                log.info("Pi received : [" + message + "]");

                while (message.contains(terminator)) {
                    messages.add(message.substring(0, message.indexOf(terminatorchar)));
                    notifyListeners();
                    message = message.substring(message.indexOf(terminatorchar) + 1);
                }
            }
        });
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
        try {
            log.info("Message Sent : [" + message + "] port :" + comPort);
            serial.write(message + terminator);
        } catch (IOException ex) {
            log.error(" ==>> SERIAL WRITE FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
        }
    }

    public void startComms() {
        try {
            // create serial config object
            SerialConfig config = new SerialConfig();
            // set default serial settings (device, baud rate, flow control, etc)
            //
            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
            // NOTE: this utility method will determine the default serial port for the
            //       detected platform and board/model.  For all Raspberry Pi models
            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
            //       environment configuration.
            config.device(comPort)
                    .baud(Baud._115200)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    .stopBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

            serial.open(config);
            // Wait till the serial port is open
            while (!serial.isOpen()) {
                Thread.sleep(250);
            }
        } catch (IOException | InterruptedException ex) {
            log.error(" ==>> SERIAL SETUP FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
        }
    }

    public void endComms() {
        try {
            // Check if open the serial port provided on the GPIO header
            if (serial.isOpen()) {
                serial.close();
            }
        } catch (IOException ex) {
            log.error(" ==>> SERIAL SHUTDOWN FAILED on port : " + comPort + " : " + ex.getMessage(), ex);
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
}
