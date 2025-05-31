import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

/**
 * Simple video transition demo that creates image sequences showing all effects
 * This works without FFmpeg by creating individual frame images
 */
public class SimpleVideoDemo {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int TRANSITION_FRAMES = 30;

    public static void main(String[] args) {
        SimpleVideoDemo demo = new SimpleVideoDemo();

        System.out.println("🎬 Creating Video Transition Demo");
        System.out.println("Generating image sequences for all transition effects...");
        System.out.println();

        // Create sample frames
        Mat frame1 = demo.createSampleFrame1();
        Mat frame2 = demo.createSampleFrame2();

        // Generate all transition effects
        demo.generateAllTransitions(frame1, frame2);

        // Clean up
        frame1.release();
        frame2.release();

        System.out.println();
        System.out.println("🎉 Demo completed! Check the generated image sequences.");
        System.out.println("💡 You can convert these to videos using:");
        System.out.println("   ffmpeg -framerate 30 -i transition_name/frame_%03d.jpg -c:v libx264 output.mp4");
    }

    /**
     * Create first sample frame (blue theme)
     */
    private Mat createSampleFrame1() {
        Mat frame = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(50, 100, 200, 255));

        // Add title
        rectangle(frame, new Rect(50, 50, WIDTH - 100, 100),
                 new Scalar(255, 255, 255, 255), -1, 8, 0);
        rectangle(frame, new Rect(55, 55, WIDTH - 110, 90),
                 new Scalar(50, 100, 200, 255), -1, 8, 0);

        // Add animated elements
        for (int i = 0; i < 5; i++) {
            int x = 200 + i * 200;
            int y = 300 + (int)(50 * Math.sin(i * 0.5));
            circle(frame, new Point(x, y), 40, new Scalar(255, 255, 255, 255), -1, 8, 0);
            circle(frame, new Point(x, y), 30, new Scalar(100, 150, 255, 255), -1, 8, 0);
        }

        // Add border
        rectangle(frame, new Rect(10, 10, WIDTH - 20, HEIGHT - 20),
                 new Scalar(255, 255, 255, 255), 5, 8, 0);

