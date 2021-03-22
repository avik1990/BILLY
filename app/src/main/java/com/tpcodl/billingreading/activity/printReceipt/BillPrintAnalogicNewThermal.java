package com.tpcodl.billingreading.activity.printReceipt;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.analogics.thermalAPI.Bluetooth_Printer_2inch_prof_ThermalAPI;
import com.analogics.thermalprinter.AnalogicsThermalPrinter;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;

public class BillPrintAnalogicNewThermal extends AppCompatActivity {

    String address = null;

    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bill_print_analogic_new_thermal);
        AnalogicsThermalPrinter conn = new AnalogicsThermalPrinter();
        try {

            conn.openBT(address);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {

            Bluetooth_Printer_2inch_prof_ThermalAPI printer = new Bluetooth_Printer_2inch_prof_ThermalAPI();
            String data = "abbjhsdfdf" + "\n";
            String printdata = "";

            printdata = printer.font_Courier_10_VIP(data);
            printdata += printer.font_Courier_19_VIP(data);
            printdata += printer.font_Courier_20_VIP(data);
            printdata += printer.font_Courier_24_VIP(data);
            printdata += printer.font_Courier_25_VIP(data);
            printdata += printer.font_Courier_27_VIP(data);
            printdata += printer.font_Courier_29_VIP(data);
            printdata += printer.font_Courier_32_VIP(data);
            printdata += printer.font_Courier_34_VIP(data);
            printdata += printer.font_Courier_38_VIP(data);
            printdata += printer.font_Courier_42_VIP(data);
            printdata += printer.font_Courier_48_VIP(data);
            conn.printData(printdata);
        } catch (Exception e) {
            // Handle communications error here.
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

        }
        try {
            conn.closeBT();
        } catch (IOException ex) {
        }
    }
}
