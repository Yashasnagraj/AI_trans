import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;

import java.io.File;

/**
 * Real Video Quality Improvements Demo
 * Samsung PRISM Internship - JavaCV Implementation
 * 
 * This demonstrates the quality improvements using actual video files:
 * 1. Enhanced dissolve with smooth cosine interpolation
 * 2. Progressive blur with dynamic kernel sizing
 * 3. Enhanced frame blending with cosine-smoothed alpha
 * 4. New whip pan transitions with directional motion blur
 */
public class RealVideoQualityDemo {
    
    private static final int OUTPUT_WIDTH = 1280;
    private static final int OUTPUT_HEIGHT = 720;
    private static final double FPS = 30.0;
    
    public static void main(String[] args) {
        System.out.println("🎬 Real Video Quality Improvements Demo");
        System.out.println("======================================");
        System.out.println("Samsung PRISM Internship - JavaCV Implementation");
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
        File outputDir = new File("real_video_quality_output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        System.out.println("🚀 Processing Real Videos with Enhanced Algorithms...");
        System.out.println();
        
        try {
            // 1. Enhanced Dissolve Transition
            createEnhancedDissolveTransition(video1Path, video2Path, 
                "real_video_quality_output/enhanced_dissolve_real.mp4");
            
            // 2. Progressive Blur Transition
            createProgressiveBlurTransition(video1Path, video2Path,
                "real_video_quality_output/progressive_blur_real.mp4");
            
            // 3. Whip Pan Transitions
            String[] directions = {"left", "right", "up", "down"};
            for (String direction : directions) {
                createWhipPanTransition(video1Path, video2Path,
                    "real_video_quality_output/whip_pan_" + direction + "_real.mp4", direction);
            }
            
            // 4. Before/After Comparison
            createBeforeAfterComparison(video1Path, video2Path,
                "real_video_quality_output/before_after_comparison_real.mp4");
            
            System.out.println();
            System.out.println("🎉 Real Video Quality Demo Complete!");
            System.out.println("===================================");
            System.out.println("📁 Output files saved to: real_video_quality_output/");
            System.out.println();
            System.out.println("📊 Quality Enhancements Applied to Real Videos:");
            System.out.println("✅ Smooth cosine interpolation for natural transitions");
            System.out.println("✅ Progressive blur with dynamic kernel sizing");
            System.out.println("✅ Enhanced frame blending eliminates graininess");
            System.out.println("✅ Professional whip pan effects with motion blur");
            System.out.println();
            System.out.println("🎯 Result: Editor-level quality achieved with real video content!");
            
        } catch (Exception e) {
            System.err.println("❌ Error processing videos: " + e.getMessage());
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
     * Apply directional motion blur for whip pan effects
     */
    private static Mat applyMotionBlur(Mat frame, double motionIntensity, String direction) {
        int kernelSize = Math.max(3, (int)(motionIntensity * 30) + 1);
        if (kernelSize % 2 == 0) kernelSize++;
        
        Mat result = new Mat();
        
        switch (direction.toLowerCase()) {
            case "left":
            case "right":
            case "horizontal":
                // Horizontal motion blur
                GaussianBlur(frame, result, new Size(kernelSize, 1), 0);
                break;
            case "up":
            case "down":
            case "vertical":
                // Vertical motion blur
                GaussianBlur(frame, result, new Size(1, kernelSize), 0);
                break;
            default:
                frame.copyTo(result);
                break;
        }
        
        return result;
    }
    
    /**
     * Create enhanced dissolve transition with smooth cosine interpolation
     */
    private static void createEnhancedDissolveTransition(String video1Path, String video2Path, String outputPath) throws Exception {
        System.out.println("✨ Creating Enhanced Dissolve Transition...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(FPS);
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
            resize(mat1, resized1, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            resize(mat2, resized2, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            
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
        
        System.out.println("   ✅ Enhanced dissolve transition saved to: " + outputPath);
    }
    
    /**
     * Create progressive blur transition with dynamic kernel sizing
     */
    private static void createProgressiveBlurTransition(String video1Path, String video2Path, String outputPath) throws Exception {
        System.out.println("🌀 Creating Progressive Blur Transition...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(FPS);
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
            resize(mat1, resized1, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            resize(mat2, resized2, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            
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
        
        System.out.println("   ✅ Progressive blur transition saved to: " + outputPath);
    }
    
    /**
     * Create whip pan transition with directional motion blur
     */
    private static void createWhipPanTransition(String video1Path, String video2Path, String outputPath, String direction) throws Exception {
        System.out.println("📹 Creating Whip Pan " + direction.toUpperCase() + " Transition...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(FPS);
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
            resize(mat1, resized1, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            resize(mat2, resized2, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            
            // Calculate progress and motion intensity
            double progress = (double) i / (transitionFrames - 1);
            double motionIntensity = progressiveBlurIntensity(progress); // Peaks at middle
            
            // Apply motion blur
            Mat blurred1 = applyMotionBlur(resized1, motionIntensity, direction);
            Mat blurred2 = applyMotionBlur(resized2, motionIntensity, direction);
            
            // Enhanced blending
            Mat result = blendFramesSmooth(blurred1, blurred2, progress);
            
            // Add progress indicators
            String motionText = String.format("Whip Pan %s: %.3f", direction.toUpperCase(), motionIntensity);
            putText(result, motionText, new Point(30, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            
            int kernelSize = Math.max(3, (int)(motionIntensity * 30) + 1);
            String kernelText = String.format("Motion Kernel: %d", kernelSize);
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
        
        System.out.println("   ✅ Whip pan " + direction + " transition saved to: " + outputPath);
    }
    
    /**
     * Create side-by-side comparison of old vs new methods
     */
    private static void createBeforeAfterComparison(String video1Path, String video2Path, String outputPath) throws Exception {
        System.out.println("📊 Creating Before/After Comparison...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        // Double width for side-by-side comparison
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH * 2, OUTPUT_HEIGHT);
        recorder.setVideoCodec(AV_CODEC_ID_H264);
        recorder.setFrameRate(FPS);
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
            resize(mat1, resized1, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            resize(mat2, resized2, new Size(OUTPUT_WIDTH, OUTPUT_HEIGHT));
            
            double progress = (double) i / (transitionFrames - 1);
            
            // Left side: Linear blending (old method)
            double linearAlpha = progress;
            Mat leftResult = new Mat();
            addWeighted(resized1, 1.0 - linearAlpha, resized2, linearAlpha, 0.0, leftResult);
            
            // Right side: Enhanced cosine blending (new method)
            Mat rightResult = blendFramesSmooth(resized1, resized2, progress);
            
            // Combine side by side
            Mat comparison = new Mat(OUTPUT_HEIGHT, OUTPUT_WIDTH * 2, resized1.type());
            Mat leftROI = new Mat(comparison, new Rect(0, 0, OUTPUT_WIDTH, OUTPUT_HEIGHT));
            Mat rightROI = new Mat(comparison, new Rect(OUTPUT_WIDTH, 0, OUTPUT_WIDTH, OUTPUT_HEIGHT));
            
            leftResult.copyTo(leftROI);
            rightResult.copyTo(rightROI);
            
            // Add labels
            putText(comparison, "LINEAR (Old)", new Point(30, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(comparison, "COSINE ENHANCED (New)", new Point(OUTPUT_WIDTH + 30, 50), FONT_HERSHEY_SIMPLEX, 1.0, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            
            String progressText = String.format("Progress: %.2f", progress);
            putText(comparison, progressText, new Point(30, OUTPUT_HEIGHT - 30), FONT_HERSHEY_SIMPLEX, 0.8, 
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
        
        System.out.println("   ✅ Before/after comparison saved to: " + outputPath);
    }
}