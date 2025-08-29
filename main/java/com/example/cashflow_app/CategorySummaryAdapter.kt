package com.example.cashflow_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow_app.R
import com.example.cashflow_app.models.CategorySummary

class CategorySummaryAdapter(private val summaryList: List<CategorySummary>) :
    RecyclerView.Adapter<CategorySummaryAdapter.SummaryViewHolder>() {

    class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
        val categoryTotal: TextView = itemView.findViewById(R.id.category_total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_category_summery, parent, false)
        return SummaryViewHolder(view)
    }


    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val item = summaryList[position]
        holder.categoryName.text = item.category
        holder.categoryTotal.text = "Rs. %.2f".format(item.total)
    }

    override fun getItemCount(): Int = summaryList.size
}
