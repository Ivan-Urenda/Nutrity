package com.example.nutrity.ui.platillo

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nutrity.R

class PlatilloFragment : Fragment() {

    companion object {
        fun newInstance() = PlatilloFragment()
    }

    private lateinit var viewModel: PlatilloViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.platillo_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlatilloViewModel::class.java)
        // TODO: Use the ViewModel
    }

}