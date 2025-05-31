import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Demo application showcasing AI-powered object-aware video transition effects
 */
public class AITransitionDemo {

    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }

        String video1 = args[0];
        String video2 = args[1];
        String outputDir = args[2];
        String modelPath = args.length > 3 ? args[3] : null;

        System.out.println("🤖 AI-Powered Video Transition Demo");
        System.out.println("=====================================");
        System.out.println("Input Video 1: " + video1);
        System.out.println("Input Video 2: " + video2);
        System.out.println("Output Directory: " + outputDir);
        if (modelPath != null) {
            System.out.println("AI Model: " + modelPath);
        } else {
            System.out.println("AI Model: Not provided (using fallback masks)");
        }
        System.out.println();

        // Create engine with AI features
        VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 90); // 3 seconds at 30fps

        // Enable AI features if model is provided
        if (modelPath != null) {
            engine.enableAIFeatures(modelPath);
            engine.setDefaultTransitionConfig("MAGIC");
        } else {
            engine.setDefaultTransitionConfig("CINEMATIC");
        }

        List<String> inputVideos = Arrays.asList(video1, video2);

        // Define AI-powered transitions to demo
        List<TransitionType> aiTransitions = Arrays.asList(
            TransitionType.OBJECT_REVEAL,
            TransitionType.OBJECT_ZOOM_IN,
            TransitionType.OBJECT_ZOOM_OUT,
            TransitionType.OBJECT_SLIDE_LEFT,
            TransitionType.OBJECT_SLIDE_RIGHT,
            TransitionType.OBJECT_FADE_IN,
            TransitionType.OBJECT_FADE_OUT,
            TransitionType.OBJECT_ROTATE_IN,
            TransitionType.OBJECT_ROTATE_OUT,
            TransitionType.OBJECT_SCALE_TRANSITION
        );

        System.out.println("Creating AI-powered transition demos...");
        System.out.println("Total transitions to process: " + aiTransitions.size());
        System.out.println();

        int successCount = 0;
        int errorCount = 0;

        // Create individual demo videos for each AI transition
        for (TransitionType transition : aiTransitions) {
            try {
                String outputPath = outputDir + "/ai_" + transition.name().toLowerCase() + "_demo.mp4";
                System.out.println("🎬 Creating demo: " + transition.name() + " -> " + outputPath);

                List<TransitionType> singleTransition = Arrays.asList(transition);
                engine.processVideosWithTransitions(inputVideos, singleTransition, outputPath);

                System.out.println("✅ Completed: " + transition.name());
                successCount++;

            } catch (Exception e) {
                System.err.println("ERROR: Error processing " + transition.name() + ": " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
            System.out.println();
        }

        // Create comparison demos with different presets
        try {
            System.out.println("🎨 Creating preset comparison demos...");
            createPresetComparisons(video1, video2, outputDir, engine);
            successCount += 4; // 4 presets

        } catch (Exception e) {
            System.err.println("ERROR: Error creating preset comparisons: " + e.getMessage());
            e.printStackTrace();
            errorCount++;
        }

        // Create mega demo with multiple AI transitions
        try {
            System.out.println("🚀 Creating AI mega demo with multiple transitions...");
            createAIMegaDemo(video1, video2, outputDir + "/ai_mega_demo.mp4", engine);
            System.out.println("✅ AI mega demo completed!");
            successCount++;

        } catch (Exception e) {
            System.err.println("ERROR: Error creating AI mega demo: " + e.getMessage());
            e.printStackTrace();
            errorCount++;
        }

        // Print summary
        System.out.println();
        System.out.println("🎉 AI Transition Demo Summary");
        System.out.println("=============================");
        System.out.println("✅ Successful demos: " + successCount);
        System.out.println("FAILED demos: " + errorCount);
        System.out.println("Check the output directory for all AI transition examples.");

        if (modelPath == null) {
            System.out.println();
            System.out.println("💡 Tip: Provide a YOLOv8 segmentation model (.onnx) as the 4th argument");
            System.out.println("   to enable true AI-powered object detection for better results!");
        }
    }

    /**
     * Create comparison demos with different transition presets
     */
    private static void createPresetComparisons(String video1, String video2, String outputDir,
                                              VideoTransitionEngine engine) throws Exception {

        String[] presets = {"SMOOTH", "DYNAMIC", "CINEMATIC", "MAGIC"};
        List<String> inputVideos = Arrays.asList(video1, video2);

        for (String preset : presets) {
            System.out.println("🎨 Creating " + preset + " preset demo...");

            // Configure engine for this preset
            engine.setDefaultTransitionConfig(preset);

            String outputPath = outputDir + "/preset_" + preset.toLowerCase() + "_object_reveal.mp4";
            List<TransitionType> transition = Arrays.asList(TransitionType.OBJECT_REVEAL);

            engine.processVideosWithTransitions(inputVideos, transition, outputPath);
            System.out.println("✅ " + preset + " preset demo completed");
        }
    }

    /**
     * Create a mega demo showcasing multiple AI transitions in sequence
     */
    private static void createAIMegaDemo(String video1, String video2, String outputPath,
                                       VideoTransitionEngine engine) throws Exception {

        // Create a sequence with multiple videos using different AI transitions
        List<String> inputVideos = Arrays.asList(video1, video2, video1, video2, video1);

        List<TransitionType> transitions = Arrays.asList(
            TransitionType.OBJECT_REVEAL,
            TransitionType.OBJECT_ZOOM_IN,
            TransitionType.OBJECT_SLIDE_LEFT,
            TransitionType.OBJECT_ROTATE_IN
        );

        // Use MAGIC preset for the mega demo
        engine.setDefaultTransitionConfig("MAGIC");

        engine.processVideosWithTransitions(inputVideos, transitions, outputPath);
    }

    /**
     * Print usage instructions
     */
    private static void printUsage() {
        System.out.println("🤖 AI-Powered Video Transition Demo");
        System.out.println("Usage: java AITransitionDemo <video1> <video2> <output_directory> [model_path]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  video1           - Path to first input video file");
        System.out.println("  video2           - Path to second input video file");
        System.out.println("  output_directory - Directory where demo videos will be saved");
        System.out.println("  model_path       - (Optional) Path to YOLOv8 segmentation model (.onnx)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java AITransitionDemo input1.mp4 input2.mp4 ./ai_output");
        System.out.println("  java AITransitionDemo video1.mp4 video2.mp4 ./output yolov8n-seg.onnx");
        System.out.println();
        System.out.println("This will create AI-powered transition demos including:");
        System.out.println("  • Object-aware reveal transitions");
        System.out.println("  • Object zoom in/out effects");
        System.out.println("  • Object slide transitions");
        System.out.println("  • Object fade in/out effects");
        System.out.println("  • Object rotation transitions");
        System.out.println("  • Object scale transitions");
        System.out.println("  • Preset comparison demos");
        System.out.println("  • AI mega demo with multiple transitions");
        System.out.println();
        System.out.println("📥 To get YOLOv8 segmentation model:");
        System.out.println("  1. Visit: https://github.com/ultralytics/yolov8/releases");
        System.out.println("  2. Download: yolov8n-seg.onnx (nano) or yolov8s-seg.onnx (small)");
        System.out.println("  3. Place in your project directory");
        System.out.println();
        System.out.println("🎯 Features:");
        System.out.println("  • AI-powered object detection and segmentation");
        System.out.println("  • Automatic exposure and lighting matching");
        System.out.println("  • Customizable animation presets");
        System.out.println("  • Smooth mask feathering and blending");
        System.out.println("  • Fallback to manual masks when AI model not available");
    }
}
