package com.example.betonadmin

class Order {
    var address: String? = null
    var product: String? = null
    var count: Int? = null
    var delivery: Boolean = false
    var price: Int? = null
    var datetime: DateTime = DateTime()
    var status: String = "В обработке"
    var id: String? = null
    var uid: String? = null
}