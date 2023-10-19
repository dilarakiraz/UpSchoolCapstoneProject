package com.dilarakiraz.upschoolcapstoneproject.ui.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.showPopup
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()

    private val saleProductsAdapter by lazy {
        SaleProductsAdapter(
            ::onProductClick,
            ::onFavoriteClick
        )
    }

    private val allProductsAdapter by lazy {
        AllProductsAdapter(
            ::onProductClick,
            ::onFavoriteClick
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            rvSaleProducts.adapter = saleProductsAdapter
            rvAllProducts.adapter = allProductsAdapter
        }
        observeData()

        loadUserNickname()
    }

    private fun observeData() = with(binding) {
        viewModel.mainState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is HomeState.Loading -> progressBar.visible()

                is HomeState.Success -> {
                    saleProductsAdapter.submitList(state.saleProducts)
                    allProductsAdapter.submitList(state.products)
                    progressBar.gone()
                }

                is HomeState.Error -> {
                    showPopup(state.throwable.message)
                    progressBar.gone()
                }

                is HomeState.EmptyScreen -> {
                    progressBar.gone()
                }

                else -> {}
            }
        }
    }

    private fun loadUserNickname() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val db = Firebase.firestore
            db.collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    val nickName = document.getString("nickname")
                    if (nickName != null) {
                        binding.tvNickname.text = nickName
                    }
                }
                .addOnFailureListener {}
        }
    }

    private fun onProductClick(id: Int) {
        val action = HomeFragmentDirections.homeToDetail(id)
        findNavController().navigate(action)
    }

    private fun onFavoriteClick(product: ProductUI) {
        viewModel.setFavoriteState(product)
    }
}