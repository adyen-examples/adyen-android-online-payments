package com.example.adyen.checkout.ui.components


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adyen.checkout.R
import com.example.adyen.checkout.ui.components.ComponentSelectContent.ComponentSelectItem
import com.example.adyen.checkout.ui.components.ComponentSelectFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_component_select.view.*

/**
 * [RecyclerView.Adapter] that can display a [ComponentSelectItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class ComponentSelectRecyclerViewAdapter(
    private val mValues: List<ComponentSelectItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ComponentSelectRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ComponentSelectItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_component_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mContentView.text = item.content

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
