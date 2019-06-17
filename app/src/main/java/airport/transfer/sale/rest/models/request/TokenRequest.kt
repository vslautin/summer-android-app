package airport.transfer.sale.rest.models.request


data class TokenRequest(val phone: String,
                        val code: String)