package exchangeratechecker

import grails.transaction.Transactional
import groovy.json.JsonSlurper

@Transactional
class ExchangeService {

    def loadCurrencies(){
        String Currencies_URI = 'http://openexchangerates.org/api/currencies.json'
        def apiURI = new URL(Currencies_URI)
        def slurper = new JsonSlurper()
        def symbols = slurper.parse(apiURI)
        symbols.each { obj -> new Currency(symbol:obj.getKey(),rate_one:0,rate_two:0,rate_three:0,name:obj.getValue(),time_one : new Date(0),time_two : new Date(0),time_three : new Date(0)).save()};
    }
}
