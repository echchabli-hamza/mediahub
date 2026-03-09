#!/bin/bash
# Dev entrypoint: runs Spring Boot + watches .java files for hot reload
# When a .java file changes, triggers recompilation Ã¢â€ â€™ DevTools auto-restarts

# Start spring-boot:run in the background
./mvnw spring-boot:run &
APP_PID=$!

# Wait for initial compilation to finish
sleep 5

echo "================================================"
echo "  HOT RELOAD ACTIVE Ã¢â‚¬â€ watching src/ for changes"
echo "================================================"

# Watch for .java file changes and trigger recompilation
while true; do
  # Use find to detect files modified in the last 3 seconds
  CHANGED=$(find src -name "*.java" -newer /tmp/.last_compile 2>/dev/null)
  if [ -n "$CHANGED" ]; then
    echo ""
    echo ">>> Change detected, recompiling..."
    ./mvnw compile -q 2>/dev/null
    echo ">>> Recompilation done Ã¢â‚¬â€ DevTools will restart the app"
  fi
  touch /tmp/.last_compile
  sleep 3
done

wait $APP_PID
