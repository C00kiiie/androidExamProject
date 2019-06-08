package com.example.examproject_v2.Model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.examproject_v2.Service.UserService;

public class User implements Parcelable {

    private String name;
    private String email;
    private String adress;
    private String secretQuestion;
    private String secretAnswer;
    private String department;
    private int zip;
    private int age;

    public User(String name, String email, String adress, String secretQuestion, String secretAnswer, String department, int zip, int age) {
        this.name = name;
        this.email = email;
        this.adress = adress;
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
        this.department = department;
        this.zip = zip;
        this.age = age;
    }


    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        adress = in.readString();
        secretQuestion = in.readString();
        secretAnswer = in.readString();
        department = in.readString();
        zip = in.readInt();
        age = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int yearOfBirth) {
        this.age = yearOfBirth;
    }


    public User(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(adress);
        dest.writeString(secretQuestion);
        dest.writeString(secretAnswer);
        dest.writeString(department);
        dest.writeInt(zip);
        dest.writeInt(age);
    }
}