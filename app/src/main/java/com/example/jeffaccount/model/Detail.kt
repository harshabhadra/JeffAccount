package com.example.jeffaccount.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class Detail (
    @SerializedName("posts")
    @Expose
    var posts: List<DetailPost>
)

data class DetailPost (
    @SerializedName("pagename")
    @Expose
    var pagename: String,

    @SerializedName("pagecontent")
    @Expose
    var pagecontent: String
)