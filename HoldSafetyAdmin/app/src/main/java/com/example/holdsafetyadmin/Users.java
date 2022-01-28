package com.example.holdsafetyadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

public class Users {
    private String ID;
    private String FirstName;
    private String MiddleName;
    private String LastName;
    private String Sex;
    private boolean isVerified;
    private String BirthDate;
    //private Date mTimestamp;

    public Users(){}

    public Users(String ID, String firstName, String middleName, String lastName, String sex, boolean isVerified, String birthDate) {
        this.ID = ID;
        this.FirstName = firstName;
        this.MiddleName = middleName;
        this.LastName = lastName;
        this.Sex = sex;
        this.isVerified = isVerified;
        this.BirthDate = birthDate;
    }

    public String getID() {
        return ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getSex() {
        return Sex;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getBirthDate() {
        return BirthDate;
    }
}
