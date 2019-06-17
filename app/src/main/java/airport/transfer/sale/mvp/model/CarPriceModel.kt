package airport.transfer.sale.mvp.model


data class CarPriceModel(val id: Int,
                         val carClass: String,
                         val carPrices: String,
                         var isActivated: Boolean,
                         val passengers: Int = 1,
                         val luggage: Int = 2,
                         val price: Int = 1000)