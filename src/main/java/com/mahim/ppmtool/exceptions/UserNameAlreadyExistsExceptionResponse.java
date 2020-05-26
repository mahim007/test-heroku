package com.mahim.ppmtool.exceptions;

public class UserNameAlreadyExistsExceptionResponse {
    private String UserNameAlreadyExists;

    public UserNameAlreadyExistsExceptionResponse(String userNameAlreadyExists) {
        UserNameAlreadyExists = userNameAlreadyExists;
    }

    public String getUserNameAlreadyExists() {
        return UserNameAlreadyExists;
    }

    public void setUserNameAlreadyExists(String userNameAlreadyExists) {
        UserNameAlreadyExists = userNameAlreadyExists;
    }
}
