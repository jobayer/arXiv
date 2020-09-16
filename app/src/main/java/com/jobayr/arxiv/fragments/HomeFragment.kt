package com.jobayr.arxiv.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jobayr.arxiv.R

class HomeFragment : Fragment() {

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        init()
        return rootView
    }

    // Starting point of this fragment
    private fun init() {
        
    }
}