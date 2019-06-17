package airport.transfer.sale.adapter

import android.view.ViewGroup
import airport.transfer.sale.GlideImageLoader
import airport.transfer.sale.rest.models.response.model.Plan
import airport.transfer.sale.ui.view.plan.*
import android.widget.EditText


class PlansAdapter(private val imageLoader: GlideImageLoader/*, private val listener: (EditText) -> Unit*/) : RecyclerViewAdapterBase<Plan, BasePlanView>() {

   /* private var mCurrentDiscount = 0

    companion object {
        private const val HEADER = 15
    }*/

    override fun onBindViewHolder(holder: ViewHolderWrapper<BasePlanView>?, position: Int) {
        /*if (position != 0) */(holder?.view as? CarPlanView)?.bind(items[position], imageLoader)
        //else (holder?.view as? PromocodeView)?.bind(mCurrentDiscount, listener)
    }

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): BasePlanView {
        return/* if (viewType == HEADER) PromocodeView_.build(parent.context)
        else*/ CarPlanView_.build(parent.context)
    }

   /* override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return HEADER
        return super.getItemViewType(position)
    }

    override fun getItem(position: Int): Plan = super.getItem(position - 1)

    fun setDiscount(discount: Int){
        mCurrentDiscount = discount
        notifyItemChanged(0)
    }

    override fun replaceAll(items: List<Plan>) {
        if (this.items.isEmpty()) addAll(items)
        else {
            this.items.clear()
            this.items.addAll(items)
            notifyItemRangeChanged(1, items.size)
        }
    }*/
}