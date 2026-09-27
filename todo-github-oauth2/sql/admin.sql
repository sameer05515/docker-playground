-- Find users
SELECT id, github_id, username, name, email, role FROM users;

-- Promote a GitHub user to admin
-- UPDATE users SET role = 'ROLE_ADMIN' WHERE github_id = 'YOUR_GITHUB_ID';
