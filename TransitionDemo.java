import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Demo application showcasing all video transition effects
 */
public class TransitionDemo {
    
    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }
        
        String video1 = args[0];
        String video2 = args[1];
        String outputDir = args[2];
        
        VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 60);
        
        // Demo all transition types
        TransitionType[] allTransitions = {
            TransitionType.CROSSFADE,
            TransitionType.FADE_IN,
            TransitionType.FADE_OUT,
            TransitionType.DISSOLVE,
            TransitionType.SLIDE_LEFT,
            TransitionType.SLIDE_RIGHT,
            TransitionType.SLIDE_UP,
            TransitionType.SLIDE_DOWN,
            TransitionType.PUSH_LEFT,
            TransitionType.PUSH_RIGHT,
            TransitionType.WIPE_LEFT,
            TransitionType.WIPE_RIGHT,
            TransitionType.WIPE_UP,
            TransitionType.WIPE_DOWN,
            TransitionType.WIPE_CIRCLE,
            TransitionType.IRIS_IN,
            TransitionType.IRIS_OUT,
            TransitionType.ZOOM_IN,
            TransitionType.ZOOM_OUT,
            TransitionType.ROTATE_CLOCKWISE,
            TransitionType.ROTATE_COUNTERCLOCKWISE,
            TransitionType.BLUR_TRANSITION,
            TransitionType.PIXELATE_TRANSITION
        };
        
        List<String> inputVideos = Arrays.asList(video1, video2);
        
        System.out.println("JavaCV Video Transition Demo");
        System.out.println("============================");
        System.out.println("Input Video 1: " + video1);
        System.out.println("Input Video 2: " + video2);
        System.out.println("Output Directory: " + outputDir);
        System.out.println();
        
        // Create individual demo videos for each transition
        for (TransitionType transition : allTransitions) {
            try {
                String outputPath = outputDir + "/" + transition.name().toLowerCase() + "_demo.mp4";
                System.out.println("Creating demo: " + transition.name() + " -> " + outputPath);
                
                List<TransitionType> singleTransition = Arrays.asList(transition);
                engine.processVideosWithTransitions(inputVideos, singleTransition, outputPath);
                
                System.out.println("✓ Completed: " + transition.name());
                
            } catch (Exception e) {
                System.err.println("✗ Error processing " + transition.name() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Create a mega demo with multiple transitions
        try {
            System.out.println("\nCreating mega demo with multiple transitions...");
            createMegaDemo(video1, video2, outputDir + "/mega_demo.mp4", engine);
            System.out.println("✓ Mega demo completed!");
            
        } catch (Exception e) {
            System.err.println("✗ Error creating mega demo: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🎉 All demos completed!");
        System.out.println("Check the output directory for all transition examples.");
    }
    
    /**
     * Create a mega demo showing multiple transitions in sequence
     */
    private static void createMegaDemo(String video1, String video2, String outputPath, 
                                     VideoTransitionEngine engine) throws Exception {
        
        // Use the same two videos multiple times with different transitions
        List<String> videos = new ArrayList<>();
        List<TransitionType> transitions = new ArrayList<>();
        
        // Add videos and transitions for a showcase
        TransitionType[] showcaseTransitions = {
            TransitionType.CROSSFADE,
            TransitionType.SLIDE_LEFT,
            TransitionType.WIPE_CIRCLE,
            TransitionType.ZOOM_IN,
            TransitionType.ROTATE_CLOCKWISE,
            TransitionType.BLUR_TRANSITION
        };
        
        for (int i = 0; i < showcaseTransitions.length + 1; i++) {
            if (i % 2 == 0) {
                videos.add(video1);
            } else {
                videos.add(video2);
            }
            
            if (i < showcaseTransitions.length) {
                transitions.add(showcaseTransitions[i]);
            }
        }
        
        engine.processVideosWithTransitions(videos, transitions, outputPath);
    }
    
    /**
     * Print usage instructions
     */
    private static void printUsage() {
        System.out.println("JavaCV Video Transition Demo");
        System.out.println("Usage: java TransitionDemo <video1> <video2> <output_directory>");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  video1           - Path to first input video file");
        System.out.println("  video2           - Path to second input video file");
        System.out.println("  output_directory - Directory where demo videos will be saved");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  java TransitionDemo input1.mp4 input2.mp4 ./output");
        System.out.println();
        System.out.println("This will create demo videos for all available transitions:");
        
        TransitionType[] allTransitions = TransitionType.values();
        for (TransitionType transition : allTransitions) {
            System.out.println("  - " + transition.name().toLowerCase() + "_demo.mp4");
        }
        System.out.println("  - mega_demo.mp4 (multiple transitions in sequence)");
    }
}
