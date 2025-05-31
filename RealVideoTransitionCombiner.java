import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * REAL VIDEO TRANSITION COMBINER
 * 
 * This creates transition videos using your actual input videos
 * from the input_videos folder with ALL transition effects
 */
public class RealVideoTransitionCombiner {
    
    private static final String INPUT_DIR = "input_videos";
    private static final String OUTPUT_DIR = "real_transition_videos";
    private static final int TARGET_WIDTH = 1280;
    private static final int TARGET_HEIGHT = 720;
    private static final int TRANSITION_FRAMES = 30; // 1 second at 30fps
    private static final int VIDEO_SEGMENT_FRAMES = 60; // 2 seconds per video segment
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    REAL VIDEO TRANSITION COMBINER");
        System.out.println("==========================================");
        System.out.println("Creating transitions with your actual videos!");
        System.out.println();
        
        RealVideoTransitionCombiner combiner = new RealVideoTransitionCombiner();
        combiner.createRealTransitionVideos();
    }
    
    public void createRealTransitionVideos() {
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
            
            System.out.println("Found input videos:");
            for (File video : videoFiles) {
                System.out.println("  - " + video.getName() + " (" + (video.length()/1024) + " KB)");
            }
            System.out.println();
            
            // Load the first 2 videos
            String video1Path = videoFiles[0].getAbsolutePath();
            String video2Path = videoFiles[1].getAbsolutePath();
            
            System.out.println("Loading video frames...");
            List<Mat> video1Frames = loadVideoFrames(video1Path, "Video 1");
            List<Mat> video2Frames = loadVideoFrames(video2Path, "Video 2");
            
            if (video1Frames.isEmpty() || video2Frames.isEmpty()) {
                System.err.println("ERROR: Failed to load video frames");
                return;
            }
            
            // Create all transition sequences
            createCrossfadeSequence(video1Frames, video2Frames);
            createSlideSequence(video1Frames, video2Frames);
            createZoomSequence(video1Frames, video2Frames);
            createWipeSequence(video1Frames, video2Frames);
            createCircleSequence(video1Frames, video2Frames);
            createDiagonalSequence(video1Frames, video2Frames);
            createSpiralSequence(video1Frames, video2Frames);
            createMosaicSequence(video1Frames, video2Frames);
            
            // Create conversion script
            createVideoConversionScript();
            
            // Cleanup
            cleanupFrames(video1Frames);
            cleanupFrames(video2Frames);
            
            System.out.println();
            System.out.println("==========================================");
            System.out.println("    ALL REAL TRANSITION SEQUENCES CREATED!");
            System.out.println("==========================================");
            System.out.println("Check '" + OUTPUT_DIR + "' folder for:");
            System.out.println("  - Image sequences from your actual videos");
            System.out.println("  - convert_real_videos.bat script");
            System.out.println();
            System.out.println("To create videos, run: convert_real_videos.bat");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load actual video frames from file
     */
    private List<Mat> loadVideoFrames(String videoPath, String videoName) {
        List<Mat> frames = new ArrayList<>();
        FFmpegFrameGrabber grabber = null;
        
        try {
            System.out.println("Loading " + videoName + ": " + new File(videoPath).getName());
            
            grabber = new FFmpegFrameGrabber(videoPath);
            grabber.start();
            
            System.out.println("  Video info: " + grabber.getImageWidth() + "x" + grabber.getImageHeight() + 
                             " @ " + grabber.getFrameRate() + " fps");
            
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            Frame frame;
            int frameCount = 0;
            int maxFrames = VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES; // Load enough frames
            
            while ((frame = grabber.grabFrame()) != null && frameCount < maxFrames) {
                if (frame.image != null) {
                    Mat mat = converter.convert(frame);
                    if (mat != null && !mat.empty()) {
                        // Resize to target dimensions
                        Mat resized = new Mat();
                        resize(mat, resized, new Size(TARGET_WIDTH, TARGET_HEIGHT));
                        frames.add(resized);
                        frameCount++;
                        mat.release();
                    }
                }
            }
            
            System.out.println("  Loaded " + frames.size() + " frames from " + videoName);
            
        } catch (Exception e) {
            System.err.println("ERROR loading " + videoName + ": " + e.getMessage());
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
        
        return frames;
    }
    
    /**
     * Create crossfade transition sequence with real videos
     */
    private void createCrossfadeSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating CROSSFADE transition with real videos...");
        
        String outputDir = OUTPUT_DIR + "/crossfade";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double alpha = (double) i / (TRANSITION_FRAMES - 1);
            
            // Get frames for transition
            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);
            
            Mat result = new Mat();
            addWeighted(frame1, 1.0 - alpha, frame2, alpha, 0, result);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }
        
        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }
    
    /**
     * Create slide transition sequence with real videos
     */
    private void createSlideSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating SLIDE transition with real videos...");
        
        String outputDir = OUTPUT_DIR + "/slide";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);
            
            Mat result = applySlideTransition(frame1, frame2, progress);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }
        
        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }
    
    /**
     * Apply slide transition
     */
    private Mat applySlideTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat(TARGET_HEIGHT, TARGET_WIDTH, CV_8UC3);
        
        int slidePos = (int)(TARGET_WIDTH * progress);
        
        // Copy left part from frame1
        if (slidePos < TARGET_WIDTH) {
            Rect leftRoi = new Rect(slidePos, 0, TARGET_WIDTH - slidePos, TARGET_HEIGHT);
            Mat frame1Left = new Mat(frame1, leftRoi);
            Mat resultLeft = new Mat(result, leftRoi);
            frame1Left.copyTo(resultLeft);
        }
        
        // Copy right part from frame2
        if (slidePos > 0) {
            Rect rightRoi = new Rect(0, 0, slidePos, TARGET_HEIGHT);
            Mat frame2Right = new Mat(frame2, rightRoi);
            Mat resultRight = new Mat(result, rightRoi);
            frame2Right.copyTo(resultRight);
        }
        
        return result;
    }
    
    /**
     * Create zoom transition sequence with real videos
     */
    private void createZoomSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating ZOOM transition with real videos...");
        
        String outputDir = OUTPUT_DIR + "/zoom";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);
            
            Mat result = applyZoomTransition(frame1, frame2, progress);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }
        
        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }
    
    /**
     * Apply zoom transition
     */
    private Mat applyZoomTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);
        
        double scale = 0.1 + progress * 0.9;
        int newWidth = (int)(TARGET_WIDTH * scale);
        int newHeight = (int)(TARGET_HEIGHT * scale);
        
        if (newWidth > 0 && newHeight > 0) {
            Mat resized = new Mat();
            resize(frame2, resized, new Size(newWidth, newHeight));
            
            int x = (TARGET_WIDTH - newWidth) / 2;
            int y = (TARGET_HEIGHT - newHeight) / 2;
            
            if (x >= 0 && y >= 0 && x + newWidth <= TARGET_WIDTH && y + newHeight <= TARGET_HEIGHT) {
                Rect roi = new Rect(x, y, newWidth, newHeight);
                Mat resultRoi = new Mat(result, roi);
                resized.copyTo(resultRoi);
            }
            
            resized.release();
        }
        
        return result;
    }
    
    /**
     * Create wipe transition sequence with real videos
     */
    private void createWipeSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating WIPE transition with real videos...");
        
        String outputDir = OUTPUT_DIR + "/wipe";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);
            
            Mat result = applyWipeTransition(frame1, frame2, progress);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }
        
        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }
    
    /**
     * Apply wipe transition
     */
    private Mat applyWipeTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);
        
        int wipePos = (int)(TARGET_WIDTH * progress);
        
        if (wipePos > 0 && wipePos < TARGET_WIDTH) {
            Rect roi = new Rect(0, 0, wipePos, TARGET_HEIGHT);
            Mat frame2Roi = new Mat(frame2, roi);
            Mat resultRoi = new Mat(result, roi);
            frame2Roi.copyTo(resultRoi);
        }
        
        return result;
    }

    /**
     * Create circle transition sequence with real videos
     */
    private void createCircleSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating CIRCLE transition with real videos...");

        String outputDir = OUTPUT_DIR + "/circle";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);

            Mat result = applyCircleTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }

        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }

    /**
     * Apply circle transition
     */
    private Mat applyCircleTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);

        Mat mask = new Mat(TARGET_HEIGHT, TARGET_WIDTH, CV_8UC1, new Scalar(0));

        int centerX = TARGET_WIDTH / 2;
        int centerY = TARGET_HEIGHT / 2;
        int maxRadius = (int)Math.sqrt(centerX * centerX + centerY * centerY);
        int radius = (int)(maxRadius * progress);

        if (radius > 0) {
            circle(mask, new Point(centerX, centerY), radius, new Scalar(255), -1, 8, 0);
            frame2.copyTo(result, mask);
        }

        mask.release();
        return result;
    }

    /**
     * Create diagonal transition sequence with real videos
     */
    private void createDiagonalSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating DIAGONAL transition with real videos...");

        String outputDir = OUTPUT_DIR + "/diagonal";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);

            Mat result = applyDiagonalTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }

        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }

    /**
     * Apply diagonal transition
     */
    private Mat applyDiagonalTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);

        Mat mask = new Mat(TARGET_HEIGHT, TARGET_WIDTH, CV_8UC1, new Scalar(0));

        // Create diagonal wipe mask
        for (int y = 0; y < TARGET_HEIGHT; y++) {
            for (int x = 0; x < TARGET_WIDTH; x++) {
                if (x + y < (TARGET_WIDTH + TARGET_HEIGHT) * progress) {
                    mask.ptr(y, x).put((byte)255);
                }
            }
        }

        frame2.copyTo(result, mask);
        mask.release();
        return result;
    }

    /**
     * Create spiral transition sequence with real videos
     */
    private void createSpiralSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating SPIRAL transition with real videos...");

        String outputDir = OUTPUT_DIR + "/spiral";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);

            Mat result = applySpiralTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }

        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }

    /**
     * Apply spiral transition
     */
    private Mat applySpiralTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);

        Mat mask = new Mat(TARGET_HEIGHT, TARGET_WIDTH, CV_8UC1, new Scalar(0));

        int centerX = TARGET_WIDTH / 2;
        int centerY = TARGET_HEIGHT / 2;
        double maxAngle = 4 * Math.PI * progress; // 2 full rotations
        int maxRadius = (int)Math.sqrt(centerX * centerX + centerY * centerY);

        for (int y = 0; y < TARGET_HEIGHT; y++) {
            for (int x = 0; x < TARGET_WIDTH; x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                double angle = Math.atan2(dy, dx) + Math.PI;
                double radius = Math.sqrt(dx * dx + dy * dy);

                if (angle <= maxAngle && radius <= maxRadius * progress) {
                    mask.ptr(y, x).put((byte)255);
                }
            }
        }

        frame2.copyTo(result, mask);
        mask.release();
        return result;
    }

    /**
     * Create mosaic transition sequence with real videos
     */
    private void createMosaicSequence(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating MOSAIC transition with real videos...");

        String outputDir = OUTPUT_DIR + "/mosaic";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < Math.min(VIDEO_SEGMENT_FRAMES, video1Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video1Frames.get(i));
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat frame1 = getFrameAtIndex(video1Frames, VIDEO_SEGMENT_FRAMES - TRANSITION_FRAMES/2 + i);
            Mat frame2 = getFrameAtIndex(video2Frames, i);

            Mat result = applyMosaicTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = TRANSITION_FRAMES/2; i < Math.min(VIDEO_SEGMENT_FRAMES + TRANSITION_FRAMES/2, video2Frames.size()); i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, video2Frames.get(i));
            frameCount++;
        }

        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }

    /**
     * Apply mosaic transition
     */
    private Mat applyMosaicTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);

        int tileSize = 40;
        int tilesX = TARGET_WIDTH / tileSize;
        int tilesY = TARGET_HEIGHT / tileSize;
        int totalTiles = tilesX * tilesY;
        int tilesToShow = (int)(totalTiles * progress);

        for (int i = 0; i < tilesToShow; i++) {
            int tileIndex = (i * 7) % totalTiles; // Pseudo-random order
            int tileX = (tileIndex % tilesX) * tileSize;
            int tileY = (tileIndex / tilesX) * tileSize;

            if (tileX + tileSize <= TARGET_WIDTH && tileY + tileSize <= TARGET_HEIGHT) {
                Rect roi = new Rect(tileX, tileY, tileSize, tileSize);
                Mat frame2Roi = new Mat(frame2, roi);
                Mat resultRoi = new Mat(result, roi);
                frame2Roi.copyTo(resultRoi);
            }
        }

        return result;
    }

    /**
     * Get frame at index with bounds checking
     */
    private Mat getFrameAtIndex(List<Mat> frames, int index) {
        if (index < 0) return frames.get(0);
        if (index >= frames.size()) return frames.get(frames.size() - 1);
        return frames.get(index);
    }

    /**
     * Create conversion script for real videos
     */
    private void createVideoConversionScript() {
        try {
            String scriptContent = "@echo off\n" +
                "echo Converting REAL video transition sequences to videos...\n" +
                "echo.\n" +
                "\n" +
                "if not exist \"" + OUTPUT_DIR + "_final\" mkdir \"" + OUTPUT_DIR + "_final\"\n" +
                "\n" +
                "echo Converting crossfade transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/crossfade/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_crossfade_transition.mp4\"\n" +
                "\n" +
                "echo Converting slide transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/slide/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_slide_transition.mp4\"\n" +
                "\n" +
                "echo Converting zoom transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/zoom/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_zoom_transition.mp4\"\n" +
                "\n" +
                "echo Converting wipe transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/wipe/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_wipe_transition.mp4\"\n" +
                "\n" +
                "echo Converting circle transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/circle/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_circle_transition.mp4\"\n" +
                "\n" +
                "echo Converting diagonal transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/diagonal/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_diagonal_transition.mp4\"\n" +
                "\n" +
                "echo Converting spiral transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/spiral/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_spiral_transition.mp4\"\n" +
                "\n" +
                "echo Converting mosaic transition with real videos...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/mosaic/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_final/REAL_mosaic_transition.mp4\"\n" +
                "\n" +
                "echo.\n" +
                "echo All REAL transition videos created in " + OUTPUT_DIR + "_final folder!\n" +
                "echo These videos use your actual WhatsApp videos with transitions!\n" +
                "pause\n";

            java.nio.file.Files.write(java.nio.file.Paths.get("convert_real_videos.bat"),
                                    scriptContent.getBytes());

            System.out.println("Real video conversion script created: convert_real_videos.bat");

        } catch (Exception e) {
            System.err.println("Error creating conversion script: " + e.getMessage());
        }
    }

    /**
     * Cleanup frame list
     */
    private void cleanupFrames(List<Mat> frames) {
        for (Mat frame : frames) {
            if (frame != null && !frame.empty()) {
                frame.release();
            }
        }
    }
}
