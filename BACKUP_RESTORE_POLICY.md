# Team PitBuzz backup / restore policy

Before trusting production race-day data:
- Take a transactional MySQL backup.
- Verify the dump is non-empty and contains core Team PitBuzz tables.
- Restore it into a disposable database whose name is different from production.
- Run the deployment smoke test against the restored database.
- Never test a restore by overwriting the live production database.
- Retain a pre-deployment backup before schema migrations.
- Race-day backups should be taken before an event and after final results/communications are complete.
