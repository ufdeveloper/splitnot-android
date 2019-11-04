package com.megshan.splitnotandroid.dto;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SplitwiseTokenResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;
}
