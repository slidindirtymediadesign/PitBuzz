# Pre-release code review gate

No beta package is considered release-ready until:
- [ ] no duplicate endpoint implementations
- [ ] no hard-coded Smith Racing/demo data in production paths
- [ ] no secrets committed
- [ ] no raw SQL errors/stack traces returned
- [ ] all SQL install steps are repeatable or explicitly versioned
- [ ] all POST routes validate authentication, authorization and input
- [ ] cross-team access tests fail correctly
- [ ] dependent riders work without login credentials
- [ ] announcement recipient routing verified
- [ ] private message sender/recipient isolation verified
- [ ] announcement auto-play deduplication verified
- [ ] race advance/correction verified against race program
- [ ] reconnect/offline behavior verified
- [ ] push retry does not duplicate user-visible alerts
- [ ] Android/iPhone API behavior matches
