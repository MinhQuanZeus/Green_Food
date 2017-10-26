package zeus.quantm.greenfood.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import zeus.quantm.greenfood.BuildConfig;
import zeus.quantm.greenfood.R;
import zeus.quantm.greenfood.activities.AddFoodActivity;
import zeus.quantm.greenfood.activities.MainActivity;
import zeus.quantm.greenfood.utils.ImageUtils;
import zeus.quantm.greenfood.utils.LibrarySupportManager;
import zeus.quantm.greenfood.utils.NetworkConnectionSupport;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    public static final String TAG = "SignUpFragment";
    public static final int RequestPermissionCode = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;

    private FirebaseAuth mAuth;

    private String mImagePathToBeAttached;
    private Bitmap mImageToBeAttached;

    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    private String phoneNumber;

    private RelativeLayout rlEmptyAvatar;
    private RelativeLayout rlAvatar;
    private ImageView ivAvatar;
    private ImageView ivAvatarBlur;
    private ImageView ivAddAvatar;
    private EditText edName;
    private EditText edEmail;
    private EditText edPassword;
    private EditText edConfPassword;
    private EditText edAddress;
    private EditText edPhone;
    private Button btnSend;



    public SignUpFragment() {
        // Required empty public constructor
    }

    public SignUpFragment setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        setupUI(view);

        return view;
    }


    private void setupUI(View view) {
        rlAvatar =(RelativeLayout) view.findViewById(R.id.rl_layout_image);
        rlEmptyAvatar =(RelativeLayout) view.findViewById(R.id.rl_layout_empty_image);
        ivAvatar = (ImageView)view.findViewById(R.id.iv_avatar);
        ivAvatarBlur = (ImageView)view.findViewById(R.id.iv_blur);
        ivAddAvatar = (ImageView)view.findViewById(R.id.iv_add_image);
        edName = (EditText)view.findViewById(R.id.et_full_name);
        edEmail = (EditText)view.findViewById(R.id.et_email);
        edPassword = (EditText)view.findViewById(R.id.et_password);
        edConfPassword = (EditText)view.findViewById(R.id.et_confirm_password);
        edAddress = (EditText)view.findViewById(R.id.et_address);
        edPhone = (EditText)view.findViewById(R.id.et_phone);
        btnSend = (Button)view.findViewById(R.id.btn_send);
        edPhone.setText(phoneNumber);
        edPhone.setEnabled(false);

        ivAddAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAttachImageDialog();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUP();
            }
        });

    }

    private void signUP(){
        if(!validate()){
            return;
        }else{
            if (!NetworkConnectionSupport.isConnected()){
                Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối internet của bạn",
                        Toast.LENGTH_SHORT).show();
                btnSend.setEnabled(true);
                return;
            }
            final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Đang đăng ký...");
            progressDialog.show();
            String email = edEmail.getText().toString();
            String password = edPassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                creatUser();
                                progressDialog.dismiss();
                                FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
                                Toast.makeText(getActivity(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Email đã được đăng ký, vui lòng sử dụng mail khác",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            // ...
                        }
                    });
        }
    }

    private void creatUser(){
        try {
            String userID = LibrarySupportManager.getInstance().randomUserTokenID();
            mDatabaseReference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users").child(mAuth.getCurrentUser().getUid());
            String key = mDatabaseReference.push().getKey();
            mStorageReference = FirebaseStorage.getInstance()
                    .getReference()
                    .child(mAuth.getCurrentUser().getUid());
        }catch (Exception ex){

        }
       // final DatabaseReference newPost = mDatabaseReference.push();
        mDatabaseReference.child("name").setValue(edName.getText().toString());
        mDatabaseReference.child("address").setValue(edAddress.getText().toString());
        mDatabaseReference.child("phone").setValue(edPhone.getText().toString());
        mDatabaseReference.child("rate").setValue("");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mImageToBeAttached.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();
        UploadTask uploadTask = mStorageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Vui lòng kiểm tra kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mDatabaseReference.child("avatar").setValue(downloadUrl.toString());
               // Toast.makeText(getActivity(), "Đăng bài thành công", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(final UploadTask.TaskSnapshot taskSnapshot) {
                //Processdialog
                //double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            }
        });

    }

    public boolean validate() {
        boolean valid = true;

        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        String confPassword = edConfPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Email không đúng định dạng");
            valid = false;
        } else {
            edEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            edPassword.setError("Mật khẩu ít nhất 4 ký tự");
            valid = false;
        } else {
            edPassword.setError(null);
        }

        if(!confPassword.equals(password)){
            edConfPassword.setError("Không khớp với mật khẩu bạn nhập");
            valid = false;
        }

        return valid;
    }


    public void updateUI() {
        if (mImageToBeAttached != null) {
            rlEmptyAvatar.setVisibility(View.INVISIBLE);
            rlAvatar.setVisibility(View.VISIBLE);
            ivAvatar.setImageBitmap(mImageToBeAttached);
            ivAvatarBlur.setImageBitmap(mImageToBeAttached);
        } else {
            rlEmptyAvatar.setVisibility(View.VISIBLE);
            rlAvatar.setVisibility(View.INVISIBLE);
        }
    }

    //avatar
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
//            if (mCurrentTaskToAttachImage != null)
//                mCurrentTaskToAttachImage = null;
            return;
        }

       // final int size = THUMBNAIL_SIZE;
        Bitmap thumbnail = null;
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                try{
                    ExifInterface exif = new ExifInterface(mImagePathToBeAttached);
                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer.parseInt(orientString) :  ExifInterface.ORIENTATION_NORMAL;
                    int rotationAngle = 0;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                    mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, options.outWidth, options.outHeight, matrix, true);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

