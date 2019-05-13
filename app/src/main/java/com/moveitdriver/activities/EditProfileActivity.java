package com.moveitdriver.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private CircleImageView circleImageView;
    private FloatingActionButton floatingActionButton;
    private EditText driverFirstName, driverLastName, driverContact, driverEmail;
    private Button saveButton;
    private Uri profileImageUri;

    private UpdateUserModelResponse updateUserModelResponse;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    private static final int REQUEST_READ_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        restHandler = new RestHandler(this, this);

        initFields();
    }

    private void initFields() {
        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        circleImageView = findViewById(R.id.driver_profile_image_edit_activity);
        driverFirstName = findViewById(R.id.first_name_edit_profile);
        driverLastName = findViewById(R.id.last_name_edit_profile);
        driverContact = findViewById(R.id.driver_contact_edit_profile);
        driverEmail = findViewById(R.id.driver_email_edit_profile);

        driverFirstName.setText(SharedPrefManager.getInstance(this).getDriverFirstName());
        driverLastName.setText(SharedPrefManager.getInstance(this).getDriverLastName());
        driverContact.setText(SharedPrefManager.getInstance(this).getDriverContact());
        driverEmail.setText(SharedPrefManager.getInstance(this).getDriverEmail());

        driverEmail.setClickable(false);
        driverContact.setClickable(false);

        floatingActionButton = findViewById(R.id.edit_profile_btn_edit_profile);
        floatingActionButton.setOnClickListener(this);

        saveButton = findViewById(R.id.save_btn_edit_activity);
        saveButton.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.edit_profile_btn_edit_profile:
                onPickImage(1);
                break;
            case R.id.save_btn_edit_activity:
                updateUserDetail();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateUserDetail() {
        pDialog.show();

        MultipartBody.Part profileImage = null; // Vehicle Front Image

        if (profileImageUri != null)
            profileImage = prepareFilePart("profile_image", profileImageUri);


        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).updateUserProfile
                        (RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                                RequestBody.create(MediaType.parse("text/plain"), driverFirstName.getText().toString()),
                                RequestBody.create(MediaType.parse("text/plain"), driverLastName.getText().toString()),
                                profileImage),
                "updateUserProfile");
    }

    private int imageCode;

    private void onPickImage(int code) {
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
                    circleImageView.setImageBitmap(fBitmap);
                    profileImageUri = getImageUri(this, fBitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("updateUserProfile")) {
                updateUserModelResponse = (UpdateUserModelResponse) response.body();
                if (updateUserModelResponse.getMessage().equalsIgnoreCase("UpdatedSuccessfully")) {
                    pDialog.dismiss();

                    String vIdStr = SharedPrefManager.getInstance(this).getVehicleId();
                    String vTypeIdStr = SharedPrefManager.getInstance(this).getVehicleTypeId();

                    SharedPrefManager.getInstance(this).driverLogin(updateUserModelResponse.getData().getId(), updateUserModelResponse.getData().getFirstname(), updateUserModelResponse.getData().getLastname(), updateUserModelResponse.getData().getEmail(), updateUserModelResponse.getData().getProfileImage(), updateUserModelResponse.getData().getContact(), vIdStr, vTypeIdStr, "Complete");
                    Toast.makeText(this, "Profile Saved Successfully ", Toast.LENGTH_LONG).show();
                    finish();
                }
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