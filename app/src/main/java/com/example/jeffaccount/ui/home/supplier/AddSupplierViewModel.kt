package com.example.jeffaccount.ui.home.supplier

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jeffaccount.JeffRepository
import com.example.jeffaccount.model.Logos
import com.example.jeffaccount.model.SupPost
import com.example.jeffaccount.model.Supplier
import com.example.jeffaccount.network.SearchSupplier
import com.example.jeffaccount.network.SearchSupplierPost
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private lateinit var jeffRepository: JeffRepository

class AddSupplierViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        jeffRepository = JeffRepository.getInstance()
    }

    private var _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap>
        get() = _imageBitmap

    private var _companyBitmap = MutableLiveData<Bitmap>()
    val companyBitmap:LiveData<Bitmap>
        get() = _companyBitmap

    private var _navigateToAddSupplierFragment = MutableLiveData<SupPost>()
    val navigateToAddSupplierFragment: LiveData<SupPost>
        get() = _navigateToAddSupplierFragment

    private var _nameList = MutableLiveData<List<String>>()
    val nameList: LiveData<List<String>>
        get() = _nameList

    private var _supplierList = MutableLiveData<List<SupPost>>()
    val supplierList: LiveData<List<SupPost>>
        get() = _supplierList

    //Add Supplier
    fun addSupplier(
        comid: String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        return jeffRepository.getAddSupplierMessage(
            comid,
            supplierName,
            streetAdd,
            coutry,
            county,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Update Supplier
    fun updateSupplier(
        comid: String,
        supplierId: String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        return jeffRepository.getUpdateSupplierMessage(
            comid,
            supplierId,
            supplierName,
            streetAdd,
            coutry,
            county,
            postCode,
            telephone,
            email,
            web
        )
    }

    //Delete Supplier
    fun deleteSupplier(
        supplierId: String
    ): LiveData<String> {
        return jeffRepository.getDeleteSupplierMessage(supplierId)
    }

    fun onSupplierItemClick(supplier: SupPost) {
        _navigateToAddSupplierFragment.value = supplier
    }

    fun doneNavigating() {
        _navigateToAddSupplierFragment.value = null
    }

    //Get list of supplier
    fun getSuppliers(comid: String): LiveData<Supplier> {
        return jeffRepository.getAllSuppliers(comid)
    }

    //Get search supplier list
    fun getSearchSupplierList(
        comid: String,
        name: String,
        apikey: String
    ): LiveData<SearchSupplier> {
        return jeffRepository.getSearchSupplierList(comid, name, apikey)
    }

    //Crate CustomerName list
    fun createSupplierNameList(custList: List<SupPost>) {
        val custNameList = mutableListOf<String>()
        for (item in custList) {
            custNameList.add(item.supname!!)
        }
        _nameList.value = custNameList.toList()
    }

    fun searchSupplierToSupplier(list: List<SearchSupplierPost>) {
        val supList: MutableList<SupPost> = mutableListOf()
        for (item in list) {
            val supplier = SupPost(
                item.supid,
                item.supname,
                item.street,
                item.postcode,
                item.telephone,
                item.supemail,
                item.web,
                item.country,
                item.county
            )
            supList.add(supplier)
        }
        _supplierList.value = supList
    }

    fun getBitmapFromUrl(url: String) {
        uiScope.launch {
            _imageBitmap.value = convertUrlToBitmap(url)
        }
    }
    //Get logo list
    fun getLogoList(comid: String): LiveData<Logos> {
        return jeffRepository.getLogoList(comid)
    }

    fun getCompanyBitmap(url:String){
        uiScope.launch {
            _companyBitmap.value = convertUrlToBitmap(url)
        }
    }

    private suspend fun convertUrlToBitmap(src: String?): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(src)
                val connection =
                    url.openConnection() as HttpsURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                Timber.e("converted to bitmap suceesfully")
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                Timber.e("Failed to convert to bitmap: ${e.message}")
                null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
