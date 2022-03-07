package com.suitcore.data.remote.wrapper

import com.google.gson.annotations.SerializedName

class Result<T> {

        @SerializedName("page")
        var page: Int? = 0

        @SerializedName("per_page")
        var perPage: Int? = 0

        @SerializedName("total")
        var total: Int? = 0

        @SerializedName("total_pages")
        var totalPages: Int? = 0

        @SerializedName("error")
        var error: String? = ""

        @SerializedName("token")
        var token: String? = ""

        @SerializedName("data")
        var data: T? = null

}