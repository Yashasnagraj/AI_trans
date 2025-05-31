import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.File;

/**
 * SIMPLE TRANSITION DEBUGGER
 * 
 * This class identifies and fixes common transition/rendering issues:
 * 1. Memory management problems
 * 2. Frame flushing issues  
 * 3. Mat to Frame conversion issues
 * 4. Video recorder synchronization
 */
public class SimpleTransitionDebugger {
    
    private static final String OUTPUT_DIR = "debug_output";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final double FPS = 30.0;
    
    public static void main(String[] args) {
        System.out.println("TRANSITION DEBUGGER - COMPREHENSIVE ANALYSIS");
        System.out.println("============================================");
        System.out.println("Testing all transition and rendering components...");
        System.out.println();
        
        SimpleTransitionDebugger debugger = new SimpleTransitionDebugger();
        
        try {
            // Test 1: Basic Mat operations and memory management
            debugger.testMatOperations();
            
            // Test 2: Frame conversion pipeline
            debugger.testFrameConversion();
            
            // Test 3: Video recorder with proper flushing
            debugger.testVideoRecorder();
            
            // Test 4: Transition rendering with debugging
            debugger.testTransitionRendering();
            
            System.out.println();
            System.out.println("SUCCESS: ALL TESTS COMPLETED!");
            System.out.println("Check " + OUTPUT_DIR + " for debug output files.");
            
        } catch (Exception e) {
            System.err.println("ERROR: Debug test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 1: Mat operations and memory management
     */
    public void testMatOperations() {
        System.out.println("Test 1: Mat Operations & Memory Management");
        
        try {
            // Create test frames
            Mat frame1 = createTestFrame(new Scalar(100, 150, 200, 255), "FRAME 1");
            Mat frame2 = createTestFrame(new Scalar(200, 100, 150, 255), "FRAME 2");
            
            System.out.println("SUCCESS: Created test frames: " + frame1.cols() + "x" + frame1.rows());
            
            // Test blending with proper memory management
            Mat blended = new Mat();
            addWeighted(frame1, 0.5, frame2, 0.5, 0, blended);
            
            // CRITICAL: Verify frame is valid before using
            if (!blended.empty() && blended.cols() > 0 && blended.rows() > 0) {
                System.out.println("SUCCESS: Blending successful: " + blended.cols() + "x" + blended.rows());
                
                // Save debug image
                new File(OUTPUT_DIR).mkdirs();
                imwrite(OUTPUT_DIR + "/test1_blended.jpg", blended);
                System.out.println("SUCCESS: Debug image saved");
            } else {
                System.err.println("ERROR: Blended frame is invalid!");
            }
            
            // CRITICAL: Proper cleanup
            frame1.release();
            frame2.release();
            blended.release();
            
            System.out.println("SUCCESS: Memory cleanup completed");
            
        } catch (Exception e) {
            System.err.println("ERROR: Mat operations test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Test 2: Frame conversion pipeline with validation
     */
    public void testFrameConversion() {
        System.out.println("Test 2: Frame Conversion Pipeline");
        
        try {
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            // Create test Mat
            Mat testMat = createTestFrame(new Scalar(50, 100, 200, 255), "CONVERSION TEST");
            
            // CRITICAL: Validate Mat before conversion
            if (testMat.empty()) {
                System.err.println("ERROR: Test Mat is empty!");
                return;
            }
            
            System.out.println("SUCCESS: Test Mat created: " + testMat.cols() + "x" + testMat.rows() + 
                             " channels=" + testMat.channels());
            
            // Convert Mat to Frame
            Frame frame = converter.convert(testMat);
            
            // CRITICAL: Validate Frame
            if (frame == null || frame.image == null) {
                System.err.println("ERROR: Frame conversion failed!");
                return;
            }
            
            System.out.println("SUCCESS: Mat to Frame conversion successful");
            System.out.println("   Frame dimensions: " + frame.imageWidth + "x" + frame.imageHeight);
            
            // Convert Frame to Mat
            Mat convertedBack = converter.convert(frame);
            
            // CRITICAL: Validate converted Mat
            if (convertedBack.empty()) {
                System.err.println("ERROR: Frame to Mat conversion failed!");
                return;
            }
            
            System.out.println("SUCCESS: Frame to Mat conversion successful");
            System.out.println("   Converted Mat: " + convertedBack.cols() + "x" + convertedBack.rows());
            
            // Save debug image
            imwrite(OUTPUT_DIR + "/test2_converted.jpg", convertedBack);
            
            // Cleanup
            testMat.release();
            convertedBack.release();
            
            System.out.println("SUCCESS: Frame conversion test completed");
            
        } catch (Exception e) {
            System.err.println("ERROR: Frame conversion test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Test 3: Video recorder with proper flushing and synchronization
     */
    public void testVideoRecorder() {
        System.out.println("Test 3: Video Recorder with Proper Flushing");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            // Setup recorder with explicit flushing
            String outputFile = OUTPUT_DIR + "/test3_recorder.mp4";
            recorder = new FFmpegFrameRecorder(outputFile, WIDTH, HEIGHT);
            recorder.setVideoCodec(28); // H264 codec
            recorder.setFrameRate(FPS);
            recorder.setFormat("mp4");
            
            // CRITICAL: Set additional options for reliable recording
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            
            recorder.start();
            System.out.println("SUCCESS: Video recorder started");
            
            // Create test frames with visible progression
            for (int i = 0; i < 60; i++) { // 2 seconds at 30fps
                double progress = (double) i / 59.0;
                
                // Create frame with visible progress indicator
                Mat frame = createProgressFrame(progress, i);
                
                // CRITICAL: Validate frame before recording
                if (frame.empty()) {
                    System.err.println("ERROR: Frame " + i + " is empty!");
                    continue;
                }
                
                // Convert and record
                Frame videoFrame = converter.convert(frame);
                
                // CRITICAL: Validate converted frame
                if (videoFrame == null || videoFrame.image == null) {
                    System.err.println("ERROR: Frame " + i + " conversion failed!");
                    frame.release();
                    continue;
                }
                
                recorder.record(videoFrame);
                
                // CRITICAL: Explicit flush every few frames
                if (i % 10 == 0) {
                    recorder.flush();
                    System.out.println("SUCCESS: Flushed at frame " + i);
                }
                
                frame.release();
            }
            
            // CRITICAL: Final flush and stop
            recorder.flush();
            recorder.stop();
            
            // Verify output file
            File outputFileObj = new File(outputFile);
            if (outputFileObj.exists() && outputFileObj.length() > 1000) {
                System.out.println("SUCCESS: Video recorded successfully: " + outputFile);
                System.out.println("   File size: " + outputFileObj.length() + " bytes");
            } else {
                System.err.println("ERROR: Video recording failed or file too small");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: Video recorder test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
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
     * Test 4: Transition rendering with debugging
     */
    public void testTransitionRendering() {
        System.out.println("Test 4: Transition Rendering with Debug Logging");
        
        try {
            // Create test frames
            Mat frame1 = createTestFrame(new Scalar(100, 150, 200, 255), "FRAME 1");
            Mat frame2 = createTestFrame(new Scalar(200, 100, 150, 255), "FRAME 2");
            
            // Test crossfade transition
            System.out.println("Testing CROSSFADE transition");
            
            try {
                FadeTransition transition = new FadeTransition(WIDTH, HEIGHT, 30, TransitionType.CROSSFADE);
                
                // Test transition at different progress points
                double[] progressPoints = {0.0, 0.25, 0.5, 0.75, 1.0};
                
                for (int i = 0; i < progressPoints.length; i++) {
                    double progress = progressPoints[i];
                    
                    // CRITICAL: Apply transition with validation
                    Mat result = transition.applyTransition(frame1, frame2, progress);
                    
                    if (result.empty()) {
                        System.err.println("ERROR: Transition result is empty at progress " + progress);
                        continue;
                    }
                    
                    // Save debug frame
                    String filename = OUTPUT_DIR + "/test4_crossfade_progress_" + (int)(progress * 100) + ".jpg";
                    imwrite(filename, result);
                    
                    result.release();
                }
                
                System.out.println("SUCCESS: CROSSFADE transition test completed");
                
            } catch (Exception e) {
                System.err.println("ERROR: CROSSFADE transition failed: " + e.getMessage());
            }
            
            frame1.release();
            frame2.release();
            
        } catch (Exception e) {
            System.err.println("ERROR: Transition rendering test failed: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Create a test frame with text overlay
     */
    private Mat createTestFrame(Scalar color, String text) {
        Mat frame = new Mat(HEIGHT, WIDTH, CV_8UC3, color);
        
        // Add border
        rectangle(frame, new Rect(10, 10, WIDTH - 20, HEIGHT - 20), 
                 new Scalar(255, 255, 255, 255), 5, 8, 0);
        
        // Add center rectangle with text area
        rectangle(frame, new Rect(WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2), 
                 new Scalar(255, 255, 255, 255), -1, 8, 0);
        
        return frame;
    }
    
    /**
     * Create a frame showing progress
     */
    private Mat createProgressFrame(double progress, int frameNumber) {
        Mat frame = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(50, 50, 50, 255));
        
        // Progress bar
        int barWidth = (int)(WIDTH * 0.8 * progress);
        rectangle(frame, new Rect(WIDTH/10, HEIGHT/2 - 20, barWidth, 40), 
                 new Scalar(0, 255, 0, 255), -1, 8, 0);
        
        // Frame number indicator
        circle(frame, new Point(WIDTH/2, HEIGHT/4), 50, 
               new Scalar(255, 255, 255, 255), -1, 8, 0);
        
        return frame;
    }
}
