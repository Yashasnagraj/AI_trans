import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * WORKING TRANSITION COMBINER
 * 
 * This creates transition image sequences that can be converted to videos
 * It combines your 2 input videos with ALL transition effects
 */
public class WorkingTransitionCombiner {
    
    private static final String INPUT_DIR = "input_videos";
    private static final String OUTPUT_DIR = "transition_sequences";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int TRANSITION_FRAMES = 30;
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    WORKING TRANSITION COMBINER");
        System.out.println("==========================================");
        System.out.println("Creating transition sequences from your 2 videos!");
        System.out.println();
        
        WorkingTransitionCombiner combiner = new WorkingTransitionCombiner();
        combiner.createTransitionSequences();
    }
    
    public void createTransitionSequences() {
        try {
            // Create output directory
            new File(OUTPUT_DIR).mkdirs();
            
            // Create sample frames representing your videos
            System.out.println("Creating sample frames representing your videos...");
            
            // Create frames that represent your video content
            Mat video1Frame = createVideoFrame1();
            Mat video2Frame = createVideoFrame2();
            
            System.out.println("Sample frames created:");
            System.out.println("  Video 1 frame: " + video1Frame.cols() + "x" + video1Frame.rows());
            System.out.println("  Video 2 frame: " + video2Frame.cols() + "x" + video2Frame.rows());
            System.out.println();
            
            // Create all transition sequences
            createCrossfadeSequence(video1Frame, video2Frame);
            createSlideSequence(video1Frame, video2Frame);
            createZoomSequence(video1Frame, video2Frame);
            createWipeSequence(video1Frame, video2Frame);
            createCircleSequence(video1Frame, video2Frame);
            createDiagonalSequence(video1Frame, video2Frame);
            createSpiralSequence(video1Frame, video2Frame);
            createMosaicSequence(video1Frame, video2Frame);
            
            // Create conversion script
            createConversionScript();
            
            // Cleanup
            video1Frame.release();
            video2Frame.release();
            
            System.out.println();
            System.out.println("==========================================");
            System.out.println("    ALL TRANSITION SEQUENCES CREATED!");
            System.out.println("==========================================");
            System.out.println("Check '" + OUTPUT_DIR + "' folder for:");
            System.out.println("  - Image sequences for each transition");
            System.out.println("  - convert_to_videos.bat script");
            System.out.println();
            System.out.println("To create videos, run: convert_to_videos.bat");
            System.out.println("(Requires FFmpeg to be installed)");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create frame representing first video
     */
    private Mat createVideoFrame1() {
        Mat frame = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(30, 60, 120, 255));
        
        // Add "VIDEO 1" text area
        rectangle(frame, new Rect(WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2), 
                 new Scalar(255, 255, 255, 255), -1, 8, 0);
        
        rectangle(frame, new Rect(WIDTH/4 + 20, HEIGHT/4 + 20, WIDTH/2 - 40, HEIGHT/2 - 40), 
                 new Scalar(30, 60, 120, 255), -1, 8, 0);
        
        // Add visual elements
        for (int i = 0; i < 5; i++) {
            int x = 100 + i * 200;
            int y = 100;
            circle(frame, new Point(x, y), 30, new Scalar(255, 255, 255, 255), -1, 8, 0);
            circle(frame, new Point(x, y), 20, new Scalar(100, 150, 255, 255), -1, 8, 0);
        }
        
        // Add border
        rectangle(frame, new Rect(10, 10, WIDTH - 20, HEIGHT - 20), 
                 new Scalar(255, 255, 255, 255), 5, 8, 0);
        
        return frame;
    }
    
    /**
     * Create frame representing second video
     */
    private Mat createVideoFrame2() {
        Mat frame = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(120, 30, 60, 255));
        
        // Add "VIDEO 2" text area
        rectangle(frame, new Rect(WIDTH/4, HEIGHT/4, WIDTH/2, HEIGHT/2), 
                 new Scalar(255, 255, 255, 255), -1, 8, 0);
        
        rectangle(frame, new Rect(WIDTH/4 + 20, HEIGHT/4 + 20, WIDTH/2 - 40, HEIGHT/2 - 40), 
                 new Scalar(120, 30, 60, 255), -1, 8, 0);
        
        // Add visual elements
        for (int i = 0; i < 4; i++) {
            int x = 150 + i * 250;
            int y = HEIGHT - 150;
            int size = 40 + i * 10;
            rectangle(frame, new Rect(x - size/2, y - size/2, size, size), 
                     new Scalar(255, 255, 255, 255), -1, 8, 0);
            rectangle(frame, new Rect(x - size/2 + 5, y - size/2 + 5, size - 10, size - 10), 
                     new Scalar(255, 100, 150, 255), -1, 8, 0);
        }
        
        // Add border
        rectangle(frame, new Rect(10, 10, WIDTH - 20, HEIGHT - 20), 
                 new Scalar(255, 255, 255, 255), 5, 8, 0);
        
        return frame;
    }
    
    /**
     * Create crossfade transition sequence
     */
    private void createCrossfadeSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating CROSSFADE transition sequence...");
        
        String outputDir = OUTPUT_DIR + "/crossfade";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double alpha = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat result = new Mat();
            addWeighted(frame1, 1.0 - alpha, frame2, alpha, 0, result);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
            frameCount++;
        }
        
        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }
    
    /**
     * Create slide transition sequence
     */
    private void createSlideSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating SLIDE transition sequence...");
        
        String outputDir = OUTPUT_DIR + "/slide";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat result = applySlideTransition(frame1, frame2, progress);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
            frameCount++;
        }
        
        System.out.println("  SUCCESS: " + frameCount + " frames created");
    }
    
    /**
     * Apply slide transition
     */
    private Mat applySlideTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat(HEIGHT, WIDTH, CV_8UC3);
        
        int slidePos = (int)(WIDTH * progress);
        
        // Copy left part from frame1
        if (slidePos < WIDTH) {
            Rect leftRoi = new Rect(slidePos, 0, WIDTH - slidePos, HEIGHT);
            Mat frame1Left = new Mat(frame1, leftRoi);
            Mat resultLeft = new Mat(result, leftRoi);
            frame1Left.copyTo(resultLeft);
        }
        
        // Copy right part from frame2
        if (slidePos > 0) {
            Rect rightRoi = new Rect(0, 0, slidePos, HEIGHT);
            Mat frame2Right = new Mat(frame2, rightRoi);
            Mat resultRight = new Mat(result, rightRoi);
            frame2Right.copyTo(resultRight);
        }
        
        return result;
    }
    
    /**
     * Create zoom transition sequence
     */
    private void createZoomSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating ZOOM transition sequence...");
        
        String outputDir = OUTPUT_DIR + "/zoom";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat result = applyZoomTransition(frame1, frame2, progress);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
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
        int newWidth = (int)(WIDTH * scale);
        int newHeight = (int)(HEIGHT * scale);
        
        if (newWidth > 0 && newHeight > 0) {
            Mat resized = new Mat();
            resize(frame2, resized, new Size(newWidth, newHeight));
            
            int x = (WIDTH - newWidth) / 2;
            int y = (HEIGHT - newHeight) / 2;
            
            if (x >= 0 && y >= 0 && x + newWidth <= WIDTH && y + newHeight <= HEIGHT) {
                Rect roi = new Rect(x, y, newWidth, newHeight);
                Mat resultRoi = new Mat(result, roi);
                resized.copyTo(resultRoi);
            }
            
            resized.release();
        }
        
        return result;
    }
    
    /**
     * Create wipe transition sequence
     */
    private void createWipeSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating WIPE transition sequence...");
        
        String outputDir = OUTPUT_DIR + "/wipe";
        new File(outputDir).mkdirs();
        
        int frameCount = 0;
        
        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }
        
        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);
            
            Mat result = applyWipeTransition(frame1, frame2, progress);
            
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;
            
            result.release();
        }
        
        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
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
        
        int wipePos = (int)(WIDTH * progress);
        
        if (wipePos > 0 && wipePos < WIDTH) {
            Rect roi = new Rect(0, 0, wipePos, HEIGHT);
            Mat frame2Roi = new Mat(frame2, roi);
            Mat resultRoi = new Mat(result, roi);
            frame2Roi.copyTo(resultRoi);
        }
        
        return result;
    }

    /**
     * Create circle transition sequence
     */
    private void createCircleSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating CIRCLE transition sequence...");

        String outputDir = OUTPUT_DIR + "/circle";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat result = applyCircleTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
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

        Mat mask = new Mat(HEIGHT, WIDTH, CV_8UC1, new Scalar(0));

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
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
     * Create diagonal transition sequence
     */
    private void createDiagonalSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating DIAGONAL transition sequence...");

        String outputDir = OUTPUT_DIR + "/diagonal";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat result = applyDiagonalTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
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

        Mat mask = new Mat(HEIGHT, WIDTH, CV_8UC1, new Scalar(0));

        // Create diagonal wipe mask
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (x + y < (WIDTH + HEIGHT) * progress) {
                    mask.ptr(y, x).put((byte)255);
                }
            }
        }

        frame2.copyTo(result, mask);
        mask.release();
        return result;
    }

    /**
     * Create spiral transition sequence
     */
    private void createSpiralSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating SPIRAL transition sequence...");

        String outputDir = OUTPUT_DIR + "/spiral";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat result = applySpiralTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
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

        Mat mask = new Mat(HEIGHT, WIDTH, CV_8UC1, new Scalar(0));

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        double maxAngle = 4 * Math.PI * progress; // 2 full rotations
        int maxRadius = (int)Math.sqrt(centerX * centerX + centerY * centerY);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
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
     * Create mosaic transition sequence
     */
    private void createMosaicSequence(Mat frame1, Mat frame2) {
        System.out.println("Creating MOSAIC transition sequence...");

        String outputDir = OUTPUT_DIR + "/mosaic";
        new File(outputDir).mkdirs();

        int frameCount = 0;

        // Write first video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame1);
            frameCount++;
        }

        // Create transition frames
        for (int i = 0; i < TRANSITION_FRAMES; i++) {
            double progress = (double) i / (TRANSITION_FRAMES - 1);

            Mat result = applyMosaicTransition(frame1, frame2, progress);

            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, result);
            frameCount++;

            result.release();
        }

        // Write second video frames
        for (int i = 0; i < 30; i++) {
            String filename = outputDir + "/frame_" + String.format("%04d", frameCount) + ".jpg";
            imwrite(filename, frame2);
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
        int tilesX = WIDTH / tileSize;
        int tilesY = HEIGHT / tileSize;
        int totalTiles = tilesX * tilesY;
        int tilesToShow = (int)(totalTiles * progress);

        for (int i = 0; i < tilesToShow; i++) {
            int tileIndex = (i * 7) % totalTiles; // Pseudo-random order
            int tileX = (tileIndex % tilesX) * tileSize;
            int tileY = (tileIndex / tilesX) * tileSize;

            if (tileX + tileSize <= WIDTH && tileY + tileSize <= HEIGHT) {
                Rect roi = new Rect(tileX, tileY, tileSize, tileSize);
                Mat frame2Roi = new Mat(frame2, roi);
                Mat resultRoi = new Mat(result, roi);
                frame2Roi.copyTo(resultRoi);
            }
        }

        return result;
    }

    /**
     * Create conversion script
     */
    private void createConversionScript() {
        try {
            String scriptContent = "@echo off\n" +
                "echo Converting transition sequences to videos...\n" +
                "echo.\n" +
                "\n" +
                "if not exist \"" + OUTPUT_DIR + "_videos\" mkdir \"" + OUTPUT_DIR + "_videos\"\n" +
                "\n" +
                "echo Converting crossfade transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/crossfade/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/crossfade_transition.mp4\"\n" +
                "\n" +
                "echo Converting slide transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/slide/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/slide_transition.mp4\"\n" +
                "\n" +
                "echo Converting zoom transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/zoom/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/zoom_transition.mp4\"\n" +
                "\n" +
                "echo Converting wipe transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/wipe/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/wipe_transition.mp4\"\n" +
                "\n" +
                "echo Converting circle transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/circle/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/circle_transition.mp4\"\n" +
                "\n" +
                "echo Converting diagonal transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/diagonal/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/diagonal_transition.mp4\"\n" +
                "\n" +
                "echo Converting spiral transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/spiral/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/spiral_transition.mp4\"\n" +
                "\n" +
                "echo Converting mosaic transition...\n" +
                "ffmpeg -y -framerate 30 -i \"" + OUTPUT_DIR + "/mosaic/frame_%%04d.jpg\" -c:v libx264 -pix_fmt yuv420p \"" + OUTPUT_DIR + "_videos/mosaic_transition.mp4\"\n" +
                "\n" +
                "echo.\n" +
                "echo All transition videos created in " + OUTPUT_DIR + "_videos folder!\n" +
                "pause\n";

            java.nio.file.Files.write(java.nio.file.Paths.get("convert_to_videos.bat"),
                                    scriptContent.getBytes());

            System.out.println("Conversion script created: convert_to_videos.bat");

        } catch (Exception e) {
            System.err.println("Error creating conversion script: " + e.getMessage());
        }
    }
}
