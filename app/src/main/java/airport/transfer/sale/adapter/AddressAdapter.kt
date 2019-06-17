package airport.transfer.sale.adapter

import android.view.ViewGroup
import airport.transfer.sale.rest.models.response.model.v2.Address
import airport.transfer.sale.ui.view.AddressView
import airport.transfer.sale.ui.view.AddressView_


class AddressAdapter : RecyclerViewAdapterBase<Address, AddressView>(){

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): AddressView {
        return AddressView_.build(parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolderWrapper<AddressView>?, position: Int) {
        holder?.view?.bind(items[position])
    }
}