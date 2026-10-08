-- Initialize individual databases for microservices and monolith
CREATE DATABASE taskflow;
CREATE DATABASE taskflow_users;
CREATE DATABASE taskflow_tasks;
CREATE DATABASE taskflow_notifications;

-- Grant permissions to default postgres user
GRANT ALL PRIVILEGES ON DATABASE taskflow TO postgres;
GRANT ALL PRIVILEGES ON DATABASE taskflow_users TO postgres;
GRANT ALL PRIVILEGES ON DATABASE taskflow_tasks TO postgres;
GRANT ALL PRIVILEGES ON DATABASE taskflow_notifications TO postgres;
