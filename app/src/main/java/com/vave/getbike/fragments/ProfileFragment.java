package com.vave.getbike.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.Profile;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.utils.HTTPUtils;

/**
 * Created by adarsht on 30/11/16.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "position";
    private static final int GALLERY_REQUET_CODE = 11111;
    private static final int CAMERA_REQUEST_CODE = 100;
    int tabIndex = 0;
    TextView title, vehicleOrLicenceNumberTitle, vehicleOrLicenceNumber;
    LinearLayout galleryView, camera;
    ImageView picture;
    Button uploadData;
    EditText number;
    //
    String customerImageBitmapToString;
    LoginSyncher loginSyncher = new LoginSyncher();
    Profile publicProfile;

    private Uri fileUri;

    public static Fragment newInstance(int position, String pageTitle) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rider_profile_update_screen, container, false);
        publicProfile = GetBikePreferences.getPublicProfile();
        if (getArguments() != null) {
            tabIndex = getArguments().getInt(ARG_PARAM1);
        }
        title = (TextView) view.findViewById(R.id.title);
        vehicleOrLicenceNumberTitle = (TextView) view.findViewById(R.id.vehicleOrLicenceNumberTitle);
        vehicleOrLicenceNumber = (TextView) view.findViewById(R.id.vehicleOrLicenceNumber);
        galleryView = (LinearLayout) view.findViewById(R.id.gallery);
        camera = (LinearLayout) view.findViewById(R.id.camera);
        picture = (ImageView) view.findViewById(R.id.picture);
        uploadData = (Button) view.findViewById(R.id.updateData);
        number = (EditText) view.findViewById(R.id.numberField);
        setOnclickListeners(galleryView, camera, uploadData);
        updateFieldsBasedOnIndex();
        return view;
    }

    private void updateFieldsBasedOnIndex() {
        if (tabIndex == 1) {
            title.setText("Upload Driving License Image");
            vehicleOrLicenceNumberTitle.setText("Driving License Number");
            vehicleOrLicenceNumber.setText("Driving License Number");
        }
        if (publicProfile != null) {
            String imageUrl = BaseSyncher.BASE_URL + "/" + ((tabIndex == 0) ? publicProfile.getVehiclePlateImageName() : publicProfile.getDrivingLicenseImageName());
            String numberInfo = ((tabIndex == 0) ? publicProfile.getVehicleNumber() : publicProfile.getDrivingLicenseNumber());

            Picasso.with(getContext()).load(imageUrl).placeholder(R.drawable.picture).into(picture);
            if (numberInfo != null) {
                number.setText("" + numberInfo);
            }
        }
    }

    private void setOnclickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gallery:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_REQUET_CODE);
                break;
            case R.id.camera:
                Intent takePicture = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
                break;
            case R.id.updateData:
                if (customerImageBitmapToString != null) {
                    String vehicleOrLicenceNumberText = number.getText().toString();
                    if (!vehicleOrLicenceNumberText.isEmpty()) {
                        new GetBikeAsyncTask(getActivity()) {
                            boolean result = false;

                            @Override
                            public void process() {
                                if (tabIndex == 0) {
                                    result = loginSyncher.storeVehicleNumber(customerImageBitmapToString, number.getText().toString());
                                } else {
                                    result = loginSyncher.storeDrivingLicense(customerImageBitmapToString, number.getText().toString());
                                }
                            }

                            @Override
                            public void afterPostExecute() {
                                if (result) {
                                    ToastHelper.redToast(getContext(), "Data successfully uploaded.");
                                } else {
                                    ToastHelper.redToast(getContext(), "Failed to upload data try again.");
                                }
                            }
                        }.execute();

                    } else {
                        ToastHelper.redToast(getContext(), "please enter valid " + ((tabIndex == 1) ? "Driving License" : "Vehicle") + "number.");

                    }
                } else {
                    ToastHelper.redToast(getContext(), "please choose " + ((tabIndex == 1) ? "Driving License" : "Vehicle") + "image");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bitmap bitmap;
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                customerImageBitmapToString = HTTPUtils.BitMapToString(bitmap);
                picture.setImageBitmap(bitmap);
            }
            if (requestCode == GALLERY_REQUET_CODE && resultCode == Activity.RESULT_OK) {
                bitmap = HTTPUtils.getBitmapFromCameraData(data, getActivity());
                customerImageBitmapToString = HTTPUtils.BitMapToString(bitmap);
                picture.setImageBitmap(bitmap);
            }
        }
    }
}
