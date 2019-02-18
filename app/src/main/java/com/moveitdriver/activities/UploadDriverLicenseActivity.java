package com.moveitdriver.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.getAllVehicleResponse.Datum;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.models.updateUserDetailResponse.UpdateUserModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.FileUtils;
import com.moveitdriver.utils.ImagePicker;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UploadDriverLicenseActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText driverLicenceNumberEditText, driverLicenceStateEditText, validVehicleTypeLicenceEditText;
    private EditText driverLicenceExpiresEditText, validDriverLicenceEditText;
    private Calendar myCalendar;
    private ImageView licenceFrontImageView, licenceBackImageView;
    private Button submitBtn;

    private Uri licenceFrontImageUri = null, licenceBackImageUri = null;
    private String driverLicenceNumberStr, driverLicenceExpiresStr, driverLicenceStateStr, validDriverLicenceStr, validVehicleTypeLicenceStr;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    private UpdateUserModelResponse object;

    private static final int REQUEST_READ_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_driver_license);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        DatePickerIns();

        driverLicenceNumberEditText = findViewById(R.id.driver_licence_number_upload_document_activity);
        driverLicenceStateEditText = findViewById(R.id.driver_licence_state_upload_document_activity);
        validVehicleTypeLicenceEditText = findViewById(R.id.driver_licence_type_upload_document_activity);
        licenceFrontImageView = findViewById(R.id.licence_front_pic_add_upload_driver_licence_activity);
        licenceBackImageView = findViewById(R.id.licence_back_pic_add_upload_driver_licence_activity);
        submitBtn = findViewById(R.id.submit_btn_upload_driver_licence_activity);

        licenceFrontImageView.setOnClickListener(this);
        licenceBackImageView.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    // ------------------------------ Override Functions  --------------------------------------- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_license_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            startActivity(new Intent(this, DriverLicenseInfoActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.licence_front_pic_add_upload_driver_licence_activity:
                onPickImage(1);
                break;
            case R.id.licence_back_pic_add_upload_driver_licence_activity:
                onPickImage(2);
                break;
            case R.id.submit_btn_upload_driver_licence_activity:
                addDriverLicence();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("addDriverLicenceDetail")) {
                pDialog.dismiss();
                object = (UpdateUserModelResponse) response.body();

                Toast.makeText(this, object.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (response != null && (response.code() == 403 || response.code() == 500)) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            try {
                ResponseBody body = response.errorBody();
                JSONObject jObj = new JSONObject(body.string());
                if (jObj.optString("status").equals("403"))
                    Constants.showAlert(this, jObj.optString("message"));
                else
                    Constants.showAlert(this, jObj.optString("message"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                Constants.showAlert(this, "Oops! API returned invalid response. Try again later.");
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        Constants.showAlert(this, errorMessage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedToImagePicking(imageCode);
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                Bitmap fBitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (fBitmap != null) {
                    licenceFrontImageView.setImageBitmap(fBitmap);
                    licenceFrontImageUri = getImageUri(this, fBitmap);
                }
                break;
            case 2:
                Bitmap bBitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (bBitmap != null) {
                    licenceBackImageView.setImageBitmap(bBitmap);
                    licenceBackImageUri = getImageUri(this, bBitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ---------------------------------   FUNCTIONS   -----------------------------------------  //

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addDriverLicence() {
        fieldInitialize(); // Initialize the input to string variables

        if (!fieldValidation()) {
            Toast.makeText(this, "Error! Please Enter Required Information", Toast.LENGTH_LONG).show();
        } else {
            if (Constants.checkInternetConnection(this)) {
                addDriverLicenceDetail();
            } else {
                final AlertDialog.Builder adb = new AlertDialog.Builder(UploadDriverLicenseActivity.this);
                adb.setTitle("Info");
                adb.setMessage("Internet not available, Cross check your internet connectivity and try again");
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setCancelable(false);

                adb.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    }
                });

                adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                adb.show();
            }
        }
    }

    public void fieldInitialize() {
        driverLicenceNumberStr = driverLicenceNumberEditText.getText().toString().trim();
        driverLicenceExpiresStr = driverLicenceExpiresEditText.getText().toString();
        driverLicenceStateStr = driverLicenceStateEditText.getText().toString().trim();
        validDriverLicenceStr = validDriverLicenceEditText.getText().toString();
        validVehicleTypeLicenceStr = validVehicleTypeLicenceEditText.getText().toString().trim();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (driverLicenceNumberStr.isEmpty()) {
            driverLicenceNumberEditText.setError("Please enter valid data");
            valid = false;
        } else if (driverLicenceExpiresStr.equals("-- SELECT DATE --")) {
            driverLicenceExpiresEditText.setError("Please select valid date");
            valid = false;
        } else if (driverLicenceStateStr.isEmpty()) {
            driverLicenceStateEditText.setError("Please enter valid data");
            valid = false;
        } else if (validDriverLicenceStr.equals("-- SELECT DATE --")) {
            validDriverLicenceEditText.setError("Please select valid date");
            valid = false;
        } else if (validVehicleTypeLicenceStr.isEmpty()) {
            validVehicleTypeLicenceEditText.setError("Please enter valid data");
            valid = false;
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addDriverLicenceDetail() {
        pDialog.show();

        MultipartBody.Part fImage = null; // Licence Front Image
        MultipartBody.Part bImage = null; // Licence Back Image

        if (licenceFrontImageUri != null)
            fImage = prepareFilePart("licenceFrontPic", licenceFrontImageUri);

        if (licenceBackImageUri != null)
            bImage = prepareFilePart("licenceBackPic", licenceBackImageUri);

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).updateUserDriverLicence(RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                RequestBody.create(MediaType.parse("text/plain"), driverLicenceNumberStr),
                RequestBody.create(MediaType.parse("text/plain"), driverLicenceExpiresStr),
                RequestBody.create(MediaType.parse("text/plain"), driverLicenceStateStr),
                RequestBody.create(MediaType.parse("text/plain"), validVehicleTypeLicenceStr),
                RequestBody.create(MediaType.parse("text/plain"), validDriverLicenceStr),
                fImage,
                bImage),
                "addDriverLicenceDetail");
    }

    private int imageCode;

    public void onPickImage(int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                proceedToImagePicking(code);
            } else {
                imageCode = code;
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
            }
        } else {
            proceedToImagePicking(code);
        }
    }

    private void proceedToImagePicking(int code) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, code);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = FileUtils.getFile(this, fileUri);
        MediaType type = MediaType.parse(getMimeType(fileUri));
        RequestBody requestFile = RequestBody.create(type, file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // DatePicker Functions...

    public void DatePickerIns() {
        String myFormat = "yyyy-mm-dd"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar = Calendar.getInstance();

        driverLicenceExpiresEditText = (EditText) findViewById(R.id.driver_licence_expires_date_upload_document_activity);
        validDriverLicenceEditText = (EditText) findViewById(R.id.valid_driver_licence_date_upload_document_activity);

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                driverLicenceExpiresEditText.setText("");
                driverLicenceExpiresEditText.setText(sdf.format(myCalendar.getTime()));
            }
        };

        driverLicenceExpiresEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UploadDriverLicenseActivity.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                validDriverLicenceEditText.setText("");
                validDriverLicenceEditText.setText(sdf.format(myCalendar.getTime()));
            }
        };

        validDriverLicenceEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(UploadDriverLicenseActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}