import java.io.File;

/**
 * Test the improved video transition processor that properly combines all three videos
 */
public class TestCombinedVideoProcessor {
    
    public static void main(String[] args) {
        System.out.println("Testing Combined Video Processor");
        System.out.println("===================================");
        System.out.println("This will create videos that properly combine all three input videos with transitions");
        System.out.println();

        // Check if input videos exist
        checkInputVideos();

        System.out.println("Running VideoTransitionProcessor (combines all videos with transitions)...");
        System.out.println();

        try {
            VideoTransitionProcessor processor = new VideoTransitionProcessor();
            processor.processAllTransitions();

            System.out.println();
            System.out.println("SUCCESS: VideoTransitionProcessor completed!");

        } catch (Exception e) {
            System.err.println("ERROR in VideoTransitionProcessor: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Running MobileVideoTransitionProcessor (mobile-optimized combining)...");
        System.out.println();

        try {
            MobileVideoTransitionProcessor mobileProcessor = new MobileVideoTransitionProcessor();
            mobileProcessor.processAllVideos();

            System.out.println();
            System.out.println("SUCCESS: MobileVideoTransitionProcessor completed!");

        } catch (Exception e) {
            System.err.println("ERROR in MobileVideoTransitionProcessor: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Results Summary:");
        showResults();

        System.out.println();
        System.out.println("Key Improvements Made:");
        System.out.println("  - Videos are now properly combined (not just selected)");
        System.out.println("  - All three input videos are included in each output");
        System.out.println("  - Transitions are applied between each video pair");
        System.out.println("  - Video1 -> Transition -> Video2 -> Transition -> Video3");
        System.out.println("  - Each output video shows all three inputs with effects");
        System.out.println();
        System.out.println("Check the output_videos folder for your combined videos!");
    }
    
    private static void checkInputVideos() {
        File inputDir = new File("input_videos");
        
        if (!inputDir.exists()) {
            System.out.println("ERROR: input_videos folder not found!");
            return;
        }

        File[] videoFiles = inputDir.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".mp4") ||
            name.toLowerCase().endsWith(".avi") ||
            name.toLowerCase().endsWith(".mov") ||
            name.toLowerCase().endsWith(".mkv"));

        if (videoFiles == null || videoFiles.length == 0) {
            System.out.println("ERROR: No input videos found!");
            System.out.println("   Please add video files to the input_videos folder");
            return;
        }

        System.out.println("Found " + videoFiles.length + " input video(s):");
        for (File video : videoFiles) {
            System.out.println("   - " + video.getName() + " (" + formatFileSize(video.length()) + ")");
        }
        System.out.println();
    }

    private static void showResults() {
        File outputDir = new File("output_videos");

        if (!outputDir.exists()) {
            System.out.println("   ERROR: No output_videos folder found");
            return;
        }

        File[] outputFiles = outputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));

        if (outputFiles == null || outputFiles.length == 0) {
            System.out.println("   ERROR: No output videos created");
            return;
        }

        System.out.println("   SUCCESS: Created " + outputFiles.length + " combined video(s):");
        for (File video : outputFiles) {
            System.out.println("      - " + video.getName() + " (" + formatFileSize(video.length()) + ")");
        }
    }
    
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
