# SQL safety standard

- User-controlled values never enter SQL through string concatenation/interpolation.
- Values use PDO prepared statements and bound parameters.
- Direct `query()`/`exec()` is limited to static SQL with no request/user values.
- Dynamic identifiers (table/column/order expressions) are never accepted directly from clients.
- Database errors are logged server-side and mapped to stable client error codes.
- Duplicate-key conflicts are handled intentionally; they are not exposed as raw SQL errors.
- Every cross-team lookup includes server-verified ownership/membership scope.
