import java.util.Arrays;
import java.util.List;

/**
 * Simple test application for AI-powered transitions
 * Tests individual AI transition types with customizable settings
 */
public class AITransitionTest {

    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
            return;
        }

        String video1 = args[0];
        String video2 = args[1];
        String transitionName = args[2];
        String outputPath = args[3];
        String modelPath = args.length > 4 ? args[4] : null;
        String presetName = args.length > 5 ? args[5] : "MAGIC";

        System.out.println("🤖 AI Transition Test");
        System.out.println("====================");
        System.out.println("Input Video 1: " + video1);
        System.out.println("Input Video 2: " + video2);
        System.out.println("Transition: " + transitionName);
        System.out.println("Output: " + outputPath);
        System.out.println("Preset: " + presetName);
        if (modelPath != null) {
            System.out.println("AI Model: " + modelPath);
        } else {
            System.out.println("AI Model: Not provided (using fallback)");
        }
        System.out.println();

        try {
            // Parse transition type
            TransitionType transitionType;
            try {
                transitionType = TransitionType.valueOf(transitionName.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("ERROR: Invalid transition type: " + transitionName);
                System.err.println("Available AI transitions:");
                printAvailableAITransitions();
                return;
            }

            // Verify it's an AI transition
            if (!isAITransition(transitionType)) {
                System.err.println("ERROR: Not an AI transition: " + transitionName);
                System.err.println("Use SimpleTransitionTest for basic transitions.");
                System.err.println("Available AI transitions:");
                printAvailableAITransitions();
                return;
            }

            // Create and configure engine
            VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 90);

            // Configure AI features
            if (modelPath != null) {
                engine.enableAIFeatures(modelPath);
                System.out.println("✅ AI features enabled with model: " + modelPath);
            } else {
                System.out.println("WARNING: AI model not provided - using fallback masks");
            }

            // Set preset
            engine.setDefaultTransitionConfig(presetName);
            System.out.println("✅ Using preset: " + presetName);

            // Test the transition configuration
            TransitionConfig config = engine.getDefaultTransitionConfig();
            System.out.println("📋 Transition Configuration:");
            System.out.println("   Easing: " + config.getEasingFunction());
            System.out.println("   Zoom Level: " + config.getZoomLevel());
            System.out.println("   Blur Amount: " + config.getBlurAmount());
            System.out.println("   Feather Radius: " + config.getFeatherRadius());
            System.out.println("   Object Detection: " + config.isUseObjectDetection());
            System.out.println("   Target Class: " + config.getTargetObjectClass());
            System.out.println();

            // Process videos
            List<String> inputVideos = Arrays.asList(video1, video2);
            List<TransitionType> transitions = Arrays.asList(transitionType);

            System.out.println("🎬 Processing transition: " + transitionType.name());
            long startTime = System.currentTimeMillis();

            engine.processVideosWithTransitions(inputVideos, transitions, outputPath);

            long endTime = System.currentTimeMillis();
            double processingTime = (endTime - startTime) / 1000.0;

            System.out.println();
            System.out.println("✅ AI transition completed successfully!");
            System.out.println("Output saved to: " + outputPath);
            System.out.println("Processing time: " + String.format("%.2f", processingTime) + " seconds");

            // Provide tips based on configuration
            provideTips(config, modelPath != null, processingTime);

        } catch (Exception e) {
            System.err.println("ERROR: Error processing AI transition: " + e.getMessage());
            e.printStackTrace();

            System.err.println();
            System.err.println("💡 Troubleshooting tips:");
            System.err.println("1. Ensure all dependencies are downloaded (run download_dependencies.bat)");
            System.err.println("2. Check that input video files exist and are readable");
            System.err.println("3. Verify output directory is writable");
            System.err.println("4. If using AI model, ensure the .onnx file is valid");
        }
    }

    /**
     * Check if transition type is an AI-powered transition
     */
    private static boolean isAITransition(TransitionType type) {
        switch (type) {
            case OBJECT_REVEAL:
            case OBJECT_ZOOM_IN:
            case OBJECT_ZOOM_OUT:
            case OBJECT_SLIDE_LEFT:
            case OBJECT_SLIDE_RIGHT:
            case OBJECT_FADE_IN:
            case OBJECT_FADE_OUT:
            case OBJECT_ROTATE_IN:
            case OBJECT_ROTATE_OUT:
            case OBJECT_SCALE_TRANSITION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Print available AI transition types
     */
    private static void printAvailableAITransitions() {
        System.err.println("  • OBJECT_REVEAL - Smooth object-based blending");
        System.err.println("  • OBJECT_ZOOM_IN - Zoom in effect on detected objects");
        System.err.println("  • OBJECT_ZOOM_OUT - Zoom out effect on detected objects");
        System.err.println("  • OBJECT_SLIDE_LEFT - Slide left with object awareness");
        System.err.println("  • OBJECT_SLIDE_RIGHT - Slide right with object awareness");
        System.err.println("  • OBJECT_FADE_IN - Fade in effect targeting objects");
        System.err.println("  • OBJECT_FADE_OUT - Fade out effect targeting objects");
        System.err.println("  • OBJECT_ROTATE_IN - Rotation transition inward");
        System.err.println("  • OBJECT_ROTATE_OUT - Rotation transition outward");
        System.err.println("  • OBJECT_SCALE_TRANSITION - Scaling effect for objects");
    }

    /**
     * Provide performance and configuration tips
     */
    private static void provideTips(TransitionConfig config, boolean hasAIModel, double processingTime) {
        System.out.println();
        System.out.println("💡 Tips and Recommendations:");

        if (!hasAIModel) {
            System.out.println("🤖 AI Model: Consider downloading a YOLOv8 model for better object detection:");
            System.out.println("   • yolov8n-seg.onnx (fastest, ~7MB)");
            System.out.println("   • yolov8s-seg.onnx (balanced, ~22MB)");
            System.out.println("   Download from: https://github.com/ultralytics/yolov8/releases");
        }

        if (processingTime > 30) {
            System.out.println("Performance: Processing took " + String.format("%.1f", processingTime) + "s. To improve:");
            System.out.println("   • Use yolov8n-seg.onnx (nano model) for faster AI inference");
            System.out.println("   • Reduce video resolution or transition duration");
            System.out.println("   • Increase JVM memory: java -Xmx4g ...");
        }

        if (config.getBlurAmount() > 5.0f) {
            System.out.println("Blur: High blur amount (" + config.getBlurAmount() + ") may slow processing");
        }

        if (config.getZoomLevel() > 1.5f) {
            System.out.println("Zoom: High zoom level (" + config.getZoomLevel() + ") may cause quality loss");
        }

        System.out.println("🎨 Presets: Try different presets for various effects:");
        System.out.println("   • SMOOTH - Gentle, professional transitions");
        System.out.println("   • DYNAMIC - Fast, energetic effects");
        System.out.println("   • CINEMATIC - Film-like, subtle transitions");
        System.out.println("   • MAGIC - AI-optimized with all features enabled");
    }

    /**
     * Print usage instructions
     */
    private static void printUsage() {
        System.out.println("🤖 AI Transition Test - Test individual AI-powered transitions");
        System.out.println("Usage: java AITransitionTest <video1> <video2> <transition> <output> [model] [preset]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  video1     - Path to first input video file");
        System.out.println("  video2     - Path to second input video file");
        System.out.println("  transition - AI transition type (see list below)");
        System.out.println("  output     - Output video file path");
        System.out.println("  model      - (Optional) Path to YOLOv8 segmentation model (.onnx)");
        System.out.println("  preset     - (Optional) Transition preset (SMOOTH, DYNAMIC, CINEMATIC, MAGIC)");
        System.out.println();
        System.out.println("Available AI Transitions:");
        printAvailableAITransitions();
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java AITransitionTest video1.mp4 video2.mp4 OBJECT_REVEAL output.mp4");
        System.out.println("  java AITransitionTest clip1.mp4 clip2.mp4 OBJECT_ZOOM_IN result.mp4 yolov8n-seg.onnx");
        System.out.println("  java AITransitionTest a.mp4 b.mp4 OBJECT_SLIDE_LEFT out.mp4 yolov8n-seg.onnx CINEMATIC");
        System.out.println();
        System.out.println("💡 For testing basic transitions, use: SimpleTransitionTest");
        System.out.println("💡 For comprehensive demos, use: AITransitionDemo");
    }
}
