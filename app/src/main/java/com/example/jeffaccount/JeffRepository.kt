package com.example.jeffaccount

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jeffaccount.dataBase.JeffDataBase
import com.example.jeffaccount.dataBase.LogInCred
import com.example.jeffaccount.dataBase.LogInDao
import com.example.jeffaccount.model.*
import com.example.jeffaccount.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class JeffRepository() {

    private val apiService = JeffApi.retrofitService
    private lateinit var logInDao: LogInDao
    private lateinit var userList: LiveData<List<LogInCred>>

    constructor(application: JeffApplication) : this() {
        val jeffDataBase = JeffDataBase.getInstance(application)
        logInDao = jeffDataBase.loginDao
        userList = logInDao.getLogInCred()
    }

    companion object {
        fun getInstance(): JeffRepository {
            return JeffRepository()
        }
    }

    //Store message after adding customer
    private var _addCustomerMessage = MutableLiveData<String>()

    //Store message after update customer
    private var _updateCustomerMessage = MutableLiveData<String>()

    //Store list of Customer
    private var _customerList = MutableLiveData<Customer>()

    //Store delete customer message
    private var _deleteCustomerMessage = MutableLiveData<String>()

    //Store add supplier message
    private var addSupplierMessage = MutableLiveData<String>()

    //Store update supplier message
    private var updateSupplierMessage = MutableLiveData<String>()

    //Store delete supplier message
    private var deleteSupplierMessage = MutableLiveData<String>()

    //Store list of supplier
    private var supplierList = MutableLiveData<Supplier>()

    //Store quotation add message
    private var quotationAddMessage = MutableLiveData<String>()

    //Store quotation update message
    private var quotationUpdateMesage = MutableLiveData<String>()

    //Store quotation delete message
    private var quotationDeleteMessage = MutableLiveData<String>()

    //Store list of quoation
    private var quotationList = MutableLiveData<Quotation>()

    //Store company add message
    private var companyAddMessage = MutableLiveData<String>()

    //Store company update message
    private var companyUpdateMessage = MutableLiveData<String>()

    //Store company delete message
    private var companyDeleteMessage = MutableLiveData<String>()

    //Store company list
    private var companyList = MutableLiveData<Company>()

    //Store purchase add message
    private var purchaseAddMessage = MutableLiveData<String>()

    //Store purchase update message
    private var purchaseUpdateMessage = MutableLiveData<String>()

    //Store purchase delete message
    private var purchaseDeleteMessage = MutableLiveData<String>()

    //Store list of purchase
    private var purchaseList = MutableLiveData<Purchase>()

    //Store time sheet add message
    private var timeSheetAddMessage = MutableLiveData<String>()

    //Store timeSheet update message
    private var timeSheetUpdateMessage = MutableLiveData<String>()

    //Store time sheet delete message
    private var timeSheetDeleteMessage = MutableLiveData<String>()

    //Store list of time sheet list
    private var timeSheetListMutableLiveData = MutableLiveData<TimeSheet>()

    //Store search customer list
    private var searchCustListMutableLiveData = MutableLiveData<SearchCustomerList>()

    //Store search list of suppliers
    private var searchSupplierListMutableLiveData = MutableLiveData<SearchSupplier>()

    //Get LogInCred list
    fun getLogInCred(): LiveData<List<LogInCred>> {
        return userList
    }

    //Insert Log in cred
    suspend fun insertLogInCred(logInCred: LogInCred) {

        withContext(Dispatchers.IO) {
            logInDao.insert(logInCred)
        }
    }

    //Delete Log in cred
    suspend fun deleteLogInCred(logInCred: LogInCred) {
        withContext(Dispatchers.IO) {
            logInDao.delete(logInCred)
        }
    }

    //Add customer
    fun getAddCustomerMessage(
        customerName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
        return _addCustomerMessage
    }

    //Add Supplier
    fun getAddSupplierMessage(
        supplierName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addSupplier(supplierName, streetAdd, coutry, postCode, telephone, email, web)
        return addSupplierMessage
    }

    //Add Company
    fun getAddCompanyMessage(
        companyName: String,
        streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addCompany(companyName, streetAdd, coutry, postCode, telephone, email, web)
        return companyAddMessage
    }

    //Add Quotation
    fun getAddQuotationMessage(quotationAdd: QuotationAdd): LiveData<String> {
        addQuotation(quotationAdd)
        return quotationAddMessage
    }

    //Add Purchase
    fun getAddPurchaseMessage(purchaseAdd: PurchaseAdd): LiveData<String> {
        addPurchase(purchaseAdd)
        return purchaseAddMessage
    }

    //Add time sheet
    fun getAddTimeSheetMessage(
        apiKey: String,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        name: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        hrs: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        addTimeSheet(
            apiKey,
            jobNo,
            quotationNo,
            vat,
            date,
            name,
            streetAdd,
            coutry,
            postCode,
            telephone,
            comment,
            itemDes
            ,
            paymentMethod,
            hrs,
            unitAmount,
            advanceAmount,
            discountAmount,
            totalAmount
        )
        return timeSheetAddMessage
    }

    //Update customer
    fun getUpdateCustomerMessage(
        customerId: String,
        customerName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        updateCustomer(customerId, customerName, streetAdd, coutry, postCode, telephone, email, web)
        return _updateCustomerMessage
    }

    //Update Supplier
    fun getUpdateSupplierMessage(
        supplierId: String, supplierName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        updateSupplier(
            supplierId, supplierName, streetAdd, coutry, postCode, telephone, email, web
        )
        return updateSupplierMessage
    }

    //Get search supplier list
    fun getSearchSupplierList(name: String, apiKey: String):LiveData<SearchSupplier>{
        getSearchSupplier(name,apiKey)
        return searchSupplierListMutableLiveData
    }

    //Update Company details
    fun getUpdateCompanyMessage(
        companyId: Int, companyName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        updateCompany(companyId, companyName, streetAdd, coutry, postCode, telephone, email, web)
        return companyUpdateMessage
    }

    //Update Supplier
    fun getUpdateQuotationMessage(quotationUpdate: QuotationUpdate): LiveData<String> {
        updateQuotation(
            quotationUpdate
        )
        return quotationUpdateMesage
    }

    //Search customer for quotation
    fun searchCustomerList(apiKey: String,name: String):LiveData<SearchCustomerList>{
        searchCustomer(apiKey,name)
        return searchCustListMutableLiveData
    }

    //Update Purchase
    fun getUpdatePurchaseMessage(purchaseUpdate: PurchaseUpdate): LiveData<String> {
        updatePurchase(purchaseUpdate)
        return purchaseUpdateMessage
    }

    //Update time sheet
    fun getUpdateTimeSheetMessage(
        apiKey: String,
        tid: Int,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        name: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        hrs: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ): LiveData<String> {
        updateTimeSheet(
            apiKey,
            tid,
            jobNo,
            quotationNo,
            vat,
            date,
            name,
            streetAdd,
            coutry,
            postCode,
            telephone,
            comment,
            itemDes
            ,
            paymentMethod,
            hrs,
            unitAmount,
            advanceAmount,
            discountAmount,
            totalAmount
        )
        return timeSheetUpdateMessage
    }

    //Delete customer
    fun getDeleteCustomerMessage(customerId: String): LiveData<String> {
        deleteUser(customerId)
        return _deleteCustomerMessage
    }

    //Delete Supplier
    fun getDeleteSupplierMessage(supplierId: String): LiveData<String> {
        deleteSupplier(supplierId)
        return deleteSupplierMessage
    }

    //Delete Company
    fun getDeleteCompanyMessage(companyId: Int): LiveData<String> {
        deleteCompany(companyId)
        return companyDeleteMessage
    }

    //Delete Quotation
    fun getDeleteQuotationMessage(quotationId: Int): LiveData<String> {
        deleteQuotation(quotationId)
        return quotationDeleteMessage
    }

    //Delete Purchase
    fun getDeletePurchaseMessage(purchaseId: Int): LiveData<String> {
        deletePurchase(purchaseId)
        return purchaseDeleteMessage
    }

    //Delete TimeSheet
    fun getDeleteTimeSheetMessage(timeSheetId: Int): LiveData<String> {
        deleteTimeSheet(timeSheetId)
        return timeSheetDeleteMessage
    }

    //Get All Customer
    fun getAllCustomer(): LiveData<Customer> {
        getCustomerList()
        return _customerList
    }

    //Get All Suppliers
    fun getAllSuppliers(): LiveData<Supplier> {
        getSupplierList()
        return supplierList
    }

    //Get all Company
    fun getAllCompany(): LiveData<Company> {
        getCompanyList()
        return companyList
    }

    //Get all purchase
    fun getAllPurchase(apiKey: String): LiveData<Purchase> {
        getPurchaseList(apiKey)
        return purchaseList
    }

    //Get All quotation
    fun getAllQuotation(apiKey: String): LiveData<Quotation> {
        getQuotationList(apiKey)
        return quotationList
    }

    //Get all time sheet
    fun getAllTimeSheet(apiKey: String): LiveData<TimeSheet> {
        getTimeSheetList(apiKey)
        return timeSheetListMutableLiveData
    }

    //Network call to get all customer list
    private fun getCustomerList() {

        apiService.getCustomerList().enqueue(object : Callback<Customer> {
            override fun onFailure(call: Call<Customer>, t: Throwable) {
                Timber.e("JeffRepository ${t.message}")
            }

            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {

                _customerList.value = response.body()
            }

        })
    }

    //Network call to get all supplier list
    private fun getSupplierList() {

        apiService.getSupplierList().enqueue(object : Callback<Supplier> {
            override fun onFailure(call: Call<Supplier>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<Supplier>, response: Response<Supplier>) {
                supplierList.value = response.body()
            }
        })
    }

    //Network call to get company list
    private fun getCompanyList() {

        apiService.getCompanyList().enqueue(object : Callback<Company> {
            override fun onFailure(call: Call<Company>, t: Throwable) {
                Timber.e("Error getting list of company")
            }

            override fun onResponse(call: Call<Company>, response: Response<Company>) {
                companyList.value = response.body()
            }
        })
    }

    //Network call to get all suppliers
    private fun getQuotationList(apiKey: String) {

        apiService.getQuotationList(apiKey).enqueue(object : Callback<Quotation> {
            override fun onFailure(call: Call<Quotation>, t: Throwable) {
                Timber.e("Error getting list of quotation: ${t.message}")
            }

            override fun onResponse(call: Call<Quotation>, response: Response<Quotation>) {
                quotationList.value = response.body()
            }
        })
    }

    //Network call to get purchase list
    private fun getPurchaseList(apiKey: String) {
        apiService.getPurchaseList(apiKey).enqueue(object : Callback<Purchase> {
            override fun onFailure(call: Call<Purchase>, t: Throwable) {
                Timber.e("Error getting purchase list")
            }

            override fun onResponse(call: Call<Purchase>, response: Response<Purchase>) {
                purchaseList.value = response.body()
            }
        })
    }

    //Network call to get Time Sheet list
    private fun getTimeSheetList(apiKey: String) {

        apiService.getTimeSheetList(apiKey).enqueue(object : Callback<TimeSheet> {
            override fun onFailure(call: Call<TimeSheet>, t: Throwable) {
                Timber.e("Error getting time sheet list ${t.message}")
            }

            override fun onResponse(call: Call<TimeSheet>, response: Response<TimeSheet>) {
                timeSheetListMutableLiveData.value = response.body()
            }
        })
    }

    //Network call to add customer
    private fun addCustomer(
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {

        apiService.addCustomer(customerName, streetAdd, coutry, postCode, telephone, email, web)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("JeffRepository", "${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {

                    val jsonObject = JSONObject(response.body()!!)
                    val message = jsonObject.optString("message")
                    _addCustomerMessage.value = message
                }

            })
    }

    //Network call to add supplier
    private fun addSupplier(
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {

        apiService.addSupplier(supplierName, streetAdd, coutry, postCode, telephone, email, web)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("JeffRepository", "${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val jsonObject = JSONObject(response.body()!!)
                    val message = jsonObject.optString("message")
                    addSupplierMessage.value = message
                }
            })
    }

    //Network call to add company
    private fun addCompany(
        companyName: String, streetAdd: String, coutry: String,
        postCode: String, telephone: String, email: String, web: String
    ) {
        apiService.addCompany(companyName, streetAdd, coutry, postCode, telephone, email, web)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.e("Error adding company ${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val jsonObject = JSONObject(response.body()!!)
                    val message = jsonObject.optString("message")
                    companyAddMessage.value = message
                }
            })
    }

    //Network call to add quotation
    private fun addQuotation(quotation: QuotationAdd) {
        apiService.addQuotation(quotation).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {

                Timber.e("Adding quotation failed: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                quotationAddMessage.value = message
            }
        })
    }

    //Network call to add purchase
    private fun addPurchase(
        purchaseAdd: PurchaseAdd
    ) {
        apiService.addPurchase(purchaseAdd).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error adding purchase ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                purchaseAddMessage.value = message
            }
        })
    }

    //Network call to add time sheet
    private fun addTimeSheet(
        apiKey: String,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        name: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        hrs: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ) {

        apiService.addTimeSheet(
            apiKey,
            jobNo,
            quotationNo,
            vat,
            date,
            name,
            streetAdd,
            coutry,
            postCode,
            telephone,
            comment,
            itemDes
            ,
            paymentMethod,
            hrs,
            unitAmount,
            advanceAmount,
            discountAmount,
            totalAmount
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error adding time sheet ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                timeSheetAddMessage.value = message
            }
        })
    }

    //Network call to update customer details
    private fun updateCustomer(
        customerId: String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateCustomer(
            customerId,
            customerName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                _updateCustomerMessage.value = message
            }
        })
    }

    //Network call to update supplier data
    private fun updateSupplier(
        supplierId: String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateSupplier(
            supplierId,
            supplierName,
            streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed to Update supplier: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                updateSupplierMessage.value = message
            }
        })
    }

    //Network call to get list of search supplier
    private fun getSearchSupplier(name: String, apiKey: String) {

        apiService.searchSupplier(name, apiKey).enqueue(object :Callback<SearchSupplier>{
            override fun onFailure(call: Call<SearchSupplier>, t: Throwable) {
                Timber.e("Error searching supplier: ${t.message}")
            }

            override fun onResponse(
                call: Call<SearchSupplier>,
                response: Response<SearchSupplier>
            ) {
             searchSupplierListMutableLiveData.value = response.body()
            }

        })
    }
    //Network call to update company details
    private fun updateCompany(
        companyId: Int,
        companyName: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateCompany(
            companyId, companyName, streetAdd,
            coutry,
            postCode,
            telephone,
            email,
            web
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error updating company details ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                companyUpdateMessage.value = message
            }
        })
    }

    //Network call to update
    private fun updateQuotation(quotationUpdate: QuotationUpdate) {

        apiService.updateQuotation(quotationUpdate).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Updating quotation failure: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                quotationUpdateMesage.value = message
            }
        })
    }

    //Network call search customer for quotation
    private fun searchCustomer(apiKey: String, name: String) {

        apiService.searchCustomer(name,apiKey).enqueue(object :Callback<SearchCustomerList>{
            override fun onFailure(call: Call<SearchCustomerList>, t: Throwable) {
                Timber.e("Search customer failed: ${t.message}")
            }

            override fun onResponse(
                call: Call<SearchCustomerList>,
                response: Response<SearchCustomerList>
            ) {
                searchCustListMutableLiveData.value = response.body()
            }
        })
    }

    //Network call update purchase
    private fun updatePurchase(purchaseUpdate: PurchaseUpdate) {
        apiService.updatePurchase(purchaseUpdate).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error updating purchase")
            }
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                purchaseUpdateMessage.value = message
            }
        })
    }

    //Network call to update time sheet
    private fun updateTimeSheet(
        apiKey: String,
        tid: Int,
        jobNo: String,
        quotationNo: String,
        vat: Double,
        date: String,
        name: String,
        streetAdd: String,
        coutry: String,
        postCode: String,
        telephone: String,
        comment: String,
        itemDes: String,
        paymentMethod: String,
        hrs: Int,
        unitAmount: Double,
        advanceAmount: Double,
        discountAmount: Double,
        totalAmount: Double
    ) {
        apiService.updateTimeSheet(
            apiKey,
            tid,
            jobNo,
            quotationNo,
            vat,
            date,
            name,
            streetAdd,
            coutry,
            postCode,
            telephone,
            comment,
            itemDes
            ,
            paymentMethod,
            hrs,
            unitAmount,
            advanceAmount,
            discountAmount,
            totalAmount
        ).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error update time sheet ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                timeSheetUpdateMessage.value = message
            }
        })
    }

    //Network call to delete Customer
    private fun deleteUser(customerId: String) {
        apiService.deleteCustomer(customerId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("JeffRepository", "${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                _deleteCustomerMessage.value = message
            }

        })
    }

    //Network call to delete supplier
    private fun deleteSupplier(supplierId: String) {

        apiService.deleteSupplier(supplierId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed to delete supplier ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                deleteSupplierMessage.value = message
            }
        })
    }

    //Network call to delete company
    private fun deleteCompany(companyId: Int) {

        apiService.deleteCompany(companyId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error delete company")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                companyDeleteMessage.value = message
            }
        })
    }

    //Network call to delte quotation
    private fun deleteQuotation(quotationId: Int) {

        apiService.deleteQuotation(quotationId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("quotation Delete failure: ${t.message} ")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                quotationDeleteMessage.value = message
            }
        })
    }

    //Network call to delete purchase
    private fun deletePurchase(purchaseId: Int) {

        apiService.deletePurchase(purchaseId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error deleting purchase")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                purchaseDeleteMessage.value = message
            }
        })
    }

    //Network call to delete time sheet
    private fun deleteTimeSheet(timeSheetId: Int) {

        apiService.deleteTimeSheet(timeSheetId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Error delete time sheet ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                timeSheetDeleteMessage.value = message
            }
        })
    }
}