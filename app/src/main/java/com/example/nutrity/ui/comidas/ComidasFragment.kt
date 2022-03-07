package com.example.nutrity.ui.comidas

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nutrity.R

class ComidasFragment : Fragment() {

    companion object {
        fun newInstance() = ComidasFragment()
    }

    private lateinit var viewModel: ComidasViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.comidas_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComidasViewModel::class.java)
        // TODO: Use the ViewModel
    }

}