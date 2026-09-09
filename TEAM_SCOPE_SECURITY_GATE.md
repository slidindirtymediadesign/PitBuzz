# Team-scope security gate

Canonical server helpers now exist for:
- team membership
- Team Owner/Admin management permission
- rider-in-team validation

Every team mutation must resolve the signed-in user first, then verify the requested team/rider relationship. Client-supplied team IDs never grant access by themselves.

Required negative tests:
- Team A member reading Team B management data => denied
- Team A manager editing Team B rider => denied
- family/crew editing team settings => denied
- Team Owner/Admin invoking Race Control => denied unless separately granted a system race role
- rider ID paired with wrong team ID => denied
