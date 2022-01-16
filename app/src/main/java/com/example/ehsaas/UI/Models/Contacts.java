package com.example.ehsaas.UI.Models;

public class Contacts {
    public String name, bio,image,id;

    public Contacts() {

    }

    public Contacts(String name, String bio, String image) {
        this.name = name;
        this.bio = bio;
        this.image = image;
    }

    public Contacts(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}