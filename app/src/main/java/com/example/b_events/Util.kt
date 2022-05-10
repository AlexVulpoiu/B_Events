package com.example.b_events

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.b_events.databinding.EventItemViewBinding

class EventItemViewHolder(val viewDataBinding: EventItemViewBinding):
    RecyclerView.ViewHolder(viewDataBinding.root)

@BindingAdapter("imageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView.context).load(url).into(imageView)
    }
}