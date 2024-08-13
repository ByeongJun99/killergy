package com.inu.killergy.Analyze;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.BoundingPoly;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Vertex;
import com.google.gson.Gson;
import com.inu.killergy.CloudVision.PackageManagerUtils;
import com.inu.killergy.CloudVision.PermissionUtils;
import com.inu.killergy.DB.History;
import com.inu.killergy.DB.MainDB;
import com.inu.killergy.DB.Notification;
import com.inu.killergy.MainActivity;
import com.inu.killergy.R;

import com.inu.killergy.MyInfo.SharedPreferenceManger;
import com.inu.killergy.model.MyInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ComponentInfoFragment extends Fragment {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyA-rF6ShBHjrcAVKv0QZ9PczRQ1pAOaCPc";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private SharedPreferenceManger sm;
    private static final String TAG = ComponentInfoFragment.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private static ImageView mMainImage;

    private static TextView SodiumValueTextView;
    private static TextView CarbohydratesValueTextView;
    private static TextView CholesterolValueTextView;
    private static TextView ProteinValueTextView;
    private static TextView SugarsValueTextView;
    private static TextView KcalValueTextView;
    private static TextView FatValueTextView;
    private static TextView TransFatValueTextView;
    private static TextView SaturatedFatValueTextView;
    private static TextView CalciumValueTextView;
    private static Group allValueGroup;
    private static TextView allergyContainValueTextView;
    private static TextView allergyFactoryValueTextView;
    private static ImageView SodiumEdit;
    private static ImageView CarbohydratesEdit;
    private static ImageView CholesterolEdit;
    private static ImageView ProteinEdit;
    private static ImageView SugarsEdit;
    private static ImageView KcalEdit;
    private static ImageView FatEdit;
    private static ImageView TransFatEdit;
    private static ImageView SaturatedFatEdit;
    private static ImageView CalciumEdit;
    private static Button saveButton;
    private static FrameLayout allergyFrame;

    float mKCAL, mSODIUM, mCARBOHYDRATES, mSUGARS, mFAT, mTRANSFAT, mSATURATEDFAT, mCHOLESTEROL, mPROTEIN, mCALCIUM;
    private boolean isAllergenFood = false;

    private static Button startButton;

    private static String[] allergenFood = {"난류", "계란", "우유", "메밀", "땅콩", "대두", "밀", "고등어",
            "게", "새우", "돼지고기", "복숭아", "토마토", "아황산류", "호두",
            "닭고기", "쇠고기", "소고기", "오징어", "조개류"};

    MainDB database;
    History fooddata;
    Notification notificationData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SharedPreferenceManger(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_component_info,null);

        database = MainDB.getInstance(getContext());
        fooddata = new History();
        notificationData = new Notification();
        startButton = (Button)view.findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        mImageDetails = (TextView)view.findViewById(R.id.image_details);
        mMainImage = (ImageView)view.findViewById(R.id.main_image);
        mMainImage.setVisibility(View.GONE);

        SodiumValueTextView = (TextView)view.findViewById(R.id.SodiumValueTextView);
        CarbohydratesValueTextView = (TextView)view.findViewById(R.id.CarbohydratesValueTextView);
        CholesterolValueTextView = (TextView)view.findViewById(R.id.CholesterolValueTextView);
        ProteinValueTextView = (TextView)view.findViewById(R.id.ProteinValueTextView);
        SugarsValueTextView = (TextView)view.findViewById(R.id.SugarsValueTextView);
        KcalValueTextView = (TextView)view.findViewById(R.id.KcalValueTextView);
        FatValueTextView = (TextView)view.findViewById(R.id.FatValueTextView);
        TransFatValueTextView = (TextView)view.findViewById(R.id.TransFatValueTextView);
        SaturatedFatValueTextView = (TextView)view.findViewById(R.id.SaturatedFatValueTextView);
        CalciumValueTextView = (TextView)view.findViewById(R.id.CalciumValueTextView);

        allValueGroup = (Group)view.findViewById(R.id.allValueGroup);
        allergyFrame = (FrameLayout)view.findViewById(R.id.allergyFrame);

        allergyContainValueTextView = (TextView)view.findViewById(R.id.allergyContainValueTextView);
        allergyFactoryValueTextView = (TextView)view.findViewById(R.id.allergyFactoryValueTextView);

        SodiumEdit = (ImageView)view.findViewById(R.id.SodiumEdit);
        CarbohydratesEdit = (ImageView)view.findViewById(R.id.CarbohydratesEdit);
        CholesterolEdit = (ImageView)view.findViewById(R.id.CholesterolEdit);
        ProteinEdit = (ImageView)view.findViewById(R.id.ProteinEdit);
        SugarsEdit = (ImageView)view.findViewById(R.id.SugarsEdit);
        KcalEdit = (ImageView)view.findViewById(R.id.KcalEdit);
        FatEdit = (ImageView)view.findViewById(R.id.FatEdit);
        TransFatEdit = (ImageView)view.findViewById(R.id.TransFatEdit);
        SaturatedFatEdit = (ImageView)view.findViewById(R.id.SaturatedFatEdit);
        CalciumEdit = (ImageView)view.findViewById(R.id.CalciumEdit);

        saveButton = (Button)view.findViewById(R.id.saveButton);

        SodiumEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(SodiumValueTextView);
            }
        });

        CarbohydratesEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(CarbohydratesValueTextView);
            }
        });

        CholesterolEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(CholesterolValueTextView);
            }
        });

        ProteinEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(ProteinValueTextView);
            }
        });

        SugarsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(SugarsValueTextView);
            }
        });

        KcalEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(KcalValueTextView);
            }
        });

        FatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(FatValueTextView);
            }
        });

        TransFatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(TransFatValueTextView);
            }
        });

        SaturatedFatEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(SaturatedFatValueTextView);
            }
        });

        CalciumEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButtonClick(CalciumValueTextView);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ERROR 값이면 저장 못하도록
                if(!validationTextView("오류")){
                    Toast.makeText(getActivity(), "오류값을 수정해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //다이얼로그 띄우기
                //타이틀 필요
                View dialogView = getLayoutInflater().inflate(R.layout.fragment_component_info_productnamedialog, null);
                final EditText editText = (EditText)dialogView.findViewById(R.id.product_name_dialog_edit_text);
                editText.setHint("제품명을 입력하세요.");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(dialogView);
                builder.setPositiveButton("입력하기", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int pos)
                    {
                        //유효성 검사 필요
                        if(editText.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fooddata.productName = editText.getText().toString();
                        fooddata.setContent(mKCAL, mSODIUM, mCARBOHYDRATES, mSUGARS, mFAT, mTRANSFAT, mSATURATEDFAT, mCHOLESTEROL, mPROTEIN, mCALCIUM);
                        mMainImage.setVisibility(View.GONE);
                        startButton.setVisibility(View.VISIBLE);
                        allValueGroup.setVisibility(View.GONE);
                        allergyFrame.setVisibility(View.GONE);
                        mImageDetails.setText("분석을 원하는 식품의 사진을 찍거나 갤러리에서 선택하세요.");
                        fooddata.searchDate = new Date();
                        database.historyDao().insertHistory(fooddata)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                }, throwable -> {
                                    Log.e("Errs","Error getting history data", throwable);});

                        //주간 그래프 확인을 위한 Sample Data

//                        for(int i=2; i<3;i++){
//                            Calendar cal = Calendar.getInstance();
//                            cal.add(Calendar.DATE, -1*i);
//                            fooddata.searchDate = cal.getTime();
//                            Random random = new Random();
//                            float f = random.nextFloat();
//                            Log.d("Random", f+"");
//                            fooddata.setContent(mKCAL*f, mSODIUM*f, mCARBOHYDRATES*f,
//                                    mSUGARS*f, mFAT*f, mTRANSFAT*f, mSATURATEDFAT*f,
//                                    mCHOLESTEROL*f, mPROTEIN*f, mCALCIUM*f);
//                            database.historyDao().insertHistory(fooddata)
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(() -> {}, throwable -> {
//                                        Log.e("Errs", "Sample Error", throwable);});
//                        }
                    }
                });

                //취소 버튼
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int neg){}
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        return view;
    }

    private boolean validationTextView(String errorCode){
        if(KcalValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(SodiumValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(CarbohydratesValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(SugarsValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(FatValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(TransFatValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(SaturatedFatValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(CholesterolValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(ProteinValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else if(CalciumValueTextView.getText().toString().equals(errorCode)){
            return false;
        }else{
            return true;
        }

    }

    private void editButtonClick(TextView txtView){
        View dialogView = getLayoutInflater().inflate(R.layout.fragment_component_info_editdialog, null);
        final EditText editText = (EditText)dialogView.findViewById(R.id.editdialog_edit_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        builder.setPositiveButton("수정하기", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int pos)
            {
                //유효성 검사 필요
                if(editText.getText().toString().equals("")){
                    return;
                }
                txtView.setText(editText.getText());
                switch(txtView.getId()){
                    case R.id.KcalValueTextView:
                        mKCAL = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.SodiumValueTextView:
                        mSODIUM = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.CarbohydratesValueTextView:
                        mCARBOHYDRATES = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.SugarsValueTextView:
                        mSUGARS = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.FatValueTextView:
                        mFAT = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.TransFatValueTextView:
                        mTRANSFAT = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.SaturatedFatValueTextView:
                        mSATURATEDFAT = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.CholesterolValueTextView:
                        mCHOLESTEROL = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.ProteinValueTextView:
                        mPROTEIN = Float.parseFloat(editText.getText().toString());
                        break;
                    case R.id.CalciumValueTextView:
                        mCALCIUM = Float.parseFloat(editText.getText().toString());
                        break;
                }
            }});

        //취소 버튼
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int neg) {}
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void startGalleryChooser() {
//        if (PermissionUtils.requestPermission(getActivity(), GALLERY_PERMISSIONS_REQUEST,
//                Manifest.permission.READ_EXTERNAL_STORAGE))
        if(true)
        {
            Intent intent = new Intent();
            intent.setType("image/*");
            //MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri), 1200);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
        }
    }

    //전에꺼 우선으로
    public void startCamera() {
//        if (PermissionUtils.requestPermission(getActivity(), CAMERA_PERMISSIONS_REQUEST,
//                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Log.d("startCamera", "StartCamera after 1");
//            Uri photoUri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName() +
//                    ".provider", getCameraFile());
//            Log.d("startCamera", "StartCamera after 2");
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            Log.d("startCamera", "StartCamera after 3");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            Log.d("startCamera", "StartCamera after 4");
//            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
//            Log.d("startCamera", "StartCamera after 5");
//        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName() +
                ".provider", getCameraFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadLocalImage(data.getData());
            getActivity().getContentResolver().takePersistableUriPermission(data.getData(),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }else if (requestCode == CAMERA_IMAGE_REQUEST) {
            Uri photoUri = FileProvider.getUriForFile(getActivity(), getContext().getPackageName()
                    + ".provider",getCameraFile());
            uploadImage(photoUri);
        }
        else{}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            Log.d("testUploadImage", "uri : " + uri);
            try {
                String imagePath = "/storage/emulated/0/Pictures/temp.jpg";
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap bitmap = scaleBitmapDown(
                        MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), (uri)), 1200);
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        // 방향 정보가 없거나 0일 경우, 회전하지 않음
                        break;
                }
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                callCloudVision(rotatedBitmap);
                mMainImage.setImageBitmap(rotatedBitmap);
                fooddata.productImage = getBytesFromBitmap(bitmap);

            }catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(getActivity(), R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(getActivity(), R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    public void uploadLocalImage(Uri uri) {
        if (uri != null) {
            try {
                Bitmap bitmap = scaleBitmapDown(
                        MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), (uri)), 1200);
                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);
                fooddata.productImage = getBytesFromBitmap(bitmap);
            }catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(getActivity(), R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(getActivity(), R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);
        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String >() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getActivity().getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);
                                    String sig =
                                            PackageManagerUtils.getSignature(getActivity().getPackageManager(), packageName);
                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };
                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();
                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature textDetection = new Feature();
                            textDetection.setType("TEXT_DETECTION");
                            textDetection.setMaxResults(10);
                            add(textDetection);
                        }});
                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                Log.d("test", result);
                mImageDetails.setText("Complete");
                extractAllText(result);
                if(isAllergenFood) {
                    CallNotification();
                    isAllergenFood = false;
                }
                lineKeyword.forEach((key, value) -> {
                    Log.d("testLinekeyword", key + " : " + value);
                });
            }
        }.execute();
    }

    private void CallNotification(){
        MainActivity activity = (MainActivity) getActivity();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        String notificationText = allergyContainValueTextView.getText().toString()+"된 음식입니다.";
        notificationData.text = notificationText;
        notificationData.date = new Date();
        activity.createNotification("DEFAULT", 1, "Killergy", notificationText, intent);
        database.notificationDao().insertNotification(notificationData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                }, throwable -> {
                    Log.e("Errs","Error getting history data", throwable);});

    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }


    List<StringBlock> linearString;
    HashMap<String, String> lineKeyword = new HashMap<>();

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> extractedString = response.getResponses().get(0).getTextAnnotations();

