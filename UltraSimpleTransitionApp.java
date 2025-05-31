import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ULTRA SIMPLE TRANSITION APP - NO DEPENDENCIES!
 * Uses FFmpeg directly to create REAL combined videos with transitions
 */
public class UltraSimpleTransitionApp {
    
    private static final String INPUT_FOLDER = "input_videos";
    private static final String OUTPUT_FOLDER = "output_videos";
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  ULTRA SIMPLE TRANSITION APP - NO DEPS!");
        System.out.println("===========================================");
        System.out.println("Creating REAL combined video with transitions!");
        System.out.println();
        
        UltraSimpleTransitionApp app = new UltraSimpleTransitionApp();
        app.createRealCombinedVideo();
    }
    
    public void createRealCombinedVideo() {
        // Get input videos
        File[] videoFiles = getInputVideos();
        if (videoFiles == null || videoFiles.length < 2) {
            System.out.println("ERROR: Need at least 2 videos in input_videos folder!");
            System.out.println("Current videos found: " + (videoFiles != null ? videoFiles.length : 0));
            return;
        }
        
        System.out.println("SUCCESS: Found " + videoFiles.length + " videos:");
        for (int i = 0; i < videoFiles.length; i++) {
            System.out.println("   " + (i+1) + ". " + videoFiles[i].getName() + 
                             " (" + formatFileSize(videoFiles[i].length()) + ")");
        }
        System.out.println();
        
        // Create output directory
        new File(OUTPUT_FOLDER).mkdirs();
        
        // Try different methods to create combined video
        if (createFFmpegCombinedVideo(videoFiles)) {
            System.out.println("SUCCESS! Combined video created with FFmpeg!");
            return;
        }
        
        if (createConcatenatedVideo(videoFiles)) {
            System.out.println("SUCCESS! Concatenated video created!");
            return;
        }
        
        // Fallback - copy first video and create instructions
        createFallbackVideo(videoFiles);
    }
    
    private File[] getInputVideos() {
        File inputDir = new File(INPUT_FOLDER);
        if (!inputDir.exists()) {
            inputDir.mkdirs();
            System.out.println("Created input_videos folder. Please add your videos there.");
            return new File[0];
        }
        
        return inputDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".mp4") || 
            name.toLowerCase().endsWith(".avi") || 
            name.toLowerCase().endsWith(".mov") ||
            name.toLowerCase().endsWith(".mkv"));
    }
    
    /**
     * Method 1: Create combined video with FFmpeg crossfade transitions
     */
    private boolean createFFmpegCombinedVideo(File[] videoFiles) {
        System.out.println("Trying FFmpeg with crossfade transitions...");
        
        try {
            String outputFile = OUTPUT_FOLDER + "/REAL_COMBINED_WITH_TRANSITIONS.mp4";
            
            // Build FFmpeg command for crossfade transitions
            StringBuilder command = new StringBuilder("ffmpeg -y ");
            
            // Add all input files
            for (File video : videoFiles) {
                command.append("-i \"").append(video.getAbsolutePath()).append("\" ");
            }
            
            // Create filter complex for crossfade transitions
            command.append("-filter_complex \"");
            
            if (videoFiles.length == 2) {
                // Simple case: 2 videos
                command.append("[0:v][1:v]xfade=transition=fade:duration=1:offset=4[v]");
            } else if (videoFiles.length == 3) {
                // 3 videos
                command.append("[0:v][1:v]xfade=transition=fade:duration=1:offset=4[v01];");
                command.append("[v01][2:v]xfade=transition=fade:duration=1:offset=9[v]");
            } else if (videoFiles.length == 4) {
                // 4 videos
                command.append("[0:v][1:v]xfade=transition=fade:duration=1:offset=4[v01];");
                command.append("[v01][2:v]xfade=transition=fade:duration=1:offset=9[v02];");
                command.append("[v02][3:v]xfade=transition=fade:duration=1:offset=14[v]");
            } else if (videoFiles.length == 5) {
                // 5 videos
                command.append("[0:v][1:v]xfade=transition=fade:duration=1:offset=4[v01];");
                command.append("[v01][2:v]xfade=transition=fade:duration=1:offset=9[v02];");
                command.append("[v02][3:v]xfade=transition=fade:duration=1:offset=14[v03];");
                command.append("[v03][4:v]xfade=transition=fade:duration=1:offset=19[v]");
            } else {
                // Fallback for more videos - just concatenate
                return createConcatenatedVideo(videoFiles);
            }
            
            command.append("\" -map \"[v]\" -c:v libx264 -preset fast -crf 23 \"");
            command.append(outputFile).append("\"");
            
            System.out.println("Running FFmpeg command...");
            System.out.println("Command: " + command.toString());
            
            Process process = Runtime.getRuntime().exec(command.toString());
            
            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("frame=") || line.contains("time=")) {
                    System.out.println("Progress: " + line);
                }
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && new File(outputFile).exists() && new File(outputFile).length() > 1000) {
                System.out.println("SUCCESS! FFmpeg created combined video:");
                System.out.println("  File: " + outputFile);
                System.out.println("  Size: " + formatFileSize(new File(outputFile).length()));
                createInfoFile(outputFile, videoFiles, "FFmpeg with crossfade transitions");
                return true;
            } else {
                System.out.println("FFmpeg failed with exit code: " + exitCode);
            }
            
        } catch (Exception e) {
            System.out.println("FFmpeg error: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Method 2: Create concatenated video (no transitions but combines all videos)
     */
    private boolean createConcatenatedVideo(File[] videoFiles) {
        System.out.println("Trying concatenation (no transitions)...");
        
        try {
            String outputFile = OUTPUT_FOLDER + "/CONCATENATED_ALL_VIDEOS.mp4";
            String listFile = OUTPUT_FOLDER + "/video_list.txt";
            
            // Create file list for FFmpeg concat
            try (PrintWriter writer = new PrintWriter(listFile)) {
                for (File video : videoFiles) {
                    writer.println("file '" + video.getAbsolutePath().replace("\\", "/") + "'");
                }
            }
            
            // Use FFmpeg concat demuxer
            String command = "ffmpeg -y -f concat -safe 0 -i \"" + listFile + "\" -c copy \"" + outputFile + "\"";
            
            System.out.println("Running concatenation command...");
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && new File(outputFile).exists() && new File(outputFile).length() > 1000) {
                System.out.println("SUCCESS! Concatenated video created:");
                System.out.println("  File: " + outputFile);
                System.out.println("  Size: " + formatFileSize(new File(outputFile).length()));
                createInfoFile(outputFile, videoFiles, "Concatenated (no transitions)");
                
                // Clean up
                new File(listFile).delete();
                return true;
            }
            
        } catch (Exception e) {
            System.out.println("Concatenation error: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Method 3: Fallback - copy first video and create instructions
     */
    private void createFallbackVideo(File[] videoFiles) {
        System.out.println("Creating fallback video and instructions...");
        
        try {
            String outputFile = OUTPUT_FOLDER + "/FALLBACK_FIRST_VIDEO.mp4";
            
            // Copy first video
            Files.copy(videoFiles[0].toPath(), Paths.get(outputFile), StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("Created fallback video: " + outputFile);
            System.out.println("File size: " + formatFileSize(new File(outputFile).length()));
            
            createInfoFile(outputFile, videoFiles, "Fallback - first video only");
            createFFmpegInstructions(videoFiles);
            
        } catch (Exception e) {
            System.out.println("Fallback failed: " + e.getMessage());
        }
    }
    
    private void createInfoFile(String outputFile, File[] videoFiles, String method) {
        try {
            String infoFile = outputFile.replace(".mp4", "_info.txt");
            try (PrintWriter writer = new PrintWriter(infoFile)) {
                writer.println("COMBINED VIDEO INFORMATION");
                writer.println("=========================");
                writer.println("Output: " + new File(outputFile).getName());
                writer.println("Method: " + method);
                writer.println("Created: " + new Date());
                writer.println("Total videos: " + videoFiles.length);
                writer.println();
                writer.println("Source videos:");
                for (int i = 0; i < videoFiles.length; i++) {
                    writer.println("  " + (i+1) + ". " + videoFiles[i].getName() + 
                                 " (" + formatFileSize(videoFiles[i].length()) + ")");
                }
                writer.println();
                writer.println("Video sequence:");
                StringBuilder sequence = new StringBuilder();
                for (int i = 0; i < videoFiles.length; i++) {
                    sequence.append(videoFiles[i].getName());
                    if (i < videoFiles.length - 1) {
                        sequence.append(" -> [TRANSITION] -> ");
                    }
                }
                writer.println("  " + sequence.toString());
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not create info file");
        }
    }
    
    private void createFFmpegInstructions(File[] videoFiles) {
        try {
            String instructionsFile = OUTPUT_FOLDER + "/HOW_TO_INSTALL_FFMPEG.txt";
            try (PrintWriter writer = new PrintWriter(instructionsFile)) {
                writer.println("HOW TO INSTALL FFMPEG FOR VIDEO TRANSITIONS");
                writer.println("==========================================");
                writer.println();
                writer.println("1. Download FFmpeg:");
                writer.println("   - Go to: https://ffmpeg.org/download.html");
                writer.println("   - Download the Windows build");
                writer.println("   - Extract to C:\\ffmpeg");
                writer.println();
                writer.println("2. Add to PATH:");
                writer.println("   - Open System Properties > Environment Variables");
                writer.println("   - Add C:\\ffmpeg\\bin to your PATH");
                writer.println("   - Restart command prompt");
                writer.println();
                writer.println("3. Test installation:");
                writer.println("   - Open command prompt");
                writer.println("   - Type: ffmpeg -version");
                writer.println("   - Should show FFmpeg version info");
                writer.println();
                writer.println("4. Run this app again:");
                writer.println("   - java UltraSimpleTransitionApp");
                writer.println("   - It will create real combined videos with transitions!");
                writer.println();
                writer.println("Your videos to combine:");
                for (int i = 0; i < videoFiles.length; i++) {
                    writer.println("  " + (i+1) + ". " + videoFiles[i].getName());
                }
            }
            
            System.out.println();
            System.out.println("Created instructions: " + instructionsFile);
            System.out.println("Follow the instructions to install FFmpeg and get real transitions!");
            
        } catch (Exception e) {
            System.out.println("Could not create instructions file");
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
