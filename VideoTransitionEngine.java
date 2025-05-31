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

    // AI-powered features
    private String aiModelPath = null;
    private TransitionConfig defaultConfig = TransitionConfig.loadPreset("SMOOTH");
    private boolean enableAIFeatures = false;

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
            // Pre-load all videos to ensure smooth transitions
            System.out.println("Pre-loading all videos for smooth transitions...");
            List<List<Mat>> allVideoFrames = new ArrayList<>();
            
            for (int i = 0; i < inputVideos.size(); i++) {
                String videoPath = inputVideos.get(i);
                System.out.println("Loading video " + (i + 1) + "/" + inputVideos.size() + ": " + videoPath);
                
                FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
                grabber.start();
                
                List<Mat> frames = new ArrayList<>();
                Frame frame;
                
                while ((frame = grabber.grabFrame()) != null) {
                    if (frame.image != null) {
                        Mat mat = converter.convert(frame);
                        if (mat != null && !mat.empty()) {
                            Mat resized = VideoProcessor.resizeFrame(mat, outputWidth, outputHeight);
                            frames.add(resized);
                        }
                    }
                }
                
                grabber.stop();
                
                if (frames.isEmpty()) {
                    System.out.println("Warning: No frames found in video " + videoPath);
                    // Add a blank frame to prevent errors
                    frames.add(VideoProcessor.createBlankFrame(outputWidth, outputHeight));
                }
                
                allVideoFrames.add(frames);
                System.out.println("Loaded " + frames.size() + " frames from video " + (i + 1));
            }
            
            // Process each video and apply transitions
            for (int i = 0; i < allVideoFrames.size(); i++) {
                List<Mat> currentVideoFrames = allVideoFrames.get(i);
                System.out.println("Processing video " + (i + 1) + "/" + inputVideos.size());
                
                // Determine how many frames to write before transition
                int framesToWrite = currentVideoFrames.size();
                if (i < inputVideos.size() - 1) {
                    framesToWrite = Math.max(1, currentVideoFrames.size() - transitionFrames / 2);
                }
                
                // Write main video frames
                for (int j = 0; j < framesToWrite; j++) {
                    Frame outputFrame = converter.convert(currentVideoFrames.get(j));
                    recorder.record(outputFrame);
                }
                
                // Apply transition if not the last video
                if (i < inputVideos.size() - 1) {
                    List<Mat> nextVideoFrames = allVideoFrames.get(i + 1);
                    System.out.println("Applying transition: " + transitions.get(i) + " between video " + 
                                      (i + 1) + " and video " + (i + 2));
                    
                    // Apply transition between current and next video
                    applyTransitionBetweenVideos(currentVideoFrames, nextVideoFrames, 
                                              transitions.get(i), recorder, converter);
                }
            }
        } finally {
            recorder.stop();
        }

        System.out.println("Video processing complete: " + outputPath);
    }
    
    /**
     * Apply transition between two videos using their pre-loaded frames
     */
    private void applyTransitionBetweenVideos(List<Mat> currentVideoFrames, List<Mat> nextVideoFrames,
                                           TransitionType transitionType, FFmpegFrameRecorder recorder,
                                           OpenCVFrameConverter.ToMat converter) throws Exception {
        
        // Get last frames from current video
        List<Mat> lastFrames = new ArrayList<>();
        int startIndex = Math.max(0, currentVideoFrames.size() - transitionFrames / 2);
        for (int i = startIndex; i < currentVideoFrames.size(); i++) {
            lastFrames.add(currentVideoFrames.get(i));
        }
        
        // Get first frames from next video
        List<Mat> firstFrames = new ArrayList<>();
        int endIndex = Math.min(nextVideoFrames.size(), transitionFrames / 2);
        for (int i = 0; i < endIndex; i++) {
            firstFrames.add(nextVideoFrames.get(i));
        }
        
        // Create transition
        BaseTransition transition = createTransition(transitionType);
        
        // Generate transition frames
        int totalTransitionFrames = Math.min(transitionFrames, 
                                          lastFrames.size() + firstFrames.size());
        
        for (int i = 0; i < totalTransitionFrames; i++) {
            double progress = (double) i / (totalTransitionFrames - 1);
            
            Mat frame1, frame2;
            
            // Determine which frames to use
            if (i < lastFrames.size()) {
                frame1 = lastFrames.get(i);
            } else {
                frame1 = lastFrames.get(lastFrames.size() - 1);
            }
            
            if (i < firstFrames.size()) {
                frame2 = firstFrames.get(i);
            } else if (!firstFrames.isEmpty()) {
                frame2 = firstFrames.get(firstFrames.size() - 1);
            } else {
                frame2 = frame1; // Fallback
            }
            
            Mat transitionFrame = transition.applyTransition(frame1, frame2, progress);
            Frame outputFrame = converter.convert(transitionFrame);
            recorder.record(outputFrame);
        }
    }

    // The applyTransition method has been replaced by applyTransitionBetweenVideos
    // which works with pre-loaded frames for better performance and smoother transitions

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

            // AI-Powered Object-Aware Transitions
            case OBJECT_REVEAL:
            case OBJECT_ZOOM_IN:
            case OBJECT_ZOOM_OUT:
            case OBJECT_SLIDE_LEFT:
            case OBJECT_SLIDE_RIGHT:
            case OBJECT_FADE_IN:
            case OBJECT_FADE_OUT:
            case OBJECT_ROTATE_IN:
            case OBJECT_ROTATE_OUT:
            case OBJECT_SCALE_TRANSITION:
                ObjectRevealTransition objectTransition = new ObjectRevealTransition(outputWidth, outputHeight, transitionFrames, type, defaultConfig);
                if (enableAIFeatures && aiModelPath != null) {
                    objectTransition.initializeMaskGenerator(aiModelPath);
                }
                return objectTransition;

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

    // AI-powered features configuration
    public void enableAIFeatures(String modelPath) {
        this.aiModelPath = modelPath;
        this.enableAIFeatures = true;
        System.out.println("AI features enabled with model: " + modelPath);
    }

    public void disableAIFeatures() {
        this.enableAIFeatures = false;
        this.aiModelPath = null;
        System.out.println("AI features disabled");
    }

    public void setDefaultTransitionConfig(TransitionConfig config) {
        this.defaultConfig = config;
    }

    public void setDefaultTransitionConfig(String presetName) {
        this.defaultConfig = TransitionConfig.loadPreset(presetName);
    }

    public TransitionConfig getDefaultTransitionConfig() {
        return defaultConfig;
    }

    public boolean isAIFeaturesEnabled() {
        return enableAIFeatures;
    }

    public String getAIModelPath() {
        return aiModelPath;
    }
}
