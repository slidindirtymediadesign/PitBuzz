# Audio behavior for beta

Announcements:
- Visual notification always remains available.
- Auto-play is controlled by the user's announcement audio setting.
- Each announcement auto-plays at most once per user.
- Reopening, refreshing, reconnecting, or changing tabs must not replay it automatically.
- Manual Play remains available after an announcement has been heard.
- Race Control can mark an announcement normal / important / urgent for visual treatment.

Private messages:
- Never auto-play by default.
- Private message arrival may use a notification sound, but message text is not spoken.
- Manual playback can be considered separately without changing the default privacy rule.

Mobile:
- Android/iPhone use the same playback-state API so behavior stays consistent between platforms.
