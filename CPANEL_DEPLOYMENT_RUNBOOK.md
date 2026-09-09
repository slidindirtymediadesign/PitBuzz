# Team PitBuzz cPanel deployment runbook

1. Create production MySQL/MariaDB database and least-privilege database user.
2. Import `schema.mysql.sql` once into the empty database.
3. Store production environment values outside the public web root.
4. Point API bootstrap configuration at those environment values.
5. Serve API only over HTTPS.
6. Call `health.php`; require API/database/schema = true.
7. Provision the first Super Admin securely; never seed a default password.
8. Create/activate the beta event and load its race program.
9. Configure the notification worker as a cron job.
10. Add FCM/APNs credentials only through environment/server secrets.
11. Run beta workflow tests with two teams to verify cross-team isolation.
12. Install Android beta and iPhone TestFlight build; test foreground/background/screen-off notifications.
