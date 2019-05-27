package com.vrb.apps.test.data.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Declaration (@PrimaryKey var id: String = "",
                        @SerializedName("firstname") var firstName: String = "",
                        @SerializedName("lastname") var lastName: String = "",
                        var placeOfWork: String = "",
                        var position: String? = null,
                        @SerializedName("linkPDF") var linkToDocument: String = "",
                        var isBookmarked: Boolean = false,
                        var note: String? = null) : RealmObject()