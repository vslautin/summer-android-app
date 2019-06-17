package airport.transfer.sale

import com.google.gson.annotations.SerializedName


class Constants {
    companion object {
        const val isTestServer = true
        const val needGPay = true

        const val REGISTRATION = "registration"
        const val REQUEST_REGISTRATION = 1
        const val REQUEST_ARRIVAL = 2
        const val REQUEST_DESTINATION = 3
        const val REQUEST_ARRIVAL_FLIGHT = 4
        const val REQUEST_DESTINATION_FLIGHT = 5
        const val REQUEST_PERMISSION = 6
        const val REQUEST_COMMENT = 7
        const val REQUEST_AUTHORIZATION = 8
        const val REQUEST_ACCEPTED = 9
        const val REQUEST_PLAN = 10
        const val REQUEST_PAYMENT = 11
        const val REQUEST_PROFILE = 12
        const val REQUEST_GPAY = 13
        const val REQUEST_CREDIT_CARDS = 14

        const val EXTRA_KEY = "key"
        const val EXTRA_POST = "post data"
        const val EXTRA_TITLE = "title"
        const val EXTRA_SUBTITLE = "subtitle"
        const val EXTRA_FLIGHT = "flight"
        const val EXTRA_FLAG = "flag"
        const val EXTRA_NAME = "name"
        const val EXTRA_MAIL = "mail"
        const val EXTRA_AIRPORT = "airport"
        const val EXTRA_TEXT = "text"

        const val DOZEN_HOURS = 1000 * 60 * 60 * 12
        const val ITEMS_PER_PAGE = 20
        const val HOW_TO_USE_LINK = "https://www.transfer.sale/ru/offer"
        const val TERMS_LINK = "https://transfer.sale/ru/terms"
        const val PAYMENT_LINK = "https://transfer.sale/ru/payment"
        val TOKEN_ERROR_CODES = arrayListOf(434, 435)

        const val CERTIFICATE = "-----BEGIN CERTIFICATE-----\n" +
                "MIIFgTCCA2kCBFrR2pwwDQYJKoZIhvcNAQELBQAwgYQxCzAJBgNVBAYTAlVTMREw\n" +
                "DwYDVQQIEwhOZXcgWW9yazERMA8GA1UEBxMITmV3IFlvcmsxEjAQBgNVBAoTCU1v\n" +
                "bGVjdWx1czEaMBgGA1UEAxMRd3d3LnRyYW5zZmVyLnNhbGUxHzAdBgkqhkiG9w0B\n" +
                "CQEWEGlkc2FmZkBnbWFpbC5jb20wHhcNMTgwNDE0MTA0MDI5WhcNMTkwNDE0MTA0\n" +
                "MDI5WjCBhDELMAkGA1UEBhMCVVMxETAPBgNVBAgTCE5ldyBZb3JrMREwDwYDVQQH\n" +
                "EwhOZXcgWW9yazESMBAGA1UEChMJTW9sZWN1bHVzMRowGAYDVQQDExF3d3cudHJh\n" +
                "bnNmZXIuc2FsZTEfMB0GCSqGSIb3DQEJARYQaWRzYWZmQGdtYWlsLmNvbTCCAiIw\n" +
                "DQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBALNgNK4FF89yzYTJF7X3UOnVcYq/\n" +
                "K+anwi9/ZfHvHf0w55u0W/hma7TQTx4KYJ4Xx6HuGXcqLW5xffPUYL/drvfGLVOP\n" +
                "39oWDs9d+C9S4UFxdQy5+aVfCCSWoVEGcDklHBKMgGntc/t/ta9anFCqbTjdwDhi\n" +
                "43J5/ctBjWI7EMBZHJNxHrpXAQ0gCzHZs+8IM/q2OpVuerCM4J2mw2I7e4PQKUR5\n" +
                "CkfXUVzhkbFy6Sk0DZvuKrKqSQf47AJu/C8pTuMgakEzZgBqildnIt7K9pZ69AlP\n" +
                "u/cedrHTerFQqS5bMDX9Kn1YWxK+S+KhWjJmfvFSUnOEtrgDLlfWXQ0KuTPKylwi\n" +
                "S+hTODAQeH8xpzHd7wVyD6G4PV/ZF7v6S6zhTAyhGaHa15S7yfoClgYvvQe6LGBO\n" +
                "5erB6YL3s9tztp/1x50H2xAJ/ZOWVOiVr4qOGr9EX6sR3fWiXW5OIHznNLnR0r8/\n" +
                "hKdCWA5zgUz09CyggZXrQEIJjmecoIzOYOJKJumcz1HBnewJIZMj6MXMSd1pag6S\n" +
                "8QXAHIqGevLbxDRIyj0EpRX8rIEa8G4GwgXZm6Hxir+lKRroBgzD++TscJRlatNE\n" +
                "jqchk3BJ+f2H0AvvFDDOcA9AI5RXsT+3Tf1cF3IJbgheVZMbfGo8KXSKNsfu1+VX\n" +
                "C6+5AW3+l1Imv6sTAgMBAAEwDQYJKoZIhvcNAQELBQADggIBAFRREJ/D7xasTCrq\n" +
                "3k7gIJnZnmFWhFnoX8yKHDjtk+dIj2s4jjNDEQOKXzC8H0eRTT6ijZr41mYiVjkk\n" +
                "Rmgy4nmQA8BddyqIXsa+jNVpZKjE2HwW0bwIaOcMmEiRcl2o4Ih085mezZY0PIu4\n" +
                "a61BklXClDORax2JxX499hWgPO7B0dFrrhflNYTl2gXMgIn5VIufsfvLumntKhIL\n" +
                "aw4WdTASwsyii80rSLIkc3SdSbRuIwUop3dTmxL5oDz9uFQv9PfDjX3P3poWHiST\n" +
                "5Sw3wQyhzgaNtOewmIszDJd2S7IbowkIwPDILWz1c7wdLAOKa3mrNSO+r2pEFH2L\n" +
                "sa/+nM3f9VJYpgbU6oQHNJjP4FkHEQ6ktcD8o1q/lT9HnSW6mqBSXfWX4MRvoh6l\n" +
                "DaNByjlk3o1qai6ao60FXd+ubqOPlSz0SeCqGci0OjvW9rQiEkXaaVI0N5rFwX+4\n" +
                "0rUhAf8xcJ6lGw/5EE99ByMwb+eFju/zR0CyC5gRZ3elMIBdyGa2R2Hs8ntQjJFD\n" +
                "lsNg7SmQzJiNzYdvm3ldi5yOJPqBAxBX0NWQPrH10iaqJ+7IXclw0VVXjST40iJ4\n" +
                "YYX8dwbI1gD5TX7NIDq21dMznzaQp33wOIqRYxRAVdr+APgERbAEWQO3hb4vzcgn\n" +
                "6jOyfqiIRpQ4+QjcooVHy03M/J1P\n" +
                "-----END CERTIFICATE-----"
    }

    enum class Status{
        @SerializedName("success") SUCCESS,
        @SerializedName("fail") FAIL
    }

    enum class OrderTense { CURRENT, COMPLETED, FAVORITE }
}