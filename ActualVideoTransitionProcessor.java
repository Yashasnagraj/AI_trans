import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * ACTUAL VIDEO TRANSITION PROCESSOR
 * 
 * This processes your REAL WhatsApp videos from input_videos folder
 * and creates transition videos using FFmpeg
 */
public class ActualVideoTransitionProcessor {
    
    private static final String INPUT_DIR = "input_videos";
    private static final String OUTPUT_DIR = "actual_video_transitions";
    private static final String TEMP_DIR = "temp_frames";
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    ACTUAL VIDEO TRANSITION PROCESSOR");
        System.out.println("==========================================");
        System.out.println("Processing your REAL WhatsApp videos!");
        System.out.println();
        
        ActualVideoTransitionProcessor processor = new ActualVideoTransitionProcessor();
        processor.processRealVideos();
    }
    
    public void processRealVideos() {
        try {
            // Create directories
            new File(OUTPUT_DIR).mkdirs();
            new File(TEMP_DIR).mkdirs();
            
            // Get input videos
            File inputDir = new File(INPUT_DIR);
            File[] videoFiles = inputDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".mp4"));
            
            if (videoFiles == null || videoFiles.length < 2) {
                System.err.println("ERROR: Need at least 2 videos in " + INPUT_DIR);
                return;
            }
            
            System.out.println("Found your WhatsApp videos:");
            for (File video : videoFiles) {
                System.out.println("  - " + video.getName() + " (" + (video.length()/1024) + " KB)");
            }
            System.out.println();
            
            // Use first 2 videos
            String video1Path = videoFiles[0].getAbsolutePath();
            String video2Path = videoFiles[1].getAbsolutePath();
            
            System.out.println("Video 1: " + videoFiles[0].getName());
            System.out.println("Video 2: " + videoFiles[1].getName());
            System.out.println();
            
            // Create all transitions with your actual videos
            createCrossfadeTransition(video1Path, video2Path);
            createSlideTransition(video1Path, video2Path);
            createZoomTransition(video1Path, video2Path);
            createWipeTransition(video1Path, video2Path);
            createCircleTransition(video1Path, video2Path);
            createFadeTransition(video1Path, video2Path);
            createPushTransition(video1Path, video2Path);
            createDissolveTransition(video1Path, video2Path);
            
            // Create master script
            createMasterScript();
            
            System.out.println();
            System.out.println("==========================================");
            System.out.println("    ALL REAL VIDEO TRANSITIONS READY!");
            System.out.println("==========================================");
            System.out.println("Run: process_real_videos.bat");
            System.out.println("This will create transitions using your actual WhatsApp videos!");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create crossfade transition
     */
    private void createCrossfadeTransition(String video1, String video2) {
        System.out.println("Preparing CROSSFADE transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_crossfade_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=fade:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("crossfade", command);
        System.out.println("  ✓ Crossfade command prepared");
    }
    
    /**
     * Create slide transition
     */
    private void createSlideTransition(String video1, String video2) {
        System.out.println("Preparing SLIDE transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_slide_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=slideleft:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("slide", command);
        System.out.println("  ✓ Slide command prepared");
    }
    
    /**
     * Create zoom transition
     */
    private void createZoomTransition(String video1, String video2) {
        System.out.println("Preparing ZOOM transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_zoom_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=zoomin:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("zoom", command);
        System.out.println("  ✓ Zoom command prepared");
    }
    
    /**
     * Create wipe transition
     */
    private void createWipeTransition(String video1, String video2) {
        System.out.println("Preparing WIPE transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_wipe_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=wipeleft:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("wipe", command);
        System.out.println("  ✓ Wipe command prepared");
    }
    
    /**
     * Create circle transition
     */
    private void createCircleTransition(String video1, String video2) {
        System.out.println("Preparing CIRCLE transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_circle_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=circleopen:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("circle", command);
        System.out.println("  ✓ Circle command prepared");
    }
    
    /**
     * Create fade transition
     */
    private void createFadeTransition(String video1, String video2) {
        System.out.println("Preparing FADE transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_fade_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=fadeblack:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("fade", command);
        System.out.println("  ✓ Fade command prepared");
    }
    
    /**
     * Create push transition
     */
    private void createPushTransition(String video1, String video2) {
        System.out.println("Preparing PUSH transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_push_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=slideright:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("push", command);
        System.out.println("  ✓ Push command prepared");
    }
    
    /**
     * Create dissolve transition
     */
    private void createDissolveTransition(String video1, String video2) {
        System.out.println("Preparing DISSOLVE transition with your videos...");
        
        String outputFile = OUTPUT_DIR + "/REAL_dissolve_transition.mp4";
        
        String command = String.format(
            "ffmpeg -y -i \"%s\" -i \"%s\" " +
            "-filter_complex \"" +
            "[0:v]trim=0:3,setpts=PTS-STARTPTS[v1];" +
            "[1:v]trim=0:3,setpts=PTS-STARTPTS[v2];" +
            "[v1][v2]xfade=transition=dissolve:duration=1:offset=2" +
            "\" " +
            "-t 5 \"%s\"",
            video1, video2, outputFile
        );
        
        writeCommand("dissolve", command);
        System.out.println("  ✓ Dissolve command prepared");
    }
    
    /**
     * Write command to file
     */
    private void writeCommand(String transitionName, String command) {
        try {
            String filename = TEMP_DIR + "/" + transitionName + "_command.txt";
            Files.write(Paths.get(filename), command.getBytes());
        } catch (IOException e) {
            System.err.println("Error writing command: " + e.getMessage());
        }
    }
    
    /**
     * Create master processing script
     */
    private void createMasterScript() {
        try {
            StringBuilder script = new StringBuilder();
            script.append("@echo off\n");
            script.append("echo ==========================================\n");
            script.append("echo    PROCESSING YOUR REAL WHATSAPP VIDEOS\n");
            script.append("echo ==========================================\n");
            script.append("echo.\n");
            script.append("echo Creating transitions with your actual videos...\n");
            script.append("echo.\n\n");
            
            script.append("if not exist \"").append(OUTPUT_DIR).append("\" mkdir \"").append(OUTPUT_DIR).append("\"\n\n");
            
            // Read and add each command
            String[] transitions = {"crossfade", "slide", "zoom", "wipe", "circle", "fade", "push", "dissolve"};
            
            for (String transition : transitions) {
                script.append("echo Creating ").append(transition.toUpperCase()).append(" transition with your videos...\n");
                
                try {
                    String commandFile = TEMP_DIR + "/" + transition + "_command.txt";
                    if (Files.exists(Paths.get(commandFile))) {
                        String command = new String(Files.readAllBytes(Paths.get(commandFile)));
                        script.append(command).append("\n");
                        script.append("if %errorlevel% neq 0 (\n");
                        script.append("    echo ERROR: Failed to create ").append(transition).append(" transition\n");
                        script.append("    pause\n");
                        script.append("    exit /b 1\n");
                        script.append(")\n");
                        script.append("echo SUCCESS: ").append(transition.toUpperCase()).append(" transition created!\n");
                        script.append("echo.\n\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error reading command file: " + e.getMessage());
                }
            }
            
            script.append("echo ==========================================\n");
            script.append("echo    ALL REAL VIDEO TRANSITIONS CREATED!\n");
            script.append("echo ==========================================\n");
            script.append("echo.\n");
            script.append("echo Check '").append(OUTPUT_DIR).append("' folder for your transition videos!\n");
            script.append("echo These videos use your actual WhatsApp videos!\n");
            script.append("echo.\n");
            script.append("dir \"").append(OUTPUT_DIR).append("\"\n");
            script.append("echo.\n");
            script.append("pause\n");
            
            Files.write(Paths.get("process_real_videos.bat"), script.toString().getBytes());
            
            System.out.println("Master script created: process_real_videos.bat");
            
        } catch (IOException e) {
            System.err.println("Error creating master script: " + e.getMessage());
        }
    }
}
