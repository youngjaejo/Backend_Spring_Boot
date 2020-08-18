package com.dev.backend.payload.request;

import javax.validation.constraints.NotBlank;

public class VertificationNumber {
    @NotBlank
    private String email;
    @NotBlank
    private String number;

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setUsername(String email) {
        this.email = email;
    }

}