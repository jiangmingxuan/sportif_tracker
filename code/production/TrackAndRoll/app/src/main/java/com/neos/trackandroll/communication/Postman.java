package com.neos.trackandroll.communication;

import android.os.AsyncTask;

import com.neos.trackandroll.model.constant.Constant;
import com.neos.trackandroll.service.AppService;
import com.neos.trackandroll.utils.LogUtils;
import com.neos.trackandroll.view.activity.RunningSessionActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public final class Postman {

    /**
     * The instance of the Postman
     */
    private static Postman instance;


    /**
     * Getter of the instance Postman
     * @return the Postman instance
     */
    public static Postman getInstance(Communication communication) {
        if (instance == null){
            instance = new Postman(communication);
        }
        return instance;
    }


    /**
     * State of socket's postman
     */
    public static final int CONNEXION = 0;
    public static final int CONNECTED = 1;
    public static final int ERROR = 2;
    public static final int KILL = 3;

    /**
     * State of the socket
     */
    public int stateSocket;

    /**
     * type of encodage.
     */
    private final static String ENCODAGE = "UTF-8";

    /**
     * The socket used for the connexion
     */
    private Socket mySocket;

    /**
     * The communication
     */
    private Communication communication;

    /**
     * the buffer used to read a message on the socket
     */
    private BufferedReader bufferedReader;

    /**
     * the buffer used to write a message on the socket
     */
    private BufferedWriter bufferedWriter;

    /**
     * the SetUpConnexion thread
     */
    private SetUpConnexion setUpConnexion;



    /**
     * Builder of the Postman
     */
    private Postman(Communication communication) {
        this.communication = communication;
        stateSocket = CONNEXION;
        this.setUpConnexion = new SetUpConnexion();
        this.setUpConnexion.execute();
    }

    public void restartCommunication(){
        stateSocket = CONNEXION;
        this.setUpConnexion.cancel(true);
        this.setUpConnexion = new SetUpConnexion();
        this.setUpConnexion.execute();
    }


    /**
     * write the message on the socket
     * @param message
     */
    public void writeMessage(String message) {
        new Write().execute(message);
    }

    /**
     * read a message on the socket and return the message
     * @return the message
     * @throws IOException
     */
    public String readMessage() throws IOException, NullPointerException {
        return bufferedReader.readLine();
    }

    /**
     * process to connect the application to the raspberry by the socket
     * @return mySocket
     */
    private class SetUpConnexion extends AsyncTask<Void, Void, Void> {
        /**
         * Called when the client is executed
         * @param arg0
         * @return
         */
        @Override
        protected Void doInBackground(Void... arg0) {
            LogUtils.d(LogUtils.DEBUG_TAG,"Creation du socket");

            stateSocket = ERROR; // default error : connection not available / has failed

            // Set up the socket
            try {
                LogUtils.d(LogUtils.DEBUG_TAG, "Wait for socket creation address " + Constant.SERVER_ADDRESS + " on port " + Constant.SERVER_PORT);
                mySocket = new Socket();
                mySocket.connect(new InetSocketAddress(Constant.SERVER_ADDRESS, Constant.SERVER_PORT), Constant.TW_CONNECTION_SOCKET);

                communication.onSocketConnected();
                LogUtils.d(LogUtils.DEBUG_TAG, "Socket Created");

                bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream(), ENCODAGE));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream(), ENCODAGE));
                stateSocket = CONNECTED;

            } catch (ConnectException ce){
                LogUtils.e(LogUtils.DEBUG_TAG,"Socket Error",ce);
                stateSocket = ERROR;
                launchTimerReportError();

            } catch (Exception e) {

                // appears when the socket is already connected
                LogUtils.e(LogUtils.DEBUG_TAG,"Socket Error",e);
                stateSocket = ERROR;
                communication.onSocketConnectionFailed();
            }
            return null;
        }

        /**
         *  Called when the task is finished
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void launchTimerReportError(){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        communication.onSocketConnectionFailed();
                    }
                }
                ,Constant.TW_CONNECTION_SOCKET
        );
    }

    /**
     * Write on the BufferWriter to send a message to the raspberry
     */
    private class Write extends AsyncTask<String, Void, Void> {
        /** Called when the client is executed */
        @Override
        protected Void doInBackground(String... mess) {
            if (mySocket != null) {
                if (!mySocket.isClosed()) {
                    try {
                        LogUtils.d(LogUtils.DEBUG_TAG, "ECRITURE...");
                        bufferedWriter.write(mess[0], 0, mess[0].length());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        LogUtils.d(LogUtils.DEBUG_TAG, "FIN ECRITURE");
                    } catch (IOException e) {
                        e.printStackTrace();
                        stateSocket = ERROR;
                        LogUtils.d(LogUtils.DEBUG_TAG, "ECRITURE ERROR");
                        communication.onSocketConnectionFailed();
                    }
                } else {
                    stateSocket = ERROR;
                }
            }
            return null;
        }

        /**
         * Called when the task is finished
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
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
                    this.stateSocket=KILL;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Getter of the socket state
     * @return the state's socket
     */
    public int getStateSocket(){
        return stateSocket;
    }
}