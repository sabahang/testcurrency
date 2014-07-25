package exchangeratechecker

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import java.awt.TexturePaintContext.Int

import grails.converters.JSON

import groovy.json.JsonSlurper
import groovy.util.XmlSlurper
import java.net.URL

@Transactional(readOnly = true)
class ExchangeRateController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Currency.list(params), model:[currencyInstanceCount: Currency.count()]
    }
    
    def getrates(){
        exchangeratechecker.Currency basecurr = exchangeratechecker.Currency.findBySymbol(params.currency_one)
        //Currency targetcurr = Currency.get(params.currency_two)
        println basecurr.symbol
        println params.currency_one
        //println targetcurr
//        ExchangeRate exrate = new ExchangeRate()
//        exrate.basecurrency = basecurr
//        exrate.targetcurrency = targetcurr
//        println exrate.save(flush: true)
        //exrate = getExchangeRate(exrate)
        
        
 
        //return exrate
    }
    def getExchangeRate(ExchangeRate ex){
        //base rate is based on USD
        
        Currency basecurr = Currency?.findBySymbol(ex.basecurrency.symbol)
        Currency targetcurr = Currency?.findBySymbol(ex.targetcurrency.symbol)
        
        //source one: http://openexchangerates.org using USD as base rate
        def (USDbaserate_base, USDbasedate_base) = getInfoFromOpenExchange(ex.basecurrency.symbol)
        def (USDbaserate_target, USDbasedate_target) = getInfoFromOpenExchange(ex.targetcurrency.symbol)
        
        ex.exchangerate_one = USDbaserate_target "/" USDbaserate_base
        ex.date_one = USDbasedate_base
        //also updates the Currency Objects With the latest rates
        basecurr.rate_one = USDbaserate_base
        basecurr.time_one = USDbasedate_base
        basecurr.save(flush: true)
        //also updates the Currency Objects With the latest rates
        targetcurr.rate_one = USDbaserate_target
        targetcurr.time_one = USDbasedate_target
        targetcurr.save(flush: true)
        
        //source two
        //source three
        ex
    }
//daram ino dor mizanam
    protected void updateBaseRateAndDate(Currency currency){

        
//        //source two
//        currencies.each { obj -> obj.rate_two = getRateFromCurrencyAPI(obj.symbol)};
//        currencies.each { obj -> Currency currency = Currency.findBySymbol(obj.symbol)
//            currency.rate_two = obj.rate_two
//            currency.save(flush: true)};
//        
//        //source three
//        currencies.each { obj -> obj.rate_three = getRateFromRateExchange(obj.symbol)};
//        currencies.each { obj -> Currency currency = Currency.findBySymbol(obj.symbol)
//            currency.rate_three = obj.rate_three
//            currency.save(flush: true)};
//        getRateFromYahooFinance("USD")
    }
    def getInfoFromYahooFinance(String sym){
        //http://finance.yahoo.com/webservice
        String Rates_URI = 'http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote'
        
        //def apiURI = new URL(Rates_URI)

        def slurper = new XmlSlurper()
        def result = slurper.parse(Rates_URI)
        resource = result.find{ 
            it.field.find {it.@name == "symbol" && it.text() == "KRW=X"}
        }.field.find {it.@name == "price"}.text()
        println resource
        //        def resource = result.'**'.find { 
        //            it.field.@name == 'symbol' &&
        //            it.field.text() == 'KRW=X'
        //        }
        //        println resource.size()
        //        println resource?.find {
        //            it.field.@name == 'price'
        //        }.text()
        //.parent()// { it['@name'] == "price" }.text()
        
        //println result.find {it.symbol == "USD=X"}
        //println result.getClass().name
        //println result.name()

        //println rate
    }
    def getInfoFromRateExchange(String sym){
        //http://rate-exchange.appspot.com/
        String Rates_URI = 'http://rate-exchange.appspot.com/currency?from=USD&to='+sym+'&q=1'
        
        def apiURI = new URL(Rates_URI)

        def slurper = new JsonSlurper()
        def currency = slurper.parse(apiURI)
        
        String result = currency.rate
        if (result != null){
            println "This Currency Is Supported"
            return currency.rate
        } else {
            println "This Currency Is not Supported"
            return 0
        }
    }
    def getInfoFromOpenExchange(String sym){
        //http://openexchangerates.org
        String Rates_URI = 'https://openexchangerates.org/api/latest.json?app_id=ac9c7766220144aab4944d14ad0931dc'
        def apiURI = new URL(Rates_URI)

        def slurper = new JsonSlurper()
        def currency = slurper.parse(apiURI)
        
        Date date = new Date(((long)currency.timestamp) * 1000)
        String result = currency.rates."$sym"
        
        //returns rate and Date 
        if (result != null){
            println "This Currency Is Supported"
            return [currency.rates."$sym",date]
        } else {
            println "This Currency Is not Supported"
            return [0,new Date()]
        }
    }
    def getInfoFromCurrencyAPI(String sym){
        //http://currency-api.appspot.com
        String Rates_URI = 'http://currency-api.appspot.com/api/USD/' +sym+ '.json?key=6279598601eae2d106fa9481a421311f4d75287e'
        def apiURI = new URL(Rates_URI)

        def slurper = new JsonSlurper()
        def currency = slurper.parse(apiURI)
        String result = currency.rate
        if (result != 'false' && result != null){
            println "This Currency Is Supported"
            return currency.rate
        } else {
            println "This Currency Is not Supported"
            return 0
        }
    }
    private def getConfig() {
        grailsApplication.config.grails.plugin.openexchangerates
    }
    def show(Currency currencyInstance) {
        respond currencyInstance
    }

    def create() {
        respond new Currency(params)
    }

    @Transactional
    def save(Currency currencyInstance) {
        if (currencyInstance == null) {
            notFound()
            return
        }

        if (currencyInstance.hasErrors()) {
            respond currencyInstance.errors, view:'create'
            return
        }

        currencyInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'currency.label', default: 'Currency'), currencyInstance.id])
                redirect currencyInstance
            }
            '*' { respond currencyInstance, [status: CREATED] }
        }
    }

    def edit(Currency currencyInstance) {
        respond currencyInstance
    }

    @Transactional
    def update(Currency currencyInstance) {
        if (currencyInstance == null) {
            notFound()
            return
        }

        if (currencyInstance.hasErrors()) {
            respond currencyInstance.errors, view:'edit'
            return
        }

        currencyInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Currency.label', default: 'Currency'), currencyInstance.id])
                redirect currencyInstance
            }
            '*'{ respond currencyInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Currency currencyInstance) {

        if (currencyInstance == null) {
            notFound()
            return
        }

        currencyInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Currency.label', default: 'Currency'), currencyInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'currency.label', default: 'Currency'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
