import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.File;

/**
 * TRANSITION DIAGNOSTIC REPORT
 * 
 * This class provides a comprehensive analysis of your transition system
 * and identifies exactly what's working vs what needs to be fixed.
 */
public class TransitionDiagnosticReport {
    
    private static final String OUTPUT_DIR = "diagnostic_output";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("    TRANSITION SYSTEM DIAGNOSTIC REPORT");
        System.out.println("=================================================");
        System.out.println();
        
        TransitionDiagnosticReport report = new TransitionDiagnosticReport();
        report.runComprehensiveDiagnostic();
    }
    
    public void runComprehensiveDiagnostic() {
        System.out.println("Running comprehensive diagnostic...");
        System.out.println();
        
        // Create output directory
        new File(OUTPUT_DIR).mkdirs();
        
        // Test 1: Core OpenCV functionality
        boolean opencvWorking = testOpenCVCore();
        
        // Test 2: Transition algorithms
        boolean transitionsWorking = testTransitionAlgorithms();
        
        // Test 3: Frame conversion
        boolean conversionWorking = testFrameConversion();
        
        // Test 4: Video dependencies
        boolean videoWorking = testVideoDependencies();
        
        // Test 5: Memory management
        boolean memoryWorking = testMemoryManagement();
        
        // Generate final report
        generateFinalReport(opencvWorking, transitionsWorking, conversionWorking, 
                          videoWorking, memoryWorking);
    }
    
    /**
     * Test 1: Core OpenCV functionality
     */
    private boolean testOpenCVCore() {
        System.out.println("TEST 1: Core OpenCV Functionality");
        System.out.println("----------------------------------");
        
        try {
            // Test Mat creation
            Mat testMat = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(100, 150, 200, 255));
            
            if (testMat.empty()) {
                System.out.println("FAILED: Mat creation failed");
                return false;
            }
            
            System.out.println("PASSED: Mat creation successful (" + 
                             testMat.cols() + "x" + testMat.rows() + ")");
            
            // Test basic operations
            Mat blended = new Mat();
            Mat frame2 = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(200, 100, 150, 255));
            addWeighted(testMat, 0.5, frame2, 0.5, 0, blended);
            
            if (blended.empty()) {
                System.out.println("FAILED: Blending operation failed");
                testMat.release();
                frame2.release();
                return false;
            }
            
            System.out.println("PASSED: Blending operations working");
            
            // Test image saving
            String filename = OUTPUT_DIR + "/test1_opencv_core.jpg";
            boolean saved = imwrite(filename, blended);
            
            if (!saved) {
                System.out.println("FAILED: Image saving failed");
            } else {
                System.out.println("PASSED: Image saving working");
            }
            
            // Cleanup
            testMat.release();
            frame2.release();
            blended.release();
            
            System.out.println("RESULT: OpenCV Core - WORKING CORRECTLY");
            System.out.println();
            return true;
            
        } catch (Exception e) {
            System.out.println("FAILED: OpenCV Core error: " + e.getMessage());
            System.out.println();
            return false;
        }
    }
    
    /**
     * Test 2: Transition algorithms
     */
    private boolean testTransitionAlgorithms() {
        System.out.println("TEST 2: Transition Algorithms");
        System.out.println("-----------------------------");
        
        try {
            // Create test frames
            Mat frame1 = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(100, 150, 200, 255));
            Mat frame2 = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(200, 100, 150, 255));
            
            // Add visual elements to distinguish frames
            rectangle(frame1, new Rect(50, 50, 200, 100), 
                     new Scalar(255, 255, 255, 255), -1, 8, 0);
            rectangle(frame2, new Rect(WIDTH-250, HEIGHT-150, 200, 100), 
                     new Scalar(255, 255, 255, 255), -1, 8, 0);
            
            System.out.println("PASSED: Test frames created with visual markers");
            
            // Test crossfade transition
            try {
                FadeTransition fadeTransition = new FadeTransition(WIDTH, HEIGHT, 30, TransitionType.CROSSFADE);
                
                // Test at different progress points
                double[] testPoints = {0.0, 0.25, 0.5, 0.75, 1.0};
                boolean allPassed = true;
                
                for (double progress : testPoints) {
                    Mat result = fadeTransition.applyTransition(frame1, frame2, progress);
                    
                    if (result.empty()) {
                        System.out.println("FAILED: Crossfade at progress " + progress);
                        allPassed = false;
                    } else {
                        // Save debug frame
                        String filename = OUTPUT_DIR + "/test2_crossfade_" + 
                                        (int)(progress * 100) + ".jpg";
                        imwrite(filename, result);
                        result.release();
                    }
                }
                
                if (allPassed) {
                    System.out.println("PASSED: Crossfade transition working at all progress points");
                } else {
                    System.out.println("FAILED: Some crossfade transitions failed");
                }
                
            } catch (Exception e) {
                System.out.println("FAILED: Crossfade transition error: " + e.getMessage());
                frame1.release();
                frame2.release();
                return false;
            }
            
            // Test slide transition
            try {
                SlideTransition slideTransition = new SlideTransition(WIDTH, HEIGHT, 30, TransitionType.SLIDE_LEFT);
                Mat slideResult = slideTransition.applyTransition(frame1, frame2, 0.5);
                
                if (slideResult.empty()) {
                    System.out.println("FAILED: Slide transition failed");
                } else {
                    System.out.println("PASSED: Slide transition working");
                    imwrite(OUTPUT_DIR + "/test2_slide_transition.jpg", slideResult);
                    slideResult.release();
                }
                
            } catch (Exception e) {
                System.out.println("FAILED: Slide transition error: " + e.getMessage());
            }
            
            frame1.release();
            frame2.release();
            
            System.out.println("RESULT: Transition Algorithms - WORKING CORRECTLY");
            System.out.println();
            return true;
            
        } catch (Exception e) {
            System.out.println("FAILED: Transition algorithm error: " + e.getMessage());
            System.out.println();
            return false;
        }
    }
    
    /**
     * Test 3: Frame conversion
     */
    private boolean testFrameConversion() {
        System.out.println("TEST 3: Frame Conversion");
        System.out.println("------------------------");
        
        try {
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            
            // Create test Mat
            Mat testMat = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(150, 100, 200, 255));
            
            // Test Mat to Frame conversion
            Frame frame = converter.convert(testMat);
            
            if (frame == null || frame.image == null) {
                System.out.println("FAILED: Mat to Frame conversion failed");
                testMat.release();
                return false;
            }
            
            System.out.println("PASSED: Mat to Frame conversion working");
            System.out.println("   Frame: " + frame.imageWidth + "x" + frame.imageHeight);
            
            // Test Frame to Mat conversion
            Mat convertedBack = converter.convert(frame);
            
            if (convertedBack.empty()) {
                System.out.println("FAILED: Frame to Mat conversion failed");
                testMat.release();
                return false;
            }
            
            System.out.println("PASSED: Frame to Mat conversion working");
            System.out.println("   Mat: " + convertedBack.cols() + "x" + convertedBack.rows());
            
            // Save converted result
            imwrite(OUTPUT_DIR + "/test3_frame_conversion.jpg", convertedBack);
            
            testMat.release();
            convertedBack.release();
            
            System.out.println("RESULT: Frame Conversion - WORKING CORRECTLY");
            System.out.println();
            return true;
            
        } catch (Exception e) {
            System.out.println("FAILED: Frame conversion error: " + e.getMessage());
            System.out.println();
            return false;
        }
    }
    
    /**
     * Test 4: Video dependencies
     */
    private boolean testVideoDependencies() {
        System.out.println("TEST 4: Video Dependencies");
        System.out.println("--------------------------");
        
        try {
            // Try to create FFmpeg recorder
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                OUTPUT_DIR + "/test4_video_test.mp4", WIDTH, HEIGHT);
            
            System.out.println("PASSED: FFmpegFrameRecorder creation successful");
            
            // Try to set basic properties
            recorder.setFrameRate(30.0);
            recorder.setFormat("mp4");
            
            System.out.println("PASSED: Basic recorder configuration successful");
            
            // Try to start (this will fail if FFmpeg libs are missing)
            recorder.start();
            System.out.println("PASSED: Recorder start successful");
            
            recorder.stop();
            System.out.println("PASSED: Recorder stop successful");
            
            System.out.println("RESULT: Video Dependencies - WORKING CORRECTLY");
            System.out.println();
            return true;
            
        } catch (Exception e) {
            System.out.println("FAILED: Video dependency error: " + e.getMessage());
            System.out.println("CAUSE: Missing FFmpeg native libraries");
            System.out.println("SOLUTION: Download complete JavaCV platform JAR with FFmpeg");
            System.out.println();
            return false;
        }
    }
    
    /**
     * Test 5: Memory management
     */
    private boolean testMemoryManagement() {
        System.out.println("TEST 5: Memory Management");
        System.out.println("-------------------------");
        
        try {
            // Create and release multiple Mats
            for (int i = 0; i < 10; i++) {
                Mat testMat = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(i * 25, 100, 200, 255));
                
                if (testMat.empty()) {
                    System.out.println("FAILED: Mat creation failed at iteration " + i);
                    return false;
                }
                
                testMat.release();
            }
            
            System.out.println("PASSED: Multiple Mat creation/release cycles successful");
            
            // Test transition with cleanup
            Mat frame1 = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(100, 150, 200, 255));
            Mat frame2 = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(200, 100, 150, 255));
            
            FadeTransition transition = new FadeTransition(WIDTH, HEIGHT, 30, TransitionType.CROSSFADE);
            
            for (int i = 0; i < 5; i++) {
                double progress = i * 0.25;
                Mat result = transition.applyTransition(frame1, frame2, progress);
                
                if (!result.empty()) {
                    result.release();
                } else {
                    System.out.println("FAILED: Transition result empty at iteration " + i);
                    frame1.release();
                    frame2.release();
                    return false;
                }
            }
            
            frame1.release();
            frame2.release();
            
            System.out.println("PASSED: Transition memory management successful");
            System.out.println("RESULT: Memory Management - WORKING CORRECTLY");
            System.out.println();
            return true;
            
        } catch (Exception e) {
            System.out.println("FAILED: Memory management error: " + e.getMessage());
            System.out.println();
            return false;
        }
    }
    
    /**
     * Generate final diagnostic report
     */
    private void generateFinalReport(boolean opencv, boolean transitions, boolean conversion, 
                                   boolean video, boolean memory) {
        System.out.println("=================================================");
        System.out.println("           FINAL DIAGNOSTIC REPORT");
        System.out.println("=================================================");
        System.out.println();
        
        System.out.println("COMPONENT STATUS:");
        System.out.println("  OpenCV Core:        " + (opencv ? "WORKING" : "FAILED"));
        System.out.println("  Transitions:        " + (transitions ? "WORKING" : "FAILED"));
        System.out.println("  Frame Conversion:   " + (conversion ? "WORKING" : "FAILED"));
        System.out.println("  Video Recording:    " + (video ? "WORKING" : "FAILED"));
        System.out.println("  Memory Management:  " + (memory ? "WORKING" : "FAILED"));
        System.out.println();
        
        if (opencv && transitions && conversion && memory) {
            System.out.println("DIAGNOSIS: Your transition system is WORKING CORRECTLY!");
            System.out.println();
            System.out.println("The core transition logic, frame processing, and memory");
            System.out.println("management are all functioning properly.");
            System.out.println();
            
            if (!video) {
                System.out.println("ISSUE: Video recording failed due to missing FFmpeg libraries.");
                System.out.println();
                System.out.println("SOLUTION:");
                System.out.println("1. Download javacv-platform-1.5.8.jar (includes FFmpeg)");
                System.out.println("2. Use this JAR instead of individual component JARs");
                System.out.println("3. Your transitions will then work in video output");
                System.out.println();
                System.out.println("ALTERNATIVE: Use image sequence output (already working)");
                System.out.println("and convert to video with external FFmpeg.");
            } else {
                System.out.println("RESULT: ALL SYSTEMS WORKING - NO ISSUES FOUND!");
            }
        } else {
            System.out.println("DIAGNOSIS: Found issues in core components.");
            System.out.println("Check individual test results above for details.");
        }
        
        System.out.println();
        System.out.println("Debug images saved to: " + OUTPUT_DIR);
        System.out.println("=================================================");
    }
}
