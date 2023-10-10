package com.dilarakiraz.upschoolcapstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
):ViewModel(){

    private var _mainState = MutableLiveData<HomeState>()
    val mainState : LiveData<HomeState>
        get() = _mainState

    init{
        getProducts()
    }

    private fun getProducts() = viewModelScope.launch{
        _mainState.value = HomeState.Loading
        val products = async { productRepository.getAllProducts() }.await()
        val saleProducts = async { productRepository.getSaleProducts() }.await()

        _mainState.value = when{
            products is Resource.Error -> HomeState.Error(products.throwable)
            products is Resource.Fail -> HomeState.EmptyScreen(products.message)
            saleProducts is Resource.Error -> HomeState.Error(saleProducts.throwable)
            saleProducts is Resource.Fail -> HomeState.EmptyScreen(saleProducts.message)

            else -> {
                val product = (products as Resource.Success)
                val sale = (saleProducts as Resource.Success)
                HomeState.Success(product.data, sale.data)
            }
        }
    }

    fun setFavoriteState(product: ProductUI) = viewModelScope.launch {
        if (product.isFavorite) productRepository.deleteFromFavorites(product)
        else productRepository.addToFavorites(product)
        getProducts()
    }
}

sealed interface HomeState{
    object Loading : HomeState
    data class EmptyScreen(val message : String) : HomeState
    data class Error(val throwable: Throwable) :HomeState
    data class Success(val saleProducts: List<ProductUI>, val products: List<ProductUI>):HomeState
}