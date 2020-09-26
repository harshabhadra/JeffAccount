package com.example.jeffaccount.ui

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.LogInActivity

import com.example.jeffaccount.R
import com.example.jeffaccount.databinding.ChangePasswordFragmentBinding

class ChangePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding:ChangePasswordFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ChangePasswordFragmentBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)

        val activity = activity as MainActivity
        val comId = activity.companyDetails.comid

        //Set on click listener to the change password button
        binding.changePassButton.setOnClickListener {

            val cPass = binding.currentPasswordEt.text.toString()
            val newPass = binding.newPasswordEt.text.toString()
            val conPass = binding.confirmPasswordEt.text.toString()

            when{
                cPass.isEmpty()->{
                    binding.currentPasswordEt.error = getString(R.string.enter_current_password)
                }
                newPass.isEmpty()->{
                    binding.newPasswordEt.error = getString(R.string.enter_new_password)
                }
                conPass.isEmpty()->{
                    binding.confirmPasswordEt.error = getString(R.string.confirm_new_password)
                }
                (!newPass.equals(conPass))->{
                    Toast.makeText(context,"Password mismatch", Toast.LENGTH_SHORT).show()
                }
                else->{
                    viewModel.changePassword(comId, cPass, newPass).observe(viewLifecycleOwner,
                        Observer {
                            it?.let {
                                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                                if(it == "success"){
                                    val intent = Intent(activity,LogInActivity::class.java)
                                    startActivity(intent)
                                    activity.finish()
                                }
                            }
                        })
                }
            }
        }
    }

}
