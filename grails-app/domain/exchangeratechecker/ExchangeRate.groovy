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
//ExchangeRate.metaClass.constructor << { int zero -> new ExchangeRate(basecurrency:new Currency(), targetcurrency:new Currency(),exchangerate_one: zero,exchangerate_two: zero,exchangerate_three: zero,date_one : new Date(zero), date_two : new Date(zero), date_three : new Date(zero)) }
