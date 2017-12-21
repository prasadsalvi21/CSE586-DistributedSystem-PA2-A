package edu.buffalo.cse.cse486586.groupmessenger1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final int SERVER_PORT = 10000;
   static final List<String> remotePort =new ArrayList<String>(5);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        remotePort.add("11108");
        remotePort.add("11112");
        remotePort.add("11116");
        remotePort.add("11120");
        remotePort.add("11124");

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        Log.i("myport",myPort);

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket",e);
            return;
        }
        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        final Button b4 = (Button) findViewById(R.id.button4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString() + "\n";
                editText.setText("");
                tv.append(msg);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
            }
        });
    }
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        int seqNumber = 0;
        private Uri uriAddress = null;


        private Uri buildUri(String scheme, String authority)
        {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            uriBuilder.scheme(scheme);
            return uriBuilder.build();
        }

        @Override
        protected Void doInBackground(ServerSocket... sockets){
            try {
                ServerSocket serverSocket = sockets[0];
                uriAddress = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
                Socket socket = null;
                String msg,ack;
                while(true)
                {
                    ack=null;
                    socket = serverSocket.accept();
                    DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    msg = is.readUTF();
                    Log.d(TAG,"Receiving msg from Client : "+msg);

                    ContentValues keyValueToInsert = new ContentValues();

                    // inserting <”key-to-insert”, “value-to-insert”>

                    keyValueToInsert.put("key", Integer.toString(seqNumber));
                    keyValueToInsert.put("value", msg);
                    seqNumber=seqNumber+1;
                    Uri newUri = getContentResolver().insert(uriAddress,keyValueToInsert);

                    DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                    ack="server ack";
                    os.writeUTF(ack);
                    os.flush();
                    Log.d(TAG,"Sending ack to Client : "+ack);

                    publishProgress(msg);
                    socket.close();
                }

            }
            catch (UnknownHostException e) {
                Log.e(TAG, "ServerTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ServerTask socket IOException");
            }
            return null;
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            TextView rtv = (TextView) findViewById(R.id.textView1);
            rtv.append(strReceived + "\t\n");

            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             *
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */

            return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            for (String rp : remotePort)
            {
                try {
                    //Log.i("RemotePort",rp);
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(rp));

                    String msgToSend = msgs[0];
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */
                    String ack = null;
                    do {
                        ack = null;
                        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                        os.writeUTF(msgToSend);
                        Log.d(TAG, "Sending Message to Server : " + msgToSend);
                        os.flush();

                        DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                        ack = is.readUTF();
                        Log.d(TAG, "Receiving ack from Server : " + ack);
                    }
                    while (!ack.equals("server ack"));
                    socket.close();
                }
                catch(UnknownHostException e){
                    Log.e(TAG, "ClientTask UnknownHostException");
                }catch(IOException e){
                    Log.e(TAG, "ClientTask socket IOException");
                }
            }
            return null;
        }
    }
}
