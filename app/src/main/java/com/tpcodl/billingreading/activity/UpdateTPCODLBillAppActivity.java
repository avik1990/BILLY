package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UpdateTPCODLBillAppActivity extends AppCompatActivity {
    private Context context;
    int FlSizeDowned=0;
    private static int totrec=0;
    private static int progressStatus = 0;
    private static Handler handler = new Handler();
    private static ProgressBar pb=null;
    private static TextView tv=null;
    private static TextView msg1=null;
    private static TextView msg2=null;

    private static int count=0;
    private static BufferedReader bufRead=null;
    private static  String strLine=null;
    private static ArrayList<String> listtFilefound = new ArrayList<String>();
    private static int totfile=0;
    private static String filefound="";
    private static int fincnt=0;
    private static int progrcnt=1;
    Button backbtn=null;
    Button dwldbtn=null;
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tpcodl_bill_app);

        pb = (ProgressBar) findViewById(R.id.pb);
        tv = (TextView) findViewById(R.id.tv);
        msg1 = (TextView) findViewById(R.id.Msg1);
        msg2 = (TextView) findViewById(R.id.msg2);
        tv.setText("Click to Start");
        backbtn = (Button) findViewById(R.id.DwldBack);
        backbtn.setVisibility(Button.GONE);
        dwldbtn = (Button) findViewById(R.id.btn);
        dwldbtn.setVisibility(Button.VISIBLE);

        dwldbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////////////////////////////
                totrec=1;
                tv.setText("Start DownLoading");
                Log.d("DemoApp", "Entering SBM totrec"+totrec);
                new ProgressTask().execute("appfile.apk");
            }
        });//end
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });//end
    }

    private class ProgressTask extends AsyncTask<String,Integer,Void> {

        protected void onPreExecute() {
            super.onPreExecute(); ///////???????
            // totrec=2919000;
            tv.setText("Start DownLoading Wait !!");
            //  pb.setMax(totrec);
            fincnt=0;
            // progrcnt=1;
            Log.d("DemoApp", "  totrec =" + totrec);
            //Log.d("DemoApp", "  file_size =" + file_size);
        }

        protected void onrr(int i) {
            pb.setMax(i); // stop the progress
            totrec=i;
          //  Log.v("Progress", "Cancelled");
        }
        protected Void doInBackground(String... arg0) {
            try {
                //Log.e("UpdateAPP", "1 Inside Update  " );
                int downLoadUpdate=0;
                doHttpDownload(AppUtils.strInputUPDATE_APK_URL + AppUtils.strInputUPDATED_APK,AppUtils.strDownloadedUPDATEAPKPath );
                //downLoadUpdate = DownloadFileHTTP.DownloadFileHTTP(SbmUtilities.strInputUPDATE_APK_URL, SbmUtilities.strInputUPDATED_APK, SbmUtilities.strDownloadedUPDATEAPKPath);
               // Log.e("UpdateAPP", "2 Inside Update  : Updated" );
            } catch (Exception e) {
              //  Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }
        protected Void doHttpDownload(String... arg0) {
            try {
                //int code = httpConnection.getResponseCode();
               // Log.e("UpdateAPP", "0 Inside doHttpDownload  " );
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                int code = c.getResponseCode();
                // Log.e("UpdateAPP", "1 Inside doHttpDownload  " + code);
                //c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();
                int file_size = c.getContentLength();
                onrr(file_size);
                Log.e("UpdateAPP", "11 Inside doHttpDownload  " + code);
                String PATH = arg0[1];
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "update.apk");

                if(outputFile.exists()){
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();
                Log.e("UpdateAPP", "2 Inside doHttpDownload:Connection OK  " );
                byte[] buffer = new byte[1024];
                int len1 = 0;

                while ((len1 = is.read(buffer)) != -1 && FlSizeDowned<=file_size) {
                    fos.write(buffer, 0, len1);
                    publishProgress(FlSizeDowned);
                    FlSizeDowned= FlSizeDowned+len1;
                    Log.d("DemoApp", "dsfsd" +FlSizeDowned+" of total file_size "+file_size );
                }
                fos.close();
                is.close();
                c.disconnect();
                Log.e("UpdateAPP", "3 Inside doHttpDownload:Downloaded  ");

            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error111! " + e.getMessage());
            }
            return null;
        }
        protected void onProgressUpdate(Integer... values) {

            fincnt= values[0]+1;
            pb.setProgress(FlSizeDowned);
            tv.setText("Downloading..." + fincnt + ">>" + totrec);
            Log.d("DemoApp", "filefound in Connection" + filefound);
            msg1.setText("Wait!! donot press any key!!");
            dwldbtn.setVisibility(Button.GONE);
        }
        protected void onPostExecute(Void result) {
            // async task finished
            try {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setDataAndType(Uri.fromFile(new File(AppUtils.strDownloadedUPDATEAPKPath + "update.apk")), "application/vnd.android.package-archive");
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                startActivity(intent1);
            }catch(Exception e){
                Log.e("UpdateAPP", "4 Inside doHttpDownload: updated  "+e);
            }
            tv.setText("Download Successful");
            Log.d("DemoApp", "fincnt " + fincnt);
            Log.d("DemoApp","totrec "+totrec);
        }
    }
}