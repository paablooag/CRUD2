package com.example.crud

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Ejercicio(
    var id: String? = null,
    var nombre: String? = null,
    var series: Int? = null,
    var repeticiones: Int? = null,
    var imagen: String? = null,
    var rating: Float? = null
) : Parcelable