        return frame;
    }

    /**
     * Create second sample frame (red theme)
     */
    private Mat createSampleFrame2() {
        Mat frame = new Mat(HEIGHT, WIDTH, CV_8UC3, new Scalar(200, 50, 100, 255));

        // Add title
        rectangle(frame, new Rect(50, HEIGHT - 150, WIDTH - 100, 100),
                 new Scalar(255, 255, 255, 255), -1, 8, 0);
        rectangle(frame, new Rect(55, HEIGHT - 145, WIDTH - 110, 90),
                 new Scalar(200, 50, 100, 255), -1, 8, 0);

        // Add animated elements
        for (int i = 0; i < 4; i++) {
            int x = 150 + i * 250;
            int y = 200;
            int size = 60 + i * 10;
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
     * Generate all transition effects
     */
    private void generateAllTransitions(Mat frame1, Mat frame2) {
        // Define all transition types
        TransitionType[] transitions = {
            TransitionType.FADE_IN,
            TransitionType.FADE_OUT,
            TransitionType.SLIDE_LEFT,
            TransitionType.SLIDE_RIGHT,
            TransitionType.SLIDE_UP,
            TransitionType.SLIDE_DOWN,
            TransitionType.ZOOM_IN,
            TransitionType.ZOOM_OUT,
            TransitionType.ROTATE_CLOCKWISE,
            TransitionType.ROTATE_COUNTERCLOCKWISE,
            TransitionType.WIPE_LEFT,
            TransitionType.WIPE_RIGHT,
            TransitionType.WIPE_UP,
            TransitionType.WIPE_DOWN,
            TransitionType.BLUR_TRANSITION,
            TransitionType.PIXELATE_TRANSITION
        };

        // Generate each transition
        for (TransitionType transitionType : transitions) {
            try {
                generateTransitionSequence(frame1, frame2, transitionType);
                System.out.println("✅ Generated: " + transitionType);
            } catch (Exception e) {
                System.err.println("❌ Error generating " + transitionType + ": " + e.getMessage());
            }
        }

        // Generate AI-enhanced transitions
        generateAITransitions(frame1, frame2);
    }

    /**
     * Generate a single transition sequence
     */
    private void generateTransitionSequence(Mat frame1, Mat frame2, TransitionType transitionType) {
        String folderName = "transition_" + transitionType.toString().toLowerCase();
        createDirectory(folderName);

        try {
            // Create transition
            BaseTransition transition = createTransition(transitionType);

            // Generate frames
            for (int i = 0; i < TRANSITION_FRAMES; i++) {
                double progress = (double) i / (TRANSITION_FRAMES - 1);
                Mat transitionFrame = transition.applyTransition(frame1, frame2, progress);

                // Save frame
                String filename = folderName + "/frame_" + String.format("%03d", i) + ".jpg";
                imwrite(filename, transitionFrame);

                transitionFrame.release();
            }

        } catch (Exception e) {
            System.err.println("Error generating transition sequence: " + e.getMessage());
        }
    }

    /**
     * Generate AI-enhanced transitions
     */
    private void generateAITransitions(Mat frame1, Mat frame2) {
        System.out.println();
        System.out.println("Generating AI-enhanced transitions...");

        String[] presets = {"SMOOTH", "DYNAMIC", "CINEMATIC", "MAGIC"};
        TransitionType[] aiTransitions = {
            TransitionType.FADE_IN,
            TransitionType.SLIDE_LEFT,
            TransitionType.ZOOM_IN
        };

        for (String preset : presets) {
            for (TransitionType transitionType : aiTransitions) {
                try {
                    generateAITransitionSequence(frame1, frame2, transitionType, preset);
                    System.out.println("✅ Generated AI: " + preset + " " + transitionType);
                } catch (Exception e) {
                    System.err.println("❌ Error generating AI " + preset + " " + transitionType + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Generate AI-enhanced transition sequence
     */
    private void generateAITransitionSequence(Mat frame1, Mat frame2, TransitionType transitionType, String preset) {
        String folderName = "ai_" + preset.toLowerCase() + "_" + transitionType.toString().toLowerCase();
        createDirectory(folderName);

        try {
            // Load AI configuration
            TransitionConfig config = TransitionConfig.loadPreset(preset);

            // Create transition
            BaseTransition transition = createTransition(transitionType);

            // Calculate frame count based on AI config
            int frameCount = (int)(config.getTransitionDuration() * 30); // 30 fps

            // Generate frames with AI easing
            for (int i = 0; i < frameCount; i++) {
                double progress = (double) i / (frameCount - 1);
                double easedProgress = config.applyEasing(progress);

                Mat transitionFrame = transition.applyTransition(frame1, frame2, easedProgress);

                // Save frame
                String filename = folderName + "/frame_" + String.format("%03d", i) + ".jpg";
                imwrite(filename, transitionFrame);

                transitionFrame.release();
            }

        } catch (Exception e) {
            System.err.println("Error generating AI transition sequence: " + e.getMessage());
        }
    }

    /**
     * Create transition instance
     */
    private BaseTransition createTransition(TransitionType type) {
        switch (type) {
            case FADE_IN:
            case FADE_OUT:
                return new FadeTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, type);
            case SLIDE_LEFT:
            case SLIDE_RIGHT:
            case SLIDE_UP:
            case SLIDE_DOWN:
                return new SlideTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, type);
            case ZOOM_IN:
            case ZOOM_OUT:
                return new ZoomTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, type);
            case ROTATE_CLOCKWISE:
            case ROTATE_COUNTERCLOCKWISE:
                return new RotateTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, type);
            case WIPE_LEFT:
            case WIPE_RIGHT:
            case WIPE_UP:
            case WIPE_DOWN:
                return new WipeTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, type);
            case BLUR_TRANSITION:
            case PIXELATE_TRANSITION:
                return new EffectTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, type);
            default:
                return new FadeTransition(WIDTH, HEIGHT, TRANSITION_FRAMES, TransitionType.FADE_IN);
        }
    }

    /**
     * Create directory if it doesn't exist
     */
    private void createDirectory(String dirName) {
        try {
            java.io.File dir = new java.io.File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Error creating directory " + dirName + ": " + e.getMessage());
        }
    }

    /**
     * Create a comprehensive demo with all effects in one sequence
     */
    public void createComprehensiveDemo(Mat frame1, Mat frame2) {
        System.out.println("Creating comprehensive demo sequence...");

        String folderName = "comprehensive_demo";
        createDirectory(folderName);

        TransitionType[] allTransitions = {
            TransitionType.FADE_IN, TransitionType.SLIDE_LEFT, TransitionType.ZOOM_IN,
            TransitionType.ROTATE_CLOCKWISE, TransitionType.WIPE_LEFT, TransitionType.BLUR_TRANSITION
        };

        int frameCounter = 0;

        for (TransitionType transitionType : allTransitions) {
            try {
                BaseTransition transition = createTransition(transitionType);

                // Add 10 frames of first video
                for (int i = 0; i < 10; i++) {
                    String filename = folderName + "/frame_" + String.format("%04d", frameCounter++) + ".jpg";
                    imwrite(filename, frame1);
                }

                // Add transition frames
                for (int i = 0; i < TRANSITION_FRAMES; i++) {
                    double progress = (double) i / (TRANSITION_FRAMES - 1);
                    Mat transitionFrame = transition.applyTransition(frame1, frame2, progress);

                    String filename = folderName + "/frame_" + String.format("%04d", frameCounter++) + ".jpg";
                    imwrite(filename, transitionFrame);

                    transitionFrame.release();
                }

                // Add 10 frames of second video
                for (int i = 0; i < 10; i++) {
                    String filename = folderName + "/frame_" + String.format("%04d", frameCounter++) + ".jpg";
                    imwrite(filename, frame2);
                }

            } catch (Exception e) {
                System.err.println("Error in comprehensive demo: " + e.getMessage());
            }
        }

        System.out.println("✅ Comprehensive demo created with " + frameCounter + " frames");
        System.out.println("💡 Convert to video with:");
        System.out.println("   ffmpeg -framerate 30 -i " + folderName + "/frame_%04d.jpg -c:v libx264 comprehensive_demo.mp4");
    }
}
