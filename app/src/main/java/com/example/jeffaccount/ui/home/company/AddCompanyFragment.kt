package com.example.jeffaccount.ui.home.company

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jeffaccount.utils.FileUtils
import com.example.jeffaccount.LogInActivity
import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.FragmentAddCompanyBinding
import com.example.jeffaccount.model.CompanyDetails
import com.example.jeffaccount.model.Logo
import com.example.jeffaccount.model.Logos
import com.example.jeffaccount.ui.MainActivity
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class AddCompanyFragment : Fragment(), OnLogoItemClickListener {

    private lateinit var addCompanyBinding: FragmentAddCompanyBinding
    private lateinit var addCompanyViewModel: CompanyViewModel
    private lateinit var companyDetails: CompanyDetails
    private lateinit var companyLogoUri: Uri
    private lateinit var companyLogoBody: RequestBody
    private lateinit var logoUri: Uri
    private lateinit var logoBody: RequestBody
    private lateinit var logoListAdapter: LogoListAdapter
    private lateinit var companyLogo: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addCompanyBinding = FragmentAddCompanyBinding.inflate(inflater, container, false)

        //Initialize ViewModel class
        addCompanyViewModel = ViewModelProvider(this).get(CompanyViewModel::class.java)

        val activity = activity as MainActivity
        activity.setToolbarText("Company")
        companyDetails = activity.companyDetails
        companyDetails?.let {
            addCompanyBinding.company = companyDetails
            Timber.e("Com image: ${companyDetails.caomimge}")
            Timber.e("Com id: ${companyDetails.comid}")
            companyDetails.caomimge?.let {
                Timber.e("image:${companyDetails.caomimge}")
                companyLogo =
                    "https://alphabusinessdesigns.com/wordpress/appproject/jtapp/${companyDetails.caomimge}"
                Picasso.get().load(companyLogo).into(addCompanyBinding.addCompanyLogoIv)
            }
        }

        //Set on click listener to save button
        addCompanyBinding.companyUpdateButton.setOnClickListener {
            val name = addCompanyBinding.companyNameTextInput.text.toString()
            val companyDes = addCompanyBinding.companyDesTextInput.text.toString()
            val street = addCompanyBinding.companyStreetTextInput.text.toString()
            val country = addCompanyBinding.companyCountryTv.text.toString()
            val postCode = addCompanyBinding.companyPostCodeTextInput.text.toString()
            val telephone = addCompanyBinding.companyTelephoneTextInput.text.toString()
            val email = addCompanyBinding.companyEmailTextInput.text.toString()
            val web = addCompanyBinding.companyWebTextInput.text.toString()
            val county = addCompanyBinding.companyCountyTextInput.text.toString()
            val comid = companyDetails.comid

            when {
                name.isEmpty() -> addCompanyBinding.companyNameTextInputLayout.error =
                    getString(R.string.enter_company_name)
                else -> {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setTitle("Do you want to update your company details?")
                    builder.setPositiveButton(
                        "Update",
                        DialogInterface.OnClickListener { dialog, which ->
                            addCompanyViewModel.updateCompany(
                                "AngE9676#254r5",
                                comid,
                                street,
                                companyDes,
                                country,
                                county,
                                postCode,
                                telephone,
                                email,
                                web
                            )
                                .observe(viewLifecycleOwner,
                                    Observer {
                                        it?.let {
                                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                            val intent = Intent(activity, LogInActivity::class.java)
                                            startActivity(intent)
                                            activity.finish()
                                        }
                                    })
                            dialog.dismiss()
                        })
                    builder.setNegativeButton(
                        "Cancel",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }

        //Set on click list to compny logo
        addCompanyBinding.addCompanyLogoIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1)
        }

        //Set on click listener to the add logo image
        addCompanyBinding.addLogoIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2)
        }

        //Adding text watcher to all input fields
        addCompanyBinding.companyNameTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyNameTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyNameTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyStreetTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyStreetTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyStreetTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyPostCodeTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyPostCodeTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyPostCodeTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyTelephoneTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyTelephoneTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyTelephoneTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyEmailTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyEmailTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyEmailTextInputLayout.isErrorEnabled = false
            }
        })

        addCompanyBinding.companyWebTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addCompanyBinding.companyWebTextInputLayout.isErrorEnabled = true
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                addCompanyBinding.companyWebTextInputLayout.isErrorEnabled = false
            }
        })

        //Get logo list
        addCompanyViewModel.getLogoList(companyDetails.comid).observe(viewLifecycleOwner, Observer {
            it?.let {
                populateLogos(it)
            }
        })

        return addCompanyBinding.root
    }

    private fun populateLogos(logos: Logos) {
        logos.logoList?.let {
            Timber.e("Logo list size: ${logos.logoList.size}")
            logoListAdapter = LogoListAdapter(logos.logoList, this)
            addCompanyBinding.logoRecyclerView.adapter = logoListAdapter
            logoListAdapter.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val dialog = createLoadingDialog()
            dialog?.show()
            companyLogoUri = data?.data!!
            Timber.e("Uri : $companyLogoUri")
            val file = File(FileUtils.getPath(companyLogoUri, context!!)!!)
            companyLogoBody = RequestBody.create(
                MediaType.parse(activity!!.contentResolver.getType(companyLogoUri)!!),
                file
            )
            addCompanyViewModel.uploadCompanyLogo(companyDetails.comid, companyLogoBody)
                .observe(viewLifecycleOwner, Observer {
                    it?.let {
                        dialog?.dismiss()
                        val message = it.message
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        val imgName = it.caomimge
                        val path =
                            "https://alphabusinessdesigns.com/wordpress/appproject/jtapp/$imgName".toUri()
                        Picasso.get().load(path).into(addCompanyBinding.addCompanyLogoIv)
                    }
                })


        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val dialog = createLoadingDialog()
            dialog?.show()
            logoUri = data?.data!!
            Timber.e("Uri: $logoUri")
            val file = File(FileUtils.getPath(logoUri, context!!)!!)
            logoBody = RequestBody.create(
                MediaType.parse(activity!!.contentResolver.getType(logoUri)!!),
                file
            )
            addCompanyViewModel.uploadImage(companyDetails.comid, logoBody)
                .observe(viewLifecycleOwner,
                    Observer {
                        it?.let {
                            dialog?.dismiss()
                        }
                    })
        }
    }

    //Create a loading Dialog
    private fun createLoadingDialog(): androidx.appcompat.app.AlertDialog? {
        val layout = LayoutInflater.from(context).inflate(R.layout.loading_layout, null)
        val builder = context.let { androidx.appcompat.app.AlertDialog.Builder(context!!) }
        builder.setCancelable(false)
        builder.setView(layout)
        return builder.create()
    }

    override fun onLogoItemClick(logo: Logo) {

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Do you want to delete this logo?")
        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
            addCompanyViewModel.deletelogo(companyDetails.comid, logo.imgid)
                .observe(viewLifecycleOwner,
                    Observer {
                        it?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            findNavController().navigate(
                                AddCompanyFragmentDirections.actionAddCompanyFragmentToHomeFragment(
                                    "welcome"
                                )
                            )
                        }
                    })
            dialog.dismiss()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        val dialog = builder.create()
        dialog.show()
    }
}
