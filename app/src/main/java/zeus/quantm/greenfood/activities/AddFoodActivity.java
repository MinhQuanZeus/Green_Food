package zeus.quantm.greenfood.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import zeus.quantm.greenfood.BuildConfig;
import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.events.ResetEditTextListener;
import zeus.quantm.greenfood.network.services.GetDistanceService;
import zeus.quantm.greenfood.network.RetrofitFactory;
import zeus.quantm.greenfood.network.models.distance.MainObject;
import zeus.quantm.greenfood.utils.LibrarySupportManager;
import zeus.quantm.greenfood.utils.MoneyTextWatcher;
import zeus.quantm.greenfood.utils.ValidateInput;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddFoodActivity extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;

    private static final String FIREBASE_POST_TITLE = "title";
    private static final String FIREBASE_POST_DESCRIPTION = "description";
    private static final String FIREBASE_POST_PRICE = "price";
    private static final String FIREBASE_POST_QUANTITY = "quantity";
    private static final String FIREBASE_POST_ADDRESS = "address";
    private static final String FIREBASE_POST_TIME = "time";
    private static final String FIREBASE_POST_IMAGE = "image";
    private static final String FIREBASE_POST_USERID = "userID";
    private static final String FIREBASE_POST_TOKEN_ID = "tokenID";
    private static final String INVALID_FOOD_NAME_MESSAGE = "Bạn phải tên món ăn";
    private static final String INVALID_FOOD_DESCRIPTION_MESSAGE = "Bạn phải nhập thông tin về món ăn";
    private static final String INVALID_FOOD_PRICE_MESSAGE = "Bạn phải nhập giá";
    private static final String INVALID_FOOD_QUANTITY_MESSAGE = "Bạn phải nhập số lượng";
    private static final String INVALID_FOOD_ADDRESS_MESSAGE = "Bạn phải nhập địa chỉ ";
    private static final String INVALID_FOOD_PHOTO = "Bạn phải chọn ảnh của món ăn";
    private static final String WRONG_ADDRESS_MESSAGE = "Địa chỉ bạn vừa nhập không hợp lệ";
    private static final String BLANK_INPUT_EXCEPTION = "Bạn hãy nhập đầy đủ thông tin";
    private static final String TAG = AddFoodActivity.class.toString();
    private static final String BLANK_INPUT_IMAGE = "Bạn phải chọn ảnh của món ăn";
    private static final String NO_INTERNET_CONNECTION = "Kiểm tra lại kết nối mạng";

    public static String DEBUG_TAG = "AppCompatActivity";
    private static final int THUMBNAIL_SIZE = 150;
    private static SimpleDateFormat mDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final int REQUEST_CODE = 1;
    private String mImagePathToBeAttached;
    private Bitmap mImageToBeAttached;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private String checkAddressAPI;
    private boolean checkValidate;
    private boolean isPosted;
    private FirebaseAuth firebaseAuth;

    private ImageView ivAddImage;
    private ImageView ivAddFoodImage;
    private ImageView ivDeleteImage;
    private RelativeLayout rlEmptyImage;
    private RelativeLayout rlImage;
    private RelativeLayout btnPost;
    private EditText etFoodName;
    private EditText etDescription;
    private EditText etFoodPrice;
    private EditText etFoodQuantity;
    private EditText etFoodAddress;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        firebaseAuth = FirebaseAuth.getInstance();
        try {
            setDefaultVariable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setupUI();
        ivAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePhotoIntent();
            }
        });
        ivDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrentPhoto();
            }
        });
        startPost(new ResetEditTextListener() {
            @Override
            public void onResultCallback(boolean result) {
                resetInputEditText(result);
            }
        });
    }

    private void setDefaultVariable() throws IOException {
        String userID = LibrarySupportManager.getInstance().randomUserTokenID();
        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("post");
        String key = mDatabaseReference.push().getKey();
        mStorageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(key);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void startPost(final ResetEditTextListener resetEditTextListener) {
        checkValidate = validateInput();
        isPosted = false;
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check connection
                if(!isNetworkAvailable()){
                    Toast.makeText(AddFoodActivity.this, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG,"Is connected : " + isNetworkAvailable());
                //new Post
                if (checkValidate && checkNullInput() && checkNullImage()) {
                    checkCorrectInputAddress(etFoodAddress.getText().toString(), new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            checkAddressAPI = result;
                            if(checkAddressAPI.equals("OK")) {
                                final DatabaseReference newPost = mDatabaseReference.push();
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.firebase_token), 0);
                                String tokenID = sharedPreferences.getString(getResources().getString(R.string.reg_token_ID), "");
                                if(!tokenID.equals("")){
                                    newPost.child(FIREBASE_POST_TOKEN_ID).setValue(tokenID);
                                }
                                newPost.child(FIREBASE_POST_TITLE).setValue(etFoodName.getText().toString());
                                newPost.child(FIREBASE_POST_DESCRIPTION).setValue(etDescription.getText().toString());
                                newPost.child(FIREBASE_POST_PRICE).setValue(Long.parseLong(etFoodPrice.getText().toString().replaceAll("[₫,. ]", "")));
                                newPost.child(FIREBASE_POST_QUANTITY).setValue(Long.parseLong(etFoodQuantity.getText().toString()));
                                newPost.child(FIREBASE_POST_ADDRESS).setValue(etFoodAddress.getText().toString());
                                newPost.child(FIREBASE_POST_TIME).setValue(LibrarySupportManager.currentDateTime());
                                newPost.child(FIREBASE_POST_USERID).setValue(firebaseAuth.getCurrentUser().getUid());

                                //post image
                              //  ivAddFoodImage.setDrawingCacheEnabled(true);
                              //  ivAddFoodImage.buildDrawingCache();
                              //  Bitmap bitmap = ivAddFoodImage.getDrawingCache();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                mImageToBeAttached.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                final byte[] data = baos.toByteArray();
                                UploadTask uploadTask = mStorageReference.putBytes(data);
                                progressDialog = new ProgressDialog(AddFoodActivity.this);
                                progressDialog.setTitle("Post Food");
                                progressDialog.show();
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        Toast.makeText(AddFoodActivity.this, "Vui lòng kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        isPosted = false;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                        newPost.child(FIREBASE_POST_IMAGE).setValue(downloadUrl.toString());
                                        progressDialog.dismiss();
                                        Toast.makeText(AddFoodActivity.this, "Đăng bài thành công", Toast.LENGTH_SHORT).show();
                                        isPosted = true;
                                        resetEditTextListener.onResultCallback(isPosted);
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {
                                        //Processdialog
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        progressDialog.setMessage(((int)progress) + "% Uploaded...");
                                        isPosted = false;
                                    }
                                });
                            } else {
                                Toast.makeText(AddFoodActivity.this, WRONG_ADDRESS_MESSAGE, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(AddFoodActivity.this, BLANK_INPUT_EXCEPTION, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetInputEditText(boolean isPosted) {
        if(isPosted) {
            etFoodName.setText("");
            etFoodName.setError(null);
            etDescription.setText("");
            etDescription.setError(null);
            etFoodQuantity.setText(null);
            etFoodQuantity.setError(null);
            etFoodPrice.setText("₫ 0");
            etFoodPrice.setError(null);
            etFoodAddress.setText("");
            etFoodAddress.setError(null);
            deleteCurrentPhoto();
            updateUI();
        }

    }

    private boolean checkNullImage() {
        if(ivAddFoodImage.getDrawable() == null){
            Toast.makeText(AddFoodActivity.this, BLANK_INPUT_IMAGE, Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    private boolean checkNullInput() {
        return (!etFoodName.getText().toString().isEmpty()
                && !etDescription.getText().toString().isEmpty()
                && !etFoodPrice.getText().toString().isEmpty()
                && !etFoodQuantity.getText().toString().isEmpty()
                && !etFoodAddress.getText().toString().isEmpty());
    }

    //validate
    private boolean validateInput() {
        ValidateInput.getInstance();
        etFoodName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etFoodName.getText().toString(), INVALID_FOOD_NAME_MESSAGE)
                        .equals("")) {
                    etFoodName.setError(INVALID_FOOD_NAME_MESSAGE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etFoodName.getText().toString(), INVALID_FOOD_NAME_MESSAGE)
                        .equals("")) {
                    etFoodName.setError(INVALID_FOOD_NAME_MESSAGE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etDescription.getText().toString(), INVALID_FOOD_DESCRIPTION_MESSAGE)
                        .equals("")) {
                    etDescription.setError(INVALID_FOOD_DESCRIPTION_MESSAGE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etDescription.getText().toString(), INVALID_FOOD_DESCRIPTION_MESSAGE)
                        .equals("")) {
                    etDescription.setError(INVALID_FOOD_DESCRIPTION_MESSAGE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etFoodQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etFoodQuantity.getText().toString(), INVALID_FOOD_QUANTITY_MESSAGE)
                        .equals("")) {
                    etFoodQuantity.setError(INVALID_FOOD_QUANTITY_MESSAGE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etFoodQuantity.getText().toString(), INVALID_FOOD_QUANTITY_MESSAGE)
                        .equals("")) {
                    etFoodQuantity.setError(INVALID_FOOD_QUANTITY_MESSAGE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etFoodAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etFoodAddress.getText().toString(), INVALID_FOOD_ADDRESS_MESSAGE)
                        .equals("")) {
                    etFoodAddress.setError(INVALID_FOOD_ADDRESS_MESSAGE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!ValidateInput
                        .checkStringLength(etFoodAddress.getText().toString(), INVALID_FOOD_ADDRESS_MESSAGE)
                        .equals("")) {
                    etFoodAddress.setError(INVALID_FOOD_ADDRESS_MESSAGE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return (etFoodName.getError() == null
                && etDescription.getError() == null
                && etFoodPrice.getError() == null
                && etFoodQuantity.getError() == null
                && etFoodAddress.getError() == null
                );
    }

    private void checkCorrectInputAddress(String inputAddress, final VolleyCallback callback){
        RetrofitFactory.getInstance("https://developers.google.com/maps/")
                .createService(GetDistanceService.class)
                .getDistance("imperial", "Ha Noi", inputAddress, "AIzaSyBs7LWRp7vadlOd79qe_c01BTwRw_KF5VE")
                .enqueue(new Callback<MainObject>() {
                    @Override
                    public void onResponse(Call<MainObject> call, Response<MainObject> response) {
                        if(response.code() == 200){
                            String statusGoogleAPI = response.body()
                                    .getRows().get(0)
                                    .getElements().get(0)
                                    .getStatus();
                            checkAddressAPI = statusGoogleAPI;
                            callback.onSuccess(checkAddressAPI);
                        }
                    }

                    @Override
                    public void onFailure(Call<MainObject> call, Throwable t) {
                    }
                });
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    private void setupUI() {
        ivAddImage = (ImageView) findViewById(R.id.iv_add_image);
        ivAddFoodImage = (ImageView) findViewById(R.id.iv_add_food);
        ivDeleteImage = (ImageView) findViewById(R.id.iv_delete_image);
        rlEmptyImage = (RelativeLayout) findViewById(R.id.rl_layout_empty_image);
        rlImage = (RelativeLayout) findViewById(R.id.rl_layout_image);
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        toolbar.setTitle(R.string.post_title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //post information
        btnPost = (RelativeLayout) findViewById(R.id.btn_post);
        etFoodName = (EditText) findViewById(R.id.et_full_name);
        etDescription = (EditText) findViewById(R.id.et_email);
        etFoodPrice = (EditText) findViewById(R.id.et_password);
        etFoodQuantity = (EditText) findViewById(R.id.et_confirm_password);
        etFoodAddress = (EditText) findViewById(R.id.et_address);
        etFoodPrice.addTextChangedListener(new MoneyTextWatcher(etFoodPrice));
        etFoodPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
        updateUI();
    }

    public void updateUI() {
        if (mImageToBeAttached != null) {
            rlEmptyImage.setVisibility(View.INVISIBLE);
            rlImage.setVisibility(View.VISIBLE);
            ivAddFoodImage.setImageBitmap(mImageToBeAttached);
        } else {
            rlEmptyImage.setVisibility(View.VISIBLE);
            rlImage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        final int size = THUMBNAIL_SIZE;
        Bitmap thumbnail = null;
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                try {
                    ExifInterface exif = new ExifInterface(mImagePathToBeAttached);
                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                    int rotationAngle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                    mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, options.outWidth, options.outHeight, matrix, true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                // Delete the temporary image file
                file.delete();
            }
            //   mImagePathToBeAttached = null;
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {
                Uri uri = data.getData();
                ContentResolver resolver = getContentResolver();
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(resolver, uri);
            } catch (IOException e) {
                Log.e(AddFoodActivity.DEBUG_TAG, "Cannot get a selected photo from the gallery.", e);
            }
        }
        updateUI();
    }

    private void dispatchTakePhotoIntent() {
        if (checkPermission()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    Log.e(AddFoodActivity.DEBUG_TAG, "Cannot create a temp image file", e);
                }

                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(AddFoodActivity.this,
                            BuildConfig.APPLICATION_ID + ".provider", photoFile));

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            requestPermission();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Add_Food-" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;
        }
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AddFoodActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddFoodActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AddFoodActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}
