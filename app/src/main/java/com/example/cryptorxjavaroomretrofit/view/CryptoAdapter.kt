package com.example.cryptorxjavaroomretrofit.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptorxjavaroomretrofit.databinding.CryptoItemBinding
import com.example.cryptorxjavaroomretrofit.model.database.CryptoEntity

class CryptoAdapter : RecyclerView.Adapter<CryptoAdapter.MyViewHolder>() {

    var cryptoList: List<CryptoEntity> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = CryptoItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cryptoList[position])
    }

    override fun getItemCount() = cryptoList.size


    inner class MyViewHolder(private val binding: CryptoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CryptoEntity) {
            with(binding) {
                textViewName.text = item.cryptoName
                textViewPrice.text = item.cryptoPrice
            }

        }

    }

}