package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import com.qps.btgenie.BluetoothManager;
import com.qps.btgenie.QABTPAccessory;
import com.tpcodl.billingreading.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;

    BluetoothManager btpObject;
    boolean closeprinter = false;
    private String rcptType="";
    private String AccNum="";
    private String TransID="";
    String devicename="nodevice";
    private String mmDeviceAdr="";
     Context context ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        context=this;

        btpObject=BluetoothManager.getInstance(this, new QABTPAccessory() {
            @Override
            public void onBluetoothDeviceFound(BluetoothDevice bluetoothDevice) {
                Log.d("DemoApp", "devicename 1  " );
            }

            @Override
            public void onClientConnectionSuccess() {
                Log.d("DemoApp", "devicename 2  " );
                //Do Not start printing here
            }

            @Override
            public void onClientConnectionFail() {
                Log.d("DemoApp", "devicename 3  " );
            }

            @Override
            public void onClientConnecting() {
                Log.d("DemoApp", "devicename 4 " );
            }

            @Override
            public void onClientDisconnectSuccess() {
                Log.d("DemoApp", "devicename 5  " );
            }

            @Override
            public void onNoClientConnected() {
                Log.d("DemoApp", "devicename 6 " );

            }

            @Override
            public void onBluetoothStartDiscovery() {
                Log.d("DemoApp", "devicename 7  " );
            }

            @Override
            public void onBluetoothNotAvailable() {
                Log.d("DemoApp", "devicename 8  " );
            }

            @Override
            public void onBatterystatuscheck(String s) {
                if(closeprinter == true){       //Added to close the printer
                    btpObject.closeConnection();
                    closeprinter = false;
                }
            }

            @Override
            public void onresponsefrmBluetoothdevice(String s) {

            }

            @Override
            public void onError(String s) {

            }

        });
        mmOutputStream=null;
        mmInputStream=null;
        mmDevice=null;
        mBluetoothAdapter=null;

        rcptType="ORIGINAL";
        AccNum="";
        String dubl="";
        String accnumber="";

        try {
            Bundle PrintBun = getIntent().getExtras();
            AccNum = PrintBun.getString("custID");
            TransID= PrintBun.getString("TransID");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        if(devicename.equals("nodevice")) {
            try {
                //     Log.d("DemoApp", "Entering findbt  " );
                devicename = findBT();
                Log.d("DemoApp", "BT found " );
            } catch (Exception ex) {

                //     Log.d("DemoApp", "Exception 1  " + ex);
            }
        }
        if(devicename.equals("BTprinter8127")){
            try{
                Log.d("DemoApp", "Entering open bt  " );
                openBT();
                Log.d("DemoApp", "BT opened ");
            } catch (Exception ex) {
                //     Log.d("DemoApp", "Exception 2  " + ex);

            }
        }

        if(devicename.substring(0,5).equals("SILBT")){
            try{
                Log.d("DemoApp", "Entering open bt  " );
                openBT();
                Log.d("DemoApp", "BT opened ");
            } catch (Exception ex) {
                //     Log.d("DemoApp", "Exception 2  " + ex);

            }
        }
        if(devicename.substring(0,2).equals("QA")) {
            try {
                Log.d("DemoApp", "Entering open bt  ");
                if (!BluetoothManager.isConnected() && mmDeviceAdr != null) {
                    btpObject.createClient(mmDeviceAdr);

                    Log.d("DemoApp", "BT opened ");
                } else {
                    System.out.println("BT Closed!..");
                }

            } catch (Exception ex) {
                //     Log.d("DemoApp", "Exception 2  " + ex);

            }
        }
        try{
            Log.d("DemoApp", "sending data  ");
          //  sendData();
            //    Log.d("DemoApp", "data sent ");
        } catch (Exception ex) {Log.d("DemoApp", "Exception 3 " + ex);
        }
        if(devicename.substring(0,2).equals("QA")) {
            try {
                closeBT();
            } catch (Exception ex) {
                Log.d("DemoApp", "Exception 3 " + ex);
            }
        }



    }

    String findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //  myLabel.setText("No bluetooth adapter available");
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // MP300 is the name of the bluetooth printer device
                    if (device.getName().equals("IMPACTSPT") || device.getName().equals("AT2TV3")) {
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //     Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        // break;
                    }else if (device.getName().equals("BTprinter8127")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //   Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        Log.d("DemoApp", "paired   " + device.getName());
                    }else if (device.getName().substring(0,5).equals("SILBT")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //   Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        Log.d("DemoApp", "paired   " + device.getName());
                    }else if (device.getName().substring(0,2).equals("QA") || device.getName().contains("QA") ){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //   Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        Log.d("DemoApp", "paired   " + device.getName());
                    }
                    else{
                        Log.d("DemoApp", "Unpaired   " + device.getName());
                    }
                }

            }
            // myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            Log.d("DemoApp", "Exception 5  " + e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("DemoApp", "Exception 6  " + e);
            e.printStackTrace();
        }
        return mmDevice.getName();
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        ////////////////
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mmDeviceAdr);
        try {
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            // AlertBox("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        ////////////////////
        int openflag=1;
        try {
            // Standard SerialPortService ID
            ParcelUuid list[] = mmDevice.getUuids();
            Log.d("DemoApp", "openbt 1   " +list[0]);
            //  mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            //   mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
            //   mmSocket= mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            if(mmSocket.isConnected()){
                Log.d("DemoApp", "openbt 1 socket connected " );
            }else{
                Log.d("DemoApp", "openbt 1  socket close " );
            }
            mmSocket.connect();
            Log.d("DemoApp", "openbt connect success   ");
            //  mBluetoothAdapter.cancelDiscovery();
            mmInputStream=null;
            mmOutputStream=null;
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            //   Toast.makeText(BillPrintActivity.this, "open blue tooth"+mmInputStream.toString(), Toast.LENGTH_LONG).show();
          //  beginListenForData();
           // strPrntMsg.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            Log.d("DemoApp", "Exception 7  " + e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("DemoApp", "Exception 8  " + e);
            e.printStackTrace();
        }

    }
    void closeBT() throws IOException {
        try {
            //  stopWorker = true;
//added
            if(mmOutputStream!= null) {
                mmOutputStream.flush();
                mmOutputStream.close();
            }
            if(mmInputStream != null)
                mmInputStream.close();
            ////
            // mmOutputStream.flush();
            //mmOutputStream.close();
            // mmInputStream.close();
            try{
                if(BluetoothManager.isConnected()) {
                    closeprinter = true;
                    btpObject.Batterystatus();
                    long curntime = System.currentTimeMillis();
                    while(curntime+10000 < System.currentTimeMillis()){
                        if(!closeprinter){
                            break;
                        }
                    }
                    if(closeprinter) {
                        btpObject.closeConnection();
                        closeprinter = false;
                    }
                }
                if (mmSocket != null) {
                    Log.d("DemoApp", "on 10 " );
                    try {Log.d("DemoApp", "on 11 " );mmSocket.close();Log.d("DemoApp", "on 12 ");mmSocket = null;} catch (Exception e) { mmSocket = null;Log.d("DemoApp", "on 8 "+e );}

                    Log.d("DemoApp", "on 13 " );
                }

            } catch (Exception e) { Log.d("DemoApp", "on 9 "+e );}
        } catch (Exception e) {
            //  Toast.makeText(BillPrintActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }//   Toast.makeText(BillPrintActivity.this, "message11" + e, Toast.LENGTH_LONG).show();

    }
}