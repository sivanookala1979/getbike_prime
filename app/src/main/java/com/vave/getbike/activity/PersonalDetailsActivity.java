package com.vave.getbike.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vave.getbike.R;
import com.vave.getbike.helpers.CircleTransform;
import com.vave.getbike.helpers.GetBikeAsyncTask;
import com.vave.getbike.helpers.GetBikePreferences;
import com.vave.getbike.helpers.ToastHelper;
import com.vave.getbike.model.SaveResult;
import com.vave.getbike.model.UserProfile;
import com.vave.getbike.syncher.BaseSyncher;
import com.vave.getbike.syncher.LoginSyncher;
import com.vave.getbike.utils.HTTPUtils;

public class PersonalDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final int GALLERY_REQUET_CODE = 11111;
    private static final int CAMERA_REQUEST_CODE = 100;
    EditText name, email, occupation, city, yearOfBirth, homeLocation, officeLocation, mobile;
    Button update;
    CheckBox verification;
    LoginSyncher loginSyncher = new LoginSyncher();
    UserProfile userProfile = new UserProfile();
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        addToolbarView();
        name = (EditText) findViewById(R.id.profileUserName);
        email = (EditText) findViewById(R.id.userEmail);
        occupation = (EditText) findViewById(R.id.userOccupation);
        city = (EditText) findViewById(R.id.userCity);
        yearOfBirth = (EditText) findViewById(R.id.userYearOfBirth);
        homeLocation = (EditText) findViewById(R.id.userHomeLocation);
        officeLocation = (EditText) findViewById(R.id.userOfficeLocation);
        mobile = (EditText) findViewById(R.id.userMobileNumber);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        verification = (CheckBox) findViewById(R.id.verification);
        userProfile = GetBikePreferences.getUserProfile();
        updateProfileDetails();

    }

    private void updateProfileDetails() {
        if (userProfile != null) {
            GetBikePreferences.setUserProfile(userProfile);
            name.setText(userProfile.getName());
            email.setText(userProfile.getEmail());
            occupation.setText(userProfile.getOccupation());
            city.setText(userProfile.getCity());
            yearOfBirth.setText(userProfile.getYearOfBirth());
            homeLocation.setText(userProfile.getHomeLocation());
            officeLocation.setText(userProfile.getOfficeLocation());
            mobile.setText(userProfile.getPhoneNumber());
            Picasso.with(getApplicationContext()).load(BaseSyncher.BASE_URL + "/" + userProfile.getProfileImage()).transform(new CircleTransform()).placeholder(R.drawable.male_profile_icon).into(profileImage);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update:
                collectData();
                SaveResult dataValid = userProfile.isDataValid();
                if (dataValid.isValid()) {
                    updateProfile(userProfile);
                } else {
                    ToastHelper.redToast(getApplicationContext(), "Please enter valid " + dataValid.getErrorMessage());
                }
                break;
            case R.id.profileImage:
                final Dialog dialog = new Dialog(PersonalDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_selection_dialog);
                ImageView gallery = (ImageView) dialog.findViewById(R.id.gallery);
                ImageView camera = (ImageView) dialog.findViewById(R.id.camera);
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, GALLERY_REQUET_CODE);
                        dialog.cancel();
                    }
                });
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePicture = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
                        dialog.cancel();
                    }
                });
                dialog.show();

                break;
        }
    }

    private void updateProfile(final UserProfile profile) {
        new GetBikeAsyncTask(PersonalDetailsActivity.this) {
            SaveResult saveResult;

            @Override
            public void process() {
                saveResult = loginSyncher.updateUserProfile(profile, profile.getProfileImage());
            }

            @Override
            public void afterPostExecute() {
                if (saveResult.isValid()) {
                    ToastHelper.redToast(getApplicationContext(), "Data successfully updated.");
                } else {
                    ToastHelper.redToast(getApplicationContext(), saveResult.getErrorMessage());
                }
            }
        }.execute();
    }

    private void collectData() {
        userProfile.setName(name.getText().toString());
        userProfile.setEmail(email.getText().toString());
        userProfile.setOccupation(occupation.getText().toString());
        userProfile.setCity(city.getText().toString());
        userProfile.setYearOfBirth(yearOfBirth.getText().toString());
        userProfile.setHomeLocation(homeLocation.getText().toString());
        userProfile.setOfficeLocation(officeLocation.getText().toString());
        userProfile.setPhoneNumber(mobile.getText().toString());
        userProfile.setMobileVerified(verification.isChecked());
        if (!userProfile.isProfileImageUpdated()) {
            profileImage.buildDrawingCache();
            Bitmap bmap = profileImage.getDrawingCache();
            userProfile.setProfileImage(HTTPUtils.BitMapToString(bmap));
            userProfile.setProfileImageUpdated(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String customerImageBitmapToString = "";
        if (data != null) {
            Bitmap bitmap;
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                customerImageBitmapToString = HTTPUtils.BitMapToString(bitmap);
                userProfile.setProfileImage(customerImageBitmapToString);
                userProfile.setProfileImageUpdated(true);
                profileImage.setImageBitmap(getCroppedBitmap(bitmap, profileImage.getWidth()));
            }
            if (requestCode == GALLERY_REQUET_CODE && resultCode == Activity.RESULT_OK) {
                bitmap = HTTPUtils.getBitmapFromCameraData(data, getApplicationContext());
                customerImageBitmapToString = HTTPUtils.BitMapToString(bitmap);
                userProfile.setProfileImage(customerImageBitmapToString);
                userProfile.setProfileImageUpdated(true);
                profileImage.setImageBitmap(getCroppedBitmap(bitmap, profileImage.getWidth()));
            }
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap, int width) {
        Bitmap output = Bitmap.createBitmap(width,
                width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, width);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(width / 2, width / 2,
                width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
