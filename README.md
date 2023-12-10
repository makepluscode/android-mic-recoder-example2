# Android Audio Recorder and Player

## Project Overview
This project is an Android application for recording and playing audio. Users can record audio and play the last recorded audio using this app. The recording and playback functionalities are controlled through separate buttons.

## Features
- **Audio Recording**: Users can start or stop audio recording through the 'Start/Stop Recording' button.
- **Audio Playback**: Users can play the last recorded audio using the 'Play' button.
- **UI Interaction**: Buttons change their color and text based on user interaction.

## Installation
The app can be built and run using Android Studio.
1. Clone the project:
   ```
   git clone https://github.com/your-username/your-project-name.git
   ```
2. Open the cloned project in Android Studio.
3. Click the 'Run' button to execute the app on a device or emulator.

## Class Structure and Responsibilities

| Class Name            | Responsibilities |
|-----------------------|------------------|
| `MainActivity`        | Manages the main screen of the application and handles user interface interactions. Processes events for the recording and playback buttons, and controls audio recording and playback functionalities using `AudioRecorderManager` and `AudioPlayerManager`. |
| `AudioRecorderManager`| Responsible for the audio recording functionality. Utilizes the `AudioRecord` class to capture audio data and save the recorded data to a file. |
| `AudioPlayerManager`  | Handles the audio playback functionality. Uses the `AudioTrack` class to play back stored audio files. |
| `PermissionManager`   | Manages the permissions required for the application. Requests necessary permissions from the user for audio recording and storage, and checks the status of these permissions. |

## Usage
- Upon launching the app, two buttons are displayed.
- Press the 'Start/Stop Recording' button to begin or end audio recording.
- Press the 'Play' button to play the last recorded audio.

## Tech Stack
- **Language**: Java
- **Framework**: Android SDK
- **Audio Processing**: Utilizes `AudioRecord` and `AudioTrack` classes

## License
This project is distributed under the [MIT License](LICENSE).

---
