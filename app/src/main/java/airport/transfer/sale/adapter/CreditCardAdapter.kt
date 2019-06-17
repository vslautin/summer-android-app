package airport.transfer.sale.adapter

import android.view.ViewGroup
import airport.transfer.sale.mvp.model.CreditCard
import airport.transfer.sale.ui.view.CreditCardView
import airport.transfer.sale.ui.view.CreditCardView_


class CreditCardAdapter(val listener: (Long) -> Unit) : RecyclerViewAdapterBase<CreditCard, CreditCardView>() {

    private var mCheckedId: Long? = null

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): CreditCardView {
        return CreditCardView_.build(parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolderWrapper<CreditCardView>?, position: Int) {
        holder?.view?.bind(items[position], mCheckedId, listener)
    }

    fun setCheckedId(id: Long?){
        mCheckedId = id
    }
}