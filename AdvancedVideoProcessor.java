import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Advanced Video Processor that creates real combined videos with JavaCV
 */
public class AdvancedVideoProcessor {
    
    private static final String INPUT_FOLDER = "input_videos";
    private static final String OUTPUT_FOLDER = "output_videos";
    
    public static void main(String[] args) {
        System.out.println("Advanced Video Processor with JavaCV");
        System.out.println("====================================");
        System.out.println("Creating real combined videos with actual transitions...");
        System.out.println();
        
        AdvancedVideoProcessor processor = new AdvancedVideoProcessor();
        processor.processVideos();
    }
    
    public void processVideos() {
        // Check input folder
        File inputDir = new File(INPUT_FOLDER);
        if (!inputDir.exists()) {
            System.out.println("ERROR: input_videos folder not found!");
            return;
        }
        
        // Get video files
        File[] videoFiles = inputDir.listFiles((dir, name) ->
            name.toLowerCase().endsWith(".mp4") ||
            name.toLowerCase().endsWith(".avi") ||
            name.toLowerCase().endsWith(".mov") ||
            name.toLowerCase().endsWith(".mkv"));
        
        if (videoFiles == null || videoFiles.length == 0) {
            System.out.println("ERROR: No video files found!");
            return;
        }
        
        System.out.println("Found " + videoFiles.length + " input video(s):");
        for (File video : videoFiles) {
            System.out.println("  - " + video.getName() + " (" + formatFileSize(video.length()) + ")");
        }
        System.out.println();
        
        // Create output folder
        File outputDir = new File(OUTPUT_FOLDER);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // Create a few sample combined videos with real transitions
        createSampleCombinedVideos(videoFiles);
    }
    
    private void createSampleCombinedVideos(File[] videoFiles) {
        System.out.println("Creating sample combined videos with real transitions...");
        System.out.println();
        
        // Create 3 sample videos with different transitions
        String[] transitions = {"fade", "slide", "zoom"};
        
        for (int i = 0; i < transitions.length && i < 3; i++) {
            String transition = transitions[i];
            String outputName = String.format("real_combined_%s_%02d.mp4", transition, i + 1);
            File outputFile = new File(OUTPUT_FOLDER, outputName);
            
            System.out.println("Creating: " + outputName);
            System.out.println("  Transition: " + transition.toUpperCase());
            System.out.println("  Processing...");
            
            try {
                boolean success = createRealCombinedVideo(videoFiles, transition, outputFile);
                if (success) {
                    System.out.println("  Status: SUCCESS (" + formatFileSize(outputFile.length()) + ")");
                } else {
                    System.out.println("  Status: FAILED");
                }
            } catch (Exception e) {
                System.out.println("  Status: ERROR - " + e.getMessage());
            }
            
            System.out.println();
        }
        
        System.out.println("Real combined videos created!");
        System.out.println("Check the output_videos folder for videos with actual transitions.");
    }
    
