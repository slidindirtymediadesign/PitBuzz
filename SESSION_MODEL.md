# Session model

One backend, two client modes:

## Browser/PWA
Secure server session cookie:
- Secure
- HttpOnly
- SameSite=Lax or stricter where compatible
- session ID regenerated at login
- CSRF token required for state-changing cookie-authenticated requests

## Android/iPhone
Opaque server session/token:
- stored in Android Keystore-backed secure storage / iOS Keychain
- sent in Authorization header
- revocable server-side
- rotated/reissued according to beta session policy
- never embedded in app source or URL

Logout invalidates the server-side session/token and unregisters or disables the device's push subscription when appropriate.
