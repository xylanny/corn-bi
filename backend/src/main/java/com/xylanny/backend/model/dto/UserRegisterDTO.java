package com.xylanny.backend.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {

    private String userName;

    private String userEmail;

    private String userPassword;

    private String checkedPassword;
}
