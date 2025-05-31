import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * SequenceTransitionDemo - Demonstrates a sequence of different transitions between multiple videos
 * 
 * This creates a video with the pattern:
 * Video1 → [Transition1] → Video2 → [Transition2] → Video3 → [Transition3] → Video4 → [Transition4] → Video5
 * 
 * Each transition is different, creating a professional-looking video sequence.
 */
public class SequenceTransitionDemo {

    private static final String INPUT_FOLDER = "input_videos";
    private static final String OUTPUT_FOLDER = "output_videos";

    public static void main(String[] args) {
        System.out.println("🎬 Sequence Transition Demo");
        System.out.println("==========================");
        System.out.println("Creating video with sequence of different transitions between videos");
        System.out.println();

        // Check for input videos
        File[] videoFiles = checkInputVideos();
        if (videoFiles == null || videoFiles.length < 5) {
            System.out.println("Need at least 5 input videos. Found: " + 
                             (videoFiles != null ? videoFiles.length : 0));
            System.out.println("Please add more videos to the input_videos folder.");
            return;
        }

        // Create output directory if it doesn't exist
        File outputDir = new File(OUTPUT_FOLDER);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Process videos with sequence of transitions
        processVideosWithTransitionSequence(videoFiles);
    }

    /**
     * Check for input videos in the input folder
     */
    private static File[] checkInputVideos() {
        File inputDir = new File(INPUT_FOLDER);
        
        if (!inputDir.exists()) {
            inputDir.mkdirs();
            System.out.println("Created input_videos folder. Please add your videos there.");
            return new File[0];
        }
        
        File[] videoFiles = inputDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".mp4") || 
            name.toLowerCase().endsWith(".avi") || 
            name.toLowerCase().endsWith(".mov"));
            
        if (videoFiles != null && videoFiles.length > 0) {
            System.out.println("Found " + videoFiles.length + " input videos:");
            for (File video : videoFiles) {
                System.out.println("  - " + video.getName());
            }
            System.out.println();
        }
        
        return videoFiles;
    }

    /**
     * Process videos with a sequence of different transitions
     * Video1 → [Transition1] → Video2 → [Transition2] → Video3 → [Transition3] → Video4 → [Transition4] → Video5
     */
    private static void processVideosWithTransitionSequence(File[] videoFiles) {
        // Use first 5 videos
        List<String> inputVideos = new ArrayList<>();
        for (int i = 0; i < Math.min(5, videoFiles.length); i++) {
            inputVideos.add(videoFiles[i].getAbsolutePath());
        }

        // Create a sequence of different transitions
        List<TransitionType> transitions = new ArrayList<>();
        transitions.add(TransitionType.FADE_IN);         // Transition 1: Fade In
        transitions.add(TransitionType.SLIDE_LEFT);      // Transition 2: Slide Left
        transitions.add(TransitionType.ZOOM_IN);         // Transition 3: Zoom In
        transitions.add(TransitionType.WIPE_CIRCLE);     // Transition 4: Wipe Circle

        // Create output file name
        String outputFileName = "sequence_transition_demo.mp4";
        File outputFile = new File(OUTPUT_FOLDER, outputFileName);
        
        // Print the transition sequence
        System.out.println("Transition sequence:");
        for (int i = 0; i < Math.min(inputVideos.size() - 1, transitions.size()); i++) {
            System.out.println(getFileName(videoFiles[i]) + " → [" + transitions.get(i) + "] → " + 
                              getFileName(videoFiles[i + 1]));
        }
        
        // Process videos with transitions
        System.out.println("\nProcessing videos with sequence transitions...");
        
        try {
            // Create video engine
            VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 30);
            
            // Process videos with transitions
            engine.processVideosWithTransitions(inputVideos, transitions, outputFile.getAbsolutePath());
            
            System.out.println("\nVideo processing complete!");
            System.out.println("Output file: " + outputFile.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("Error processing videos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get file name without path
     */
    private static String getFileName(File file) {
        return file.getName();
    }
    
    /**
     * Create AI-powered version with person-focused transitions
     */
    private static void createAIVersion(File[] videoFiles) {
        // Use first 5 videos
        List<String> inputVideos = new ArrayList<>();
        for (int i = 0; i < Math.min(5, videoFiles.length); i++) {
            inputVideos.add(videoFiles[i].getAbsolutePath());
        }

        // Create a sequence of different AI transitions
        List<TransitionType> transitions = new ArrayList<>();
        transitions.add(TransitionType.OBJECT_FADE_IN);      // AI Transition 1: Person Fade
        transitions.add(TransitionType.OBJECT_SLIDE_LEFT);   // AI Transition 2: Person Slide
        transitions.add(TransitionType.OBJECT_ZOOM_IN);      // AI Transition 3: Person Zoom
        transitions.add(TransitionType.OBJECT_REVEAL);       // AI Transition 4: Person Reveal

        // Create output file name
        String outputFileName = "ai_sequence_transition_demo.mp4";
        File outputFile = new File(OUTPUT_FOLDER, outputFileName);
        
        // Print the transition sequence
        System.out.println("\nAI Transition sequence:");
        for (int i = 0; i < Math.min(inputVideos.size() - 1, transitions.size()); i++) {
            System.out.println(getFileName(videoFiles[i]) + " → [" + transitions.get(i) + "] → " + 
                              getFileName(videoFiles[i + 1]));
        }
        
        // Process videos with AI transitions
        System.out.println("\nProcessing videos with AI sequence transitions...");
        
        try {
            // Create video engine with AI features
            VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 30);
            
            // Enable AI features
            engine.enableAIFeatures("models/selfie_segmenter.tflite");
            engine.setDefaultTransitionConfig("CINEMATIC");
            
            // Process videos with transitions
            engine.processVideosWithTransitions(inputVideos, transitions, outputFile.getAbsolutePath());
            
            System.out.println("\nAI Video processing complete!");
            System.out.println("Output file: " + outputFile.getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("Error processing AI videos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}