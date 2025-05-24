#!/bin/bash

# Start backend services
cd /workspace/localmarket/hyperlocalbymanus
echo "Starting backend services..."

# Install required packages if not already installed
pip install -r requirements.txt

# Start all services
echo "Starting user service..."
python run_service.py user &
USER_PID=$!
echo "User service started with PID: $USER_PID"

echo "Starting seller service..."
python run_service.py seller &
SELLER_PID=$!
echo "Seller service started with PID: $SELLER_PID"

echo "Starting customer service..."
python run_service.py customer &
CUSTOMER_PID=$!
echo "Customer service started with PID: $CUSTOMER_PID"

echo "Starting catalog service..."
python run_service.py catalog &
CATALOG_PID=$!
echo "Catalog service started with PID: $CATALOG_PID"

echo "Starting admin service..."
python run_service.py admin &
ADMIN_PID=$!
echo "Admin service started with PID: $ADMIN_PID"

BACKEND_PIDS="$USER_PID $SELLER_PID $CUSTOMER_PID $CATALOG_PID $ADMIN_PID"
echo "All backend services started"

# Start frontend
cd /workspace/localmarket/localmarket-frontend
echo "Starting frontend services..."

# Install dependencies if not already installed
npm install

# Start the frontend
npm run dev &
FRONTEND_PID=$!
echo "Frontend started with PID: $FRONTEND_PID"

echo "All services started. Press Ctrl+C to stop."

# Wait for user to press Ctrl+C
trap "kill $BACKEND_PIDS $FRONTEND_PID; exit" INT
wait