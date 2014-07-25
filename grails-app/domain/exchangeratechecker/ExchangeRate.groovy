package exchangeratechecker

class ExchangeRate {
    Currency basecurrency //symbol of base currecy
    Currency targetcurrency // symbol of target currency
    double exchangerate_one
    double exchangerate_two
    double exchangerate_three
    Date date_one
    Date date_two
    Date date_three

    static constraints = {
    }
}
