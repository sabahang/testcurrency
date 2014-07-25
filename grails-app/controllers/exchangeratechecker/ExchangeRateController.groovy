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
        if ((params.currency_one == null) || (params.currency_two == null)){
            //this should all be done in a constructor but the constructor syntax was new to me 
            //and I didn't have time to figure out the errors after the first try
            print "First time page loadin..."
            ExchangeRate ex = new ExchangeRate()
            ex.exchangerate_one = 0
            ex.exchangerate_two = 0
            ex.exchangerate_three = 0
            ex.date_one = new Date(0)
            ex.date_two = new Date(0)
            ex.date_three = new Date(0)
            ex.basecurrency = new Currency()
            ex.targetcurrency = new Currency()
            return [exchangeobj:ex]
        }
        exchangeratechecker.Currency basecurr = exchangeratechecker.Currency.findBySymbol(params.currency_one)
        exchangeratechecker.Currency targetcurr = exchangeratechecker.Currency.findBySymbol(params.currency_two)
        
        ExchangeRate exrate = new ExchangeRate()
        if (basecurr != null){
            println "Base Currency was NOT null"
            exrate.basecurrency = basecurr
        }
        if (targetcurr != null){
            println "Target Currency was NOT null"
            exrate.targetcurrency = targetcurr
        }
        //initializing ExchangeRate object members (this should be done in a constructor)
        exrate.date_one = new Date(0)
        exrate.date_two = new Date(0)
        exrate.date_three = new Date(0)
        exrate.exchangerate_one = 0
        exrate.exchangerate_two = 0
        exrate.exchangerate_three = 0
        
        //calling the rate method
        exrate = getExchangeRate(exrate)

        [exchangeobj:exrate]
    }
    def getExchangeRate(ExchangeRate ex){
        //base rate is based on USD when the source did not offer exchange rate between any random currencies
        //println ex.basecurrency.symbol
        Currency basecurr = Currency.findBySymbol(ex.basecurrency.symbol)
        Currency targetcurr = Currency.findBySymbol(ex.targetcurrency.symbol)
        
        //source one: http://openexchangerates.org using USD as base rate (all currencies based off of USD)
        def (USDbaserate_base, USDbasedate_base) = getInfoFromOpenExchange(ex.basecurrency.symbol)
        def (USDbaserate_target, USDbasedate_target) = getInfoFromOpenExchange(ex.targetcurrency.symbol)
        
        ex.exchangerate_one = (double)USDbaserate_target / (double)USDbaserate_base
        ex.date_one = USDbasedate_base
        println "This is running"
        println ex.date_one
        println ex.exchangerate_one
        //also updates the Currency Objects With the latest rates
        basecurr.rate_one = USDbaserate_base
        basecurr.time_one = USDbasedate_base
        if (!basecurr.save(flush: true)){
            println "Couldn't save Base Currency object"
            basecurr.errors.each { println it}
        }
        //also updates the Currency Objects With the latest rates
        targetcurr.rate_one = USDbaserate_target
        targetcurr.time_one = USDbasedate_target
        if (!targetcurr.save(flush: true)){
            println "Couldn't save Target Currency object"
            basecurr.errors.each { println it}
        }
        //source two: http://rate-exchange.appspot.com/
        def exrate_two = getInfoFromRateExchange(ex.basecurrency.symbol,ex.targetcurrency.symbol)

        ex.exchangerate_two = exrate_two
        ex.date_two = new Date() //the original source is XE as per the info on the service page but as date was not provided, NOW is used instead 
        
        basecurr.rate_two = getInfoFromRateExchange("USD",ex.basecurrency.symbol)
        basecurr.time_two = new Date()
        if (!basecurr.save(flush: true)){
            println "Couldn't save Base Currency object"
            basecurr.errors.each { println it}
        }
        //also updates the Currency Objects With the latest rates
        targetcurr.rate_two = getInfoFromRateExchange("USD",ex.targetcurrency.symbol)
        targetcurr.time_two = USDbasedate_target
        if (!targetcurr.save(flush: true)){
            println "Couldn't save Target Currency object"
            basecurr.errors.each { println it}
        }
        
        
        //source three (yaho finance was to be used but I ran into issues parsing the XML (the incomplete method is below)
        
        def exrate_three = getInfoFromFreeCurrencyConverter(ex.basecurrency.symbol,ex.targetcurrency.symbol)

        ex.exchangerate_three = exrate_three
        ex.date_three = new Date() //the original source is XE as per the info on the service page but as date was not provided, NOW is used instead 
        
        basecurr.rate_three = getInfoFromFreeCurrencyConverter("USD",ex.basecurrency.symbol)
        basecurr.time_three = new Date()
        if (!basecurr.save(flush: true)){
            println "Couldn't save Base Currency object"
            basecurr.errors.each { println it}
        }
        //also updates the Currency Objects With the latest rates
        targetcurr.rate_three = getInfoFromFreeCurrencyConverter("USD",ex.targetcurrency.symbol)
        targetcurr.time_three = USDbasedate_target
        if (!targetcurr.save(flush: true)){
            println "Couldn't save Target Currency object"
            basecurr.errors.each { println it}
        }
        
        if (!ex.save(flush: true)){
                println "Couldn't save exchange object"
                ex.errors.each { println it}
        }
        return ex
    }

    def getInfoFromYahooFinance(String sym){
        //under maintenance
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
    def getInfoFromRateExchange(String basesym, String targetsym){
        
        //http://rate-exchange.appspot.com/
        String Rates_URI = 'http://rate-exchange.appspot.com/currency?from='+basesym+'&to='+targetsym+'&q=1'
        
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
    def getInfoFromFreeCurrencyConverter(String basesym, String targetsym){
        //http://www.freecurrencyconverterapi.com/api/convert?q=USD-EUR
        String Rates_URI = 'http://www.freecurrencyconverterapi.com/api/convert?q='+basesym+'-'+targetsym
        
        def apiURI = new URL(Rates_URI)

        def slurper = new JsonSlurper()
        def currency = slurper.parse(apiURI)
        println "3rd result"
        String query = basesym+'-'+targetsym
        Map jasonresult = (Map)currency
        def results = jasonresult.get("query")
        def rate = results.get("val")
        if ((String)rate != null){
            println "This Currency Is Supported"
            return (double)rate
        } else {
            println "This Currency Is not Supported"
            return 0
        }
    }
    def getInfoFromCurrencyAPI(String sym){
        //not used as the available currencies are so few, just leaving here for future reference
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
