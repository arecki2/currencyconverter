# CurrencyConverter
A RESTful web application to convert between Polish zlotys and selected foreign currencies.

The exchange rates are fetched from public REST API maintained by National Bank of Poland: http://api.nbp.pl/en.html

# Supported methods

`/currency/{id}` - retrieves exchange rate from PLN to a given currency. `{id}` is the 3-letter currency code.

`/currencies/available` - lists currencies and convertion operations supported. Currently only conversions from/to PLN are supported.

`/currencies/exchangerates` - gives exchange rates from and to PLN for all supported currencies.

`/convert/{from}/{to}?value={value}` - the actual converter service. `{from}` and `{to}` are currencies conversion will be performed to, while in the `{value}` parameter amount of money to convert can be passed. When not passed, this value equals 1.

## A compiled JAR file is included to run the 

# Technology stack

- Java 8+
- Spring Boot v2.1.7
- Gradle 7.0
