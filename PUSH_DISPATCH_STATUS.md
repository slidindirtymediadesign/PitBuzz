# Push dispatch status

Implemented:
- provider-neutral dispatch boundary
- Android FCM routing
- iOS APNs/FCM routing boundary
- environment-only credential checks
- transactional queue claim
- enabled-device fan-out
- per-device delivery receipts
- bounded exponential retry
- dead-letter state after repeated failure
- cron-safe worker batch

Not yet implemented:
- actual signed FCM HTTP transport
- actual APNs HTTP/2 transport
- production credentials
- real-device delivery verification

The adapters intentionally fail closed until credentials/transports are configured; no fake successful delivery is reported.
