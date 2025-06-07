# JavaCV AI-Powered Video Transition Engine

A comprehensive video transition system built with JavaCV and OpenCV, featuring 33+ different transition effects including AI-powered object-aware transitions for seamless video editing.

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
- **BLUR_TRANSITION** - Enhanced progressive blur with dynamic kernel sizing
- **PIXELATE_TRANSITION** - Videos pixelate and crossfade

#### 📹 Whip Pan Transitions (NEW)
- **WHIP_PAN_LEFT** - Realistic camera pan left with horizontal motion blur
- **WHIP_PAN_RIGHT** - Realistic camera pan right with horizontal motion blur
- **WHIP_PAN_UP** - Realistic camera pan up with vertical motion blur
- **WHIP_PAN_DOWN** - Realistic camera pan down with vertical motion blur

#### 🤖 AI-Powered Object-Aware Transitions
- **OBJECT_REVEAL** - Smart object-based blending using AI segmentation
- **OBJECT_ZOOM_IN** - Zoom effects focused on detected objects
- **OBJECT_ZOOM_OUT** - Zoom out effects with object awareness
- **OBJECT_SLIDE_LEFT** - Slide transitions targeting specific objects
- **OBJECT_SLIDE_RIGHT** - Object-aware slide effects
- **OBJECT_FADE_IN** - Fade effects targeting detected objects
- **OBJECT_FADE_OUT** - Smart fade out with object detection
- **OBJECT_ROTATE_IN** - Rotation transitions around objects
- **OBJECT_ROTATE_OUT** - Object-focused rotation effects
- **OBJECT_SCALE_TRANSITION** - Scaling effects for object regions

## 🛠 Setup

### Prerequisites
- Java 8 or higher
- JavaCV library (javacv-platform-1.5.8.jar)
- **NEW**: ONNX Runtime for Java (onnxruntime-1.15.1.jar) - for AI features
- **OPTIONAL**: YOLOv8 segmentation model (.onnx) - for object detection

### Installation

#### Option A: Automatic Setup (Recommended)
```bash
# Download all dependencies automatically
download_dependencies.bat

# Compile with AI support
compile_ai.bat
```

#### Option B: Manual Setup
1. Download JavaCV from [GitHub Releases](https://github.com/bytedeco/javacv/releases)
2. Download ONNX Runtime from [Maven Central](https://repo1.maven.org/maven2/com/microsoft/onnxruntime/onnxruntime/1.15.1/)
3. (Optional) Download YOLOv8 model from [Ultralytics](https://github.com/ultralytics/yolov8/releases)
4. Place all JAR files in the project directory

### Quick Start
```bash
# Basic transitions (original functionality)
compile_and_run.bat video1.mp4 video2.mp4 ./output

# AI-powered transitions
compile_ai.bat
java -cp ".;javacv-platform-1.5.8.jar;onnxruntime-1.15.1.jar" AITransitionDemo video1.mp4 video2.mp4 ./ai_output

# With AI model for full object detection
java -cp ".;javacv-platform-1.5.8.jar;onnxruntime-1.15.1.jar" AITransitionDemo video1.mp4 video2.mp4 ./ai_output yolov8n-seg.onnx
```

## 🚀 Usage

### Quality Improvements Demo (NEW)
Experience the enhanced transition quality:
```bash
java -cp ".;javacv-platform-1.5.8.jar" QualityImprovementDemo input1.mp4 input2.mp4 ./output
```

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

## 🎯 Quality Improvements (Samsung PRISM Internship)

### ✨ Enhanced Transition Quality
This engine now features professional-grade transition quality with the following improvements:

#### 1. **Smooth Dissolve Transitions**
- **Issue Resolved**: Eliminated grainy, abrupt pixel transitions
- **Solution**: Implemented smooth cosine interpolation: `(1 - cos(progress * π)) / 2`
- **Result**: Natural-looking fades with seamless visual flow

#### 2. **Progressive Blur Transitions**
- **Issue Resolved**: Static blur kernels causing harsh, uneven results
- **Solution**: Dynamic kernel sizing with progressive blur intensity
- **Formula**: `kernelSize = max(3, (int)(blurIntensity * 30) + 1)`
- **Result**: High-quality, adaptive blur with no visual artifacts

#### 3. **Enhanced Frame Blending**
- **Issue Resolved**: Visible seams from basic addWeighted blending
- **Solution**: Cosine-smoothed alpha progression for seamless fusion
- **Result**: Professional-grade blending across all transition types

#### 4. **Whip Pan Transitions (NEW)**
- **Feature**: Realistic camera pan simulation with directional motion blur
- **Directions**: Left, Right, Up, Down
- **Technique**: Combined frame translation with motion blur
- **Result**: Dynamic, camera-like transitions emulating natural movement

### 📊 Mathematical Models
- **Easing Function**: `smoothAlpha = (1 - cos(progress * π)) / 2`
- **Progressive Blur**: `blurIntensity = 4.0 * progress * (1.0 - progress)`
- **Motion Blur Kernel**: `kernelSize = max(3, (int)(motionIntensity * 30) + 1)`

## 📁 File Structure

```
├── TransitionType.java          # Enum defining all transition types (updated)
├── VideoProcessor.java          # Enhanced utility functions with new blending methods
├── BaseTransition.java          # Abstract base class with cosine interpolation
├── FadeTransition.java          # Enhanced fade effects with smooth interpolation
├── SlideTransition.java         # Slide effect implementations
├── WipeTransition.java          # Wipe effect implementations
├── ZoomTransition.java          # Zoom effect implementations
├── RotateTransition.java        # Rotation effect implementations
├── EffectTransition.java        # Enhanced special effects with progressive blur
├── WhipPanTransition.java       # NEW: Whip pan transitions with motion blur
├── VideoTransitionEngine.java   # Main processing engine (updated)
├── QualityImprovementDemo.java  # NEW: Demo showcasing quality improvements
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
