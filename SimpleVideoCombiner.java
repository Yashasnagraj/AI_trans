import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * SIMPLE VIDEO COMBINER WITH TRANSITIONS
 * 
 * This will combine your 2 input videos with smooth transitions
 */
public class SimpleVideoCombiner {
    
    private static final String INPUT_DIR = "input_videos";
    private static final String OUTPUT_DIR = "transition_videos";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final double FPS = 30.0;
    private static final int TRANSITION_FRAMES = 30; // 1 second transition
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    SIMPLE VIDEO COMBINER WITH TRANSITIONS");
        System.out.println("==========================================");
        System.out.println("Combining your 2 videos with ALL transitions!");
        System.out.println();
        
        SimpleVideoCombiner combiner = new SimpleVideoCombiner();
        combiner.combineVideos();
    }
    
    public void combineVideos() {
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
            
            System.out.println("Found videos:");
            for (File video : videoFiles) {
                System.out.println("  - " + video.getName());
            }
            System.out.println();
            
            // Load videos
            String video1Path = videoFiles[0].getAbsolutePath();
            String video2Path = videoFiles[1].getAbsolutePath();
            
            System.out.println("Loading video frames...");
            List<Mat> video1Frames = loadVideo(video1Path, "Video 1");
            List<Mat> video2Frames = loadVideo(video2Path, "Video 2");
            
            if (video1Frames.isEmpty() || video2Frames.isEmpty()) {
                System.err.println("ERROR: Failed to load videos");
                return;
            }
            
            // Create different transition videos
            createCrossfadeVideo(video1Frames, video2Frames);
            createSlideVideo(video1Frames, video2Frames);
            createZoomVideo(video1Frames, video2Frames);
            createWipeVideo(video1Frames, video2Frames);
            createCircleVideo(video1Frames, video2Frames);
            
            // Cleanup
            cleanupFrames(video1Frames);
            cleanupFrames(video2Frames);
            
            System.out.println();
            System.out.println("==========================================");
            System.out.println("    ALL TRANSITION VIDEOS CREATED!");
            System.out.println("==========================================");
            System.out.println("Check '" + OUTPUT_DIR + "' folder for results!");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load video frames
     */
    private List<Mat> loadVideo(String videoPath, String name) {
        List<Mat> frames = new ArrayList<>();
        FFmpegFrameGrabber grabber = null;
        
        try {
            System.out.println("Loading " + name + "...");
            
            grabber = new FFmpegFrameGrabber(videoPath);
            grabber.start();
            
            OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
            Frame frame;
            int count = 0;
            int maxFrames = 120; // 4 seconds at 30fps
            
            while ((frame = grabber.grabFrame()) != null && count < maxFrames) {
                if (frame.image != null) {
                    Mat mat = converter.convert(frame);
                    if (mat != null && !mat.empty()) {
                        Mat resized = new Mat();
                        resize(mat, resized, new Size(WIDTH, HEIGHT));
                        frames.add(resized);
                        count++;
                        mat.release();
                    }
                }
            }
            
            System.out.println("  Loaded " + frames.size() + " frames");
            
        } catch (Exception e) {
            System.err.println("ERROR loading " + name + ": " + e.getMessage());
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
        
        return frames;
    }
    
    /**
     * Create crossfade transition video
     */
    private void createCrossfadeVideo(List<Mat> video1, List<Mat> video2) {
        System.out.println("Creating CROSSFADE transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/crossfade_transition.mp4";
            recorder = new FFmpegFrameRecorder(outputPath, WIDTH, HEIGHT);
            recorder.setVideoCodec(28); // H264
            recorder.setFrameRate(FPS);
            recorder.setFormat("mp4");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            recorder.start();
            
            int totalFrames = 0;
            
            // Write first video (minus transition overlap)
            int video1End = Math.max(1, video1.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1End; i++) {
                Frame videoFrame = converter.convert(video1.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }
            
            // Apply crossfade transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double alpha = (double) i / (TRANSITION_FRAMES - 1);
                
                Mat frame1 = getFrame(video1, video1End + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrame(video2, i);
                
                Mat result = new Mat();
                addWeighted(frame1, 1.0 - alpha, frame2, alpha, 0, result);
                
                Frame videoFrame = converter.convert(result);
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
                result.release();
            }
            
            // Write second video
            int video2Start = TRANSITION_FRAMES/2;
            for (int i = video2Start; i < video2.size(); i++) {
                Frame videoFrame = converter.convert(video2.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: crossfade_transition.mp4 (" + 
                                 (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: Crossfade video failed");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Create slide transition video
     */
    private void createSlideVideo(List<Mat> video1, List<Mat> video2) {
        System.out.println("Creating SLIDE transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/slide_transition.mp4";
            recorder = new FFmpegFrameRecorder(outputPath, WIDTH, HEIGHT);
            recorder.setVideoCodec(28);
            recorder.setFrameRate(FPS);
            recorder.setFormat("mp4");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            recorder.start();
            
            int totalFrames = 0;
            
            // Write first video
            int video1End = Math.max(1, video1.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1End; i++) {
                Frame videoFrame = converter.convert(video1.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }
            
            // Apply slide transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                
                Mat frame1 = getFrame(video1, video1End + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrame(video2, i);
                
                Mat result = applySlideTransition(frame1, frame2, progress);
                
                Frame videoFrame = converter.convert(result);
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
                result.release();
            }
            
            // Write second video
            int video2Start = TRANSITION_FRAMES/2;
            for (int i = video2Start; i < video2.size(); i++) {
                Frame videoFrame = converter.convert(video2.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: slide_transition.mp4 (" + 
                                 (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: Slide video failed");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Apply slide transition effect
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
     * Create zoom transition video
     */
    private void createZoomVideo(List<Mat> video1, List<Mat> video2) {
        System.out.println("Creating ZOOM transition video...");
        
        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        
        try {
            String outputPath = OUTPUT_DIR + "/zoom_transition.mp4";
            recorder = new FFmpegFrameRecorder(outputPath, WIDTH, HEIGHT);
            recorder.setVideoCodec(28);
            recorder.setFrameRate(FPS);
            recorder.setFormat("mp4");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            recorder.start();
            
            int totalFrames = 0;
            
            // Write first video
            int video1End = Math.max(1, video1.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1End; i++) {
                Frame videoFrame = converter.convert(video1.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }
            
            // Apply zoom transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                
                Mat frame1 = getFrame(video1, video1End + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrame(video2, i);
                
                Mat result = applyZoomTransition(frame1, frame2, progress);
                
                Frame videoFrame = converter.convert(result);
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
                result.release();
            }
            
            // Write second video
            int video2Start = TRANSITION_FRAMES/2;
            for (int i = video2Start; i < video2.size(); i++) {
                Frame videoFrame = converter.convert(video2.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }
            
            recorder.stop();
            
            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: zoom_transition.mp4 (" + 
                                 (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: Zoom video failed");
            }
            
        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Apply zoom transition effect
     */
    private Mat applyZoomTransition(Mat frame1, Mat frame2, double progress) {
        Mat result = new Mat();
        
        // Start with frame1 as base
        frame1.copyTo(result);
        
        // Calculate zoom parameters
        double scale = 0.1 + progress * 0.9; // Zoom from 10% to 100%
        int newWidth = (int)(WIDTH * scale);
        int newHeight = (int)(HEIGHT * scale);
        
        if (newWidth > 0 && newHeight > 0) {
            // Resize frame2
            Mat resized = new Mat();
            resize(frame2, resized, new Size(newWidth, newHeight));
            
            // Center the resized frame
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
     * Create wipe transition video
     */
    private void createWipeVideo(List<Mat> video1, List<Mat> video2) {
        System.out.println("Creating WIPE transition video...");

        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        try {
            String outputPath = OUTPUT_DIR + "/wipe_transition.mp4";
            recorder = new FFmpegFrameRecorder(outputPath, WIDTH, HEIGHT);
            recorder.setVideoCodec(28);
            recorder.setFrameRate(FPS);
            recorder.setFormat("mp4");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            recorder.start();

            int totalFrames = 0;

            // Write first video
            int video1End = Math.max(1, video1.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1End; i++) {
                Frame videoFrame = converter.convert(video1.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }

            // Apply wipe transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);

                Mat frame1 = getFrame(video1, video1End + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrame(video2, i);

                Mat result = applyWipeTransition(frame1, frame2, progress);

                Frame videoFrame = converter.convert(result);
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
                result.release();
            }

            // Write second video
            int video2Start = TRANSITION_FRAMES/2;
            for (int i = video2Start; i < video2.size(); i++) {
                Frame videoFrame = converter.convert(video2.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }

            recorder.stop();

            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: wipe_transition.mp4 (" +
                                 (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: Wipe video failed");
            }

        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Apply wipe transition effect
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
     * Create circle transition video
     */
    private void createCircleVideo(List<Mat> video1, List<Mat> video2) {
        System.out.println("Creating CIRCLE transition video...");

        FFmpegFrameRecorder recorder = null;
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        try {
            String outputPath = OUTPUT_DIR + "/circle_transition.mp4";
            recorder = new FFmpegFrameRecorder(outputPath, WIDTH, HEIGHT);
            recorder.setVideoCodec(28);
            recorder.setFrameRate(FPS);
            recorder.setFormat("mp4");
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("crf", "23");
            recorder.start();

            int totalFrames = 0;

            // Write first video
            int video1End = Math.max(1, video1.size() - TRANSITION_FRAMES/2);
            for (int i = 0; i < video1End; i++) {
                Frame videoFrame = converter.convert(video1.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }

            // Apply circle transition
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);

                Mat frame1 = getFrame(video1, video1End + i - TRANSITION_FRAMES/2);
                Mat frame2 = getFrame(video2, i);

                Mat result = applyCircleTransition(frame1, frame2, progress);

                Frame videoFrame = converter.convert(result);
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
                result.release();
            }

            // Write second video
            int video2Start = TRANSITION_FRAMES/2;
            for (int i = video2Start; i < video2.size(); i++) {
                Frame videoFrame = converter.convert(video2.get(i));
                if (videoFrame != null) {
                    recorder.record(videoFrame);
                    totalFrames++;
                }
            }

            recorder.stop();

            File outputFile = new File(outputPath);
            if (outputFile.exists() && outputFile.length() > 10000) {
                System.out.println("  SUCCESS: circle_transition.mp4 (" +
                                 (outputFile.length()/1024) + " KB, " + totalFrames + " frames)");
            } else {
                System.out.println("  ERROR: Circle video failed");
            }

        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
        } finally {
            if (recorder != null) {
                try {
                    recorder.stop();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Apply circle transition effect
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
     * Get frame with bounds checking
     */
    private Mat getFrame(List<Mat> frames, int index) {
        if (index < 0) return frames.get(0);
        if (index >= frames.size()) return frames.get(frames.size() - 1);
        return frames.get(index);
    }

    /**
     * Cleanup frames
     */
    private void cleanupFrames(List<Mat> frames) {
        for (Mat frame : frames) {
            if (frame != null && !frame.empty()) {
                frame.release();
            }
        }
    }
}
