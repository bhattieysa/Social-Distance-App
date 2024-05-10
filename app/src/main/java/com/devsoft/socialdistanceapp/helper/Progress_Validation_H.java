package com.devsoft.socialdistanceapp.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;

public class Progress_Validation_H {
    public ProgressDialog progressDialog;
    public Activity activity;
    public boolean flag = false;
    public String encoded="";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    Dialog choose_dialog;
    Dialog dialog;
    public static final int PICK_IMAGE = 1;
    public Progress_Validation_H(Activity activity ) {
        this.activity = activity;
    }



    public void show_Dialog(){
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void hide_Dialog(){
        if (progressDialog!=null)
            if (progressDialog.isShowing())
                progressDialog.hide();
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)activity. getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean editText_Validation(EditText editText){
        if (editText.getText().toString().length()<=0){
            editText.setError("Please fill this FILED.");
            Toast.makeText(activity, editText.getHint()+" درج نہیں ہے ", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    public boolean editText_Validation_cnic(EditText editText){
        if (editText.getText().toString().length()!=13){
            editText.setError("Please enter complete CNIC.");
            Toast.makeText(activity, "شناختی کارڈ نمبر غلط ہے", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
    public boolean image_Validation(File img_capture){
        if (img_capture==null){
            Toast.makeText(activity, "Please capture image", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
    public boolean spinner_Validation(Spinner spinner){
        if (spinner.getSelectedItemPosition()==0){
            spinner.performClick();
            return false;
        }
        else
            return true;
    }
}
