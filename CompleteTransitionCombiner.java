import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * COMPLETE TRANSITION COMBINER
 * 
 * This application will properly combine your 2 input videos with ALL transition effects:
 * - Crossfade, Slide, Zoom, Wipe, Circle, and more
 * - Creates separate videos for each transition type
 * - Combines actual video frames with smooth transitions
 */
public class CompleteTransitionCombiner {
    
    private static final String INPUT_DIR = "input_videos";
    private static final String OUTPUT_DIR = "transition_videos";
    private static final int TARGET_WIDTH = 1280;
    private static final int TARGET_HEIGHT = 720;
    private static final double FRAME_RATE = 30.0;
    private static final int TRANSITION_FRAMES = 45; // 1.5 seconds at 30fps
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("    COMPLETE VIDEO TRANSITION COMBINER");
        System.out.println("==============================================");
        System.out.println("Combining your 2 videos with ALL transitions!");
        System.out.println();
        
        CompleteTransitionCombiner combiner = new CompleteTransitionCombiner();
        combiner.combineVideosWithAllTransitions();
    }
    
    public void combineVideosWithAllTransitions() {
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
            
            System.out.println("Found " + videoFiles.length + " input videos:");
            for (File video : videoFiles) {
                System.out.println("  - " + video.getName() + " (" + (video.length()/1024) + " KB)");
            }
            System.out.println();
            
            // Load the first 2 videos
            String video1Path = videoFiles[0].getAbsolutePath();
            String video2Path = videoFiles[1].getAbsolutePath();
            
            System.out.println("Loading videos...");
            List<Mat> video1Frames = loadVideoFrames(video1Path, "Video 1");
            List<Mat> video2Frames = loadVideoFrames(video2Path, "Video 2");
            
            if (video1Frames.isEmpty() || video2Frames.isEmpty()) {
                System.err.println("ERROR: Failed to load video frames");
                return;
            }
            
            // Create videos with different transitions
            createTransitionVideo(video1Frames, video2Frames, TransitionType.CROSSFADE, "crossfade");
            createTransitionVideo(video1Frames, video2Frames, TransitionType.SLIDE_LEFT, "slide_left");
            createTransitionVideo(video1Frames, video2Frames, TransitionType.SLIDE_RIGHT, "slide_right");
            createTransitionVideo(video1Frames, video2Frames, TransitionType.ZOOM_IN, "zoom_in");
            createTransitionVideo(video1Frames, video2Frames, TransitionType.ZOOM_OUT, "zoom_out");
            createWipeTransitionVideo(video1Frames, video2Frames, "wipe_horizontal");
            createCircleTransitionVideo(video1Frames, video2Frames, "circle_expand");
            createDiagonalTransitionVideo(video1Frames, video2Frames, "diagonal_wipe");
            
            // Create a master video with all transitions
            createMasterTransitionVideo(video1Frames, video2Frames);
            
            // Cleanup
            cleanupFrames(video1Frames);
            cleanupFrames(video2Frames);
            
            System.out.println();
            System.out.println("==============================================");
            System.out.println("    ALL TRANSITION VIDEOS CREATED!");
            System.out.println("==============================================");
            System.out.println("Check the '" + OUTPUT_DIR + "' folder for:");
            System.out.println("  - Individual transition videos");
            System.out.println("  - Master video with all transitions");
            System.out.println("==============================================");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load video frames from file
     */
    private List<Mat> loadVideoFrames(String videoPath, String videoName) {
        List<Mat> frames = new ArrayList<>();
        FFmpegFrameGrabber grabber = null;
        
        try {
            System.out.println("Loading " + videoName + ": " + new File(videoPath).getName());
            
            grabber = new FFmpegFrameGrabber(videoPath);
            grabber.start();
            
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            Frame frame;
            int frameCount = 0;
            int maxFrames = 150; // Limit to ~5 seconds at 30fps
            
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
     * Create transition video using BaseTransition classes
     */
    private void createTransitionVideo(List<Mat> video1Frames, List<Mat> video2Frames, 
                                     TransitionType transitionType, String outputName) {
        System.out.println("Creating " + outputName + " transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/" + outputName + "_transition.mp4";
            recorder = setupRecorder(outputPath);
            
            BaseTransition transition = createTransition(transitionType);
            int totalFrames = 0;
            
            // Write first video frames (minus transition overlap)
            int video1EndFrame = Math.max(1, video1Frames.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1EndFrame; i++) {
                recordFrame(recorder, converter, video1Frames.get(i));
                totalFrames++;
            }
            
            // Apply transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                
                // Get frames for transition
                Mat frame1 = getFrameAtIndex(video1Frames, video1EndFrame + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrameAtIndex(video2Frames, i);
                
                Mat transitionFrame = transition.applyTransition(frame1, frame2, progress);
                
                if (!transitionFrame.empty()) {
                    recordFrame(recorder, converter, transitionFrame);
                    totalFrames++;
                    transitionFrame.release();
                }
            }
            
            // Write remaining second video frames
            int video2StartFrame = TRANSITION_FRAMES/2;
            for (int i = video2StartFrame; i < video2Frames.size(); i++) {
                recordFrame(recorder, converter, video2Frames.get(i));
                totalFrames++;
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: " + outputName + " (" + (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: " + outputName + " failed or too small");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR creating " + outputName + ": " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
    }
    
    /**
     * Create custom wipe transition
     */
    private void createWipeTransitionVideo(List<Mat> video1Frames, List<Mat> video2Frames, String outputName) {
        System.out.println("Creating " + outputName + " transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/" + outputName + "_transition.mp4";
            recorder = setupRecorder(outputPath);
            
            int totalFrames = 0;
            
            // Write first video
            int video1EndFrame = Math.max(1, video1Frames.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1EndFrame; i++) {
                recordFrame(recorder, converter, video1Frames.get(i));
                totalFrames++;
            }
            
            // Apply wipe transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                
                Mat frame1 = getFrameAtIndex(video1Frames, video1EndFrame + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrameAtIndex(video2Frames, i);
                
                Mat result = applyWipeTransition(frame1, frame2, progress);
                
                if (!result.empty()) {
                    recordFrame(recorder, converter, result);
                    totalFrames++;
                    result.release();
                }
            }
            
            // Write second video
            int video2StartFrame = TRANSITION_FRAMES/2;
            for (int i = video2StartFrame; i < video2Frames.size(); i++) {
                recordFrame(recorder, converter, video2Frames.get(i));
                totalFrames++;
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: " + outputName + " (" + (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: " + outputName + " failed");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR creating " + outputName + ": " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
    }
    
    /**
     * Apply horizontal wipe transition
     */
    private Mat applyWipeTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);
        
        int wipePosition = (int)(TARGET_WIDTH * progress);
        
        if (wipePosition > 0 && wipePosition < TARGET_WIDTH) {
            Rect roi = new Rect(0, 0, wipePosition, TARGET_HEIGHT);
            Mat frame2Roi = new Mat(frame2, roi);
            Mat resultRoi = new Mat(result, roi);
            frame2Roi.copyTo(resultRoi);
        }
        
        return result;
    }
    
    /**
     * Create circle expand transition
     */
    private void createCircleTransitionVideo(List<Mat> video1Frames, List<Mat> video2Frames, String outputName) {
        System.out.println("Creating " + outputName + " transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/" + outputName + "_transition.mp4";
            recorder = setupRecorder(outputPath);
            
            int totalFrames = 0;
            
            // Write first video
            int video1EndFrame = Math.max(1, video1Frames.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1EndFrame; i++) {
                recordFrame(recorder, converter, video1Frames.get(i));
                totalFrames++;
            }
            
            // Apply circle transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                
                Mat frame1 = getFrameAtIndex(video1Frames, video1EndFrame + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrameAtIndex(video2Frames, i);
                
                Mat result = applyCircleTransition(frame1, frame2, progress);
                
                if (!result.empty()) {
                    recordFrame(recorder, converter, result);
                    totalFrames++;
                    result.release();
                }
            }
            
            // Write second video
            int video2StartFrame = TRANSITION_FRAMES/2;
            for (int i = video2StartFrame; i < video2Frames.size(); i++) {
                recordFrame(recorder, converter, video2Frames.get(i));
                totalFrames++;
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: " + outputName + " (" + (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: " + outputName + " failed");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR creating " + outputName + ": " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
    }
    
    /**
     * Apply circle expand transition
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
     * Create diagonal wipe transition
     */
    private void createDiagonalTransitionVideo(List<Mat> video1Frames, List<Mat> video2Frames, String outputName) {
        System.out.println("Creating " + outputName + " transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/" + outputName + "_transition.mp4";
            recorder = setupRecorder(outputPath);
            
            int totalFrames = 0;
            
            // Write first video
            int video1EndFrame = Math.max(1, video1Frames.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1EndFrame; i++) {
                recordFrame(recorder, converter, video1Frames.get(i));
                totalFrames++;
            }
            
            // Apply diagonal transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                
                Mat frame1 = getFrameAtIndex(video1Frames, video1EndFrame + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrameAtIndex(video2Frames, i);
                
                Mat result = applyDiagonalTransition(frame1, frame2, progress);
                
                if (!result.empty()) {
                    recordFrame(recorder, converter, result);
                    totalFrames++;
                    result.release();
                }
            }
            
            // Write second video
            int video2StartFrame = TRANSITION_FRAMES/2;
            for (int i = video2StartFrame; i < video2Frames.size(); i++) {
                recordFrame(recorder, converter, video2Frames.get(i));
                totalFrames++;
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: " + outputName + " (" + (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: " + outputName + " failed");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR creating " + outputName + ": " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
    }
    
    /**
     * Apply diagonal wipe transition
     */
    private Mat applyDiagonalTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        frame1.copyTo(result);
        
        Mat mask = new Mat(TARGET_HEIGHT, TARGET_WIDTH, CV_8UC1, new Scalar(0));
        
        // Create diagonal wipe mask
        Point[] points = new Point[3];
        points[0] = new Point(0, 0);
        points[1] = new Point(TARGET_WIDTH * progress, 0);
        points[2] = new Point(0, TARGET_HEIGHT * progress);
        
        if (progress > 0) {
            // Fill triangle
            for (int y = 0; y < TARGET_HEIGHT; y++) {
                for (int x = 0; x < TARGET_WIDTH; x++) {
                    if (x + y < (TARGET_WIDTH + TARGET_HEIGHT) * progress) {
                        mask.ptr(y, x).put((byte)255);
                    }
                }
            }
            
            frame2.copyTo(result, mask);
        }
        
        mask.release();
        return result;
    }

    /**
     * Create master video with all transitions
     */
    private void createMasterTransitionVideo(List<Mat> video1Frames, List<Mat> video2Frames) {
        System.out.println("Creating MASTER video with all transitions...");

        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        try {
            String outputPath = OUTPUT_DIR + "/MASTER_all_transitions.mp4";
            recorder = setupRecorder(outputPath);

            int totalFrames = 0;

            // Array of all transition types
            TransitionType[] transitions = {
                TransitionType.CROSSFADE,
                TransitionType.SLIDE_LEFT,
                TransitionType.ZOOM_IN
            };

            String[] customTransitions = {
                "wipe", "circle", "diagonal"
            };

            // For each transition, show: video1 -> transition -> video2 -> transition -> video1...
            for (int t = 0; t < transitions.length + customTransitions.length; t++) {

                // Write first video segment
                int segmentFrames = Math.min(60, video1Frames.size()); // 2 seconds
                for (int i = 0; i < segmentFrames; i++) {
                    recordFrame(recorder, converter, video1Frames.get(i));
                    totalFrames++;
                }

                // Apply transition
                for (int i = 0; i < TRANSITION_FRAMES; i++) {
                    double progress = (double) i / (TRANSITION_FRAMES - 1);

                    Mat frame1 = getFrameAtIndex(video1Frames, segmentFrames - TRANSITION_FRAMES/2 + i);
                    Mat frame2 = getFrameAtIndex(video2Frames, i);
                    Mat transitionFrame = null;

                    if (t < transitions.length) {
                        // Use BaseTransition
                        BaseTransition transition = createTransition(transitions[t]);
                        transitionFrame = transition.applyTransition(frame1, frame2, progress);
                    } else {
                        // Use custom transitions
                        int customIndex = t - transitions.length;
                        switch (customIndex) {
                            case 0: // wipe
                                transitionFrame = applyWipeTransition(frame1, frame2, progress);
                                break;
                            case 1: // circle
                                transitionFrame = applyCircleTransition(frame1, frame2, progress);
                                break;
                            case 2: // diagonal
                                transitionFrame = applyDiagonalTransition(frame1, frame2, progress);
                                break;
                        }
                    }

                    if (transitionFrame != null && !transitionFrame.empty()) {
                        recordFrame(recorder, converter, transitionFrame);
                        totalFrames++;
                        transitionFrame.release();
                    }
                }

                // Write second video segment
                for (int i = 0; i < segmentFrames; i++) {
                    recordFrame(recorder, converter, video2Frames.get(i));
                    totalFrames++;
                }
            }

            recorder.stop();

            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: MASTER video (" + (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: MASTER video failed");
            }

        } catch (Exception e) {
            System.err.println("  ERROR creating MASTER video: " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore cleanup errors
                }
            }
        }
    }

    /**
     * Setup video recorder
     */
    private FFmpegFrameRecorder setupRecorder(String outputPath) throws Exception {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, TARGET_WIDTH, TARGET_HEIGHT);
        recorder.setVideoCodec(28); // H264
        recorder.setFrameRate(FRAME_RATE);
        recorder.setFormat("mp4");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "23");
        recorder.start();
        return recorder;
    }

    /**
     * Record a frame to video
     */
    private void recordFrame(FFmpegFrameRecorder recorder, OpenCVFrameConverter.ToMat converter, Mat frame) {
        try {
            if (!frame.empty()) {
                Frame videoFrame = converter.convert(frame);
                if (videoFrame != null && videoFrame.image != null) {
                    recorder.record(videoFrame);
                }
            }
        } catch (Exception e) {
            System.err.println("Error recording frame: " + e.getMessage());
        }
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
     * Create transition instance
     */
    private BaseTransition createTransition(TransitionType type) {
        switch (type) {
            case CROSSFADE:
                return new FadeTransition(TARGET_WIDTH, TARGET_HEIGHT, TRANSITION_FRAMES, type);
            case SLIDE_LEFT:
                return new SlideTransition(TARGET_WIDTH, TARGET_HEIGHT, TRANSITION_FRAMES, type);
            case SLIDE_RIGHT:
                return new SlideTransition(TARGET_WIDTH, TARGET_HEIGHT, TRANSITION_FRAMES, type);
            case ZOOM_IN:
                return new ZoomTransition(TARGET_WIDTH, TARGET_HEIGHT, TRANSITION_FRAMES, type);
            case ZOOM_OUT:
                return new ZoomTransition(TARGET_WIDTH, TARGET_HEIGHT, TRANSITION_FRAMES, type);
            default:
                return new FadeTransition(TARGET_WIDTH, TARGET_HEIGHT, TRANSITION_FRAMES, TransitionType.CROSSFADE);
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
