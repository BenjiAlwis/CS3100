# Expected Bugs Summary

## BankAccount.java (7 bugs):

1. **deposit()**: Wrong comparison for deposit validation (`<` instead of `<=`)
2. **withdraw()**: Inverted status check (`== ACTIVE` instead of `!= ACTIVE`)
3. **withdraw()**: Wrong comparison for withdrawal limit (`>` instead of `>=`)
4. **applyMonthlyFee()**: Missing `break` statement in switch (fall-through bug)
5. **getTransactionsByType()**: Modifying collection while iterating (ConcurrentModificationException)
6. **getAccountHealth()**: Wrong balance check for overdrawn (`== 0` instead of `< 0`)
7. **withdraw()**: Missing CHECKING case in switch (though it has no restrictions, incomplete handling)
