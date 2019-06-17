package airport.transfer.sale.rest.models.response.model


data class Airline(val id: Int,
                   val iata: String,
                   val name: String){

    override fun toString(): String {
        return name
    }
}