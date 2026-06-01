#!/bin/bash
JDBC_URL="jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}?sslmode=require"
echo "JDBC_URL is: ${JDBC_URL}"
exec java -jar app.jar
