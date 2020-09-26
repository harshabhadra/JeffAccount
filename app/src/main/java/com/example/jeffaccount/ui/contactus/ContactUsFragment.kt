package com.example.jeffaccount.ui.contactus

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.R
import com.example.jeffaccount.ui.about.AboutViewModel


class ContactUsFragment : Fragment() {

    companion object {
        fun newInstance() =
            ContactUsFragment()
    }

    private lateinit var viewModel: ContactUsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.contact_us_fragment, container, false)

        val viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        val contactTv:TextView = view.findViewById(R.id.contact_tv)
        viewModel.getDetails(2).observe(viewLifecycleOwner, Observer {
            it?.let {
                contactTv.text = it.posts[0].pagecontent
            }
        })

        return view
    }

}
