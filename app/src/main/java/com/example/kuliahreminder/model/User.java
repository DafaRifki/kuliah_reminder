package com.example.kuliahreminder.model;

public class User {
    private int id;
    private String namaLengkap;
    private String email;
    private String password;
    private String createdAt;

    public User() {
    }

    public User(String namaLengkap, String email, String password) {
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.password = password;
    }

    public User(int id, String namaLengkap, String email, String password) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", namaLengkap='" + namaLengkap + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
