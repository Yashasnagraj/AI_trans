import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * REAL Combined Video Processor - Actually combines ALL videos into ONE sequence
 * Video1 -> [Transition] -> Video2 -> [Transition] -> Video3 -> [Transition] -> Video4 -> [Transition] -> Video5
 */
public class RealCombinedVideoProcessor {

    private static final String INPUT_FOLDER = "input_videos";
    private static final String OUTPUT_FOLDER = "output_videos";

    public static void main(String[] args) {
        System.out.println("REAL Combined Video Processor");
        System.out.println("============================");
        System.out.println("Combining ALL videos into ONE sequence with transitions!");
        System.out.println();

        RealCombinedVideoProcessor processor = new RealCombinedVideoProcessor();
        processor.processAllVideosIntoOneSequence();
    }

    /**
     * Process all videos into ONE combined sequence
     */
    public void processAllVideosIntoOneSequence() {
        File inputDir = new File(INPUT_FOLDER);
        File outputDir = new File(OUTPUT_FOLDER);

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] videoFiles = inputDir.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".mp4") ||
            name.toLowerCase().endsWith(".avi") ||
            name.toLowerCase().endsWith(".mov") ||
            name.toLowerCase().endsWith(".mkv"));

        if (videoFiles == null || videoFiles.length < 2) {
            System.out.println("Need at least 2 videos to create transitions. Found: " + 
                             (videoFiles != null ? videoFiles.length : 0));
            return;
        }

        System.out.println("Found " + videoFiles.length + " input videos:");
        for (File video : videoFiles) {
            System.out.println("  - " + video.getName() + " (" + formatFileSize(video.length()) + ")");
        }
        System.out.println();

        // Create ONE combined video with ALL input videos
        createCombinedSequenceVideo(videoFiles);
    }

    /**
     * Create ONE video that combines ALL input videos with transitions
     */
    private void createCombinedSequenceVideo(File[] videoFiles) {
        try {
            // Create output file name
            String outputName = "COMBINED_ALL_VIDEOS_WITH_TRANSITIONS.mp4";
            File outputFile = new File(OUTPUT_FOLDER, outputName);

            System.out.println("Creating combined video: " + outputName);
            System.out.println("Input videos: " + videoFiles.length);

            // Convert all video files to input paths
            List<String> inputVideos = new ArrayList<>();
            for (File videoFile : videoFiles) {
                inputVideos.add(videoFile.getAbsolutePath());
                System.out.println("  Adding: " + videoFile.getName());
            }

            // Create transitions between each video (n-1 transitions for n videos)
            List<TransitionType> transitions = new ArrayList<>();
            TransitionType[] transitionTypes = {
                TransitionType.CROSSFADE,
                TransitionType.SLIDE_LEFT,
                TransitionType.ZOOM_IN,
                TransitionType.WIPE_CIRCLE
            };

            for (int i = 0; i < videoFiles.length - 1; i++) {
                TransitionType transition = transitionTypes[i % transitionTypes.length];
                transitions.add(transition);
                System.out.println("  Transition " + (i+1) + ": " + transition);
            }

            System.out.println();
            System.out.println("Processing with JavaCV...");
            System.out.println("This will create ONE video with ALL " + videoFiles.length + " videos combined!");

            // Create video engine with high quality settings
            VideoTransitionEngine engine = new VideoTransitionEngine(1280, 720, 30.0, 60);

            // REAL VIDEO PROCESSING - This combines ALL videos into ONE!
            engine.processVideosWithTransitions(inputVideos, transitions, outputFile.getAbsolutePath());

            System.out.println();
            System.out.println("SUCCESS! Combined video created:");
            System.out.println("  Output: " + outputFile.getAbsolutePath());
            
            if (outputFile.exists()) {
                System.out.println("  File size: " + formatFileSize(outputFile.length()));
                System.out.println("  Contains: ALL " + videoFiles.length + " videos with " + transitions.size() + " transitions");
            }

        } catch (Exception e) {
            System.err.println("ERROR creating combined video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Format file size for display
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
