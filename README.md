# Team PitBuzz self-hosted working build

Beta-critical backend now covers authentication, teams, dependent riders, family/crew,
rider class subscriptions, member-to-rider following, Current Race / Up Next,
private messages, audio preferences, and provider-neutral push-device storage.

Mobile portability:
- push subscriptions explicitly distinguish web / Android / iOS
- provider is not hard-coded to Floot
- private-message audio defaults OFF
- Team Owner/Admin cannot advance race status unless separately granted race_admin/super_admin

Next working batch: announcements targeting/filtering, message read state, notification dispatch adapter,
then Android/iOS shell configuration and end-to-end beta test checklist.
