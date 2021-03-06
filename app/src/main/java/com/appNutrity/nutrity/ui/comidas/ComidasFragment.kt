package com.appNutrity.nutrity.ui.comidas

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appNutrity.nutrity.adapter.RecipeAdapter
import com.appNutrity.nutrity.apis.APIClient
import com.appNutrity.nutrity.databinding.ComidasFragmentBinding
import com.appNutrity.nutrity.models.RootObjectModel
import com.appNutrity.nutrity.response.SearchRecipes
import com.appNutrity.nutrity.utils.APICredential
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

class ComidasFragment : Fragment(), android.widget.SearchView.OnQueryTextListener {

    private var _binding: ComidasFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipes: ArrayList<RootObjectModel>
    private lateinit var adapter: RecipeAdapter
    private lateinit var searchView: android.widget.SearchView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(ComidasViewModel::class.java)

        _binding = com.appNutrity.nutrity.databinding.ComidasFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        val root: View = binding.root

        binding.SearchComidas.setOnQueryTextListener(this)

        progressBar = binding.progressBar
        recyclerView = binding.rvRecipes
        searchView = binding.SearchComidas
        searchView.onActionViewCollapsed()
        searchView.setOnQueryTextListener(this)

        return root
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchRecipes(query)
        }
        return true
    }

    override fun onQueryTextChange(nextText: String?): Boolean {
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun searchRecipes(query: String) {

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {

            progressBar.visibility = View.VISIBLE

            var searchRecipesCall: Call<SearchRecipes> = withContext(Dispatchers.IO) {
                var retrofit: Retrofit = Retrofit.Builder().baseUrl(APICredential().BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                var apiClient: APIClient = retrofit.create(APIClient::class.java)
                return@withContext apiClient.getRecipesBySearch(
                    APICredential().TYPE,
                    query,
                    APICredential().APP_ID,
                    APICredential().API_KEY
                )
            }


            delay(1000)
            searchRecipesCall.enqueue(object : Callback<SearchRecipes> {
                override fun onResponse(
                    call: Call<SearchRecipes>,
                    response: Response<SearchRecipes>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        recipes = response.body()!!.getFoodRecipes()
                    }
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    adapter = RecipeAdapter(recipes)
                    recyclerView.adapter = adapter
                    progressBar.visibility = View.GONE

                }

                override fun onFailure(call: Call<SearchRecipes>, t: Throwable) {
                    Toast.makeText(context, "Ha ocurrido un error" + t.message, Toast.LENGTH_SHORT)
                        .show()
                }

            })

        }
    }

    override fun onResume() {
        super.onResume()
        binding.SearchComidas.setQuery("", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
