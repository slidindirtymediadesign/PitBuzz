# Request security standard

For beta production:
- HTTPS only.
- Authenticated web requests use secure, HttpOnly, SameSite cookies.
- Native Android/iPhone sessions use a server-issued opaque session/token stored in platform secure storage.
- Every mutation validates authentication and authorization server-side.
- Browser cookie-authenticated mutations require CSRF protection.
- Native token-authenticated requests do not rely on browser cookies and therefore do not use cookie CSRF semantics.
- CORS is allowlisted to PitBuzz-owned origins; never `*` with credentials.
- Login/register/invite/message-send/race-control mutations are rate limited.
- Passwords use PHP `password_hash()` / `password_verify()`.
- No credentials, VAPID private keys, FCM service credentials, APNs keys or DB passwords are committed to the app/repository.
- Production error responses never expose SQL, filesystem paths, stack traces or secrets.
