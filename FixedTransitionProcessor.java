import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.ffmpeg.global.avcodec;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXED TRANSITION PROCESSOR
 * 
 * This class addresses ALL common transition/rendering issues:
 * ✅ Proper memory management with explicit cleanup
 * ✅ Frame validation before processing
 * ✅ Explicit flushing and synchronization
 * ✅ Error handling and recovery
 * ✅ Progress tracking and logging
 */
public class FixedTransitionProcessor {
    
    private static final String OUTPUT_DIR = "fixed_output";
    private int width = 1280;
    private int height = 720;
    private double frameRate = 30.0;
    private int transitionFrames = 30;
    
    public static void main(String[] args) {
        System.out.println("🔧 FIXED TRANSITION PROCESSOR");
        System.out.println("=============================");
        System.out.println("Processing with ALL rendering fixes applied...");
        System.out.println();
        
        FixedTransitionProcessor processor = new FixedTransitionProcessor();
        
        try {
            // Test with sample frames
            processor.createSampleTransitionVideo();
            
            // Test with input videos if available
            processor.processInputVideos();
            
        } catch (Exception e) {
            System.err.println("ERROR: Processing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a sample transition video with proper rendering
     */
    public void createSampleTransitionVideo() {
        System.out.println("🎬 Creating sample transition video with fixes...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            // Setup output
            new File(OUTPUT_DIR).mkdirs();
            String outputFile = OUTPUT_DIR + "/fixed_sample_transitions.mp4";
            
            // CRITICAL FIX 1: Proper recorder setup with explicit options
            recorder = new FFmpegFrameRecorder(outputFile, width, height);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFrameRate(frameRate);
            recorder.setFormat("mp4");
            
            // CRITICAL FIX 2: Add codec options for reliability
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            recorder.setVideoOption("pix_fmt", "yuv420p");
            
            recorder.start();
            System.out.println("✅ Recorder started with fixed settings");
            
            // Create test frames
            Mat frame1 = createSampleFrame(new Scalar(100, 150, 200, 255), "BLUE FRAME");
            Mat frame2 = createSampleFrame(new Scalar(200, 100, 150, 255), "RED FRAME");
            
            // CRITICAL FIX 3: Validate frames before processing
            if (frame1.empty() || frame2.empty()) {
                throw new RuntimeException("Sample frames are invalid!");
            }
            
            System.out.println("✅ Sample frames created and validated");
            
            // Test multiple transitions with proper cleanup
            TransitionType[] transitions = {
                TransitionType.CROSSFADE,
                TransitionType.SLIDE_LEFT,
                TransitionType.ZOOM_IN
            };
            
            int totalFrames = 0;
            
            for (TransitionType transitionType : transitions) {
                System.out.println("Processing transition: " + transitionType);
                
                try {
                    BaseTransition transition = createTransition(transitionType);
                    
                    // Add static frames before transition
                    for (int i = 0; i < 30; i++) {
                        Frame videoFrame = converter.convert(frame1);
                        
                        // CRITICAL FIX 4: Validate frame before recording
                        if (videoFrame == null || videoFrame.image == null) {
                            System.err.println("❌ Invalid frame at static section");
                            continue;
                        }
                        
                        recorder.record(videoFrame);
                        totalFrames++;
                        
                        // CRITICAL FIX 5: Periodic flushing
                        if (totalFrames % 30 == 0) {
                            recorder.flush();
                        }
                    }
                    
                    // Apply transition
                    for (int i = 0; i < transitionFrames; i++) {
                        double progress = (double) i / (transitionFrames - 1);
                        
                        // CRITICAL FIX 6: Apply transition with error handling
                        Mat transitionFrame = null;
                        try {
                            transitionFrame = transition.applyTransition(frame1, frame2, progress);
                            
                            // Validate transition result
                            if (transitionFrame.empty()) {
                                System.err.println("❌ Empty transition frame at progress " + progress);
                                continue;
                            }
                            
                            Frame videoFrame = converter.convert(transitionFrame);
                            
                            if (videoFrame == null || videoFrame.image == null) {
                                System.err.println("❌ Invalid video frame conversion");
                                continue;
                            }
                            
                            recorder.record(videoFrame);
                            totalFrames++;
                            
                        } catch (Exception e) {
                            System.err.println("❌ Transition error at progress " + progress + ": " + e.getMessage());
                        } finally {
                            // CRITICAL FIX 7: Always cleanup transition frames
                            if (transitionFrame != null && !transitionFrame.empty()) {
                                transitionFrame.release();
                            }
                        }
                    }
                    
                    System.out.println("✅ " + transitionType + " completed");
                    
                } catch (Exception e) {
                    System.err.println("❌ " + transitionType + " failed: " + e.getMessage());
                }
            }
            
            // CRITICAL FIX 8: Final flush and proper shutdown
            recorder.flush();
            recorder.stop();
            recorder = null;
            
            // Cleanup sample frames
            frame1.release();
            frame2.release();
            
            // Verify output
            File outputFileObj = new File(outputFile);
            if (outputFileObj.exists() && outputFileObj.length() > 10000) {
                System.out.println("✅ Sample video created successfully!");
                System.out.println("   File: " + outputFile);
                System.out.println("   Size: " + outputFileObj.length() + " bytes");
                System.out.println("   Frames: " + totalFrames);
            } else {
                System.err.println("❌ Sample video creation failed or file too small");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Sample video creation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // CRITICAL FIX 9: Ensure recorder is always closed
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
        
        System.out.println();
    }
    
    /**
     * Process input videos with fixed rendering
     */
    public void processInputVideos() {
        System.out.println("🎬 Processing input videos with fixes...");
        
        File inputDir = new File("input_videos");
        if (!inputDir.exists()) {
            System.out.println("ℹ️ No input_videos directory found, skipping real video processing");
            return;
        }
        
        File[] videoFiles = inputDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".mp4") || 
            name.toLowerCase().endsWith(".avi") || 
            name.toLowerCase().endsWith(".mov"));
        
        if (videoFiles == null || videoFiles.length < 2) {
            System.out.println("ℹ️ Need at least 2 videos for processing, found: " + 
                             (videoFiles != null ? videoFiles.length : 0));
            return;
        }
        
        try {
            List<String> inputPaths = new ArrayList<>();
            for (File video : videoFiles) {
                inputPaths.add(video.getAbsolutePath());
            }
            
            List<TransitionType> transitions = new ArrayList<>();
            for (int i = 0; i < videoFiles.length - 1; i++) {
                transitions.add(TransitionType.CROSSFADE);
            }
            
            String outputFile = OUTPUT_DIR + "/fixed_combined_video.mp4";
            
            // Use fixed video transition engine
            FixedVideoTransitionEngine engine = new FixedVideoTransitionEngine(width, height, frameRate, transitionFrames);
            engine.processVideosWithTransitions(inputPaths, transitions, outputFile);
            
            System.out.println("✅ Input videos processed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Input video processing failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Create a sample frame with proper validation
     */
    private Mat createSampleFrame(Scalar color, String label) {
        Mat frame = new Mat(height, width, CV_8UC3, color);
        
        // Add visual elements
        rectangle(frame, new Rect(50, 50, width - 100, height - 100), 
                 new Scalar(255, 255, 255, 255), 5, 8, 0);
        
        // Add center indicator
        circle(frame, new Point(width/2, height/2), 50, 
               new Scalar(255, 255, 255, 255), -1, 8, 0);
        
        return frame;
    }
    
    /**
     * Create transition with error handling
     */
    private BaseTransition createTransition(TransitionType type) {
        try {
            switch (type) {
                case CROSSFADE:
                    return new FadeTransition(width, height, transitionFrames, type);
                case SLIDE_LEFT:
                    return new SlideTransition(width, height, transitionFrames, type);
                case ZOOM_IN:
                    return new ZoomTransition(width, height, transitionFrames, type);
                default:
                    return new FadeTransition(width, height, transitionFrames, TransitionType.CROSSFADE);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to create transition " + type + ": " + e.getMessage());
            return new FadeTransition(width, height, transitionFrames, TransitionType.CROSSFADE);
        }
    }
}
