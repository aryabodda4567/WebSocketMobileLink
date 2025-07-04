# WebSocketMobileLink

Welcome to the WebSocket-Based Mobile-Computer Connection System using LAN! This project provides seamless connectivity between your mobile device and computer by utilizing WebSocket technology. Whether you want to remotely control your computer from your mobile device or simply establish a quick and secure connection, this system has got you covered.

## Features

- **WebSocket Server in Python:**  
  A robust WebSocket server (Python) serves as the backbone, enabling real-time communication between your mobile device and computer.

- **QR Code-Based Connection:**  
  Connect your mobile device to your computer easily. Just ensure both devices are on the same Wi-Fi, scan the generated QR code using the mobile app, and you’re instantly connected.

- **Android Application:**  
  A dedicated Android app allows you to connect by scanning a QR code. Once connected, you are ready to go.

- **Remote Control:**  
  After connection, a webpage is opened on your computer. From this web interface, you can send commands and instructions to your mobile device for remote control within the same Wi-Fi.

- **Seamless Interaction:**  
  The system provides an intuitive and seamless interaction between your mobile device and computer.

## How to Use

1. **Clone the repository:**
    ```sh
    git clone https://github.com/aryabodda4567/WebSocketMobileLink.git
    ```

2. **Set up the Controller Server:**
    - Navigate to the `Controller Server` directory.
    - Install dependencies:
      ```sh
      pip install -r requirements.txt
      ```
    - Run the server:
      ```sh
      python main.py
      ```

3. **Set up the Android Controller:**
    - Open the `Controller` project in Android Studio.
    - Build and install the APK on your Android device.

4. **Connect:**
    - Open the installed app and scan the QR code displayed by the server.
    - A web page will open in your computer’s browser. From there, you can access and control the mobile device.

## Note

- If the system is not working perfectly after the first run, remove the browser from RAM and restart the server.

---

**Experience the future of mobile-computer connectivity with our WebSocket-Based Mobile-Computer Connection System. Say goodbye to the hassles of traditional connection methods and enjoy the convenience of remote control and interaction. Get started today!**
