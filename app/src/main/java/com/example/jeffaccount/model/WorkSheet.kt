package com.example.jeffaccount.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WorkSheet(
    val purchaseList:List<PurchasePost>,
    val invoiceList: List<Invoice>,
    val timesheetList:List<TimeSheetPost>
):Parcelable