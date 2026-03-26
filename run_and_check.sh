#!/usr/bin/env bash
set -eu

java -jar target/*.jar > /tmp/spring_server.log 2>&1 &
pid=$!
echo "server pid: $pid"
echo "waiting for http://localhost:8080 (timeout 60s)..."
for i in {1..60}; do
  code=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/ || echo 000)
  echo "attempt $i: $code"
  if [ "$code" = "200" ]; then
    break
  fi
  sleep 1
done

echo "--- / (first 120 lines)"
curl -s http://localhost:8080/ | sed -n '1,120p'

echo "--- link tags:"
curl -s http://localhost:8080/ | grep -i '<link' | sed -n '1,20p' || true

echo "--- CSS headers:"
curl -s -D - http://localhost:8080/style.css -o /tmp/style.css.headers || true
sed -n '1,40p' /tmp/style.css.headers 2>/dev/null || true

echo "--- CSS snippet:"
curl -s http://localhost:8080/style.css | sed -n '1,80p'

echo "stopping server pid $pid"
kill $pid || true
wait $pid 2>/dev/null || true

echo 'server stopped'
