## Play Store
Store emulator using Akka actors

## Libraries

* Scala 2.12.2
* SBT 0.13.5

|   library        |   Version         |
|   -------------    |   -------------   |
|   (play-slick)                                                                |   2.5.11 |
|   (akka-actor)                                                                |   2.5.11 |
|   (akka-testkit)                                                              |   2.5.11 |
|   (org.scalatest)                                                             |   3.1.2  |


## Restrictions

  - Only USD,COP and EUR are accepted currencies
  - Only can buy one item in the same order

## API Routes

|   Route            |   Http         |
|   -------------     |   -------------  |
|   (/products)                                                                  |    GET   |
|   (/products/reserve)                                                     |    POST   |
|   (/products/buy)                                                              |    POST  |

## How it works ?

 To start the app just digit sbt run in the project directory, the app will run in 0.0.0.0:9000

    - First, look for all the products in the store machine (/products).

    - Second, select the product you want to order (/products/reserve).
      This will give you a requestId that you have to use to pay the product.

    - Finally, pay the product value following the currency restrictions and using the requestId (/products/pay)





