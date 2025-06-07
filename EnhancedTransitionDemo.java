import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.File;

/**
 * Enhanced Video Transition Demo - Samsung PRISM Internship
 * 
 * Demonstrates quality improvements in JavaCV-based video transitions:
 * 1. Enhanced dissolve with smooth cosine interpolation
 * 2. Progressive blur with dynamic kernel sizing  
 * 3. Enhanced frame blending with cosine-smoothed alpha
 * 4. New whip pan transitions with directional motion blur
 */
public class EnhancedTransitionDemo {
    
    private static final int OUTPUT_WIDTH = 1280;
    private static final int OUTPUT_HEIGHT = 720;
    private static final double FRAME_RATE = 30.0;
    
    public static void main(String[] args) {
        System.out.println("🎬 AI Video Transition Engine - Quality Improvements Demo");
        System.out.println("========================================================");
        System.out.println("Samsung PRISM Internship - Enhanced JavaCV Implementation");
        System.out.println();
        
        try {
            // Create output directory
            File outputDir = new File("enhanced_javacv_output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            String video1Path = "input_videos/video1.mp4";
            String video2Path = "input_videos/video2.mp4";
            
            // Check if input videos exist
            if (!new File(video1Path).exists() || !new File(video2Path).exists()) {
                System.out.println("❌ Input videos not found!");
                return;
            }
            
            System.out.println("🚀 Generating Enhanced Transition Demonstrations...");
            System.out.println();
            
            // 1. Enhanced Dissolve Transition
            createEnhancedDissolveTransition(video1Path, video2Path, 
                "enhanced_javacv_output/enhanced_dissolve_demo.mp4", 90);
            
            // 2. Progressive Blur Transition
            createProgressiveBlurTransition(video1Path, video2Path,
                "enhanced_javacv_output/progressive_blur_demo.mp4", 90);
            
            // 3. Whip Pan Transitions
            createWhipPanTransition(video1Path, video2Path,
                "enhanced_javacv_output/whip_pan_left_demo.mp4", "LEFT", 90);
            
            createWhipPanTransition(video1Path, video2Path,
                "enhanced_javacv_output/whip_pan_right_demo.mp4", "RIGHT", 90);
            
            // 4. Before/After Comparison
            createComparisonDemo(video1Path, video2Path,
                "enhanced_javacv_output/before_after_comparison.mp4", 90);
            
            System.out.println();
            System.out.println("🎉 Quality Improvements Demo Complete!");
            System.out.println("=====================================");
            System.out.println("📁 Output files saved to: enhanced_javacv_output/");
            System.out.println();
            System.out.println("📊 Quality Enhancements Demonstrated:");
            System.out.println("✅ Smooth cosine interpolation for dissolve transitions");
            System.out.println("✅ Progressive blur with dynamic kernel sizing");
            System.out.println("✅ Enhanced frame blending with cosine-smoothed alpha");
            System.out.println("✅ New whip pan transitions with directional motion blur");
            System.out.println();
            System.out.println("🎯 Result: Professional-grade transition quality achieved!");
            
        } catch (Exception e) {
            System.err.println("❌ Error during demo generation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Enhanced cosine interpolation for smooth transitions
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
     * Apply directional motion blur for whip pan effects
     */
    private static Mat applyMotionBlur(Mat frame, double motionIntensity, String direction) {
        int kernelSize = motionBlurKernelSize(motionIntensity);
        if (kernelSize % 2 == 0) kernelSize++;
        
        Mat result = new Mat();
        
        switch (direction.toUpperCase()) {
            case "LEFT":
            case "RIGHT":
            case "HORIZONTAL":
                // Horizontal motion blur
                GaussianBlur(frame, result, new Size(kernelSize, 1), 0);
                break;
            case "UP":
            case "DOWN":
            case "VERTICAL":
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
    private static void createEnhancedDissolveTransition(String video1Path, String video2Path, 
                                                        String outputPath, int durationFrames) throws Exception {
        System.out.println("🎭 Creating Enhanced Dissolve Transition...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(FRAME_RATE);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        
        for (int frameNum = 0; frameNum < durationFrames; frameNum++) {
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
            
            // Calculate progress and apply enhanced blending
            double progress = (double) frameNum / (durationFrames - 1);
            Mat result = blendFramesSmooth(resized1, resized2, progress);
            
            // Add progress indicator text
            String progressText = String.format("Enhanced Dissolve - Progress: %.2f", progress);
            String alphaText = String.format("Cosine Alpha: %.3f", cosineInterpolation(progress));
            
            putText(result, progressText, new Point(10, 30), FONT_HERSHEY_SIMPLEX, 0.7, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(result, alphaText, new Point(10, 60), FONT_HERSHEY_SIMPLEX, 0.7, 
                   new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(result);
            recorder.record(outputFrame);
            
            // Cleanup
            mat1.release();
            mat2.release();
            resized1.release();
            resized2.release();
            result.release();
        }
        
        grabber1.stop();
        grabber2.stop();
        recorder.stop();
        
        System.out.println("   ✅ Enhanced dissolve saved to: " + outputPath);
    }
    
    /**
     * Create progressive blur transition with dynamic kernel sizing
     */
    private static void createProgressiveBlurTransition(String video1Path, String video2Path,
                                                       String outputPath, int durationFrames) throws Exception {
        System.out.println("🌀 Creating Progressive Blur Transition...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(FRAME_RATE);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        for (int frameNum = 0; frameNum < durationFrames; frameNum++) {
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
            double progress = (double) frameNum / (durationFrames - 1);
            double blurIntensity = progressiveBlurIntensity(progress);
            
            // Apply progressive blur to both frames
            Mat blurred1 = applyProgressiveBlur(resized1, blurIntensity);
            Mat blurred2 = applyProgressiveBlur(resized2, blurIntensity);
            
            // Enhanced blending
            Mat result = blendFramesSmooth(blurred1, blurred2, progress);
            
            // Add progress indicator text
            String progressText = String.format("Progressive Blur - Intensity: %.3f", blurIntensity);
            String kernelText = String.format("Kernel Size: %d", Math.max(3, (int)(blurIntensity * 30) + 1));
            
            putText(result, progressText, new Point(10, 30), FONT_HERSHEY_SIMPLEX, 0.7, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(result, kernelText, new Point(10, 60), FONT_HERSHEY_SIMPLEX, 0.7, 
                   new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(result);
            recorder.record(outputFrame);
            
            // Cleanup
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
        
        System.out.println("   ✅ Progressive blur saved to: " + outputPath);
    }
    
    /**
     * Create whip pan transition with directional motion blur
     */
    private static void createWhipPanTransition(String video1Path, String video2Path,
                                               String outputPath, String direction, int durationFrames) throws Exception {
        System.out.println("📹 Creating Whip Pan " + direction + " Transition...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(FRAME_RATE);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        for (int frameNum = 0; frameNum < durationFrames; frameNum++) {
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
            double progress = (double) frameNum / (durationFrames - 1);
            double motionIntensity = 4.0 * progress * (1.0 - progress); // Peaks at middle
            
            // Apply motion blur
            Mat blurred1 = applyMotionBlur(resized1, motionIntensity, direction);
            Mat blurred2 = applyMotionBlur(resized2, motionIntensity, direction);
            
            // Enhanced blending
            Mat result = blendFramesSmooth(blurred1, blurred2, progress);
            
            // Add progress indicator text
            String progressText = String.format("Whip Pan %s - Motion: %.3f", direction, motionIntensity);
            String kernelText = String.format("Kernel Size: %d", motionBlurKernelSize(motionIntensity));
            
            putText(result, progressText, new Point(10, 30), FONT_HERSHEY_SIMPLEX, 0.7, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(result, kernelText, new Point(10, 60), FONT_HERSHEY_SIMPLEX, 0.7, 
                   new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(result);
            recorder.record(outputFrame);
            
            // Cleanup
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
        
        System.out.println("   ✅ Whip pan " + direction + " saved to: " + outputPath);
    }
    
    /**
     * Create side-by-side comparison of old vs new methods
     */
    private static void createComparisonDemo(String video1Path, String video2Path,
                                           String outputPath, int durationFrames) throws Exception {
        System.out.println("📊 Creating Before/After Comparison Demo...");
        
        FFmpegFrameGrabber grabber1 = new FFmpegFrameGrabber(video1Path);
        FFmpegFrameGrabber grabber2 = new FFmpegFrameGrabber(video2Path);
        
        grabber1.start();
        grabber2.start();
        
        // Double width for side-by-side comparison
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, OUTPUT_WIDTH * 2, OUTPUT_HEIGHT);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(FRAME_RATE);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.start();
        
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        for (int frameNum = 0; frameNum < durationFrames; frameNum++) {
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
            
            double progress = (double) frameNum / (durationFrames - 1);
            
            // Left side: Linear blending (old method)
            double linearAlpha = progress;
            Mat leftResult = new Mat();
            addWeighted(resized1, 1.0 - linearAlpha, resized2, linearAlpha, 0.0, leftResult);
            
            // Right side: Enhanced cosine blending (new method)
            Mat rightResult = blendFramesSmooth(resized1, resized2, progress);
            
            // Combine side by side
            Mat comparison = new Mat();
            hconcat(new MatVector(leftResult, rightResult), comparison);
            
            // Add labels
            putText(comparison, "LINEAR (Old)", new Point(10, 30), FONT_HERSHEY_SIMPLEX, 0.8, 
                   new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(comparison, "COSINE ENHANCED (New)", new Point(OUTPUT_WIDTH + 10, 30), 
                   FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(255, 255, 255, 0), 2, LINE_AA, false);
            putText(comparison, String.format("Progress: %.2f", progress), new Point(10, OUTPUT_HEIGHT - 20), 
                   FONT_HERSHEY_SIMPLEX, 0.6, new Scalar(0, 255, 255, 0), 2, LINE_AA, false);
            
            Frame outputFrame = converter.convert(comparison);
            recorder.record(outputFrame);
            
            // Cleanup
            mat1.release();
            mat2.release();
            resized1.release();
            resized2.release();
            leftResult.release();
            rightResult.release();
            comparison.release();
        }
        
        grabber1.stop();
        grabber2.stop();
        recorder.stop();
        
        System.out.println("   ✅ Comparison demo saved to: " + outputPath);
    }
}