import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.ffmpeg.global.avcodec;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main video transition engine for processing video files with various transition effects
 */
public class VideoTransitionEngine {
    private int outputWidth = 1280;
    private int outputHeight = 720;
    private double frameRate = 30.0;
    private int transitionFrames = 30; // 1 second at 30fps

    public VideoTransitionEngine() {}

    public VideoTransitionEngine(int width, int height, double frameRate, int transitionFrames) {
        this.outputWidth = width;
        this.outputHeight = height;
        this.frameRate = frameRate;
        this.transitionFrames = transitionFrames;
    }

    /**
     * Process multiple videos with transitions between them
     */
    public void processVideosWithTransitions(List<String> inputVideos, List<TransitionType> transitions,
                                           String outputPath) throws Exception {
        if (inputVideos.size() < 2) {
            throw new IllegalArgumentException("At least 2 input videos are required");
        }

        if (transitions.size() != inputVideos.size() - 1) {
            throw new IllegalArgumentException("Number of transitions must be one less than number of videos");
        }

        // Initialize output recorder
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, outputWidth, outputHeight);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(frameRate);
        recorder.setFormat("mp4");
        recorder.start();

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        try {
            for (int i = 0; i < inputVideos.size(); i++) {
                System.out.println("Processing video " + (i + 1) + "/" + inputVideos.size() + ": " + inputVideos.get(i));

                // Process current video
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputVideos.get(i));
                grabber.start();

                List<Mat> videoFrames = new ArrayList<>();
                Frame frame;

                // Read all frames from current video
                while ((frame = grabber.grabFrame()) != null) {
                    if (frame.image != null) {
                        Mat mat = converter.convert(frame);
                        if (mat != null && !mat.empty()) {
                            Mat resized = VideoProcessor.resizeFrame(mat, outputWidth, outputHeight);
                            videoFrames.add(resized);
                        }
                    }
                }
                grabber.stop();

                if (videoFrames.isEmpty()) {
                    System.out.println("Warning: No frames found in video " + inputVideos.get(i));
                    continue;
                }

                // Write video frames (except last few if there's a transition coming)
                int framesToWrite = videoFrames.size();
                if (i < inputVideos.size() - 1) {
                    framesToWrite = Math.max(1, videoFrames.size() - transitionFrames / 2);
                }

                for (int j = 0; j < framesToWrite; j++) {
                    Frame outputFrame = converter.convert(videoFrames.get(j));
                    recorder.record(outputFrame);
                }

                // Apply transition if not the last video
                if (i < inputVideos.size() - 1) {
                    System.out.println("Applying transition: " + transitions.get(i));
                    applyTransition(videoFrames, inputVideos.get(i + 1), transitions.get(i), recorder, converter);
                }
            }

        } finally {
            recorder.stop();
        }

        System.out.println("Video processing complete: " + outputPath);
    }

    /**
     * Apply transition between current video frames and next video
     */
    private void applyTransition(List<Mat> currentFrames, String nextVideoPath, TransitionType transitionType,
                               FFmpegFrameRecorder recorder, OpenCVFrameConverter.ToMat converter) throws Exception {

        // Get last frames from current video
        List<Mat> lastFrames = new ArrayList<>();
        int startIndex = Math.max(0, currentFrames.size() - transitionFrames / 2);
        for (int i = startIndex; i < currentFrames.size(); i++) {
            lastFrames.add(currentFrames.get(i));
        }

        // Get first frames from next video
        FFmpegFrameGrabber nextGrabber = new FFmpegFrameGrabber(nextVideoPath);
        nextGrabber.start();

        List<Mat> nextFrames = new ArrayList<>();
        Frame frame;
        int frameCount = 0;

        while ((frame = nextGrabber.grabFrame()) != null && frameCount < transitionFrames / 2) {
            if (frame.image != null) {
                Mat mat = converter.convert(frame);
                if (mat != null && !mat.empty()) {
                    Mat resized = VideoProcessor.resizeFrame(mat, outputWidth, outputHeight);
                    nextFrames.add(resized);
                    frameCount++;
                }
            }
        }
        nextGrabber.stop();

        // Create transition
        BaseTransition transition = createTransition(transitionType);

        // Generate transition frames
        int totalTransitionFrames = Math.min(transitionFrames, lastFrames.size() + nextFrames.size());

        for (int i = 0; i < totalTransitionFrames; i++) {
            double progress = (double) i / (totalTransitionFrames - 1);

            Mat frame1, frame2;

            // Determine which frames to use
            if (i < lastFrames.size()) {
                frame1 = lastFrames.get(i);
            } else {
                frame1 = lastFrames.get(lastFrames.size() - 1);
            }

            if (i < nextFrames.size()) {
                frame2 = nextFrames.get(i);
            } else if (!nextFrames.isEmpty()) {
                frame2 = nextFrames.get(nextFrames.size() - 1);
            } else {
                frame2 = frame1; // Fallback
            }

            Mat transitionFrame = transition.applyTransition(frame1, frame2, progress);
            Frame outputFrame = converter.convert(transitionFrame);
            recorder.record(outputFrame);
        }
    }

    /**
     * Create appropriate transition object based on type
     */
    private BaseTransition createTransition(TransitionType type) {
        switch (type) {
            case FADE_IN:
            case FADE_OUT:
            case CROSSFADE:
            case DISSOLVE:
                return new FadeTransition(outputWidth, outputHeight, transitionFrames, type);

            case SLIDE_LEFT:
            case SLIDE_RIGHT:
            case SLIDE_UP:
            case SLIDE_DOWN:
            case PUSH_LEFT:
            case PUSH_RIGHT:
                return new SlideTransition(outputWidth, outputHeight, transitionFrames, type);

            case WIPE_LEFT:
            case WIPE_RIGHT:
            case WIPE_UP:
            case WIPE_DOWN:
            case WIPE_CIRCLE:
            case IRIS_IN:
            case IRIS_OUT:
                return new WipeTransition(outputWidth, outputHeight, transitionFrames, type);

            case ZOOM_IN:
            case ZOOM_OUT:
                return new ZoomTransition(outputWidth, outputHeight, transitionFrames, type);

            case ROTATE_CLOCKWISE:
            case ROTATE_COUNTERCLOCKWISE:
                return new RotateTransition(outputWidth, outputHeight, transitionFrames, type);

            case BLUR_TRANSITION:
            case PIXELATE_TRANSITION:
                return new EffectTransition(outputWidth, outputHeight, transitionFrames, type);

            default:
                return new FadeTransition(outputWidth, outputHeight, transitionFrames, TransitionType.CROSSFADE);
        }
    }

    // Getters and setters
    public void setOutputDimensions(int width, int height) {
        this.outputWidth = width;
        this.outputHeight = height;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public void setTransitionFrames(int frames) {
        this.transitionFrames = frames;
    }
}
