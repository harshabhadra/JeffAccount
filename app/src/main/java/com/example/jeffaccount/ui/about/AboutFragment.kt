package com.example.jeffaccount.ui.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jeffaccount.R
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class AboutFragment : Fragment() {

    private lateinit var aboutViewModel:AboutViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.about_fragment,container,false)

        val aboutTv:TextView = view.findViewById(R.id.about_tv)
        //Initializing ViewModel class
        aboutViewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        aboutViewModel.getDetails(1).observe(viewLifecycleOwner, Observer {
            aboutTv.text = it.posts[0].pagecontent
        })
        return  view
        }

}
