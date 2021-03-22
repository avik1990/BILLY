package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.bollingModel.BillingResponseModel;
import com.tpcodl.billingreading.uploadingrequestModel.HeaderresDetails;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Button btn_download, btn_downloadInstallwise;
    private Button btn_download_local;
    private ProgressBar upload_progress;
    private TextView tv_upload_percentage;
    private DialogUtils dUtils;
    private ActivityUtils utils;
    private DialogUtils dutils;
    private TextView tv_total_record;
    private TextView tv_total_record_title;
    private ImageView iv_back;
    private TextView tv_title;
    private int count = 0;
    private List<HeaderresDetails> hlist;
    private DatabaseHelper mDBHelper;
    private RequestModel model;
    String from = "";
    DatabaseHelper helpar;
    String USER_TYPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mContext = this;
        helpar = new DatabaseHelper(mContext);
        btn_download = findViewById(R.id.btn_download);
        btn_downloadInstallwise = findViewById(R.id.btn_downloadInstallwise);

      /*  if (!PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            btn_downloadInstallwise.setVisibility(View.VISIBLE);
        }*/
        btn_download_local = findViewById(R.id.btn_download_local);
        upload_progress = findViewById(R.id.upload_progress);
        tv_upload_percentage = findViewById(R.id.tv_upload_percentage);
        tv_total_record = findViewById(R.id.tv_total_record);
        tv_total_record_title = findViewById(R.id.tv_total_record_title);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);

        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            tv_title.setText("DOWNLOAD ACTIVITY -SBM");
        } else {
            tv_title.setText("DOWNLOAD ACTIVITY -NON SBM");
        }

        btn_download.setOnClickListener(this);
        btn_downloadInstallwise.setOnClickListener(this);
        dutils = new DialogUtils(this);
        dUtils = new DialogUtils(this);
        utils = ActivityUtils.getInstance();
        model = new RequestModel();
        mDBHelper = new DatabaseHelper(this);
        from = getIntent().getStringExtra(getString(R.string.from));

        try {
            mDBHelper.createDataBase();
            Log.e("DatabaseHelper", "Created");
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        iv_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download: {
                if (helpar.checkSentFlag()) {
                    downloadBillingdata("2");
                } else {
                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Download Disabled!!");
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage("Please Upload billed data before downloading");
                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
            }
            case R.id.btn_downloadInstallwise: {
                if (helpar.checkSentFlag()) {

                    if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                        downloadBillingdata("3");
                    } else {
                        downloadBillingdata("1");
                    }
                } else {
                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Download Disabled!!");
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage("Please Upload billed data before downloading");
                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
            }
        }
    }

    private void downloadBillingdata(String flag) {
        ArrayList<String> notInsertedDataHeader = new ArrayList<>();
        ArrayList<String> notInsertedDataChild = new ArrayList<>();
        HashMap<String, ArrayList<String>> totalData = new HashMap<>();
        ArrayList<String> insertedData = new ArrayList<>();

        if (AppUtils.isInternetAvailable(this)) {
            dUtils.showDialog("Downloading", "Please wait..");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            JsonObject object = new JsonObject();
            object.addProperty("type", Constant.appType);
            object.addProperty("user_id", utils.getUserID());
            if (flag.equalsIgnoreCase("1")) {
                object.addProperty("search_flag", Constant.downLoadType);
            }
            if (flag.equalsIgnoreCase("3")) {
                object.addProperty("search_flag", Constant.consymerTypeSBM);
            }

            if (from.equalsIgnoreCase("SBM")) {
                object.addProperty("consumer_type", Constant.consymerTypeSBM);
            } else {
                object.addProperty("consumer_type", Constant.consymerTypeNONSBM);
            }

            Call<BillingResponseModel> call = service.getBillingdata(utils.getAuthToken(), object);
            call.enqueue(new Callback<BillingResponseModel>() {
                @Override
                public void onResponse(Call<BillingResponseModel> call, Response<BillingResponseModel> response) {
                    BillingResponseModel object = response.body();
                    dUtils.dismissDialog();
                    try {
                        if (object.statusCode == 200) {
                            mDBHelper.MoveDataToTemp();
                            new ExportDatabaseFileTask().execute();
                            if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                                if (flag.equalsIgnoreCase("3")) {
                                    USER_TYPE = "S";
                                    helpar.deleteDataHeaderInstallationWiseSBM(USER_TYPE);
                                    helpar.deleteDataChildInstallationWiseSBM(USER_TYPE);
                                } else {
                                    USER_TYPE = "S";
                                    helpar.deleteDataChild1(USER_TYPE);
                                    helpar.deleteDataHeader1(USER_TYPE);
                                }

                            } else {
                                USER_TYPE = "X";
                                helpar.deleteDataHeaderInstallationWise(USER_TYPE);
                                helpar.deleteDataChildInstallationWise(USER_TYPE);
                            }
                            helpar.inserBillingHeader(object);
                            dUtils.dismissDialog();
                        } else {
                            dutils.showErrorDialog("Error", object.message);
                        }
                    } catch (Exception e) {
                        dutils.showErrorDialog("Error", "Something went wrong!!");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BillingResponseModel> call, Throwable t) {
                    dUtils.dismissDialog();
                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }


            });
        } else {
            dUtils.dismissDialog();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
        // private final ProgressDialog dialog = new ProgressDialog(mContext);

        // can use UI thread here
        protected void onPreExecute() {
            // this.dialog.setMessage("Exporting database...");
            //  this.dialog.show();
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
           /* if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }*/

            if (success) {
                //Toast.makeText(mContext, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(mContext, "Export failed", Toast.LENGTH_SHORT).show();
            }
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
    }

}