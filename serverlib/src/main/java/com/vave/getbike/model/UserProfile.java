package com.vave.getbike.model;

import com.vave.getbike.utils.StringUtils;

/**
 * Created by adarsht on 05/12/16.
 */

public class UserProfile {

    String name="";
    String email="";
    String occupation="";
    String city="";
    String yearOfBirth="";
    String homeLocation="";
    String officeLocation="";
    String phoneNumber="";
    String profileImage="";
    boolean mobileVerified;
    boolean profileImageUpdated;
    char gender;

    public boolean isProfileImageUpdated() {
        return profileImageUpdated;
    }

    public void setProfileImageUpdated(boolean profileImageUpdated) {
        this.profileImageUpdated = profileImageUpdated;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(String homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public SaveResult isDataValid() {
        SaveResult result = new SaveResult();
        if (isProfileImageUpdated()) {
            if (StringUtils.isStringValid(getName())) {
                if (StringUtils.isStringValid(getEmail())) {
                    if (StringUtils.isStringValid(getOccupation())) {
                        if (StringUtils.isStringValid(getCity())) {
                            if (StringUtils.isStringValid(getYearOfBirth())) {
                                if (StringUtils.isStringValid(getHomeLocation())) {
                                    if (StringUtils.isStringValid(getPhoneNumber())) {
                                        result.setValid(true);
                                    } else {
                                        result.setErrorMessage("mobile");
                                    }
                                } else {
                                    result.setErrorMessage("home location");
                                }
                            } else {
                                result.setErrorMessage("year of birth");
                            }
                        } else {
                            result.setErrorMessage("city");
                        }
                    } else {
                        result.setErrorMessage("occupation");
                    }
                } else {
                    result.setErrorMessage("email");
                }
            } else {
                result.setErrorMessage("name");
            }
        }else {
            result.setErrorMessage("profile image");
        }
        return result;
    }

}
