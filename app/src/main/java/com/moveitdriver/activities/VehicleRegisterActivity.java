package com.moveitdriver.activities;

import android.Manifest;
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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.addVehicleDetailResponse.AddVehicleModelResponse;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class VehicleRegisterActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText vehicleRegisterNumberEditText;
    private Button submitBtn;
    private ImageView vehicleRegImageView;

    private Uri vehicleRegImageUri = null;
    private String registrationNumberStr;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    private AddVehicleModelResponse object;
    private GetAllVehicleModelResponse getAllVehicleModelResponse;

    private String nextStep;
    private static final int REQUEST_READ_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getAllVehicleModelResponse = (GetAllVehicleModelResponse) getIntent().getSerializableExtra("vehicleDetail");

        if(getAllVehicleModelResponse.getUser().getNextStep().equals("Complete"))
            nextStep = "Complete";
        else if(getAllVehicleModelResponse.getUser().getNextStep().equals("4"))
            nextStep = "5";
        else
            nextStep = getAllVehicleModelResponse.getUser().getNextStep();

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        vehicleRegisterNumberEditText = findViewById(R.id.registration_number_vehicle_register_activity);
        vehicleRegImageView = findViewById(R.id.registration_document_image_vehicle_register_activity);
        submitBtn = findViewById(R.id.submit_btn_register_vehicle_activity);

        vehicleRegImageView.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        try {
            if (!getAllVehicleModelResponse.getData().get(0).getRegistrationNumber().equals("")) {
                vehicleRegisterNumberEditText.setText(getAllVehicleModelResponse.getData().get(0).getRegistrationNumber());
            }
        } catch (Exception e) {

        }
    }

    // ------------------------------ Override Functions  --------------------------------------- //

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration_document_image_vehicle_register_activity:
                onPickImage(1);
                break;
            case R.id.submit_btn_register_vehicle_activity:
                vehicleRegistrationDetail();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("addVehicleRegistrationDetail")) {
                pDialog.dismiss();
                object = (AddVehicleModelResponse) response.body();

                Constants.NEXT_STEP = nextStep;

                String idStr = SharedPrefManager.getInstance(this).getDriverId();
                String fNameStr = SharedPrefManager.getInstance(this).getDriverFirstName();
                String lNameStr = SharedPrefManager.getInstance(this).getDriverLastName();
                String emailStr = SharedPrefManager.getInstance(this).getDriverEmail();
                String picStr = SharedPrefManager.getInstance(this).getDriverPic();
                String vIdStr = SharedPrefManager.getInstance(this).getVehicleId();
                String vTypeIdStr = SharedPrefManager.getInstance(this).getVehicleTypeId();
                String contactStr = SharedPrefManager.getInstance(this).getDriverContact();

                SharedPrefManager.getInstance(this).driverLogin(idStr, fNameStr, lNameStr, emailStr, picStr, contactStr, vIdStr, vTypeIdStr, nextStep);

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
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (bitmap != null) {
                    vehicleRegImageView.setImageBitmap(bitmap);
                    vehicleRegImageUri = getImageUri(this, bitmap);
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
    public void vehicleRegistrationDetail() {
        fieldInitialize(); // Initialize the input to string variables

        if (!fieldValidation()) {
            Toast.makeText(this, "Error! Please Enter Required Information", Toast.LENGTH_LONG).show();
        } else {
            if (Constants.checkInternetConnection(this)) {
                addVehicleRegistrationDetail();
            } else {
                final AlertDialog.Builder adb = new AlertDialog.Builder(this);
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
        registrationNumberStr = vehicleRegisterNumberEditText.getText().toString().trim();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (registrationNumberStr.isEmpty()) {
            vehicleRegisterNumberEditText.setError("Please enter valid data");
            valid = false;
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addVehicleRegistrationDetail() {
        pDialog.show();

        MultipartBody.Part regImage = null; // Vehicle Register Image

        if (vehicleRegImageUri != null)
            regImage = prepareFilePart("registrationImage", vehicleRegImageUri);

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).editRegistrationVehicleDetail(
                RequestBody.create(MediaType.parse("text/plain"), getAllVehicleModelResponse.getData().get(0).getId()),
                RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                RequestBody.create(MediaType.parse("text/plain"), registrationNumberStr),
                RequestBody.create(MediaType.parse("text/plain"), nextStep),
                regImage),
                "addVehicleRegistrationDetail");
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
}