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
import okhttp3.RequestBody
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

    //Store invoice list
    private var invoiceListMutableLiveData = MutableLiveData<InvoiceList>()

    //Store message of invoice save
    private var invoiceSaveMutableLiveData = MutableLiveData<String>()

    //Store message of invoice update
    private var invoiceUpdateMutableLiveData = MutableLiveData<String>()

    //Store delete invoice message
    private var invoiceDeleteMutableLiveData = MutableLiveData<String>()

    //Store search purchase value
    private var purchaseSearchMutableLiveData = MutableLiveData<Purchase>()

    //Store search invoice value
    private var invoiceSearchMutableLiveData = MutableLiveData<InvoiceList>()

    //Store search time sheet value
    private var timeSheetSearchMutableLiveData = MutableLiveData<TimeSheet>()

    //Store search quotation value
    private var quotationSearchMutableLiveData = MutableLiveData<Quotation>()

    //Store password change message
    private var changePasswordMutableLiveData = MutableLiveData<String>()

    //Store image message
    private var uploadCompanyLogo = MutableLiveData<CompanyLogo>()

    //Store logo upload message
    private var uploadLogoMutableLiveData = MutableLiveData<String>()

    //Store logo list
    private var logoListMutableLiveData = MutableLiveData<Logos>()

    //Store details
    private var detailMutableLiveData = MutableLiveData<Detail>()

    //Get country list
    private var countryListMutableLiveData = MutableLiveData<List<Country>>()

    //Delete logo
    private var deleteLogoMutableLiveData = MutableLiveData<String>()

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

    //Delete logo
    fun deleteLogo(comId: String, imageId: String): LiveData<String> {
        deleteSingleLogo(comId, imageId)
        return deleteLogoMutableLiveData
    }

    //GEt country list
    fun getcountries(): LiveData<List<Country>> {
        getCountryList()
        return countryListMutableLiveData
    }

    //Get details
    fun getDetails(pageId: Int): LiveData<Detail> {
        getComDetails(pageId)
        return detailMutableLiveData
    }

    //Upload company logo
    fun uploadLogo(comid: String, logobody: RequestBody): LiveData<CompanyLogo> {
        uploadLogoToServer(comid, logobody)
        return uploadCompanyLogo
    }

    //Add customer
    fun getAddCustomerMessage(
        comid: String,
        customerName: String, streetAdd: String, coutry: String, county: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addCustomer(comid, customerName, streetAdd, coutry, county, postCode, telephone, email, web)
        return _addCustomerMessage
    }

    //Add Supplier
    fun getAddSupplierMessage(
        comid: String, supplierName: String, streetAdd: String, coutry: String, county: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        addSupplier(comid, supplierName, streetAdd, coutry, county, postCode, telephone, email, web)
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

    //Add Invoice
    fun getAddInvoiceMessage(invoiceAdd: InvoiceAdd): LiveData<String> {
        addInvoice(invoiceAdd)
        return invoiceSaveMutableLiveData
    }

    //Add Purchase
    fun getAddPurchaseMessage(purchaseAdd: PurchaseAdd): LiveData<String> {
        addPurchase(purchaseAdd)
        return purchaseAddMessage
    }

    //Add time sheet
    fun getAddTimeSheetMessage(timeSheetAdd: TimeSheetAdd): LiveData<String> {
        addTimeSheet(timeSheetAdd)
        return timeSheetAddMessage
    }

    //Update customer
    fun getUpdateCustomerMessage(
        comid: String,
        customerId: String,
        customerName: String, streetAdd: String, coutry: String, county: String,
        postCode: String, telephone: String, email: String, web: String
    ): LiveData<String> {
        updateCustomer(
            comid,
            customerId,
            customerName,
            streetAdd,
            coutry,
            county,
            postCode,
            telephone,
            email,
            web
        )
        return _updateCustomerMessage
    }

    //Update Supplier
    fun getUpdateSupplierMessage(
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
        updateSupplier(
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
        return updateSupplierMessage
    }

    //Get search supplier list
    fun getSearchSupplierList(
        comid: String,
        name: String,
        apiKey: String
    ): LiveData<SearchSupplier> {
        getSearchSupplier(comid, name, apiKey)
        return searchSupplierListMutableLiveData
    }

    //Update Company details
    fun getUpdateCompanyMessage(
        apiKey: String,
        companyId: String,
        streetAdd: String,
        companyDes: String,
        country: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ): LiveData<String> {
        updateCompany(
            apiKey,
            companyId,
            streetAdd,
            companyDes,
            country,
            county,
            postCode,
            telephone,
            email,
            web
        )
        return companyUpdateMessage
    }

    //Update Supplier
    fun getUpdateQuotationMessage(quotationUpdate: QuotationUpdate): LiveData<String> {
        updateQuotation(
            quotationUpdate
        )
        return quotationUpdateMesage
    }

    //Update Invoice
    fun getUpdateInvoiceMessage(invoiceUpdate: InvoiceUpdate): LiveData<String> {
        updateInvoice(invoiceUpdate)
        return invoiceUpdateMutableLiveData
    }

    //Search customer for quotation
    fun searchCustomerList(
        comid: String,
        apiKey: String,
        name: String
    ): LiveData<SearchCustomerList> {
        searchCustomer(comid, apiKey, name)
        return searchCustListMutableLiveData
    }

    //Update Purchase
    fun getUpdatePurchaseMessage(purchaseUpdate: PurchaseUpdate): LiveData<String> {
        updatePurchase(purchaseUpdate)
        return purchaseUpdateMessage
    }

    //Update time sheet
    fun getUpdateTimeSheetMessage(timeSheetUpdate: TimeSheetUpdate): LiveData<String> {
        updateTimeSheet(timeSheetUpdate)
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

    //Delete Invoice
    fun getDeleteInvoiceMessage(apiKey: String, invoiceId: Int): LiveData<String> {
        deleteInvoice(apiKey, invoiceId)
        return invoiceDeleteMutableLiveData
    }

    //Delete TimeSheet
    fun getDeleteTimeSheetMessage(timeSheetId: Int): LiveData<String> {
        deleteTimeSheet(timeSheetId)
        return timeSheetDeleteMessage
    }

    //Get All Customer
    fun getAllCustomer(companyId: String): LiveData<Customer> {
        getCustomerList(companyId)
        return _customerList
    }

    //Get All Suppliers
    fun getAllSuppliers(comid: String): LiveData<Supplier> {
        getSupplierList(comid)
        return supplierList
    }

    //Get all Company
    fun getAllCompany(): LiveData<Company> {
        getCompanyList()
        return companyList
    }

    //Get all purchase
    fun getAllPurchase(comid: String, apiKey: String): LiveData<Purchase> {
        getPurchaseList(comid, apiKey)
        return purchaseList
    }

    //Get All quotation
    fun getAllQuotation(comid: String, apiKey: String): LiveData<Quotation> {
        getQuotationList(comid, apiKey)
        return quotationList
    }

    //Get all invoices
    fun getAllInvoices(comid: String, apiKey: String): LiveData<InvoiceList> {
        getInvoiceList(comid, apiKey)
        return invoiceListMutableLiveData
    }

    //Get all time sheet
    fun getAllTimeSheet(comid: String, apiKey: String): LiveData<TimeSheet> {
        getTimeSheetList(comid, apiKey)
        return timeSheetListMutableLiveData
    }

    //search purchase
    fun searchPurchase(comid: String, jobNo: String?, quotationNo: String?, customerName: String?, apiKey: String): LiveData<Purchase> {
        searchPurchaseList(comid, jobNo, quotationNo, customerName, apiKey)
        return purchaseSearchMutableLiveData
    }

    //Search invoice
    fun searchInvoice(
        comid: String,
        jobNo: String?,
        quotationNo: String?,
        customerName: String?,
        apiKey: String
    ): LiveData<InvoiceList> {
        searchInvoiceList(comid, jobNo, quotationNo, customerName, apiKey)
        return invoiceSearchMutableLiveData
    }

    //Search Timesheet
    fun searchTimeSheet(
        comid: String, jobNo: String?,
        quotationNo: String?,
        custName: String?, apiKey: String
    ): LiveData<TimeSheet> {
        searchTimeSheetList(comid, jobNo, quotationNo, custName, apiKey)
        return timeSheetSearchMutableLiveData
    }

    //Search quotation
    fun searchQuotation(comid: String, jobNo: String, apiKey: String): LiveData<Quotation> {
        searchQuotationList(comid, jobNo, apiKey)
        return quotationSearchMutableLiveData
    }

    //Search quotation
    fun searchQuotationByQuotation(
        comid: String,
        quotationNo: String,
        apiKey: String
    ): LiveData<Quotation> {
        searchQuotationListByQuotation(comid, quotationNo, apiKey)
        return quotationSearchMutableLiveData
    }

    //Search quotation
    fun searchQuotationByCustomer(
        comid: String,
        customerName: String,
        apiKey: String
    ): LiveData<Quotation> {
        searchQuotationListByCustomer(comid, customerName, apiKey)
        return quotationSearchMutableLiveData
    }

    //Change password
    fun changePassword(comid: String, password: String, newPass: String): LiveData<String> {
        setNewPassword(comid, password, newPass)
        return changePasswordMutableLiveData
    }

    //Upload logo
    fun uploadLogoB(comid: String, logo: RequestBody): LiveData<String> {
        uploadImage(comid, logo)
        return uploadLogoMutableLiveData
    }

    //Get logo list
    fun getLogoList(comid: String): LiveData<Logos> {
        getLogos(comid)
        return logoListMutableLiveData
    }

    //Network call to get all customer list
    private fun getCustomerList(companyId: String) {

        apiService.getCustomerList(companyId).enqueue(object : Callback<Customer> {
            override fun onFailure(call: Call<Customer>, t: Throwable) {
                Timber.e("JeffRepository ${t.message}")
            }

            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {

                _customerList.value = response.body()
            }

        })
    }

    //Network call to get all supplier list
    private fun getSupplierList(comid: String) {

        apiService.getSupplierList(comid).enqueue(object : Callback<Supplier> {
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
    private fun getQuotationList(comid: String, apiKey: String) {

        apiService.getQuotationList(comid, apiKey).enqueue(object : Callback<Quotation> {
            override fun onFailure(call: Call<Quotation>, t: Throwable) {
                Timber.e("Error getting list of quotation: ${t.message}")
            }

            override fun onResponse(call: Call<Quotation>, response: Response<Quotation>) {
                quotationList.value = response.body()
            }
        })
    }

    //Network call to get all invoices
    private fun getInvoiceList(comid: String, apiKey: String) {
        apiService.getInvoiceList(comid, apiKey).enqueue(object : Callback<InvoiceList> {
            override fun onFailure(call: Call<InvoiceList>, t: Throwable) {
                Timber.e("Failed getting invoices")
            }

            override fun onResponse(call: Call<InvoiceList>, response: Response<InvoiceList>) {
                invoiceListMutableLiveData.value = response.body()
            }
        })
    }

    //Network call to get purchase list
    private fun getPurchaseList(comid: String, apiKey: String) {
        apiService.getPurchaseList(comid, apiKey).enqueue(object : Callback<Purchase> {
            override fun onFailure(call: Call<Purchase>, t: Throwable) {
                Timber.e("Error getting purchase list")
            }

            override fun onResponse(call: Call<Purchase>, response: Response<Purchase>) {
                purchaseList.value = response.body()
            }
        })
    }

    //Network call to get Time Sheet list
    private fun getTimeSheetList(comid: String, apiKey: String) {

        apiService.getTimeSheetList(comid, apiKey).enqueue(object : Callback<TimeSheet> {
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
        comid: String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {

        apiService.addCustomer(
            comid,
            customerName,
            streetAdd,
            coutry,
            county,
            postCode,
            telephone,
            email,
            web
        )
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
        comid: String,
        supplierName: String,
        streetAdd: String,
        coutry: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {

        apiService.addSupplier(
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

    //Network call to save invoice
    private fun addInvoice(invoiceAdd: InvoiceAdd) {

        apiService.addInvoice(invoiceAdd).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("failed saving invoice: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                invoiceSaveMutableLiveData.value = message
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
        timeSheetAdd: TimeSheetAdd
    ) {

        apiService.addTimeSheet(
            timeSheetAdd
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
        comid: String,
        customerId: String,
        customerName: String,
        streetAdd: String,
        coutry: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateCustomer(
            comid,
            customerId,
            customerName,
            streetAdd,
            coutry,
            county,
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
    ) {
        apiService.updateSupplier(
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
    private fun getSearchSupplier(comid: String, name: String, apiKey: String) {

        apiService.searchSupplier(comid, name, apiKey).enqueue(object : Callback<SearchSupplier> {
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
        apiKey: String,
        companyId: String,
        streetAdd: String,
        companyDes: String,
        country: String,
        county: String,
        postCode: String,
        telephone: String,
        email: String,
        web: String
    ) {
        apiService.updateCompany(
            apiKey,
            companyId,
            streetAdd,
            companyDes,
            country,
            county,
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

    //Network call to update invoice
    private fun updateInvoice(invoiceUpdate: InvoiceUpdate) {

        apiService.updateInvoice(invoiceUpdate).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed updating invoice")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                invoiceUpdateMutableLiveData.value = message
            }
        })
    }

    //Network call search customer for quotation
    private fun searchCustomer(comid: String, apiKey: String, name: String) {

        apiService.searchCustomer(comid, name, apiKey)
            .enqueue(object : Callback<SearchCustomerList> {
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
    private fun updateTimeSheet(timeSheetUpdate: TimeSheetUpdate) {
        apiService.updateTimeSheet(timeSheetUpdate).enqueue(object : Callback<String> {
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

    //Network call to delete invoice
    private fun deleteInvoice(apiKey: String, invoiceId: Int) {

        apiService.deleteInvoice(apiKey, invoiceId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("failed to delete invoice data: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                invoiceDeleteMutableLiveData.value = message
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

    //Network call to search purchase list
    private fun searchPurchaseList(comid: String, jobNo: String?, quotationNo: String?, customerName: String?, apiKey: String) {

        apiService.searchPurchase(comid, jobNo, quotationNo, customerName, apiKey).enqueue(object : Callback<Purchase> {
            override fun onFailure(call: Call<Purchase>, t: Throwable) {
                Timber.e("Failed to get purchase by search: ${t.message}")
                purchaseSearchMutableLiveData.value = null
            }

            override fun onResponse(call: Call<Purchase>, response: Response<Purchase>) {
                if (response.isSuccessful && response.body() != null) {
                    purchaseSearchMutableLiveData.value = response.body()
                } else {
                    purchaseSearchMutableLiveData.value = null
                }
            }

        })
    }

    //Network call to search invoice
    private fun searchInvoiceList(
        comid: String,
        jobNo: String?,
        quotationNo: String?,
        customerName: String?,
        apiKey: String
    ) {
        apiService.searchInvoice(comid, jobNo, quotationNo, customerName, apiKey)
            .enqueue(object : Callback<InvoiceList> {
                override fun onFailure(call: Call<InvoiceList>, t: Throwable) {
                    Timber.e("Failed to search invoice: ${t.message}")
                    invoiceSearchMutableLiveData.value = null
                }

                override fun onResponse(call: Call<InvoiceList>, response: Response<InvoiceList>) {
                    if (response.isSuccessful && response.body() != null) {
                        Timber.e("Success in search invoice: ${response.body().toString()}")
                        invoiceSearchMutableLiveData.value = response.body()
                    } else {
                        invoiceSearchMutableLiveData.value = null
                    }
                }
            })
    }

    //Network call to search time sheet
    private fun searchTimeSheetList(
        comid: String,
        jobNo: String?,
        quotationNo: String?,
        custName: String?,
        apiKey: String
    ) {
        apiService.searchTimeSheet(comid, jobNo, quotationNo, custName, apiKey)
            .enqueue(object : Callback<TimeSheet> {
                override fun onFailure(call: Call<TimeSheet>, t: Throwable) {
                    Timber.e("Failed to search time sheet: ${t.message}")
                    timeSheetSearchMutableLiveData.value = null
                }

                override fun onResponse(call: Call<TimeSheet>, response: Response<TimeSheet>) {
                    if (response.isSuccessful && response.body() != null) {
                        timeSheetSearchMutableLiveData.value = response.body()
                    } else {
                        timeSheetSearchMutableLiveData.value = null
                    }
                }
            })
    }

    //Network call to search quotation list
    private fun searchQuotationList(comid: String, jobNo: String, apiKey: String) {

        apiService.searchQuotation(comid, jobNo, apiKey).enqueue(object : Callback<Quotation> {
            override fun onFailure(call: Call<Quotation>, t: Throwable) {
                Timber.e("failed to get quotation search list")
            }

            override fun onResponse(call: Call<Quotation>, response: Response<Quotation>) {
                if (response.isSuccessful && response.body() != null) {
                    quotationSearchMutableLiveData.value = response.body()
                } else {
                    quotationSearchMutableLiveData.value = null
                }
            }
        })
    }

    //Network call to search quotation by quotation no
    private fun searchQuotationListByQuotation(comid: String, quotationNO: String, apiKey: String) {

        apiService.searchQuotationByQuta(comid, quotationNO, apiKey)
            .enqueue(object : Callback<Quotation> {
                override fun onFailure(call: Call<Quotation>, t: Throwable) {
                    Timber.e("Failed to get quotation by quotation no. : ${t.message}")
                }

                override fun onResponse(call: Call<Quotation>, response: Response<Quotation>) {
                    Timber.e("Response is: ${response.body().toString()},")
                    quotationSearchMutableLiveData.value = response.body()
                }
            })
    }

    //Network call to search quotation by  customer name
    private fun searchQuotationListByCustomer(
        comid: String,
        custoemerName: String,
        apiKey: String
    ) {

        apiService.searchQuotationByName(comid, custoemerName, apiKey)
            .enqueue(object : Callback<Quotation> {
                override fun onFailure(call: Call<Quotation>, t: Throwable) {
                    Timber.e("Failed to get quotation by quotation no. : ${t.message}")
                }

                override fun onResponse(call: Call<Quotation>, response: Response<Quotation>) {
                    quotationSearchMutableLiveData.value = response.body()
                }
            })
    }

    //Network call to change password
    private fun setNewPassword(comid: String, password: String, newPass: String) {
        apiService.changePassword(comid, password, newPass).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed to change password")
                changePasswordMutableLiveData.value = "Something went wrong! try again later"
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                changePasswordMutableLiveData.value = message
            }
        })
    }

    //Network call to upload logo
    private fun uploadLogoToServer(comid: String, logobody: RequestBody) {

        apiService.uploadCompanyLogo(comid, logobody).enqueue(object : Callback<CompanyLogo> {
            override fun onFailure(call: Call<CompanyLogo>, t: Throwable) {
                Timber.e("Failed to upload logo, ${t.message}")
            }

            override fun onResponse(call: Call<CompanyLogo>, response: Response<CompanyLogo>) {
                Timber.e("Response: ${response.body().toString()}")
                uploadCompanyLogo.value = response.body()
            }
        })
    }

    //Network call to upload image
    private fun uploadImage(comid: String, logo: RequestBody) {

        apiService.uploadLogo(comid, logo).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed uploading logo: ${t.message}")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Timber.e("Message sucess: ${response.body().toString()}")
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                uploadLogoMutableLiveData.value = message
            }
        })
    }

    //Network call to get logos
    private fun getLogos(comid: String) {

        apiService.getLogoList(comid).enqueue(object : Callback<Logos> {
            override fun onFailure(call: Call<Logos>, t: Throwable) {
                Timber.e("Failed to get logo list: ${t.message}")
            }

            override fun onResponse(call: Call<Logos>, response: Response<Logos>) {
                Timber.e("Logo list response: ${response.body().toString()}")
                logoListMutableLiveData.value = response.body()
            }
        })
    }

    //Netwrok call to  get details
    private fun getComDetails(pageId: Int) {

        apiService.getDetails(pageId).enqueue(object : Callback<Detail> {
            override fun onFailure(call: Call<Detail>, t: Throwable) {
                Timber.e("failed to get details: ${t.message}")
            }

            override fun onResponse(call: Call<Detail>, response: Response<Detail>) {
                detailMutableLiveData.value = response.body()
            }
        })
    }

    //Network call to get country list
    private fun getCountryList() {
        apiService.getCountryList().enqueue(object : Callback<List<Country>> {
            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Timber.e("Failed to get country list : ${t.message}")
            }

            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                countryListMutableLiveData.value = response.body()
            }
        })
    }

    //Network call to delete logo
    private fun deleteSingleLogo(comId: String, imageId: String) {

        apiService.deleteLogo(comId, imageId).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Timber.e("Failed to delete logo")
                deleteLogoMutableLiveData.value = t.message
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Timber.e("Message sucess: ${response.body().toString()}")
                val jsonObject = JSONObject(response.body()!!)
                val message = jsonObject.optString("message")
                deleteLogoMutableLiveData.value = message
            }
        })
    }
}