//                if (mCurrentTaskToAttachImage == null) {
//                    thumbnail = ThumbnailUtils.extractThumbnail(mImageToBeAttached, size, size);
//                }

                // Delete the temporary image file
                file.delete();
            }
            mImagePathToBeAttached = null;
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {

                Uri uri = data.getData();
                ContentResolver resolver = getActivity().getContentResolver();
                int rotationAngle = getOrientation(getActivity(),uri);
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(resolver, uri);
                Matrix matrix = new Matrix();
                matrix.setRotate(rotationAngle, (float) mImageToBeAttached.getWidth() / 2, (float) mImageToBeAttached.getHeight() / 2);
                mImageToBeAttached = Bitmap.createBitmap(mImageToBeAttached, 0, 0, mImageToBeAttached.getWidth(), mImageToBeAttached.getHeight(), matrix, true);
//                if (mCurrentTaskToAttachImage == null) {
//                    AssetFileDescriptor asset = resolver.openAssetFileDescriptor(uri, "r");
//                    thumbnail = ImageUtil.thumbnailFromDescriptor(asset.getFileDescriptor(), size, size);
//                }
            } catch (IOException e) {
                Log.e(TAG, "Cannot get a selected photo from the gallery.", e);
            }
        }

//        if (mImageToBeAttached != null) {
//            if (mCurrentTaskToAttachImage != null) {
//                attachImage(mCurrentTaskToAttachImage, mImageToBeAttached);
//                mImageToBeAttached = null;
//            }
//        }

//        if (thumbnail != null) {
//            ImageView imageView = (ImageView) findViewById(R.id.image);
//            imageView.setImageBitmap(thumbnail);
//        }

        // Ensure resetting the task to attach an image
        updateUI();
    }

    public int getOrientation(Context context, Uri photoUri) {
    /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }



    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "Cannot create a temp image file", e);
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile));
                if (checkPermission()) {
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } else {
                    requestPermission();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "TODO_LITE-" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;
        }
//        ViewGroup view = (ViewGroup) findViewById(R.id.create_task);
//        ImageView imageView = (ImageView) view.findViewById(R.id.image);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
    }

    private void displayAttachImageDialog() {
        CharSequence[] items;
        if (mImageToBeAttached != null)
            items = new CharSequence[]{"Take photo", "Choose photo", "Delete photo"};
        else
            items = new CharSequence[]{"Take photo", "Choose photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Anh dai dien");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    dispatchTakePhotoIntent();
                } else if (item == 1) {
                    dispatchChoosePhotoIntent();
                } else {
                    deleteCurrentPhoto();
                }
            }
        });
        builder.show();
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
                        Toast.makeText(getActivity(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

}
