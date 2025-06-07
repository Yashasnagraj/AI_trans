import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Special effect transitions (blur, pixelate, etc.)
 */
public class EffectTransition extends BaseTransition {
    private TransitionType type;
    
    public EffectTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case BLUR_TRANSITION:
                return blurTransition(frame1, frame2, progress);
            case PIXELATE_TRANSITION:
                return pixelateTransition(frame1, frame2, progress);
            default:
                return blurTransition(frame1, frame2, progress);
        }
    }
    
    /**
     * Enhanced blur transition with progressive blur and smooth blending
     */
    private Mat blurTransition(Mat frame1, Mat frame2, double progress) {
        double easedProgress = easeInOut(progress);
        
        // Calculate blur intensity using improved parabolic curve
        double blurIntensity = 4.0 * progress * (1.0 - progress);
        
        // Apply progressive blur with dynamic kernel sizing
        Mat blurredFrame1 = VideoProcessor.applyProgressiveBlur(frame1, blurIntensity);
        Mat blurredFrame2 = VideoProcessor.applyProgressiveBlur(frame2, blurIntensity);
        
        // Enhanced crossfade with smooth alpha progression
        return VideoProcessor.blendFramesSmooth(blurredFrame1, blurredFrame2, easedProgress);
    }
    
    /**
     * Pixelate transition - frames pixelate and crossfade
     */
    private Mat pixelateTransition(Mat frame1, Mat frame2, double progress) {
        double easedProgress = easeInOut(progress);
        
        // Calculate pixelation intensity (peaks at middle of transition)
        double pixelIntensity = 4.0 * progress * (1.0 - progress); // Parabolic curve
        int pixelSize = Math.max(1, (int)(pixelIntensity * 20));
        
        // Apply pixelation to both frames
        Mat pixelatedFrame1 = VideoProcessor.pixelateFrame(frame1, pixelSize);
        Mat pixelatedFrame2 = VideoProcessor.pixelateFrame(frame2, pixelSize);
        
        // Crossfade between pixelated frames
        return VideoProcessor.blendFrames(pixelatedFrame1, pixelatedFrame2, easedProgress);
    }
}
