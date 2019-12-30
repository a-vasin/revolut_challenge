REST API for money transfer

Requires java to be run. Run command: `java -jar revolut-challenge-1.0.jar`. JAR-file in repository root.

Expected workflow:
* Account creation
* Money deposit/withdraw/transfer

All POST-methods expect JSON as payload and all methods return JSON as answer.
GET-method uses request parameter.

Following methods are provided:
* POST `/create-account`

Creates new account.

Takes single integer parameter `id` in JSON-payload. 
No restriction for id to be positive.

Methods creates new account with zero balance on it. 
If account has been created before - nothing happens and response contains appropriate message.

Example input:
```
{
  "id": 1
}
```

Example output:
```
{
    "status": "SUCCESS",
    "message": "Account was successfully created"
}
```

* POST `/deposit`

Adds money to account. Account should be created before invoking deposit.

Takes two parameters:
* `id` - integer number
* `amount` - integer number strictly greater than zero

Example input:
```
{
  "id": 1,
  "amount": 500
}
```

Example output:
```
{
    "status": "SUCCESS",
    "message": "New balance: 500"
}
```

In case of error:
```
{
    "status": "ERROR",
    "message": "Provided account ID does not exist"
}
```

* POST `/withdraw`

Removes money from account. 
Parameters and output similar to `/withdraw`, additionaly it should be enough money for withdrawing, otherwise error with appropriate message will be returned.

* POST `/transfer`

Transfers money from one account to another.

Same restriction as before for amount (positive and enough money on balance). Resource and destination should be different.

Parameters:
* `fromId`
* `toId`
* `amount`

Example input:
```
{
  "fromId": 1,
  "toId": 1,
  "amount": 500
}
```

Example output:
```
{
    "status": "ERROR",
    "message": "Accounts should be different"
}
```

* GET `/get-balance`

Single request parameter - `id`.

Example request:
`http://localhost:4567/get-balance?id=2`

Example output:
```
{
    "status": "SUCCESS",
    "message": "Current balance: 0"
}
```

Tests:
* `MoneyTransferManagerTest` - unit-tests for functionality/behavior/corner cases/etc.
* `MoneyTransferServiceTest` - integration tests for REST API input/output format
