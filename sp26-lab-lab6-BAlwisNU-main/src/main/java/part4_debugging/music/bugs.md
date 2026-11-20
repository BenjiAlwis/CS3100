# Expected Bugs Summary

## Music.java (6 bugs):

1. **seperateInstruments()**: Off-by-one error (`<=` instead of `<`) causes ArrayIndexOutOfBoundsException
2. **seperateInstruments()**: Incorrectly maps each instrument type to the entire array instead of grouping by type
3. **playAll()**: Missing newline between instruments (docs say each instrument on new line)
4. **hasInstrumentType()**: Uses `!=` instead of `==` to check type match
5. **hasInstrumentType()**: Returns `true` by default instead of `false`
6. **countByType()**: Sets `count = 1` instead of incrementing (`count++` or `count += 1`)

## Design Issues (intentional - to test AI analysis):

- Enum named `Instruments` (plural) while arrays are named `instruments` - confusing naming
- Package-private fields in `Instrument` base class instead of private
