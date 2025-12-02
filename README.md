# MsgHarvester Android Project

This project builds an Android app **MsgHarvester** that reads SMS messages (existing inbox + incoming),
extracts OTPs and sends each message to your server with HMAC verification.

Server endpoint (preconfigured): https://dikshtech.com/readsmsapp/readdata.php

API_KEY embedded: a3f7c91e4b20489fb6d12ea7c8f0e1b74e9d3a2c56b147d8f03bc9e5a7d1c42f
SHARED_SECRET embedded: (hidden in repository) - embedded in code.

## How to build
1. Open in Android Studio or upload the ZIP to an online builder (e.g., https://appetize.io/upload).
2. Build release APK and install on device.
3. Grant SMS permissions when prompted.

## Notes
- Keep API_KEY and SHARED_SECRET secure. Rotating is recommended.
- This project is ready-to-build (Option A requested).
