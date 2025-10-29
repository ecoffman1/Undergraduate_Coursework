#!/bin/bash
set -e
echo "compiling typescript"
tsc --strict server.ts
tsc --strict game.ts
echo "launching server..."
node server.js &
echo "launching client..."
open http://127.0.0.1:8080/client.html &
echo "waiting for all processes to finish..."
wait
echo "Done."