import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;

import java.io.File;

/**
 * Quality Mathematics Demo - JavaCV Implementation
 * Samsung PRISM Internship - Enhanced Transition Quality
 * 
 * This demonstrates the mathematical improvements and creates actual video output
 * showing the enhanced algorithms working with real video files.
 */
public class QualityMathDemo {
    
    public static void main(String[] args) {
        System.out.println("🎬 Quality Mathematics Demo - JavaCV Implementation");
        System.out.println("==================================================");
        System.out.println("Samsung PRISM Internship - Enhanced Transition Quality");
        System.out.println();
        
        // Input video paths
        String video1Path = "input_videos/video1.mp4";
        String video2Path = "input_videos/video2.mp4";
        
        // Check if input videos exist
        if (!new File(video1Path).exists() || !new File(video2Path).exists()) {
            System.err.println("❌ Input videos not found!");
            System.err.println("Expected: " + video1Path + " and " + video2Path);
            return;
        }
        
        // Create output directory
        File outputDir = new File("quality_math_output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        System.out.println("🚀 Testing Enhanced Mathematical Algorithms...");
        System.out.println();
        
        try {
            // Test 1: Mathematical Functions
            testMathematicalFunctions();
            
            // Test 2: Create Enhanced Dissolve Video
            createEnhancedDissolveVideo(video1Path, video2Path, outputDir);
            
            // Test 3: Create Progressive Blur Video
            createProgressiveBlurVideo(video1Path, video2Path, outputDir);
            
            // Test 4: Create Before/After Comparison
            createComparisonVideo(video1Path, video2Path, outputDir);
            
            System.out.println();
            System.out.println("🎉 Quality Mathematics Demo Complete!");
            System.out.println("====================================");
            System.out.println("📁 Output files saved to: quality_math_output/");
            System.out.println();
            System.out.println("📊 Quality Enhancements Demonstrated:");
            System.out.println("✅ Enhanced cosine interpolation mathematics");
            System.out.println("✅ Progressive blur intensity calculations");
            System.out.println("✅ Motion blur kernel sizing algorithms");
            System.out.println("✅ Real video processing with enhanced quality");
            System.out.println();
            System.out.println("🎯 Result: Professional-grade mathematical models implemented!");
            
        } catch (Exception e) {
            System.err.println("❌ Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Enhanced easing using cosine interpolation for natural transitions
     */
    private static double cosineInterpolation(double progress) {
        return (1 - Math.cos(progress * Math.PI)) / 2;
    }
    
    /**
     * Calculate progressive blur intensity using parabolic curve
     */
    private static double progressiveBlurIntensity(double progress) {
        return 4.0 * progress * (1.0 - progress);
    }
    
    /**
     * Calculate motion blur kernel size
     */
    private static int motionBlurKernelSize(double motionIntensity) {
        return Math.max(3, (int)(motionIntensity * 30) + 1);
    }
    
    /**
     * Enhanced frame blending with smooth cosine alpha progression
     */
    private static Mat blendFramesSmooth(Mat frame1, Mat frame2, double alpha) {
        double smoothAlpha = cosineInterpolation(alpha);
        Mat result = new Mat();
        addWeighted(frame1, 1.0 - smoothAlpha, frame2, smoothAlpha, 0.0, result);
        return result;
    }
    
    /**
     * Apply progressive blur with dynamic kernel sizing
     */
    private static Mat applyProgressiveBlur(Mat frame, double blurIntensity) {
        int kernelSize = Math.max(3, (int)(blurIntensity * 30) + 1);
        if (kernelSize % 2 == 0) kernelSize++;
        
        Mat blurred = new Mat();
        GaussianBlur(frame, blurred, new Size(kernelSize, kernelSize), 0);
        return blurred;
    }
    
    /**
     * Test mathematical functions
     */
    private static void testMathematicalFunctions() {
        System.out.println("🧮 Testing Enhanced Mathematical Functions...");
        
        // Test cosine interpolation vs linear
        System.out.println("   📈 Cosine Interpolation vs Linear:");
        double[] testValues = {0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0};
        
        for (double progress : testValues) {
            double linear = progress;
            double cosine = cosineInterpolation(progress);
            double difference = Math.abs(cosine - linear);
            
            System.out.printf("     Progress: %.2f -> Linear: %.3f, Cosine: %.3f (Diff: %.3f)%n", 
                            progress, linear, cosine, difference);
        }
        
        // Test progressive blur intensity
        System.out.println("   🌀 Progressive Blur Intensity:");
        for (double progress : testValues) {
            double blurIntensity = progressiveBlurIntensity(progress);
            int kernelSize = Math.max(3, (int)(blurIntensity * 30) + 1);
            
            System.out.printf("     Progress: %.2f -> Blur: %.3f, Kernel: %d%n", 
                            progress, blurIntensity, kernelSize);
        }
        
        // Test motion blur kernel calculation
        System.out.println("   📹 Motion Blur Kernel Calculation:");
        for (double progress : testValues) {
            double motionIntensity = progressiveBlurIntensity(progress);
            int kernelSize = motionBlurKernelSize(motionIntensity);
            
            System.out.printf("     Progress: %.2f -> Motion: %.3f, Kernel: %d%n", 
                            progress, motionIntensity, kernelSize);
        }
        
        System.out.println("   ✅ Mathematical functions test completed");
    }
    
    /**
     * Create enhanced dissolve video demonstration
     */
    private static void createEnhancedDissolveVideo(String video1Path, String video2Path, File outputDir) throws Exception {
        System.out.println("✨ Creating Enhanced Dissolve Video...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        String outputPath = outputDir.getPath() + "/enhanced_dissolve_demo.mp4";
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, 1280, 720);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(30.0);
        recorder.setFormat("mp4");
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        int transitionFrames = 90; // 3 seconds at 30fps
        
        for (int i = 0; i < transitionFrames; i++) {
            Frame frame1 = grabber1.grab();
            Frame frame2 = grabber2.grab();
            
            if (frame1 == null || frame2 == null) break;
            
            Mat mat1 = converter.convert(frame1);
            Mat mat2 = converter.convert(frame2);
            
            if (mat1 == null || mat2 == null) continue;
            
            // Resize frames to output dimensions
            Mat resized1 = new Mat();
            Mat resized2 = new Mat();
            resize(mat1, resized1, new Size(1280, 720));
            resize(mat2, resized2, new Size(1280, 720));
            
            // Calculate progress and apply enhanced blending
            double progress = (double) i / (transitionFrames - 1);
            Mat result = blendFramesSmooth(resized1, resized2, progress);
            
            // Add progress indicator
            String progressText = String.format("Enhanced Dissolve: %.2f", progress);
            putText(result, progressText, new Point(30, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            
            String alphaText = String.format("Cosine Alpha: %.3f", cosineInterpolation(progress));
            putText(result, alphaText, new Point(30, 90), FONT_HERSHEY_SIMPLEX, 0.8, 
                   new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(result);
            recorder.record(outputFrame);
            
            // Clean up
            mat1.release();
            mat2.release();
            resized1.release();
            resized2.release();
            result.release();
        }
        
        grabber1.stop();
        grabber2.stop();
        recorder.stop();
        
        System.out.println("   ✅ Enhanced dissolve video saved to: " + outputPath);
    }
    
    /**
     * Create progressive blur video demonstration
     */
    private static void createProgressiveBlurVideo(String video1Path, String video2Path, File outputDir) throws Exception {
        System.out.println("🌀 Creating Progressive Blur Video...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        String outputPath = outputDir.getPath() + "/progressive_blur_demo.mp4";
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, 1280, 720);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(30.0);
        recorder.setFormat("mp4");
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        int transitionFrames = 90;
        
        for (int i = 0; i < transitionFrames; i++) {
            Frame frame1 = grabber1.grab();
            Frame frame2 = grabber2.grab();
            
            if (frame1 == null || frame2 == null) break;
            
            Mat mat1 = converter.convert(frame1);
            Mat mat2 = converter.convert(frame2);
            
            if (mat1 == null || mat2 == null) continue;
            
            // Resize frames
            Mat resized1 = new Mat();
            Mat resized2 = new Mat();
            resize(mat1, resized1, new Size(1280, 720));
            resize(mat2, resized2, new Size(1280, 720));
            
            // Calculate progress and blur intensity
            double progress = (double) i / (transitionFrames - 1);
            double blurIntensity = progressiveBlurIntensity(progress);
            
            // Apply progressive blur
            Mat blurred1 = applyProgressiveBlur(resized1, blurIntensity);
            Mat blurred2 = applyProgressiveBlur(resized2, blurIntensity);
            
            // Enhanced blending
            Mat result = blendFramesSmooth(blurred1, blurred2, progress);
            
            // Add progress indicators
            String progressText = String.format("Progressive Blur: %.3f", blurIntensity);
            putText(result, progressText, new Point(30, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            
            int kernelSize = Math.max(3, (int)(blurIntensity * 30) + 1);
            String kernelText = String.format("Kernel Size: %d", kernelSize);
            putText(result, kernelText, new Point(30, 90), FONT_HERSHEY_SIMPLEX, 0.8, 
                   new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(result);
            recorder.record(outputFrame);
            
            // Clean up
            mat1.release();
            mat2.release();
            resized1.release();
            resized2.release();
            blurred1.release();
            blurred2.release();
            result.release();
        }
        
        grabber1.stop();
        grabber2.stop();
        recorder.stop();
        
        System.out.println("   ✅ Progressive blur video saved to: " + outputPath);
    }
    
    /**
     * Create side-by-side comparison video
     */
    private static void createComparisonVideo(String video1Path, String video2Path, File outputDir) throws Exception {
        System.out.println("📊 Creating Before/After Comparison Video...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        String outputPath = outputDir.getPath() + "/before_after_comparison.mp4";
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, 2560, 720); // Double width
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(30.0);
        recorder.setFormat("mp4");
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        int transitionFrames = 90;
        
        for (int i = 0; i < transitionFrames; i++) {
            Frame frame1 = grabber1.grab();
            Frame frame2 = grabber2.grab();
            
            if (frame1 == null || frame2 == null) break;
            
            Mat mat1 = converter.convert(frame1);
            Mat mat2 = converter.convert(frame2);
            
            if (mat1 == null || mat2 == null) continue;
            
            // Resize frames
            Mat resized1 = new Mat();
            Mat resized2 = new Mat();
            resize(mat1, resized1, new Size(1280, 720));
            resize(mat2, resized2, new Size(1280, 720));
            
            double progress = (double) i / (transitionFrames - 1);
            
            // Left side: Linear blending (old method)
            double linearAlpha = progress;
            Mat leftResult = new Mat();
            addWeighted(resized1, 1.0 - linearAlpha, resized2, linearAlpha, 0.0, leftResult);
            
            // Right side: Enhanced cosine blending (new method)
            Mat rightResult = blendFramesSmooth(resized1, resized2, progress);
            
            // Combine side by side
            Mat comparison = new Mat(720, 2560, resized1.type());
            Mat leftROI = new Mat(comparison, new Rect(0, 0, 1280, 720));
            Mat rightROI = new Mat(comparison, new Rect(1280, 0, 1280, 720));
            
            leftResult.copyTo(leftROI);
            rightResult.copyTo(rightROI);
            
            // Add labels
            putText(comparison, "LINEAR (Old)", new Point(30, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(comparison, "COSINE ENHANCED (New)", new Point(1310, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            
            String progressText = String.format("Progress: %.2f", progress);
            putText(comparison, progressText, new Point(30, 690), FONT_HERSHEY_SIMPLEX, 0.8, 
                   new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(comparison);
            recorder.record(outputFrame);
            
            // Clean up
            mat1.release();
            mat2.release();
            resized1.release();
            resized2.release();
            leftResult.release();
            rightResult.release();
            comparison.release();
            leftROI.release();
            rightROI.release();
        }
        
        grabber1.stop();
        grabber2.stop();
        recorder.stop();
        
        System.out.println("   ✅ Comparison video saved to: " + outputPath);
    }
}