package com.example.simplechat.services.updates.models

import com.google.gson.annotations.SerializedName

enum class UpdateType(val type: String) {

    @SerializedName("new")
    NEW("new"),

    @SerializedName("remove")
    REMOVE("remove"),

    @SerializedName("change")
    CHANGE("change")
}