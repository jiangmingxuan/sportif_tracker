package com.neos.trackandroll.cervo.communication.anchor.tcp;

import com.neos.trackandroll.cervo.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public final class TcpAnchorPostmanClient {

    /**
     * State of socket's postmanUDPClient
     */
    public static final int CONNEXION = 0;
    public static final int CONNECTED = 1;
    public static final int ERROR = 2;
    public static final int KILL = 3;

    /**
     * Time waiting for a socket connection
     */
    public static final int TW_CONNECTION_SOCKET = 5000;

    /**
     * State of the socket
     */
    public int stateSocket;

    /**
     * Port use for the tcpCommunicationTCPAnchor
     */
    public static final int SERVER_PORT = 8784;

    /**
     * Address use for the tcpCommunicationTCPAnchor
     */
    public static final String IP_ADDRESS = "192.168.11.192";

    /**
     * type of encodage.
     */
    private static final String ENCODAGE = "UTF-8";

    /**
     * The socket used for the connexion
     */
    private Socket mySocket;

    /**
     * the buffer used to read a message on the socket
     */
    private BufferedReader bufferedReader;

    /**
     * the buffer used to write a message on the socket
     */
    private BufferedWriter bufferedWriter;

    /**
     * The maestro communication
     */
    private TcpAnchorCommunication tcpCommunicationTCPAnchor;

    /**
     * Singleton management
     */
    private static TcpAnchorPostmanClient instance;

    /**
     * Builder of the Postman
     *
     * @param tcpCommunicationTCPAnchor : the tcp communication instance with the anchor
     */
    private TcpAnchorPostmanClient(TcpAnchorCommunication tcpCommunicationTCPAnchor) {
        stateSocket = CONNEXION;
        TcpAnchorPostmanClient.SetUpConnexion setUpConnexion = new TcpAnchorPostmanClient.SetUpConnexion();
        setUpConnexion.start();
        this.tcpCommunicationTCPAnchor = tcpCommunicationTCPAnchor;
    }

    /**
     * Process called to relaunch the postman
     */
    public void relaunchPostman() {
        stateSocket = CONNEXION;
        TcpAnchorPostmanClient.SetUpConnexion setUpConnexion = new TcpAnchorPostmanClient.SetUpConnexion();
        setUpConnexion.start();
    }

    /**
     * Getter of the instance Postman
     *
     * @param tcpAnchorCommunication : the tcp communication instance with the anchor
     * @return the instance Postman
     */
    public static TcpAnchorPostmanClient getInstance(TcpAnchorCommunication tcpAnchorCommunication) {
        if (instance == null) {
            instance = new TcpAnchorPostmanClient(tcpAnchorCommunication);
        }
        return instance;
    }

    /**
     * write the message on the socket
     *
     * @param message : the message to send
     */
    public void writeMessage(String message) {
        new TcpAnchorPostmanClient.Write(message).start();
    }

    /**
     * read a message on the socket and return the message
     *
     * @return the message
     * @throws IOException          : the input output exception
     * @throws NullPointerException : the null pointer exception
     */
    public String readMessage() throws IOException, NullPointerException {
        return bufferedReader.readLine();
    }

    /**
     * process to create the connection socket to the tcp anchor postman client
     */
    private class SetUpConnexion extends Thread {

        @Override
        public void run() {
            super.run();
            LogUtils.d("TCP Anchor Creation du socket");

            stateSocket = ERROR; // default error : connection not available / has failed

            // Set up the socket
            try {

                mySocket = new Socket();
                mySocket.connect(
                        new InetSocketAddress(InetAddress.getByName(IP_ADDRESS), SERVER_PORT),
                        TW_CONNECTION_SOCKET
                );

                bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream(), ENCODAGE));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream(), ENCODAGE));
                stateSocket = CONNECTED;

                LogUtils.d("TCP Anchor Socket Created");
                tcpCommunicationTCPAnchor.launchReadThread();

            } catch (ConnectException e) {
                LogUtils.e("Tcp Anchor postman client error -> ", e);
                stateSocket = ERROR;
                launchTimerReportError();

            } catch (Exception e) {
                LogUtils.e("Tcp Anchor postman client error => ", e);
                stateSocket = ERROR;
                tcpCommunicationTCPAnchor.getCommunicationState().onTcpAnchorFailed();
            }
        }
    }

    /**
     * Process called to launch the timer to tell the Brain that the tcp anchor communication failed
     */
    public void launchTimerReportError() {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        tcpCommunicationTCPAnchor.getCommunicationState().onTcpAnchorFailed();
                    }
                },
                TW_CONNECTION_SOCKET
        );
    }

    /**
     * Write on the BufferWriter to send a message to the anchor
     */
    private class Write extends Thread {

        /**
         * The message to send
         */
        private String messageToSend;

        /**
         * Main constructor of the write thread to write on the communication socket
         *
         * @param messageToSend : the message to send
         */
        public Write(String messageToSend) {
            this.messageToSend = messageToSend;
        }

        /**
         * Called when the client is executed
         */
        @Override
        public void run() {
            if (mySocket != null) {
                if (!mySocket.isClosed()) {
                    try {
                        LogUtils.d("MESSAGE SEND > " + messageToSend);
                        bufferedWriter.write(messageToSend, 0, messageToSend.length());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        stateSocket = ERROR;
                        LogUtils.d("ECRITURE ERROR");
                    }
                } else {
                    stateSocket = ERROR;
                }
            }
        }
    }

    /**
     * Process called to close the socket
     */
    public void closeSocket() {
        if (this.mySocket != null) {
            if (this.mySocket.isConnected()) {
                try {
                    this.mySocket.close();
                    this.stateSocket = KILL;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Getter of the socket state
     *
     * @return the state's socket
     */
    public int getStateSocket() {
        return stateSocket;
    }
}
