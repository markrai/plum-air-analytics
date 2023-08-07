-- Step 1: Insert new token data
UPDATE ecobee_token
SET
    access_token = 'YOUR_NEW_ACCESS_TOKEN_VALUE',
    access_token_expires_at = DATE_ADD(NOW(), INTERVAL 50 MINUTE),
    refresh_token = 'YOUR_NEW_REFRESH_TOKEN_VALUE',
    refresh_token_expires_at = DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 YEAR), INTERVAL -1 DAY);

-- Step 2: Delete all older rows
DELETE FROM ecobee_token
WHERE access_token_expires_at NOT IN (
    SELECT MAX(access_token_expires_at) FROM ecobee_token
);
