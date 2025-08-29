package com.example.cashflow_app

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.Transaction

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.transaction_title)
        val amount: TextView = view.findViewById(R.id.transaction_amount)
        val date: TextView = view.findViewById(R.id.transaction_date)
        val type: TextView = view.findViewById(R.id.transaction_type)
        val editIcon: ImageView = view.findViewById(R.id.edit_icon)
        val deleteIcon: ImageView = view.findViewById(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.itemView.context

        holder.title.text = transaction.title
        holder.amount.text = String.format("%.2f", transaction.amount)
        holder.date.text = transaction.date
        holder.type.text = transaction.type

        holder.deleteIcon.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Delete Transaction")
                setMessage("Are you sure you want to delete this transaction?")
                setPositiveButton("Yes") { _, _ ->
                    transactions.removeAt(position)
                    SharedPrefHelper.saveTransactions(context, transactions)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, transactions.size)
                    Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    onDataChanged()
                }
                setNegativeButton("No", null)
                create()
                show()
            }
        }

        holder.editIcon.setOnClickListener {
            val intent = Intent(context, EditTransaction::class.java).apply {
                putExtra("transaction_id", transaction.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = transactions.size
}
