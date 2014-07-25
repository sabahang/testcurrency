<%@ page import="exchangeratechecker.Currency" %>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Select Currencies to see rates</title>
    </head>
    <body>
        <div>
            <g:form name="selectCurrencyForm" url="[action:'getrates',controller:'exchangeRate']">
                <g:select name="currency_one" from="${Currency.list()}" optionValue="name" optionKey="symbol"/>
                <g:select name="currency_two" from="${Currency.list()}" optionValue="name" optionKey="symbol" />
                
                <g:submitButton name="submit" value="Show Rates" />
                
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
                <tr><th>Exchange Currencies</th><th>Open Exchange</th><th>Currency API</th><th>Rate Exchange</th></tr>
                
<!--                <tr><td> 1 ${exrate?.basecurrency?.symbol} in ${exrate?.targetcurrency?.symbol}
                    </td><td>${exrate?.exchangerate_one}</td><td>Currency API</td><td>Rate Exchange</td></tr>-->

            </table>

        </div>

    </body>
</html>
