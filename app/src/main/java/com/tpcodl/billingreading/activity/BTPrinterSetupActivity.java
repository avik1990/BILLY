package com.tpcodl.billingreading.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;
import com.tpcodl.billingreading.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class BTPrinterSetupActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    ListView listDevicesFound;
    Button btnScanDevice, backBtnSET;
    TextView stateBluetooth, btaddressTv;
    BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter mBluetoothAdapter = null;
    static final UUID MY_UUID = UUID.randomUUID();
    private ImageView iv_back;
    private TextView tv_title;
    String address = "";

    EditText bluetoothTXT;
    public final String DATA_PATH1 = Environment.getExternalStorageDirectory() + "/";

    ArrayAdapter<String> btArrayAdapter;

    /**
     * Called when the activity is first created.
     */
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btprinter_setup);
        btaddressTv = (TextView) findViewById(R.id.btaddTV);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // ***********************************************************

        try {

            FileInputStream fstream = new FileInputStream(DATA_PATH1
                    + "BTaddress.txt");

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                btaddressTv.setText(strLine);
            }

            in.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());

        }

        // ***********************************************************

        btnScanDevice = (Button) findViewById(R.id.scandevice);

        stateBluetooth = (TextView) findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listDevicesFound = (ListView) findViewById(R.id.devicesfound);
        btArrayAdapter = new ArrayAdapter<String>(BTPrinterSetupActivity.this,
                android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);

        bluetoothTXT = (EditText) findViewById(R.id.bluetoothAds);

        backBtnSET = (Button) findViewById(R.id.backBtnSET);
        backBtnSET.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                onBackPressed();
            }

        });


        CheckBlueToothState();

        btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

        registerReceiver(ActionFoundReceiver, new IntentFilter(
                BluetoothDevice.ACTION_FOUND));

        listDevicesFound.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                /*
                 * Toast.makeText(getApplicationContext(),
                 * ""+listDevicesFound.getCount(), Toast.LENGTH_SHORT).show();
                 */
                String selection = (String) (listDevicesFound
                        .getItemAtPosition(position));
                Toast.makeText(getApplicationContext(),
                        "BLUETOOTH ADDRESS IS SAVED SUCCESSFULLY",
                        Toast.LENGTH_SHORT).show();

                address = selection.substring(0, 17);
                bluetoothTXT.setText(address);

                try {

                    File myFile = new File(DATA_PATH1 + "BTaddress.txt");
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(
                            fOut);
                    myOutWriter.append(address);
                    myOutWriter.close();
                    fOut.close();
                    Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
//create the object for the Bluetooth_Printer_2inch_Impact class

//call the any  method in Bluetooth_Printer_2inch_Impact class and save the return value in String variable
                    String doubleHeight = BPImpact.font_Double_Height_On();
                    String Printdata = "";
                    Printdata = "Hello Analogics";

//create the object for the AnalogicsImpactPrinter class
                    AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();

//call the Call_Printer(String BTaddress, String printdata)method in Call_Printer class

//pass the parameters to the Call_Printer method to print the data
                    //  print.Call_Printer(address,doubleHeight+Printdata);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Alertmessage();

            }
        });

        // *********************back Button***********************************

        // **************************************************************

    }

    @Override
    protected void onDestroy() {
           /* System.runFinalizersOnExit(true);
            System.exit(0);*/
        super.onDestroy();

    }

    private void CheckBlueToothState() {
        if (bluetoothAdapter == null) {
            stateBluetooth.setText("Bluetooth NOT support");
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    stateBluetooth
                            .setText("Bluetooth is currently in device discovery process.");
                } else {
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnScanDevice.setEnabled(true);
                }
            } else {
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private Button.OnClickListener btnScanDeviceOnClickListener = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if (bluetoothAdapter != null) {
                btArrayAdapter.clear();
                bluetoothAdapter.startDiscovery();
            }


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBlueToothState();
        }
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btArrayAdapter.add(device.getAddress() + "\n"
                        + device.getName());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void Alertmessage() {

        if (mBluetoothAdapter == null) {

            Toast.makeText(this, "Bluetooth is not available.",
                    Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this,
                    "Please enable your BT and re-run this program.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}