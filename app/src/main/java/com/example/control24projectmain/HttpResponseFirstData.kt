package com.example.control24projectmain

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

data class HttpResponseFirstData (
    val key: String,
    val objects: ArrayList<ObjectData>
)

data class ObjectData (
    val id: Int,
    val name: String,
    val category: String,
    val client: String,
    val auto_num: String,
    val auto_model: String,
    var is_expanded: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(client)
        parcel.writeString(auto_num)
        parcel.writeString(auto_model)
        parcel.writeByte(if (is_expanded) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjectData> {
        override fun createFromParcel(parcel: Parcel): ObjectData {
            return ObjectData(parcel)
        }

        override fun newArray(size: Int): Array<ObjectData?> {
            return arrayOfNulls(size)
        }
    }
}