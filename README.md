# xShow

KVision app to host a slideshow of local content for viewing in browsers.

Currently only tested on Windows 11 with Chrome.

## Setup

1. Edit `server\src\main\resources\config.json` to point to the directory containing the images and videos you want to
   display.
2. Start the server by running `./gradlew :server:run`
3. Start the client by running `./gradlew -t wasmJsRun`. This will start the app in development mode and automatically
   rebuild the client when changes are made.
4. Open a browser and navigate to `http://localhost:8081/`. This may happen automatically when running the client.

