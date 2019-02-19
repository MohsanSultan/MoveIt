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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.addVehicleDetailResponse.AddVehicleModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.FileUtils;
import com.moveitdriver.utils.ImagePicker;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class VehicleInsuranceActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText insuranceCompanyNameEditText, insuranceTypeEditText, inspectionReportEditText;
    private TextView insuranceCertificateExpiresTextView, insuranceEffectiveTextView;
    private Calendar myCalendar;
    private ImageView insuranceCertificateImageView;
    private Button submitBtn;

    private Uri insuranceCertificateImageUri = null;
    private String insuranceCompanyNameStr, insuranceTypeStr, inspectionReportStr, insuranceCertificateExpiresStr, insuranceEffectiveStr;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    private AddVehicleModelResponse object;

    private static final int REQUEST_READ_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_insurance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        DatePickerIns();

        insuranceCompanyNameEditText = findViewById(R.id.compnay_name_vehicle_insurance_activity);
        insuranceTypeEditText = findViewById(R.id.insurance_type_vehicle_insurance_activity);
        inspectionReportEditText = findViewById(R.id.inspection_report_vehicle_insurance_activity);
        insuranceCertificateImageView = findViewById(R.id.certificate_image_vehicle_insurance_activity);
        submitBtn = findViewById(R.id.submit_btn_vehicle_insurance_activity);

        insuranceCertificateImageView.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    // ------------------------------ Override Functions  --------------------------------------- //

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.certificate_image_vehicle_insurance_activity:
                onPickImage(1);
                break;
            case R.id.submit_btn_vehicle_insurance_activity:
                addVehicleInsurance();
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("addDriverLicenceDetail")) {
                pDialog.dismiss();
                object = (AddVehicleModelResponse) response.body();

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
                    insuranceCertificateImageView.setImageBitmap(bitmap);
                    insuranceCertificateImageUri = getImageUri(this, bitmap);
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
    public void addVehicleInsurance() {
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
        insuranceCompanyNameStr = insuranceCompanyNameEditText.getText().toString();
        insuranceTypeStr = insuranceTypeEditText.getText().toString();
        inspectionReportStr = inspectionReportEditText.getText().toString();
        insuranceCertificateExpiresStr = insuranceCertificateExpiresTextView.getText().toString();
        insuranceEffectiveStr = insuranceEffectiveTextView.getText().toString();
    }

    public boolean fieldValidation() {
        boolean valid = true;

        if (insuranceCompanyNameStr.isEmpty()) {
            insuranceCompanyNameEditText.setError("Please enter valid data");
            valid = false;
        } else if (insuranceTypeStr.isEmpty()) {
            insuranceTypeEditText.setError("Please enter valid data");
            valid = false;
        } else if (inspectionReportStr.isEmpty()) {
            inspectionReportEditText.setError("Please enter valid data");
            valid = false;
        } else if (insuranceCertificateExpiresStr.equals("-- SELECT DATE --")) {
            insuranceCertificateExpiresTextView.setError("Please select valid date");
            valid = false;
        } else if (insuranceEffectiveStr.equals("-- SELECT DATE --")) {
            insuranceEffectiveTextView.setError("Please select valid date");
            valid = false;
        }

        return valid;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addVehicleRegistrationDetail() {
        pDialog.show();

        MultipartBody.Part cerImage = null; // Licence Front Image

        if (insuranceCertificateImageUri != null)
            cerImage = prepareFilePart("vehicleInsuranceCertificatePic", insuranceCertificateImageUri);

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).editInsuranceVehicleDetail(
                RequestBody.create(MediaType.parse("text/plain"), "5c6188061f332025b741171d"),
                RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                RequestBody.create(MediaType.parse("text/plain"), insuranceCompanyNameStr),
                RequestBody.create(MediaType.parse("text/plain"), insuranceTypeStr),
                RequestBody.create(MediaType.parse("text/plain"), inspectionReportStr),
                RequestBody.create(MediaType.parse("text/plain"), insuranceCertificateExpiresStr),
                RequestBody.create(MediaType.parse("text/plain"), insuranceEffectiveStr),
                cerImage),
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
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar = Calendar.getInstance();

        insuranceCertificateExpiresTextView = findViewById(R.id.certificate_expires_vehicle_insurance_activity);
        insuranceEffectiveTextView = findViewById(R.id.effective_date_vehicle_insurance_activity);

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                insuranceCertificateExpiresTextView.setText("");
                insuranceCertificateExpiresTextView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        insuranceCertificateExpiresTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(VehicleInsuranceActivity.this, date1, myCalendar
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

                insuranceEffectiveTextView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        insuranceEffectiveTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(VehicleInsuranceActivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}