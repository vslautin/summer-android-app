package airport.transfer.sale.mvp.view

import airport.transfer.sale.rest.models.response.model.v2.Address


interface AddressesView : BaseView{
    fun onAddressListReceived(addresses: List<Address>, isClosest: Boolean)
    fun onCoordinatesReceived(address: Address)
    fun onCoordinatesAbsent()
}