/*        for (EntityAnnotation txt:extractedString) {
            if(txt != null){
                Log.d("txt",txt.getDescription());
                message += String.format(Locale.US,"%s",txt.getScore()+"\n");
            }
            else { message += "nothing";}
        }
 */
        linearString = new ArrayList<>();
        if(extractedString != null) {
            for (int i = 1; i < extractedString.size(); i++) {
                EntityAnnotation textAnnotation = extractedString.get(i);
                BoundingPoly boundingPoly = textAnnotation.getBoundingPoly();
                Log.d("Vertex", boundingPoly.getVertices().get(0) +""+ boundingPoly.getVertices().get(1) +""
                        +boundingPoly.getVertices().get(2) +""+ boundingPoly.getVertices().get(3) + textAnnotation.getDescription());
                StringBlock stringBlock = new StringBlock(boundingPoly.getVertices().get(0),
                        boundingPoly.getVertices().get(1), boundingPoly.getVertices().get(2),
                        boundingPoly.getVertices().get(3), textAnnotation.getDescription());

                linearString.add(stringBlock);

            }

        } else {

        }

        if (extractedString != null) {
            message = extractedString.get(0).getDescription();
        } else {
            message = "nothing";
        }
        lineKeyword.put(SODIUM,_KeyWordLineStringHorizontal(SODIUM));
        lineKeyword.put(CARBOHYDRATES,_KeyWordLineStringHorizontal(CARBOHYDRATES));
        lineKeyword.put(SUGARS,_KeyWordLineStringHorizontal(SUGARS));
        lineKeyword.put(FAT,_KeyWordLineStringHorizontal(FAT));
        lineKeyword.put(TRANSFAT,_KeyWordLineStringHorizontal(TRANSFAT));
        lineKeyword.put(SATURATEDFAT,_KeyWordLineStringHorizontal(SATURATEDFAT));
        lineKeyword.put(CHOLESTEROL,_KeyWordLineStringHorizontal(CHOLESTEROL));
        lineKeyword.put(PROTEIN,_KeyWordLineStringHorizontal(PROTEIN));
        lineKeyword.put(CALCIUM,_KeyWordLineStringHorizontal(CALCIUM));

        for (Map.Entry<String, String> entry : lineKeyword.entrySet()){
            String lines = entry.getValue();
            String key = entry.getKey();
            Log.d("horizonTest", key + " : " + lines);
        }

        boolean verticalFlag = false;
        int errorCount = 0;
        for(Map.Entry<String, String> entry : lineKeyword.entrySet()) {
            String value = entry.getValue();
            if(value.trim().equals("") || (value.matches("-999")))
                errorCount++;
            if(errorCount > 4){
                verticalFlag = true;
                break;
            }
        }

        Log.d("vertical",verticalFlag + " / " + errorCount);

        if(verticalFlag){
            //세로 방향 탐색
            Log.d("testOrientation", "Vertical");
            lineKeyword.put(SODIUM,_KeyWordLineStringVertical(SODIUM));
            lineKeyword.put(CARBOHYDRATES,_KeyWordLineStringVertical(CARBOHYDRATES));
            lineKeyword.put(SUGARS,_KeyWordLineStringVertical(SUGARS));
            lineKeyword.put(FAT,_KeyWordLineStringVertical(FAT));
            lineKeyword.put(TRANSFAT,_KeyWordLineStringVertical(TRANSFAT));
            lineKeyword.put(SATURATEDFAT,_KeyWordLineStringVertical(SATURATEDFAT));
            lineKeyword.put(CHOLESTEROL,_KeyWordLineStringVertical(CHOLESTEROL));
            lineKeyword.put(PROTEIN,_KeyWordLineStringVertical(PROTEIN));
            lineKeyword.put(CALCIUM,_KeyWordLineStringVertical(CALCIUM));
        }
        else{
            Log.d("testOrientation", "Horizontal");
        }

        return message;
    }


    //가로탐색
    //한 줄 씩 탐색 원본
    private String KeyWordLineString(String keyword){
        String lineText = "";
        int maxY = 0, minY = 0, maxX = 0;
        boolean fatFlag = false; //지방이 여러개 뽑혀서 하나만 뽑기 위한 flag
        for (int i = 0 ; i < linearString.size() ; i++){
            if(lineText != ""){
                if (linearString.get(i).getMiddleY() < maxY && linearString.get(i).getMiddleY() > minY){
                    Log.d("lineText",maxX +" "+linearString.get(i).getLeft()+" "+linearString.get(i).getText());
                    if (maxX >= linearString.get(i).getLeft()) {
                        lineText += linearString.get(i).getText();
                        maxX = linearString.get(i).getRight();
                    } else {
                        lineText += " " + linearString.get(i).getText();
                        maxX = linearString.get(i).getRight();
                    }
                }
            }
            if(!linearString.get(i).getText().contains(keyword)){
                keyword = selectKeyword(keyword,linearString.get(i).getText());
            }
            if(linearString.get(i).getText().contains(keyword)){
                if (!fatFlag && linearString.get(i).getText().equals(FAT)){
                    lineText += linearString.get(i).getText();
                    maxY = linearString.get(i).getTop();
                    minY = linearString.get(i).getBottom();
                    maxX = linearString.get(i).getRight();
                    fatFlag = true;
                }else{
                    lineText += linearString.get(i).getText();
                    maxY = linearString.get(i).getTop();
                    minY = linearString.get(i).getBottom();
                    maxX = linearString.get(i).getRight();
                }
            }
        }
        Log.d("lineText","Line : " + lineText);
        return lineText;
    }

    private String _KeyWordLineStringHorizontal(String keyword){
        //Log.d("Line2","Line2 called : "+keyword);
        String lineText = "";
        String oriKeyword = keyword;
        StringBlock keywordBox = new StringBlock();
        StringBlock lastBox = new StringBlock();
        boolean fatFlag = false; //지방이 여러개 뽑혀서 하나만 뽑기 위한 flag
        boolean isKeyword = false;
        int fatIndex;

        for(int i = 0; i < linearString.size(); i++){
            if(!linearString.get(i).getText().contains(keyword))
                keyword = selectKeyword(keyword,linearString.get(i).getText());
            if(linearString.get(i).getText().contains(keyword)){
                     isKeyword = true;
                if (linearString.get(i).getText().equals(FAT)){
                    if(!fatFlag){
                        keywordBox = linearString.get(i);
                        lastBox = linearString.get(i);
                        //lineText += linearString.get(i).getText();
                        lineText += " ";
                        fatFlag = true;
                        fatIndex = i;
                    }
                }else{
                    keywordBox = linearString.get(i);
                    lastBox = linearString.get(i);
                    //lineText += linearString.get(i).getText();
                    lineText += " ";
                }
                for(int j = 0; j < linearString.size(); j++){
                    if(lineText.equals(""))
                        break;
                    else{
                        if(isIntersectionH(keywordBox, linearString.get(j))){

                            //Log.d("Line2","Line2 append called");
                            //Log.d("testText", keyword + " : " + linearString.get(i).getText());
                            float dirX = lastBox.getMiddleRightX()-linearString.get(j).getMiddleLeftX();
                            float dirY = lastBox.getMiddleRightY()-linearString.get(j).getMiddleLeftY();
                            if(Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2)) < 1){
                                lineText += linearString.get(j).getText();
                                lastBox = linearString.get(j);
                            }
                            else{
                                lineText += " "+linearString.get(j).getText();
                                lastBox = linearString.get(j);
                            }
                        }
                    }
                }
                //젖산칼슘 때문에 lastValidate 여기다 해야 할 듯
                Log.d("middleTest", keyword + " : " + lineText);
                Log.d("validateTest", keyword + " : " + lastValidate(oriKeyword, lineText));
                if(lastValidate(oriKeyword, lineText))
                    return NO_CONTAIN;
                if(lineText.contains(keyword))
                    lineText = lineText.substring(lineText.indexOf(keyword));
                Log.d("middleTest", keyword + " : " + lineText);
                String result = filterValue(oriKeyword,lineText);
                if(!result.equals(ERROR)){
                    Log.d("testText", keyword + " : " + result);
                    //지방이 진짜 지방이 아니면 return 하면 안됨
                    if (keyword.equals(FAT))
                        fatIndex = -1;
                    return result;
                }
                //return result;
            }else{
                lineText = "";
            }
            //키워드가 없는 경우를 제외하고 마지막에 여기 오면 무조건 에러
            if(i == linearString.size()-1 && lineText.equals("")){
                Log.d("Line2",keyword + " Line : " + lineText);
                if(!isKeyword)
                    return NO_CONTAIN;
                return ERROR;
            }
        }

        Log.d("Line2",keyword + " Line : " + lineText);
        return lineText;
    }

    //세로탐색
    private String _KeyWordLineStringVertical(String keyword){
        String lineText = "";
        String oriKeyword = keyword;
        StringBlock keywordBox = new StringBlock();
        StringBlock lastBox = new StringBlock();
        boolean fatFlag = false; //지방이 여러개 뽑혀서 하나만 뽑기 위한 flag
        boolean isKeyword = false;
        int fatIndex;

        for(int i = 0; i < linearString.size(); i++){
            if(!linearString.get(i).getText().contains(keyword))
                keyword = selectKeyword(keyword,linearString.get(i).getText());
            if(linearString.get(i).getText().contains(keyword)){
                isKeyword = true;
                if (linearString.get(i).getText().equals(FAT)){
                    if(!fatFlag){
                        keywordBox = linearString.get(i);
                        lastBox = linearString.get(i);
                        //lineText += linearString.get(i).getText();
                        lineText += " ";
                        fatFlag = true;
                        fatIndex = i;
                    }
                }else{
                    keywordBox = linearString.get(i);
                    lastBox = linearString.get(i);
                    //lineText += linearString.get(i).getText();
                    lineText += " ";
                }
                for(int j = 0; j < linearString.size(); j++){
                    if(lineText.equals(""))
                        break;
                    else{
                        if(isIntersectionV(keywordBox, linearString.get(j))){
                            //Log.d("Line2","Line2 append called");
                            //Log.d("testText", keyword + " : " + linearString.get(i).getText());
                            float dirX = lastBox.getMiddleRightX()-linearString.get(j).getMiddleLeftX();
                            float dirY = lastBox.getMiddleRightY()-linearString.get(j).getMiddleLeftY();
                            if(Math.sqrt(Math.pow(dirX, 2) + Math.pow(dirY, 2)) < 1){
                                lineText += linearString.get(j).getText();
                                lastBox = linearString.get(j);
                            }
                            else{
                                lineText += " "+linearString.get(j).getText();
                                lastBox = linearString.get(j);
                            }
                        }
                    }
                }
                //젖산칼슘 때문에 lastValidate 여기다 해야 할 듯
                Log.d("middleTest", keyword + " : " + lineText);
                Log.d("validateTest", keyword + " : " + lastValidate(oriKeyword, lineText));
                if(lastValidate(oriKeyword, lineText))
                    return NO_CONTAIN;
                if(lineText.contains(keyword))
                    lineText = lineText.substring(lineText.indexOf(keyword));
                Log.d("middleTest", keyword + " : " + lineText);
                String result = filterValue(oriKeyword,lineText);
                if(!result.equals(ERROR)){
                    Log.d("testText", keyword + " : " + result);
                    //지방이 진짜 지방이 아니면 return 하면 안됨
                    if (keyword.equals(FAT))
                        fatIndex = -1;
                }
                return result;
            }else{
                lineText = "";
            }
            //키워드가 없는 경우를 제외하고 마지막에 여기 오면 무조건 에러
            if(i == linearString.size()-1 && lineText.equals("")){
                Log.d("Line2",keyword + " Line : " + lineText);
                if(!isKeyword)
                    return NO_CONTAIN;
                return ERROR;
            }
        }

        Log.d("Line2",keyword + " Line : " + lineText);
        return lineText;
    }

    boolean isIntersectionH(StringBlock mainSb, StringBlock subSb){
        double x1 = mainSb.getRightTop().getX();
        double y1 = mainSb.getRightTop().getY();
        double x2 = mainSb.getRightBottom().getX();
        double y2 = mainSb.getRightBottom().getY();

        double a = subSb.getInclination();
        double b = subSb.getYIntercept();


        if(a == 0){
            return (b - x1) * (b - x2) < 0;
        }
        if(a == -999)
            a = 0;

        double lineValue1 = a * x1 + b;
        double lineValue2 = a * x2 + b;

        if(mainSb.getText().equals(SODIUM) && subSb.getText().equals("140mg")){
            Log.d("intersect", "a = " + a);
            Log.d("intersect", "b = " + b);
            Log.d("intersect", "y1 = " + y1 + ", y2 = " + y2);
            Log.d("intersect", "lineValue1 = " + lineValue1 + ", lineValue2 = " + lineValue2);
        }

        return (lineValue1 - y1) * (lineValue2 - y2) < 0;
    }

    boolean isIntersectionV(StringBlock mainSb, StringBlock subSb){
        double x1 = mainSb.getLeftTop().getX();
        double y1 = mainSb.getLeftTop().getY();
        double x2 = mainSb.getRightTop().getX();
        double y2 = mainSb.getRightTop().getY();

        double a1 = subSb.getInclination2();
        double b1 = subSb.getYIntercept2();

        if(mainSb.getText().equals("트랜스") || mainSb.getText().equals("포화")){
            a1 = subSb.getInclination3();
            b1 = subSb.getYIntercept3();
        }

        if(a1 == 0){
            return (b1 - x1) * (b1 - x2) < 0;
        }

        double lineValue1 = a1 * x1 + b1;
        double lineValue2 = a1 * x2 + b1;

        return (lineValue1 - y1) * (lineValue2 - y2) < 0;
    }

    private String allergyCheck(String str){
        isAllergenFood = true;
        String result = "";
        String containText = "";
        String sameFactoryText = "";
        int containIndex = 0;
        int sameFactoryIndex = 0;

        if(str.contains("함유"))
            containIndex = str.indexOf("함유");

        if(str.contains("같은") || str.contains("혼입")){
            sameFactoryIndex = str.indexOf("같은");
            if(str.contains("혼입"))
                sameFactoryIndex = str.indexOf("혼입");
        }


        if(str.contains("함유")){
            String containValueText = "";
            if (containIndex < sameFactoryIndex)
                containValueText = str.substring(0, containIndex);
            else
                containValueText = str.substring(sameFactoryIndex, containIndex);
            for(String value: getMyAllergies()){
                if(containValueText.contains(value))
                    containText += value + " ";
            }

            containText += "함유";
        }

        if(str.contains("같은") || str.contains("혼입")){
            String sameFactoryValueText = "";
            if(containIndex < sameFactoryIndex)
                sameFactoryValueText = str.substring(containIndex, sameFactoryIndex);
            else
                sameFactoryValueText = str.substring(0, sameFactoryIndex);
            for(String value: getMyAllergies()){
                if(sameFactoryValueText.contains(value))
                    sameFactoryText += value + " ";
            }

            sameFactoryText += "와 같은 시설에서 제조";
        }

        fooddata.allergies = containText;
        fooddata.sameFactory = sameFactoryText;

        if (containText.equals("") || containText.equals("함유")) {
            isAllergenFood = false;
            containText = "없음";
        }

        if (sameFactoryText.equals("") || sameFactoryText.equals("와 같은 시설에서 제조")) {
            sameFactoryText = "없음";
        }

        result += containText + "/" +sameFactoryText;
        return result;
    }

    static final String SODIUM = "나트륨";
    static final String CARBOHYDRATES = "탄수화물";
    static final String CHOLESTEROL = "콜레스테롤";
    static final String PROTEIN = "단백질";
    static final String SUGARS = "당류";
    static final String FAT = "지방";
    static final String TRANSFAT = "트랜스지방";
    static final String SATURATEDFAT = "포화지방";
    static final String CALCIUM = "칼슘";
    static final String KCAL = "kcal";
    static final String NO_CONTAIN = "0";
    static final String ERROR = "-999";
    private static final ArrayList<String> SodiumArray = new ArrayList<String>(Arrays.asList("Sodium","sodium","SODIUM","Sel","Zout","LIES"));
    private static final ArrayList<String> CarbohydratesArray = new ArrayList<String>(Arrays.asList("Carbohydrates","carbohydrates","CARBOHYDRATES","Glucides","Koolhydraten"));
    private static final ArrayList<String> SugarArray = new ArrayList<String>(Arrays.asList("담류","풍류","Sugar", "sugar","SUGARS","sucres","suikers"));
    private static final ArrayList<String> FatArray = new ArrayList<String>(Arrays.asList("Fat","fat","FAT","Matières","Vetten"));
    private static final ArrayList<String> TransArray = new ArrayList<String>(Arrays.asList("트랜스", "TransFat", "transfat","TRANSFAT"));
    private static final ArrayList<String> SaturatedArray = new ArrayList<String>(Arrays.asList("포화","SaturatedFat","saturatedfat","SATURATEDFAT","saturés","verzadigde"));
    private static final ArrayList<String> CholesterolArray = new ArrayList<String>(Arrays.asList("Cholesterol", "cholesterol","CHOLESTEROL"));
    private static final ArrayList<String> ProteinArray = new ArrayList<String>(Arrays.asList("Protein","protein","PROTEIN","Protéines","Eiwitten"));
    private static final ArrayList<String> CalciumArray = new ArrayList<String>(Arrays.asList("Calcium","calcium","CALCIUM"));

    private void extractAllText(String str){
        allValueGroup.setVisibility(View.VISIBLE);
        allergyFrame.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);
        mMainImage.setVisibility(View.VISIBLE);

        String extractedKcal = filterValue(KCAL,str);
        String extractedSodium = lineKeyword.get(SODIUM);
        String extractedCarbohydrates = lineKeyword.get(CARBOHYDRATES);
        String extractedSugars = lineKeyword.get(SUGARS);
        String extractedFat = lineKeyword.get(FAT);
        String extractedTransFat = lineKeyword.get(TRANSFAT);
        String extractedSaturatedFat = lineKeyword.get(SATURATEDFAT);
        String extractedCholesterol = lineKeyword.get(CHOLESTEROL);
        String extractedProtein = lineKeyword.get(PROTEIN);
        String extractedCalcium = lineKeyword.get(CALCIUM);


        mKCAL = Float.parseFloat(extractedKcal);
        mSODIUM = preventNumberError(extractedSodium);
        mCARBOHYDRATES = preventNumberError(extractedCarbohydrates);
        mSUGARS = preventNumberError(extractedSugars);
        mFAT = preventNumberError(extractedFat);
        mTRANSFAT = preventNumberError(extractedTransFat);
        mSATURATEDFAT = preventNumberError(extractedSaturatedFat);
        mCHOLESTEROL = preventNumberError(extractedCholesterol);
        mPROTEIN = preventNumberError(extractedProtein);
        mCALCIUM = preventNumberError(extractedCalcium);

        KcalValueTextView.setText(convertValue(extractedKcal));
        SodiumValueTextView.setText(convertValue(extractedSodium));
        CarbohydratesValueTextView.setText(convertValue(extractedCarbohydrates));
        SugarsValueTextView.setText(convertValue(extractedSugars));
        FatValueTextView.setText(convertValue(extractedFat));
        TransFatValueTextView.setText(convertValue(extractedTransFat));
        SaturatedFatValueTextView.setText(convertValue(extractedSaturatedFat));
        CholesterolValueTextView.setText(convertValue(extractedCholesterol));
        ProteinValueTextView.setText(convertValue(extractedProtein));
        CalciumValueTextView.setText(convertValue(extractedCalcium));

        allergyContainValueTextView.setText(allergyCheck(str).substring(0,allergyCheck(str).indexOf("/")));
        allergyFactoryValueTextView.setText(allergyCheck(str).substring(allergyCheck(str).indexOf("/")+1));
    }

    private static float preventNumberError(String str){
        if(str.equals("")) {
            return Float.parseFloat(NO_CONTAIN);
        }
        else {
            return Float.parseFloat(str);
        }
    }

    private static String convertValue(String result){
        if(result.equals(NO_CONTAIN))
            return "0";
        else if(result.equals(ERROR))
            return "오류";
        else return result;
    }

    //단위X 콤마X
    private static String filterValue(String keyword, String mainStr){
        String result;
        if(!keyword.equals(KCAL)){
            if(mainStr == null){mainStr = "";}
            result = extractValue2(keyword, mainStr);
            Log.d("testResult", keyword + " : " + result);
            if(result.equals(NO_CONTAIN))
                return NO_CONTAIN;
            if(result.equals(ERROR) || !result.matches("^[0-9 mg,.]*$")){
                return ERROR;
            }else{
                if(result.contains("mg"))
                    return result.replace(",",".").replace("mg","");
                else
                    return result.replace(",",".").replace("g","");
            }
        }else{
            result = extractKcal(keyword, mainStr);
            if(result.equals(NO_CONTAIN))
                return NO_CONTAIN;
            if(!result.matches("^[0-9 ,kcal]*$")){
                return ERROR;
            }else{
                return result;
            }
        }
    }

    //**나트륨, **칼슘 방지
    private static boolean lastValidate(String keyword, String str){
        boolean flag = false;
        boolean loopFlag = true;
        int keywordIndex = str.indexOf(keyword);

        while(loopFlag){
            int backIndex = keywordIndex - 1;
            if(backIndex < 0) {
                return flag;
            }

            String backWord = Character.toString(str.toCharArray()[backIndex]);

            String lastWord = Character.toString(keyword.toCharArray()[keyword.length()-1]);


            if(!backWord.equals(" ") && !backWord.equals(lastWord)) {
                Log.d("lastWord", "lastWord : " + lastWord + " / " + "backWrod : " + backWord);
                flag = true;
                loopFlag = false;
                if(keyword.equals(FAT) && (str.contains(TRANSFAT) || str.contains(SATURATEDFAT))){
                    flag = false;
                }
                break;
            }

            str = str.substring(keywordIndex+keyword.length());
            Log.d("subStringTest", str);
            keywordIndex = str.indexOf(keyword);

            if(keywordIndex < 0)
                break;
        }

        Log.d("validateResult", keyword + " : " + flag);

        return flag;
    }

    private static String extractValue2(String keyword, String str){
        String result = "";
        String newKeyword;
        boolean mgFlag = keyword.equals(SODIUM) || keyword.equals(CHOLESTEROL) || keyword.equals(CALCIUM);
        boolean gFlag = keyword.equals(CARBOHYDRATES) || keyword.equals(SUGARS) || keyword.equals(FAT) || keyword.equals(TRANSFAT) || keyword.equals(SATURATEDFAT) || keyword.equals(PROTEIN);
        newKeyword = selectKeyword(keyword,str);
        int index;

        if (!str.contains(newKeyword))
            return NO_CONTAIN;
        if(!str.contains("g") && !str.contains("mg"))
            return ERROR;

        String newStr = str;
        str = str.replace(",",".").replace(" ","").replace("O","0");
        if(mgFlag && str.contains("mg")){
            index = str.indexOf("mg")-1;
            if(index<= 0)
                return ERROR;
            while(index >= 0){
                if(!Character.toString(str.toCharArray()[index]).matches("^[0-9.]*$"))
                    break;
                result += str.toCharArray()[index];
                index--;
            }
        }else if(gFlag && str.contains("g")){
            index = str.indexOf("g")-1;
            if(index<= 0)
                return ERROR;
            while(index >= 0){
                if(!Character.toString(str.toCharArray()[index]).matches("^[0-9.]*$"))
                    break;
                result += str.toCharArray()[index];
                index--;
            }
        }

        if (result.equals("")){
            if(lastValidate(newKeyword, newStr))
                return NO_CONTAIN;
            else
                return ERROR;
        }


        StringBuilder sb = new StringBuilder(result);
        sb.reverse();
        String reversedStr = sb.toString();
        return reversedStr;
    }

    private static String validateIndex(String keyword, String str){
        String validateText = str.replace(" ","");
        int lastCount = countKeyword(keyword, validateText);

        if(keyword.equals("포화") || keyword.equals(SATURATEDFAT))
            Log.d("testKeyword", keyword + " : " + str);

        //애초에 없음
        if(!validateText.contains(keyword))
            return NO_CONTAIN;
        else{
            if(str.trim().replace(" ","").equals(keyword)){
                return NO_CONTAIN;
            }
            if(!str.matches(".*\\d.*"))
                return NO_CONTAIN;


            for(int startCount = 0; startCount < lastCount; startCount++){
                if(validateText.contains(keyword)){
                    int backIndex = validateText.indexOf(keyword) + keyword.length();

                    if(keyword.equals("포화") || keyword.equals("트랜스")){
                        backIndex += 2;
                    }

                    if(backIndex >= validateText.length())
                        backIndex = validateText.length()-1;

                    if(validateText.substring(backIndex).matches(".*\\d.*")){
                        Log.d("testValid", keyword + " : " + validateText);
                        return validateText;
                    }

                    validateText = validateText.substring(backIndex);
                }
            }
        }

        //있는데 못 뽑음
        return ERROR;
    }

    private static int countKeyword(String keyword, String str){
        int count = 0;
        while(str.contains(keyword)){
            count++;
            int beginIndex = str.indexOf(keyword)+keyword.length();
            if(beginIndex > str.length())
                beginIndex = str.lastIndexOf(keyword)+1;
            str = str.substring(beginIndex);
        }
        return count;
    }

    //키워드 인식 오류 피하는 코드
    private static String selectKeyword(String keyword, String str){
        switch (keyword){
            case SODIUM:
                if(!str.contains(keyword)){
                    for(String altSodium : SodiumArray)
                        if(str.contains(altSodium))
                            keyword = altSodium;
                }
                break;
            case CARBOHYDRATES:
                if(!str.contains(keyword)){
                    for(String altCarbohydrates : CarbohydratesArray)
                        if(str.contains(altCarbohydrates))
                            keyword = altCarbohydrates;
                }
                break;
            case SUGARS:
                if(!str.contains(keyword)){
                    for(String altSugar : SugarArray)
                        if(str.contains(altSugar))
                            keyword = altSugar;
                }
                break;
            case FAT:
                if(!str.contains(keyword)){
                    for(String altFat : FatArray)
                        if(str.contains(altFat))
                            keyword = altFat;
                }
                break;
            case TRANSFAT:
                if(!str.contains(keyword)){
                    for(String altTransfat : TransArray)
                        if(str.contains(altTransfat))
                            keyword = altTransfat;
                }
                break;
            case SATURATEDFAT:
                if(!str.contains(keyword)){
                    for(String altSaturatedfat : SaturatedArray)
                        if(str.contains(altSaturatedfat))
                            keyword = altSaturatedfat;
                }
                break;
            case CHOLESTEROL:
                if(!str.contains(keyword)){
                    for(String altCholesterol : CholesterolArray)
                        if(str.contains(altCholesterol))
                            keyword = altCholesterol;
                }
                break;
            case PROTEIN:
                if(!str.contains(keyword)){
                    for(String altProtein : ProteinArray)
                        if(str.contains(altProtein))
                            keyword = altProtein;
                }
                break;
            case CALCIUM:
                if(!str.contains(keyword)){
                    for(String altCalcium : CalciumArray)
                        if(str.contains(altCalcium))
                            keyword = altCalcium;
                }
                break;
            default:
                break;
        }
        return keyword;
    }

    //옛날 분석 코드
    private static String extractValue(String keyword, String str){
        keyword = selectKeyword(keyword,str);
        str = validateIndex(keyword, str);
        if(str.equals(ERROR))
            return ERROR;
        if(str.equals(NO_CONTAIN))
            return NO_CONTAIN;

        int firstIndex = str.indexOf(keyword);
        //길이 조절 필요
        int MINUMINDEX = 9;
        int lastIndex = firstIndex + keyword.length();
        if(keyword.equals("포화") || keyword.equals("트랜스")){
            if (str.contains(FAT))
                lastIndex += 2;
        }
        int endIndex = firstIndex + keyword.length() + MINUMINDEX;
        if(endIndex > str.length())
            endIndex = str.length();
        String searchedText = "";

        for(int index = lastIndex; index < endIndex; index++){
            String resultText = "";
            resultText += str.toCharArray()[index];
            if (resultText.matches("^[가-힣]*$"))
                break;
            if(searchedText.endsWith("g")){
                break;
            }
            searchedText += resultText;

        }

        searchedText = searchedText.trim();

        if(searchedText.endsWith("g") && searchedText.contains("O"))
            searchedText = searchedText.replace("O", "0");

        //여러 경우 판단
        if(searchedText.equals("")){ //아무것도 뽑아내지 못할 경우
            return ERROR;
        }else if(searchedText.trim().length() == 0) {   //공백만 뽑아낼 경우
            return ERROR;
        }else if(!searchedText.matches("^[0-9 mg,.]*$")){    //뽑아낼 결과에 포함된 내용이 숫자나 mg, 콤마, 공백이 아닐 경우
            //return searchedText;
            return ERROR;
        }else if(!searchedText.trim().replace(" ","").endsWith("g")){  //뽑아낸 결과가 g으로 끝나지 않을 경우
            searchedText = searchedText.trim();
            searchedText = searchedText.replace(searchedText.charAt(searchedText.length() - 1), 'g');
//            return searchedText;
            return ERROR;
        }else if(searchedText.equals("g") || searchedText.equals("mg")){
            return ERROR;
        }else{  //정상
            searchedText= searchedText.replace("\n","");
            return searchedText.trim();
        }
    }

    //kcal 정보 추출을 위하여 문자열을 뒤집는 함수
    private static String reversingText(String ori){
        String reversedText = "";
        for(int backIndex = ori.length()-1; backIndex >= 0; backIndex--){
            reversedText += ori.toCharArray()[backIndex];
        }
        return reversedText;
    }

    private static String extractKcal(String kcal, String str){
        if(!str.contains(kcal)){
            return NO_CONTAIN;
        }
        ArrayList<String> searchedTextArr = new ArrayList<>();  //추출한 kcal 정보를 담을 리스트
        ArrayList<Integer> kcalNumArr = new ArrayList<>();  //kcal를 정수화하여 담을 리스트
        int Kcal = 0;
        searchedTextArr = putKcalToList(str, kcal);

        //리스트에 있는 kcal들을 정수로 바꾸어 정수형 리스트에 저장
        for(String candidate : searchedTextArr){
            kcalNumArr.add(Integer.parseInt(candidate.replace(",","")));
        }
        Collections.sort(kcalNumArr);

        boolean containsOtherValue = false;
        for (int value : kcalNumArr) {
            if (value != 2000) {
                containsOtherValue = true;
                break;
            }
        }

        if(Collections.frequency(kcalNumArr, 2000) > 1 && !containsOtherValue){
            Kcal = 2000;
        }else{
            if(kcalNumArr.isEmpty())
                Kcal = Integer.parseInt(NO_CONTAIN);
            else
                Kcal = Collections.min(kcalNumArr);
        }

        return Integer.toString(Kcal);
    }

    private static ArrayList<String> putKcalToList(String str, String keyword){
        ArrayList<String> searchedTextArr = new ArrayList<>();  //추출한 kcal 정보를 담을 리스트
        //API가 추출한 텍스트에 있는 모든 kcal를 리스트에 저장
        while(str.contains(keyword)){
            int initialIndex = 0;   //텍스트의 처음부터 탐색
            initialIndex = str.indexOf(keyword, initialIndex);
            if(initialIndex-12 > 0 && (str.substring(initialIndex-12, initialIndex).contains("%") || str.substring(initialIndex-12, initialIndex).contains("율"))){
                str = str.substring(initialIndex+4);
                continue;
            }
            String searchedText = "";
            int index = initialIndex - 1;
            String resultText = "";
            resultText += str.toCharArray()[index];

            while(resultText.matches("^[0-9 ,]*$")){
                searchedText += resultText;
                index--;
                resultText = "";
                resultText += str.toCharArray()[index];
            }

            searchedText = reversingText(searchedText);

            if(!searchedText.equals("")){
                searchedTextArr.add(searchedText.trim());
            }

            str = str.substring(initialIndex+4);
        }
        return searchedTextArr;
    }

    private List<String> getMyAllergies(){
        String myInfoJson = sm.getString(SharedPreferenceManger.MY_INFO_PREFERENCE, "my_info");
        Gson gson = new Gson();
        MyInfo myinfo = gson.fromJson(myInfoJson, MyInfo.class);
        List<String> allergies = myinfo.getAllergyList();
//        for(String key:myinfo.getAllergyList()){
//            switch (key){
//                case "난류(가금류)":
//                    allergies.add("난류");
//                    allergies.add("계란");
//                    allergies.add("가금류");
//                    break;
//                case "쇠고기":
//                    allergies.add("소고기");
//                    break;
//                case "아황산 포함식품":
//                    allergies.add("아황산류");
//                    break;
//                default:
//                    break;
//            }
//        }
        return allergies;
    }
}