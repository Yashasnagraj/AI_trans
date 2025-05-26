import java.util.Arrays;
import java.util.List;

/**
 * Simple test application for individual transitions
 */
public class SimpleTransitionTest {
    
    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
            return;
        }
        
        String video1 = args[0];
        String video2 = args[1];
        String transitionName = args[2].toUpperCase();
        String outputPath = args[3];
        
        try {
            // Parse transition type
            TransitionType transitionType = TransitionType.valueOf(transitionName);
            
            // Create engine
            VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 60);
            
            // Process videos
            List<String> inputVideos = Arrays.asList(video1, video2);
            List<TransitionType> transitions = Arrays.asList(transitionType);
            
            System.out.println("JavaCV Simple Transition Test");
            System.out.println("=============================");
            System.out.println("Input Video 1: " + video1);
            System.out.println("Input Video 2: " + video2);
            System.out.println("Transition: " + transitionType.name());
            System.out.println("Output: " + outputPath);
            System.out.println();
            
            System.out.println("Processing...");
            engine.processVideosWithTransitions(inputVideos, transitions, outputPath);
            
            System.out.println("✓ Transition completed successfully!");
            System.out.println("Output saved to: " + outputPath);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid transition type '" + transitionName + "'");
            System.err.println("Available transitions:");
            for (TransitionType type : TransitionType.values()) {
                System.err.println("  - " + type.name());
            }
        } catch (Exception e) {
            System.err.println("Error processing videos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void printUsage() {
        System.out.println("JavaCV Simple Transition Test");
        System.out.println("Usage: java SimpleTransitionTest <video1> <video2> <transition> <output>");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  video1     - Path to first input video file");
        System.out.println("  video2     - Path to second input video file");
        System.out.println("  transition - Transition type (see list below)");
        System.out.println("  output     - Output video file path");
        System.out.println();
        System.out.println("Available transitions:");
        for (TransitionType type : TransitionType.values()) {
            System.out.println("  - " + type.name());
        }
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java SimpleTransitionTest input1.mp4 input2.mp4 CROSSFADE output.mp4");
        System.out.println("  java SimpleTransitionTest video1.mp4 video2.mp4 SLIDE_LEFT result.mp4");
        System.out.println("  java SimpleTransitionTest clip1.mp4 clip2.mp4 WIPE_CIRCLE transition.mp4");
    }
}
