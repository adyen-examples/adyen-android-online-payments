package com.example.adyen.checkout.ui.cart


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.adyen.checkout.R
import com.example.adyen.checkout.ui.cart.CartContent.CartItem
import com.example.adyen.checkout.ui.cart.CartViewFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_cart_view.view.*

/**
 * [RecyclerView.Adapter] that can display a [CartItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class CartRecyclerViewAdapter(
    private val mValues: List<CartItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as CartItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_cart_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val image = holder.itemView.context.resources.getIdentifier(
            item.image,
            "drawable",
            "com.example.adyen.checkout"
        )
        holder.mImageView.setBackgroundResource(image)
        holder.mNameView.text = item.name
        holder.mPriceView.text = item.price.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mImageView: ImageView = mView.item_image
        val mNameView: TextView = mView.item_name
        val mPriceView: TextView = mView.item_price

        override fun toString(): String {
            return super.toString() + " '" + mNameView.text + "'" + mPriceView.text + "'"
        }
    }
}
