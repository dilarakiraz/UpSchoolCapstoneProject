package com.dilarakiraz.upschoolcapstoneproject.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemDetailImageBinding

/**
 * Created on 31.10.2023
 * @author Dilara Kiraz
 */

class DetailImagesAdapter :
    RecyclerView.Adapter<DetailImagesAdapter.ProductsViewHolder>() {

    private val list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder =
        ProductsViewHolder(
            ItemDetailImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) =
        holder.bind(list[position])

    inner class ProductsViewHolder(private var binding: ItemDetailImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {
            binding.ivProduct.loadImage(item)
        }
    }

    override fun getItemCount() = list.size

    fun updateList(updatedList: List<String>) {
        list.clear()
        list.addAll(updatedList)
        notifyDataSetChanged()
    }
}