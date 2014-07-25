<%@ page import="exchangeratechecker.Currency" %>
<%@ page import="exchangeratechecker.ExchangeRate" %>

<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Select Currencies to see rates</title>
    </head>
    <body>
        <div>
            <h1>Currency Exchange Rate Aggregator by @sabahang</h1>
        </div>
        <div>
            <g:form name="selectCurrencyForm" url="[action:'getrates',controller:'exchangeRate']">
                <p>Base Currency: </p>
                <g:select name="currency_one" from="${Currency.list()}" optionValue="name" optionKey="symbol"/>
                <p>Target Currency: </p>
                <g:select name="currency_two" from="${Currency.list()}" optionValue="name" optionKey="symbol" />
                
                <g:submitButton name="Query" value="Show Rates" />
                
            </g:form>

        </div>
        <div>
            <style type="text/css">
                .tftable {font-size:12px;color:#333333;width:100%;border-width: 1px;border-color: #9dcc7a;border-collapse: collapse;}
                .tftable th {font-size:12px;background-color:#abd28e;border-width: 1px;padding: 8px;border-style: solid;border-color: #9dcc7a;text-align:left;}
                .tftable tr {background-color:#ffffff;}
                .tftable td {font-size:12px;border-width: 1px;padding: 8px;border-style: solid;border-color: #9dcc7a;}
                .tftable tr:hover {background-color:#ffff99;}
            </style>

            <table class="tftable" border="1">
                <tr><th>Exchange Currencies</th><th>openexchangerates.org (${exchangeobj.date_one})</th><th>rate-exchange.appspot.com (${exchangeobj.date_two})</th><th>freecurrencyconverterapi.com (${exchangeobj.date_three})</th></tr>
                
                <tr><td> 1 ${exchangeobj?.basecurrency?.symbol} in ${exchangeobj?.targetcurrency?.symbol}
                    </td><td>${exchangeobj?.exchangerate_one}</td><td>${exchangeobj?.exchangerate_two}</td><td>${exchangeobj?.exchangerate_three}</td></tr>

            </table>

        </div>

    </body>
</html>
