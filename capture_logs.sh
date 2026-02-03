#!/bin/bash

# Clean up old containers first
bash clean.sh

echo "--- Starting services with v14 and capturing logs to application.log ---"
echo "You can inspect application.log in another terminal or text editor."

# Run docker compose and redirect both stdout and stderr to application.log, while also showing it on console
sudo docker compose -p product-v14 up --build 2>&1 | tee application.log
