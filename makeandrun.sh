#!/bin/bash

# Clean up old containers first
bash clean.sh

echo "--- Starting services with v14 ---"
sudo docker compose -p product-v14 up --build

