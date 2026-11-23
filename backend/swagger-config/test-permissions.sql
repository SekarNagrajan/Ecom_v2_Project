-- Test permissions for user 'alice'
-- Run these SQL commands to give alice access to different API groups

-- Give alice tracking permission
INSERT INTO user_permissions (username, permission_type, permission_value, description) 
VALUES ('alice', 'ENDPOINT', 'tracking', 'Access to tracking endpoints');

-- Give alice schedule permission
INSERT INTO user_permissions (username, permission_type, permission_value, description) 
VALUES ('alice', 'ENDPOINT', 'schedule', 'Access to schedule endpoints');

-- Give alice vessel permission
INSERT INTO user_permissions (username, permission_type, permission_value, description) 
VALUES ('alice', 'ENDPOINT', 'vessel', 'Access to vessel endpoints');

-- Give alice port permission
INSERT INTO user_permissions (username, permission_type, permission_value, description) 
VALUES ('alice', 'ENDPOINT', 'port', 'Access to port endpoints');

-- Check what permissions alice has
SELECT * FROM user_permissions WHERE username = 'alice';

-- Check if alice exists in users table
SELECT * FROM ecomapi_users WHERE username = 'alice';
