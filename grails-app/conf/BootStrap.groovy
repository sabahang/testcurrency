import exchangeratechecker.Currency

class BootStrap {
    
    def ExchangeService
    
    def init = { servletContext ->
        if (!Currency.count()){
            ExchangeService.loadCurrencies()
        }
    }
    
    def destroy = {
        Currency.executeUpdate('delete from Currency')
        ExchangeRate.executeUpdate('delete from Exchange_Rate')
    }
}