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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.addVehicleDetailResponse.AddVehicleModelResponse;
import com.moveitdriver.models.carMakesResponse.Datum;
import com.moveitdriver.models.carMakesResponse.MakesResponse;
import com.moveitdriver.models.carModelsResponse.ModelResponse;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.moveitdriver.R.layout.custom_spinner_item;

public class AddVehicleActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private Spinner vehicleMakeSpinner, vehicleModelSpinner, vehicleYearSpinner, vehicleColorSpinner;
    private ImageView vehicleFrontImageView, vehicleBackImageView, vehicleLeftImageView, vehicleRightImageView;
    private Button addVehicleBtn;

    private String selectedCarMake, selectedCarModel, selectedCarYear, selectedCarColor;
    private Uri carFrontImageUri, carBackImageUri, carLeftImageUri, carRightImageUri;

    private RestHandler restHandler;
    private ProgressDialog pDialog;

    private MakesResponse makesResponse;
    private ModelResponse modelResponse;
    private AddVehicleModelResponse addVehicleModelResponse;
    private List<Datum> makesArrayList;
    private List<com.moveitdriver.models.carModelsResponse.Datum> modelsArrayList;

    private ArrayAdapter<String> arrayAdapter;

    private GetAllVehicleModelResponse getAllVehicleModelResponse;

    private String nextStep;
    private static final int REQUEST_READ_STORAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getAllVehicleModelResponse = (GetAllVehicleModelResponse) getIntent().getSerializableExtra("vehicleDetail");

        if(getAllVehicleModelResponse.getUser().getNextStep().equals("Complete"))
            nextStep = "Complete";
        else if(getAllVehicleModelResponse.getUser().getNextStep().equals("2"))
            nextStep = "3";
        else
            nextStep = getAllVehicleModelResponse.getUser().getNextStep();

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        // Declare Progress Dialog...

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        vehicleMakeSpinner = findViewById(R.id.vehicle_make_spinner_add_vehicle_activity);
        vehicleModelSpinner = findViewById(R.id.vehicle_model_spinner_add_vehicle_activity);
        vehicleYearSpinner = findViewById(R.id.vehicle_year_spinner_add_vehicle_activity);
        vehicleColorSpinner = findViewById(R.id.vehicle_color_spinner_add_vehicle_activity);
        vehicleFrontImageView = findViewById(R.id.vehicle_front_pic_add_vehicle_activity);
        vehicleBackImageView = findViewById(R.id.vehicle_back_pic_add_vehicle_activity);
        vehicleLeftImageView = findViewById(R.id.vehicle_left_pic_add_vehicle_activity);
        vehicleRightImageView = findViewById(R.id.vehicle_right_pic_add_vehicle_activity);
        addVehicleBtn = findViewById(R.id.add_vehicle_detail_btn_add_vehicle_activity);

        // Button Listener...
        vehicleFrontImageView.setOnClickListener(this);
        vehicleBackImageView.setOnClickListener(this);
        vehicleLeftImageView.setOnClickListener(this);
        vehicleRightImageView.setOnClickListener(this);
        addVehicleBtn.setOnClickListener(this);

        // Load Makes And Models Of Vehicles...
        getCarMakes();

        // Declare Spinners Here...
        /* ***************   Vehicle Years Spinner Adapter  *******************/
        ArrayAdapter<CharSequence> carYearAdapter = ArrayAdapter.createFromResource(this,
                R.array.vehicle_years_array, android.R.layout.simple_spinner_item);
        carYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleYearSpinner.setAdapter(carYearAdapter);

        /* ***************   Vehicle Colors Spinner Adapter  *******************/
        ArrayAdapter<CharSequence> carColorAdapter = ArrayAdapter.createFromResource(this,
                R.array.vehicle_colors_array, android.R.layout.simple_spinner_item);
        carColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleColorSpinner.setAdapter(carColorAdapter);
    }

    // -------------------------       Override Functions         --------------------------------//

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_vehicle_detail_btn_add_vehicle_activity:
                selectedCarYear = vehicleYearSpinner.getSelectedItem().toString();
                selectedCarColor = vehicleColorSpinner.getSelectedItem().toString();

                if(getIntent().getStringExtra("path").equals("new")){
                    addVehicleDetail();
                } else if(getIntent().getStringExtra("path").equals("old")) {
                    updateVehicleDetail();
                }
                break;
            case R.id.vehicle_front_pic_add_vehicle_activity:
                onPickImage(1);
                break;
            case R.id.vehicle_back_pic_add_vehicle_activity:
                onPickImage(2);
                break;
            case R.id.vehicle_left_pic_add_vehicle_activity:
                onPickImage(3);
                break;
            case R.id.vehicle_right_pic_add_vehicle_activity:
                onPickImage(4);
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (method.equalsIgnoreCase("getMakes")) {
                makesResponse = (MakesResponse) response.body();
                if (makesResponse.getMessage().equalsIgnoreCase("Makes Fetched Successfully")) {
                    pDialog.dismiss();

                    List<String> model = new ArrayList<>();
                    makesArrayList = new ArrayList<>();
                    makesArrayList.addAll(makesResponse.getData());
                    for (int i = 0; i < makesArrayList.size(); i++) {
                        model.add(makesArrayList.get(i).getMake());
                    }

                    arrayAdapter = new ArrayAdapter<String>(this, custom_spinner_item, model);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicleMakeSpinner.setAdapter(arrayAdapter);

                    vehicleMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedCarMake = makesArrayList.get(i).getId();
                            getCarModels(selectedCarMake);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            } else if (method.equalsIgnoreCase("getModels")) {
                modelResponse = (ModelResponse) response.body();
                if (modelResponse.getMessage().equalsIgnoreCase("Models Fetched Successfully")) {
                    pDialog.dismiss();

                    List<String> model = new ArrayList<>();
                    modelsArrayList = new ArrayList<>();
                    modelsArrayList.addAll(modelResponse.getData());
                    for (int i = 0; i < modelsArrayList.size(); i++) {
                        model.add(modelsArrayList.get(i).getTitle());
                    }
                    arrayAdapter = new ArrayAdapter<String>(this, custom_spinner_item, model);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vehicleModelSpinner.setAdapter(arrayAdapter);

                    vehicleModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedCarModel = modelsArrayList.get(i).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            } else if (method.equalsIgnoreCase("addVehicleDetail")) {
                addVehicleModelResponse = (AddVehicleModelResponse) response.body();
                pDialog.dismiss();

                Constants.NEXT_STEP = nextStep;

                String idStr = SharedPrefManager.getInstance(this).getDriverId();
                String fNameStr = SharedPrefManager.getInstance(this).getDriverFirstName();
                String lNameStr = SharedPrefManager.getInstance(this).getDriverLastName();
                String emailStr = SharedPrefManager.getInstance(this).getDriverEmail();
                String picStr = SharedPrefManager.getInstance(this).getDriverPic();
                String vIdStr = addVehicleModelResponse.getVehicleInfo().getVehicleId();
                String vTypeIdStr = addVehicleModelResponse.getVehicleInfo().getVehicleTypeid();
                String contactStr = SharedPrefManager.getInstance(this).getDriverContact();

                SharedPrefManager.getInstance(this).driverLogin(idStr, fNameStr, lNameStr, emailStr, picStr, contactStr, vIdStr, vTypeIdStr, nextStep);

                Toast.makeText(this, "" + addVehicleModelResponse.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else if (method.equalsIgnoreCase("editVehicleDetail")) {
                addVehicleModelResponse = (AddVehicleModelResponse) response.body();
                pDialog.dismiss();

                Constants.NEXT_STEP = nextStep;

                String idStr = SharedPrefManager.getInstance(this).getDriverId();
                String fNameStr = SharedPrefManager.getInstance(this).getDriverFirstName();
                String lNameStr = SharedPrefManager.getInstance(this).getDriverLastName();
                String emailStr = SharedPrefManager.getInstance(this).getDriverEmail();
                String picStr = SharedPrefManager.getInstance(this).getDriverPic();
                String vIdStr = addVehicleModelResponse.getVehicleInfo().getVehicleId();
                String vTypeIdStr = addVehicleModelResponse.getVehicleInfo().getVehicleTypeid();
                String contactStr = SharedPrefManager.getInstance(this).getDriverContact();

                SharedPrefManager.getInstance(this).driverLogin(idStr, fNameStr, lNameStr, emailStr, picStr, contactStr, vIdStr, vTypeIdStr, nextStep);

                Toast.makeText(this, "" + addVehicleModelResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ---------------------------------    Override Functions   --------------------------------- //

    private void getCarMakes() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getMakes(), "getMakes");
    }

    private void getCarModels(String makeId) {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getModels(makeId), "getModels");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addVehicleDetail() {
        pDialog.show();

        MultipartBody.Part fImage = null; // Vehicle Front Image
        MultipartBody.Part bImage = null; // Vehicle Back Image
        MultipartBody.Part lImage = null; // Vehicle Left Image
        MultipartBody.Part rImage = null; // Vehicle Right Image

        if (carFrontImageUri != null)
            fImage = prepareFilePart("carFrontPic", carFrontImageUri);

        if (carBackImageUri != null)
            bImage = prepareFilePart("carBackPic", carBackImageUri);

        if (carLeftImageUri != null)
            lImage = prepareFilePart("carLeftPic", carLeftImageUri);

        if (carRightImageUri != null)
            rImage = prepareFilePart("carRightPic", carRightImageUri);

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).addVehicleDetail(
                RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarMake),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarModel),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarYear),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarColor),
                RequestBody.create(MediaType.parse("text/plain"), nextStep),
                fImage,
                bImage,
                lImage,
                rImage),
                "addVehicleDetail");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateVehicleDetail() {
        pDialog.show();

        MultipartBody.Part fImage = null; // Vehicle Front Image
        MultipartBody.Part bImage = null; // Vehicle Back Image
        MultipartBody.Part lImage = null; // Vehicle Left Image
        MultipartBody.Part rImage = null; // Vehicle Right Image

        if (carFrontImageUri != null)
            fImage = prepareFilePart("carFrontPic", carFrontImageUri);

        if (carBackImageUri != null)
            bImage = prepareFilePart("carBackPic", carBackImageUri);

        if (carLeftImageUri != null)
            lImage = prepareFilePart("carLeftPic", carLeftImageUri);

        if (carRightImageUri != null)
            rImage = prepareFilePart("carRightPic", carRightImageUri);

        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).editVehicleDetail(
                RequestBody.create(MediaType.parse("text/plain"), getAllVehicleModelResponse.getData().get(0).getId()),
                RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getDriverId()),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarMake),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarModel),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarYear),
                RequestBody.create(MediaType.parse("text/plain"), selectedCarColor),
                RequestBody.create(MediaType.parse("text/plain"), SharedPrefManager.getInstance(this).getNextStep()),
                fImage,
                bImage,
                lImage,
                rImage),
                "editVehicleDetail");
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
                    vehicleFrontImageView.setImageBitmap(fBitmap);
                    carFrontImageUri = getImageUri(this, fBitmap);
                }
                break;
            case 2:
                Bitmap bBitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (bBitmap != null) {
                    vehicleBackImageView.setImageBitmap(bBitmap);
                    carBackImageUri = getImageUri(this, bBitmap);
                }
                break;
            case 3:
                Bitmap lBitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (lBitmap != null) {
                    vehicleLeftImageView.setImageBitmap(lBitmap);
                    carLeftImageUri = getImageUri(this, lBitmap);
                }
                break;
            case 4:
                Bitmap rBitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (rBitmap != null) {
                    vehicleRightImageView.setImageBitmap(rBitmap);
                    carRightImageUri = getImageUri(this, rBitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
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