# JavaCV Video Transition Engine

A comprehensive video transition system built with JavaCV and OpenCV, featuring 23 different transition effects for seamless video editing.

## 🎬 Features

### Transition Effects Available

#### Fade Transitions
- **CROSSFADE** - Smooth blend between two videos
- **FADE_IN** - Video gradually appears from black
- **FADE_OUT** - Video gradually disappears to black
- **DISSOLVE** - Linear crossfade effect

#### Slide Transitions
- **SLIDE_LEFT** - New video slides in from right
- **SLIDE_RIGHT** - New video slides in from left
- **SLIDE_UP** - New video slides in from bottom
- **SLIDE_DOWN** - New video slides in from top
- **PUSH_LEFT** - Both videos move left together
- **PUSH_RIGHT** - Both videos move right together

#### Wipe Transitions
- **WIPE_LEFT** - New video appears from left edge
- **WIPE_RIGHT** - New video appears from right edge
- **WIPE_UP** - New video appears from top edge
- **WIPE_DOWN** - New video appears from bottom edge
- **WIPE_CIRCLE** - New video appears in expanding circle
- **IRIS_IN** - Circular opening reveals new video
- **IRIS_OUT** - Circular closing hides old video

#### Zoom Transitions
- **ZOOM_IN** - Old video zooms in while new video fades in
- **ZOOM_OUT** - New video starts zoomed and zooms out

#### Rotation Transitions
- **ROTATE_CLOCKWISE** - Videos rotate clockwise during transition
- **ROTATE_COUNTERCLOCKWISE** - Videos rotate counterclockwise during transition

#### Effect Transitions
- **BLUR_TRANSITION** - Videos blur and crossfade
- **PIXELATE_TRANSITION** - Videos pixelate and crossfade

## 🛠 Setup

### Prerequisites
- Java 8 or higher
- JavaCV library (javacv-platform-1.5.8.jar)

### Installation
1. Download JavaCV from [GitHub Releases](https://github.com/bytedeco/javacv/releases)
2. Place `javacv-platform-1.5.8.jar` in the project directory
3. Compile using the provided batch script or manually

### Quick Start
```bash
# Compile all files
compile_and_run.bat video1.mp4 video2.mp4 ./output

# Or compile manually
javac -cp "javacv-platform-1.5.8.jar" *.java
```

## 🚀 Usage

### Demo Application
Create demo videos for all transition types:
```bash
java -cp ".;javacv-platform-1.5.8.jar" TransitionDemo input1.mp4 input2.mp4 ./output
```

### Simple Transition Test
Test individual transitions:
```bash
java -cp ".;javacv-platform-1.5.8.jar" SimpleTransitionTest video1.mp4 video2.mp4 CROSSFADE output.mp4
```

### Programmatic Usage
```java
// Create engine
VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 60);

// Define videos and transitions
List<String> videos = Arrays.asList("video1.mp4", "video2.mp4", "video3.mp4");
List<TransitionType> transitions = Arrays.asList(
    TransitionType.CROSSFADE, 
    TransitionType.SLIDE_LEFT
);

// Process
engine.processVideosWithTransitions(videos, transitions, "output.mp4");
```

## 📁 File Structure

```
├── TransitionType.java          # Enum defining all transition types
├── VideoProcessor.java          # Utility functions for video processing
├── BaseTransition.java          # Abstract base class for transitions
├── FadeTransition.java          # Fade effect implementations
├── SlideTransition.java         # Slide effect implementations
├── WipeTransition.java          # Wipe effect implementations
├── ZoomTransition.java          # Zoom effect implementations
├── RotateTransition.java        # Rotation effect implementations
├── EffectTransition.java        # Special effect implementations
├── VideoTransitionEngine.java   # Main processing engine
├── TransitionDemo.java          # Demo application
├── SimpleTransitionTest.java    # Simple test application
├── compile_and_run.bat          # Compilation script
└── README.md                    # This file
```

## ⚙️ Configuration

### Engine Parameters
- **Output Resolution**: Default 1280x720, customizable
- **Frame Rate**: Default 30fps, customizable
- **Transition Duration**: Default 60 frames (2 seconds), customizable

### Customization Example
```java
VideoTransitionEngine engine = new VideoTransitionEngine();
engine.setOutputDimensions(1920, 1080);  // Full HD
engine.setFrameRate(60.0);                // 60fps
engine.setTransitionFrames(90);           // 1.5 seconds at 60fps
```

## 🎯 Performance Notes

- **Video-only processing**: Audio streams are excluded for simplicity
- **Automatic resizing**: Input videos are resized to match output dimensions
- **Memory efficient**: Processes videos in chunks to manage memory usage
- **Cross-platform**: Works on Windows, macOS, and Linux

## 🔧 Troubleshooting

### Common Issues
1. **JavaCV not found**: Ensure `javacv-platform-1.5.8.jar` is in the classpath
2. **Out of memory**: Reduce video resolution or transition duration
3. **Codec issues**: Ensure input videos are in supported formats (MP4, AVI, MOV)

### Supported Formats
- **Input**: MP4, AVI, MOV, MKV, and most common video formats
- **Output**: MP4 with H.264 codec

## 📊 Examples

The demo application creates individual examples for each transition type:
- `crossfade_demo.mp4`
- `slide_left_demo.mp4`
- `wipe_circle_demo.mp4`
- `zoom_in_demo.mp4`
- `rotate_clockwise_demo.mp4`
- `blur_transition_demo.mp4`
- And many more...

Plus a `mega_demo.mp4` showcasing multiple transitions in sequence.

## 🤝 Contributing

Feel free to extend this system with additional transition effects by:
1. Adding new transition types to `TransitionType.java`
2. Creating new transition classes extending `BaseTransition`
3. Updating the engine to handle new transition types

## 📄 License

This project is open source and available under the MIT License.
# JAVA_IMPLEMENTATION
