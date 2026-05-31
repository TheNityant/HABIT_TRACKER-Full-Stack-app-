#!/usr/bin/env bash

BASE=${API_BASE:-http://localhost:3001}

echo "Health check:"
curl -s ${BASE}/health | jq || curl -s ${BASE}/health

echo "\nExposure assessment:"
curl -s -X POST ${BASE}/api/assessments/exposure \
  -H "Content-Type: application/json" \
  -H "x-demo-user-id: alice" \
  -d '{"publicInstagram":true,"locationSharing":false}' | jq


echo "\nActivate emergency:"
curl -s -X POST ${BASE}/api/emergency/activate \
  -H "Content-Type: application/json" \
  -H "x-demo-user-id: alice" \
  -d '{"reason":"Threat received","exposureAnswers":{"publicInstagram":true},"threatAnswers":{"directThreats":true}}' | jq


echo "\nList evidence:"
curl -s ${BASE}/api/evidence -H "x-demo-user-id: alice" | jq


echo "\nList audit logs:"
curl -s ${BASE}/api/audit -H "x-demo-user-id: alice" | jq


echo "\nNote: to test file upload, run the curl multipart example in the README."