    private boolean createRealCombinedVideo(File[] videoFiles, String transition, File outputFile) {
        FFmpegFrameRecorder recorder = null;
        
        try {
            // Use first video to get properties
            FFmpegFrameGrabber firstGrabber = new FFmpegFrameGrabber(videoFiles[0].getAbsolutePath());
            firstGrabber.start();
            
            int width = firstGrabber.getImageWidth();
            int height = firstGrabber.getImageHeight();
            double frameRate = firstGrabber.getFrameRate();
            
            firstGrabber.stop();
            
            // Create recorder
            recorder = new FFmpegFrameRecorder(outputFile.getAbsolutePath(), width, height);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(frameRate);
            recorder.setVideoBitrate(1000000); // 1 Mbps
            recorder.start();
            
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            // Process each video with transitions
            for (int i = 0; i < Math.min(videoFiles.length, 3); i++) { // Limit to 3 videos for demo
                System.out.println("    Processing video " + (i + 1) + "/" + Math.min(videoFiles.length, 3));
                
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFiles[i].getAbsolutePath());
                grabber.start();
                
                Frame frame;
                int frameCount = 0;
                int maxFrames = (int)(frameRate * 3); // 3 seconds per video
                
                while ((frame = grabber.grabFrame()) != null && frameCount < maxFrames) {
                    if (frame.image != null) {
                        Mat mat = converter.convert(frame);
                        if (mat != null && !mat.empty()) {
                            // Apply transition effect based on type
                            Mat processedMat = applyTransitionEffect(mat, transition, frameCount, maxFrames);
                            Frame processedFrame = converter.convert(processedMat);
                            recorder.record(processedFrame);
                            
                            processedMat.release();
                        }
                        mat.release();
                        frameCount++;
                    }
                }
                
                grabber.stop();
                
                // Add transition frames between videos
                if (i < Math.min(videoFiles.length, 3) - 1) {
                    addTransitionFrames(recorder, converter, transition, width, height, (int)frameRate);
                }
            }
            
            recorder.stop();
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating real combined video: " + e.getMessage());
            if (recorder != null) {
                try { recorder.stop(); } catch (Exception ex) {}
            }
            return false;
        }
    }
    
    private Mat applyTransitionEffect(Mat inputMat, String transition, int frameIndex, int totalFrames) {
        Mat result = inputMat.clone();
        
        try {
            double progress = (double)frameIndex / totalFrames;
            
            switch (transition) {
                case "fade":
                    // Apply fade effect
                    double alpha = 0.7 + 0.3 * Math.sin(progress * Math.PI);
                    result.convertTo(result, -1, alpha, 0);
                    break;
                    
                case "slide":
                    // Apply slide effect (shift image slightly)
                    int shiftX = (int)(10 * Math.sin(progress * 2 * Math.PI));
                    Mat shifted = new Mat();
                    Mat transform = getRotationMatrix2D(new Point2f(result.cols()/2, result.rows()/2), 0, 1.0);
                    transform.ptr(0, 2).putDouble(shiftX);
                    warpAffine(result, shifted, transform, result.size());
                    shifted.copyTo(result);
                    shifted.release();
                    transform.release();
                    break;
                    
                case "zoom":
                    // Apply zoom effect
                    double scale = 0.9 + 0.2 * Math.sin(progress * Math.PI);
                    Mat zoomed = new Mat();
                    Mat zoomTransform = getRotationMatrix2D(new Point2f(result.cols()/2, result.rows()/2), 0, scale);
                    warpAffine(result, zoomed, zoomTransform, result.size());
                    zoomed.copyTo(result);
                    zoomed.release();
                    zoomTransform.release();
                    break;
            }
            
        } catch (Exception e) {
            // If effect fails, return original
        }
        
        return result;
    }
    
    private void addTransitionFrames(FFmpegFrameRecorder recorder, OpenCVFrameConverter.ToMat converter,
                                   String transition, int width, int height, int frameRate) {
        try {
            // Add 30 transition frames (1 second)
            for (int i = 0; i < 30; i++) {
                Mat transitionFrame = createTransitionFrame(width, height, transition, i, 30);
                Frame frame = converter.convert(transitionFrame);
                recorder.record(frame);
                transitionFrame.release();
            }
        } catch (Exception e) {
            // Ignore transition frame errors
        }
    }
    
    private Mat createTransitionFrame(int width, int height, String transition, int frameIndex, int totalFrames) {
        Mat frame = new Mat(height, width, CV_8UC3, new Scalar(0, 0, 0, 255));
        
        try {
            double progress = (double)frameIndex / totalFrames;
            
            switch (transition) {
                case "fade":
                    // Create fade transition frame
                    int brightness = (int)(255 * Math.sin(progress * Math.PI));
                    frame.setTo(new Scalar(brightness, brightness, brightness, 255));
                    break;
                    
                case "slide":
                    // Create slide transition frame
                    int linePos = (int)(width * progress);
                    line(frame, new Point(linePos, 0), new Point(linePos, height), 
                         new Scalar(255, 255, 255, 255), 3, 8, 0);
                    break;
                    
                case "zoom":
                    // Create zoom transition frame
                    int radius = (int)(Math.min(width, height) * 0.3 * progress);
                    circle(frame, new Point(width/2, height/2), radius, 
                           new Scalar(255, 255, 255, 255), -1, 8, 0);
                    break;
            }
            
        } catch (Exception e) {
            // Return black frame if creation fails
        }
        
        return frame;
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
