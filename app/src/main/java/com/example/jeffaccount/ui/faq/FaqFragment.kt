package com.example.jeffaccount.ui.faq

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


class FaqFragment : Fragment() {

    companion object {
        fun newInstance() = FaqFragment()
    }

    private lateinit var viewModel: FaqViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.faq_fragment, container, false)
        val viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)

        val textView:TextView = view.findViewById(R.id.faq_tv)
        viewModel.getDetails(3).observe(viewLifecycleOwner, Observer {
            it?.let {
                textView.text = it.posts[0].pagecontent
            }
        })

        return view
    }

}
