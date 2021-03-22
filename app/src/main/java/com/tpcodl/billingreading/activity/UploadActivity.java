package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.database.DatabaseKeys;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.UploadDataResponseModel;
import com.tpcodl.billingreading.reponseModel.UploadSbmCheck;
import com.tpcodl.billingreading.uploadingrequestModel.ChildReqDetails;
import com.tpcodl.billingreading.uploadingrequestModel.HeaderresDetails;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.utils.UploadUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ImageView iv_back;
    private TextView tv_title;
    private Button btn_upload_server;
    private Button btn_upload_local;
    private DialogUtils dUtils;
    private ActivityUtils utils;
    private DialogUtils dutils;
    private RequestModel model;
    private DatabaseHelper mDBHelper;
    private List<HeaderresDetails> hlist;
    private List<String> installationList = new ArrayList<>();
    String from = "";
    List<NSBMData> list = new ArrayList<>();
    UploadUtils uUtils;
    String varCond = "";
    DatabaseHelper db;
    private ProgressDialog mProgressDialogUpload;
    String FLAG = "0";
    int count = 0;
    Button btn_MassUploaad, btn_chk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mContext = this;
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        btn_chk = findViewById(R.id.btn_chk);
        btn_chk.setOnClickListener(this);
        uUtils = new UploadUtils(mContext);
        tv_title.setText(mContext.getResources().getString(R.string.upload_activity));
        btn_upload_server = findViewById(R.id.btn_upload_server);
        btn_MassUploaad = findViewById(R.id.btn_MassUploaad);
        //intent.putExtra("FROM", "NONSBM");
        db = new DatabaseHelper(mContext);
        from = getIntent().getStringExtra("FROM");
        mProgressDialogUpload = new ProgressDialog(mContext);
        mProgressDialogUpload.setMessage("Uploading");
        mProgressDialogUpload.setCancelable(false);
        mProgressDialogUpload.setCanceledOnTouchOutside(false);
        dUtils = new DialogUtils(this);

        utils = ActivityUtils.getInstance();
        dutils = new DialogUtils(this);
        model = new RequestModel();
        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.createDataBase();
            // Log.e("DatabaseHelper", "Created");
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        btn_upload_server.setOnClickListener(this);
        btn_MassUploaad.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload_server: {
                FLAG = "1";
                /*String query = "delete from TBL_UPLOAD_RAW_DATA";
                db.updateSlab(query);*/
                new AsyncTaskUpload().execute();
                break;
            }
            case R.id.btn_MassUploaad: {
                FLAG = "1";
                String query = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG='0'";
                db.updateSlab(query);
                new AsyncTaskUpload().execute();
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.btn_chk: {
                if (AppUtils.isInternetAvailable(mContext)) {
                    mProgressDialogUpload = new ProgressDialog(mContext);
                    mProgressDialogUpload.setMessage("Verifying...");
                    mProgressDialogUpload.setCancelable(false);
                    mProgressDialogUpload.setCanceledOnTouchOutside(false);
                    mProgressDialogUpload.show();
                    uploadDataCheck();
                } else {
                    UtilsClass.showToastShort(mContext, "No Internet Connection!!");
                }
            }
        }
    }

    private class AsyncTaskUpload extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialogUpload.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            list.clear();
            list = mDBHelper.getNBSMRInstallationOnly(FLAG);
            //Log.e("COUNTERBILLED", "" + list.size());
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (list.size() > 0) {
                if (AppUtils.isInternetAvailable(mContext)) {
                    new AsyncUploadSingleTaskExample().execute();
                    mProgressDialogUpload.dismiss();
                } else {
                    mProgressDialogUpload.dismiss();
                    UtilsClass.showToastShort(mContext, "No Internet Connection!!");
                }
            } else {
                mProgressDialogUpload.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("No Data to Upload!!");
                builder.setTitle("Message");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    private class AsyncUploadSingleTaskExample extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progressDialog1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(mContext);
            progressDialog1.setCancelable(false);
            progressDialog1.setMessage("Uploading data, please wait...");
            progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog1.setProgress(0);
            progressDialog1.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            progressDialog1.setMax(list.size());
            try {
                count = 0;
                for (int i = 0; i < list.size(); i++) {
                    varCond = " where INSTALLATION='" + list.get(i).getINSTALLATION() + "'";
                    Log.e("VARCONTD", varCond);
                    //Log.e("UploadCount", list.get(i).getINSTALLATION() + " " + i);
                    utils.setSerchCondition(varCond);
                    uUtils.getHeaderdetails(varCond);
                    model = uUtils.getChilddetails(varCond);
                    uploadData(varCond);
                    publishProgress(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog1.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog1.dismiss();
            UtilsClass.showToastLong(mContext, "Records Uploaded Successfully");
        }
    }

    public void uploadData(String Condition) {
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);

       /* Gson g = new Gson();
        g.toJson(model);
        Log.e("model", g.toJson(model));
        db.insertNote(g.toJson(model), "");*/


        Call<UploadDataResponseModel> call = service.uploadData(utils.getAuthToken(), model);
        call.enqueue(new Callback<UploadDataResponseModel>() {
            @Override
            public void onResponse(Call<UploadDataResponseModel> call, Response<UploadDataResponseModel> response) {
                UploadDataResponseModel object = response.body();

                System.out.println("status code==="+object.statusCode);

                try {
                    if (object.statusCode == 200) {
                        db.updatesendFlag1(Condition);
                        PreferenceHandler.setEnableBilling(mContext, "");
                        try {
                            utils.setAppVersion(object.softwareVersionNo.toString().trim());
                        } catch (Exception e) {
                        }
                    } else if (object.statusCode == 410) {
                        db.updatesendFlag1(Condition);
                        PreferenceHandler.setEnableBilling(mContext, "");
                        try {
                            utils.setAppVersion(object.softwareVersionNo.toString().trim());
                        } catch (Exception e) {
                        }
                    } else {
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<UploadDataResponseModel> call, Throwable t) {
                //dUtils.dismissDialog();
                t.printStackTrace();
                Log.i("TAG", t.getMessage());
                // Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void uploadDataCheck() {
        JsonObject object = new JsonObject();
        object.addProperty("userId", utils.getUserID());

        if (from.equalsIgnoreCase("SBM")) {
            object.addProperty("dbType", Constant.consymerTypeSBM);
        } else {
            object.addProperty("dbType", Constant.consymerTypeNONSBM);
        }

        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<UploadSbmCheck> call = service.uploadCheckData(utils.getAuthToken(), object);
        call.enqueue(new Callback<UploadSbmCheck>() {

            @Override
            public void onResponse(Call<UploadSbmCheck> call, Response<UploadSbmCheck> response) {
                UploadSbmCheck response1 = response.body();
                try {
                    if (response1.getStatusCode().equalsIgnoreCase("200")) {
                        for (int i = 0; i < response1.getHeaderData().size(); i++) {
                            checkUploadedDataInstallation(response1.getHeaderData().get(i).getInstallation());
                        }
                    }
                    mProgressDialogUpload.dismiss();
                    UtilsClass.showToastShort(mContext, "Data Successfully Checked!!");

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<UploadSbmCheck> call, Throwable t) {
                //dUtils.dismissDialog();
                t.printStackTrace();
                mProgressDialogUpload.dismiss();
                Log.i("TAG", t.getMessage());
                // Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUploadedDataInstallation(String installation) {
        if (!db.isExistsHeader(installation)) {
            String query = "UPDATE TBL_SPOTBILL_HEADER_DETAILS set SENT_FLAG='0' WHERE READ_FLAG='1' and INSTALLATION='" + installation + "'";
            Log.e("query", query);
            db.insertIntoTEMPTARIFF(query);
        }
    }

    /*private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(context);

        // can use UI thread here
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            File dbFile = new File(Environment.getDataDirectory() + "/data/com.tpcodl.billingreading/databases/SMRD.db");
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyy_hh:mm:ss");
            String format = s.format(new Date());

            String localDBName = "SMRD_" + utils.getUserID() + "_" + format + ".db";
            File file = new File(exportDir, localDBName.trim());

            try {
                file.createNewFile();
                this.copyFile(dbFile, file);
                return true;
            } catch (IOException e) {
                Log.e("mypck", e.getMessage(), e);
                return false;
            }
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
        }

        void copyFile(File src, File dst) throws IOException {
            FileChannel inChannel = new FileInputStream(src).getChannel();
            FileChannel outChannel = new FileOutputStream(dst).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
        }
    }*/
}


