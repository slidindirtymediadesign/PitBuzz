# Required automated regression matrix

Authentication:
- anonymous protected endpoint => 401
- expired session => 401
- valid user => allowed only within scope

Team isolation:
- Team A cannot read/change Team B riders, members, messages or preferences
- dependent rider requires no user account
- Team Admin cannot gain Race Control privileges

Race Control:
- ordinary member/Team Admin advance => 403
- Race Control advance follows race-program sort order
- skipped race is not promoted
- manual correction persists
- active event isolates its race program

Announcements:
- Everyone reaches eligible signed-in users
- Class reaches followers of riders in that class
- Rider reaches followers of that rider
- unrelated team/user does not receive rider-targeted content
- auto-play records once and never repeats automatically

Private messages:
- only sender/recipient can read
- unrelated user => 403/404 without leaking content
- read state is per recipient
- no automatic speech

Reliability:
- repeated push job execution is idempotent
- reconnect does not duplicate race alerts
- bootstrap works with no team/event
- service failure preserves cached race state client-side
