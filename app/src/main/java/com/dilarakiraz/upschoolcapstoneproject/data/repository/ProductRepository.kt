package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.mapper.mapToProductEntity
import com.dilarakiraz.upschoolcapstoneproject.data.model.mapper.mapToProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.AddToCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.ClearCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.DeleteFromCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.BaseResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.source.local.ProductDao
import com.dilarakiraz.upschoolcapstoneproject.data.source.remote.ProductService


/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

class ProductRepository(
    private val productService: ProductService,
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(): Resource<List<ProductUI>> {
        val favorites = productDao.getProductIds()
        val result = productService.getProducts()

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favorites)
            }
        }
    }

    suspend fun getSaleProducts(): Resource<List<ProductUI>> {
        val favorites = productDao.getProductIds()
        val result = productService.getSaleProducts()

        return result.call {
            result.products.orEmpty().filter { (it.salePrice ?: 0.0) > 0.0 }.map {
                it.mapToProductUI(favorites)
            }
        }
    }

    suspend fun getProductDetail(id: Int): Resource<ProductUI> {
        val favorites = productDao.getProductIds()
        val result = productService.getProductDetail(id)

        return result.call {
            result.product.mapToProductUI(favorites)
        }
    }

    suspend fun addToFavorites(product: ProductUI) =
        productDao.addToFavorites(product.mapToProductEntity())

    suspend fun deleteFromFavorites(product: ProductUI) =
        productDao.deleteFromFavorites(product.mapToProductEntity())

    suspend fun getFavorites(): Resource<List<ProductUI>> =
        try {
            val products = productDao.getProducts().map {
                it.mapToProductUI()
            }
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun clearFavorites(): Resource<Unit> {
        return try {
            productDao.clearFavorites()
            Resource.Success(Unit)
        } catch (e: Throwable) {
            Resource.Error(e)
        }
    }

    suspend fun getCartProducts(userId: String): Resource<List<ProductUI>> {
        val favorites = productDao.getProductIds()
        val result = productService.getCartProducts(userId)

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favorites)
            }
        }
    }

    suspend fun addToCart(userId: String, id: Int): Resource<Unit> {
        val result = productService.addToCart(AddToCartRequest(userId, id))
        return result.call {}
    }

    suspend fun deleteFromCart(userId: String, id: Int): Resource<Unit> {
        val result = productService.deleteFromCart(DeleteFromCartRequest(userId, id))
        return result.call {}
    }

    suspend fun clearCart(userId: String): Resource<Unit> {
        val result = productService.clearCart(ClearCartRequest(userId))
        return result.call {}
    }

    suspend fun getProductsByCategory(category: String): Resource<List<ProductUI>> {
        val favorites = productDao.getProductIds()
        val result = productService.getProductsByCategory(category)

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favorites)
            }
        }
    }

    suspend fun getCategories(): Resource<List<String>> =
        try {
            Resource.Success(productService.getCategories().categories.orEmpty())
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun searchProduct(query: String): Resource<List<ProductUI>> {
        val favorites = productDao.getProductIds()
        val result = productService.searchProduct(query)

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favorites)
            }
        }
    }

    private fun <T : Any> BaseResponse.call(onSuccess: () -> T): Resource<T> {
        return try {
            return if (status == 200) {
                Resource.Success(onSuccess())
            } else {
                Resource.Fail(this.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(throwable = e)
        }
    }
}