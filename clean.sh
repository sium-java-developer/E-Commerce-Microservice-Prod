#!/bin/bash

echo "--- Stopping and removing v14 containers and images ---"
sudo docker compose -p product-v14 down --remove-orphans --volumes -t 0
