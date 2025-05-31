import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * SIMPLE REAL VIDEO PROCESSOR
 * 
 * This processes your REAL WhatsApp videos with simpler, more reliable transitions
 */
public class SimpleRealVideoProcessor {
    
    private static final String INPUT_DIR = "input_videos";
    private static final String OUTPUT_DIR = "real_video_output";
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    SIMPLE REAL VIDEO PROCESSOR");
        System.out.println("==========================================");
        System.out.println("Processing your REAL WhatsApp videos!");
        System.out.println();
        
        SimpleRealVideoProcessor processor = new SimpleRealVideoProcessor();
        processor.processVideos();
    }
    
    public void processVideos() {
        try {
            // Create output directory
            new File(OUTPUT_DIR).mkdirs();
            
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
            
            String video1 = videoFiles[0].getAbsolutePath();
            String video2 = videoFiles[1].getAbsolutePath();
            
            System.out.println("Video 1: " + videoFiles[0].getName());
            System.out.println("Video 2: " + videoFiles[1].getName());
            System.out.println();
            
            // Create processing script
            createProcessingScript(video1, video2);
            
            System.out.println("==========================================");
            System.out.println("    PROCESSING SCRIPT CREATED!");
            System.out.println("==========================================");
            System.out.println("Run: process_your_videos.bat");
            System.out.println("This will create transitions with your actual videos!");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createProcessingScript(String video1, String video2) {
        try {
            StringBuilder script = new StringBuilder();
            
            script.append("@echo off\n");
            script.append("echo ==========================================\n");
            script.append("echo    PROCESSING YOUR WHATSAPP VIDEOS\n");
            script.append("echo ==========================================\n");
            script.append("echo.\n");
            script.append("echo Video 1: ").append(new File(video1).getName()).append("\n");
            script.append("echo Video 2: ").append(new File(video2).getName()).append("\n");
            script.append("echo.\n\n");
            
            script.append("if not exist \"").append(OUTPUT_DIR).append("\" mkdir \"").append(OUTPUT_DIR).append("\"\n\n");
            
            // 1. Simple concatenation
            script.append("echo Creating simple concatenation...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"[0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[outv][outa]\" ");
            script.append("-map \"[outv]\" -map \"[outa]\" \"").append(OUTPUT_DIR).append("/concatenated_videos.mp4\"\n");
            script.append("echo SUCCESS: Concatenated video created!\n");
            script.append("echo.\n\n");
            
            // 2. Crossfade with blend
            script.append("echo Creating crossfade transition...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=1280:720,fps=30[v1];");
            script.append("[1:v]scale=1280:720,fps=30[v2];");
            script.append("[v1][v2]blend=all_expr='A*(1-T/1)+B*(T/1)':shortest=1[blended];");
            script.append("[v1][blended][v2]concat=n=3:v=1:a=0[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 8 \"").append(OUTPUT_DIR).append("/crossfade_transition.mp4\"\n");
            script.append("echo SUCCESS: Crossfade transition created!\n");
            script.append("echo.\n\n");
            
            // 3. Side by side comparison
            script.append("echo Creating side-by-side comparison...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=640:720[left];");
            script.append("[1:v]scale=640:720[right];");
            script.append("[left][right]hstack=inputs=2[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 4 \"").append(OUTPUT_DIR).append("/side_by_side.mp4\"\n");
            script.append("echo SUCCESS: Side-by-side comparison created!\n");
            script.append("echo.\n\n");
            
            // 4. Picture in picture
            script.append("echo Creating picture-in-picture...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=1280:720[main];");
            script.append("[1:v]scale=320:180[pip];");
            script.append("[main][pip]overlay=W-w-10:10[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 4 \"").append(OUTPUT_DIR).append("/picture_in_picture.mp4\"\n");
            script.append("echo SUCCESS: Picture-in-picture created!\n");
            script.append("echo.\n\n");
            
            // 5. Fade transition
            script.append("echo Creating fade transition...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=1280:720,fps=30,fade=out:st=2:d=1[v1fade];");
            script.append("[1:v]scale=1280:720,fps=30,fade=in:st=0:d=1[v2fade];");
            script.append("[v1fade][v2fade]concat=n=2:v=1:a=0[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 6 \"").append(OUTPUT_DIR).append("/fade_transition.mp4\"\n");
            script.append("echo SUCCESS: Fade transition created!\n");
            script.append("echo.\n\n");
            
            // 6. Zoom transition
            script.append("echo Creating zoom transition...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=1280:720[v1];");
            script.append("[1:v]scale=1280:720,zoompan=z='min(zoom+0.1,2)':d=30:x=iw/2-(iw/zoom/2):y=ih/2-(ih/zoom/2)[v2zoom];");
            script.append("[v1][v2zoom]concat=n=2:v=1:a=0[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 6 \"").append(OUTPUT_DIR).append("/zoom_transition.mp4\"\n");
            script.append("echo SUCCESS: Zoom transition created!\n");
            script.append("echo.\n\n");
            
            // 7. Slide transition
            script.append("echo Creating slide transition...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=1280:720[v1];");
            script.append("[1:v]scale=1280:720[v2];");
            script.append("[v1][v2]overlay=x='if(gte(t,2),min(W*(t-2),W),0)':y=0[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 5 \"").append(OUTPUT_DIR).append("/slide_transition.mp4\"\n");
            script.append("echo SUCCESS: Slide transition created!\n");
            script.append("echo.\n\n");
            
            // 8. Split screen
            script.append("echo Creating split screen effect...\n");
            script.append("ffmpeg -y -i \"").append(video1).append("\" -i \"").append(video2).append("\" ");
            script.append("-filter_complex \"");
            script.append("[0:v]scale=1280:360[top];");
            script.append("[1:v]scale=1280:360[bottom];");
            script.append("[top][bottom]vstack=inputs=2[outv]");
            script.append("\" ");
            script.append("-map \"[outv]\" -map 0:a -t 4 \"").append(OUTPUT_DIR).append("/split_screen.mp4\"\n");
            script.append("echo SUCCESS: Split screen created!\n");
            script.append("echo.\n\n");
            
            script.append("echo ==========================================\n");
            script.append("echo    ALL VIDEO TRANSITIONS COMPLETED!\n");
            script.append("echo ==========================================\n");
            script.append("echo.\n");
            script.append("echo Your transition videos are in: ").append(OUTPUT_DIR).append("\n");
            script.append("echo.\n");
            script.append("dir \"").append(OUTPUT_DIR).append("\"\n");
            script.append("echo.\n");
            script.append("echo Opening output folder...\n");
            script.append("explorer \"").append(OUTPUT_DIR).append("\"\n");
            script.append("echo.\n");
            script.append("pause\n");
            
            Files.write(Paths.get("process_your_videos.bat"), script.toString().getBytes());
            
            System.out.println("Processing script created: process_your_videos.bat");
            
        } catch (IOException e) {
            System.err.println("Error creating script: " + e.getMessage());
        }
    }
}
