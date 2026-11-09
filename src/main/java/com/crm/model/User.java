package com.crm.model;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String address;
    private String phone;
    private int roleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return roleId == 1;
    }

    public boolean isManager() {
        return roleId == 1 || roleId == 2;
    }

    public String getRoleName() {
        if (roleId == 1) {
            return "ADMIN";
        }
        if (roleId == 2) {
            return "LEADER";
        }
        return "MEMBER";
    }
}
