package com.goldfiction.smartpillow.smartpillow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import java.io.*;
import java.util.Set;
import java.util.UUID;



public class MainActivity extends ActionBarActivity {
    EditText lightText;
    EditText soundText;
    EditText viberationText;
    EditText logText;
    EditText btidText;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightText=(EditText)this.findViewById(R.id.lighttext);
        soundText=(EditText)this.findViewById(R.id.soundtext);
        viberationText=(EditText)this.findViewById(R.id.viberationtext);
        logText=(EditText)this.findViewById(R.id.logtext);
        btidText=(EditText)this.findViewById(R.id.btid);


        Button openButton = (Button)findViewById(R.id.btopen);
        Button closeButton = (Button)findViewById(R.id.btclose);
        Button graphButton = (Button)findViewById(R.id.open_graph_activity);
        //Open Button
        openButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    findBT();
                    openBT();
                }
                catch (IOException ex) { }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    closeBT();
                }
                catch (IOException ex) { }
            }
        });

        graphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GraphActivity.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickLightBtn(View v){
        String msg="Light Set to "+lightText.getText().toString();
        String cmd="L|"+lightText.getText().toString();
        log(msg);
        try
        {
            sendData(cmd);
        }
        catch (IOException ex) { }    }
    public void onClickSoundBtn(View v){
        String msg="Sound Set to "+soundText.getText().toString();
        String cmd="S|"+soundText.getText().toString();
        log(msg);
        try
        {
            sendData(cmd);
        }
        catch (IOException ex) { }    }
    public void onClickVibBtn(View v){
        String msg="Viberation Set to " + viberationText.getText().toString();
        String cmd="V|"+viberationText.getText().toString();
        log(msg);
        try
        {
            sendData(cmd);
        }
        catch (IOException ex) { }
    }



    String log="";
    public String log(String in){
        log+=in+"\r\n";
        logText.setText(log);
        return log;
    }

    void findBT()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            log("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals(btidText.getText().toString()))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        log("Bluetooth Device Found");
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        log("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            log(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String msgin) throws IOException
    {
        String msg = msgin;
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        log("Data Sent");
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        log("Bluetooth Closed");
    }



}
