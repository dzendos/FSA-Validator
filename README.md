# FSA-Validator

Implements an FSA validator. Given an FSA description, the program outputs the result file that contains an error description (see validation result) or a report, indicating if FSA is complete (or incomplete) and warning (see warning messages) if any. Warnings should be sorted according to their code. 

## Validation result
### Errors:
E1: A state 's' is not in the set of states
E2: Some states are disjoint
E3: A transition 'a' is not represented in the alphabet
E4: Initial state is not defined
E5: Input file is malformed

### Report:
FSA is complete/incomplete

### Warnings:
W1: Accepting state is not defined
W2: Some states are not reachable from the initial state
W3: FSA is nondeterministic
