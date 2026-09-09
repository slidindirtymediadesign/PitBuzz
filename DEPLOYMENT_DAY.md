# Team PitBuzz deployment day

Run in this order:

1. Upload the current canonical package only.
2. Create the empty MySQL/MariaDB database and least-privilege user.
3. Import `schema.mysql.sql`.
4. Configure production environment values outside public web root.
5. Run `php tests/deployment_smoke.php`.
6. Run the disposable beta fixture/workflow against staging, never production.
7. Configure notification-worker cron.
8. Configure FCM and test Android foreground/background/screen-off.
9. Configure APNs and test iPhone foreground/background/screen-off.
10. Run reconnect/offline race-state tests on both platforms.
11. Re-run `tests/run_beta_gate.sh`.
12. Clear `RELEASE_BLOCKERS.json` only from actual passing runtime evidence.
13. Publish/distribute only after explicit approval.
