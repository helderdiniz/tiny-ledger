# tiny-ledger

A tiny ledger.

## Assumptions / Decisions

Given that the goal is building a 'tiny' simple ledger...

- Decided to not have 'user' or 'tenant' distinction per Transaction
- Assuming that the same currency is used for all operations, but added will still mark it as a possibility to add it in
  the future via `Money` value object
- Assuming that the users/producers provide valid UUID's as referenceId
- Assuming amounts should be formatted as 2 decimal places and `HALF_UP` rounding mode is applied for simplicity
- It could be interesting having several movements per transaction (e.g. `[{deposit, $1.00},{withdrawal, $0.50}]` but
  that's
  better suited when we want to support partial refunds or split payments, so I'm using 'plain' Transactions instead.
- I will not proceed on implementing a reference/linking mechanism between transactions (in case of deposit
  cancellations) to keep scope minimal.
- Balance is calculated after every transaction update, making it a 'running total' instead of computing it on every
  event
- Transaction timestamps are self-managed, instead of allowing the user to set it (a more robust option would be to have
  both (`transactionDate` and `acceptedDate`) but I found it to be overkill)

Business assumptions:

- Rejecting withdrawals that would make the balance negative
- Rejecting no-op ($0.00) operations and amounts that would round to zero ($0.002)
- Knowing a specific balance over time (other than the **current**) is not needed
- Idempotency is important for payment processing, but it is not mentioned in the problem statement. So I've implemented
  a very simplistic approach with a check if the transaction exists already
    - If the transaction was created, 201
    - If the transaction was previously created, 200
- Idempotency is achieved with `referenceId` provided by the user/producer/client
- No directions regarding error handling in the problem statement,
  so [RFC 9457](https://www.rfc-editor.org/info/rfc9457/) standard was adopted.

Other specific assumptions...

- Considering amounts as always positive in requests, leaving the `TransactionType` to determine the direction
- Given that we're using in memory persistence, I followed a simplistic approach for API pagination with page/limit
- "DB-style" atomic operations are out of scope, but still considered thread-safety concerns and implemented simple
  locks.
- Design-first API: `api.yml` serves as source of truth when it comes to represent the API contract (personal
  preference)

Testing:

- Kept unit tests simple to cover errors and business logic;
- Tested the functional requirements via integration tests for simplicity (quicker validation, and no mocks)

## Application

This is a Java 25, Spring Boot 4 service that offers Tiny Ledger's functionality over an HTTP REST API.

### Prerequisites

- Java 25
- Maven 3

### How to run

It is configured to use code generation for its API definition (OpenAPI spec), so in order to compile & run it properly,
with all necessary classes, executing `mvn generate:sources` or `mvn compile` is required.

After generating sources, **run with Maven**

```bash
mvn spring-boot:run
```

### View It

This service is shipped with Swagger UI if you want to visualize its best
UI: http://127.0.0.1:8077/swagger-ui/index.html

### Execute

Below you can find some cURLs with example usage. You can replace `"'"$(uuidgen)"'"` with your desired UUID, otherwise,
you can leave that function as it will generate it as per [curl spec](https://curl.se/mail/archive-2024-05/0023.html).

#### Money movement: record a deposit (201 Created)

```bash
curl -i -X POST http://127.0.0.1:8077/transaction \
    -H 'Content-Type: application/json' \
    -d '{"referenceId":"'"$(uuidgen)"'","amount":100.00,"type":"DEPOSIT","description":"salary"}'
```

#### Money movement: record a withdrawal (201 Created)

```bash
curl -i -X POST http://127.0.0.1:8077/transaction \
    -H 'Content-Type: application/json' \
    -d '{"referenceId":"'"$(uuidgen)"'","amount":30.00,"type":"WITHDRAWAL","description":"coffee"}'
```

#### Money movement: Idempotency — replaying referenceId returns 200 (not re-applied)

```bash
REF=$(uuidgen)
curl -i -X POST http://127.0.0.1:8077/transaction -H 'Content-Type: application/json' \
    -d '{"referenceId":"'"$REF"'","amount":10.00,"type":"DEPOSIT"}'   # 201 Created
curl -i -X POST http://127.0.0.1:8077/transaction -H 'Content-Type: application/json' \
    -d '{"referenceId":"'"$REF"'","amount":10.00,"type":"DEPOSIT"}'   # 200 OK
```

#### View current balance

```bash
curl -i http://127.0.0.1:8077/balance
```

#### Transaction history

```bash
curl -i 'http://127.0.0.1:8077/transaction'
```

#### Transaction history (paginated)

```bash
curl -i 'http://127.0.0.1:8077/transaction?limit=10&page=0'
```

#### Example error response: withdrawal beyond balance

```bash
curl -i -X POST http://127.0.0.1:8077/transaction -H 'Content-Type: application/json' \
-d '{"referenceId":"'"$(uuidgen)"'","amount":999999.00,"type":"WITHDRAWAL"}'
```

Expected HTTP 422

### Sample data

If you want to explore GET endpoints before creating transactions, there's a helper hidden from Swagger UI that creates
a bunch of transactions.

```bash
curl -i -X POST http://127.0.0.1:8077/helper/transaction/populate
```

-----------------------------

## Original Problem Statement

You are expected to implement a set of apis to power a simple ledger.

From a functional perspective, the following features should be implemented:

- Ability to record money movements (ie: deposits and withdrawals)
- View current balance
- View transaction history

From a technical perspective:

- We expect you to deliver a functional web application (no UI, just the apis) that can be run locally.
- You can use any programming language and framework of your choice.
- For the sake of simplicity, we strongly suggest you use in-memory data structures to store the data (for example: a
  map or an array), and it should not be necessary to install any optional software to run it (besides any libraries
  that you choose to use).

You are free to make assumptions whenever you feel it is necessary, but please
document them.

Please try to keep it simple. The objective is to understand your approach to
problems and your thought process rather than a test of your technical knowledge,
even if it means having to make trade-offs.

This means that you are not expected to deliver any of the below:

- authentication/authorisation
- logging / monitoring
- transactions/atomic operations
- ...

(Feel free to cut down other parts as much as you need to fit the solution into the time you have available)
