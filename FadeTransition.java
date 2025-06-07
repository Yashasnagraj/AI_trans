import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 * Fade transition effects (fade in, fade out, crossfade)
 */
public class FadeTransition extends BaseTransition {
    private TransitionType type;
    
    public FadeTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case FADE_IN:
                return fadeIn(frame2, progress);
            case FADE_OUT:
                return fadeOut(frame1, progress);
            case CROSSFADE:
                return crossfade(frame1, frame2, progress);
            case DISSOLVE:
                return dissolve(frame1, frame2, progress);
            default:
                return crossfade(frame1, frame2, progress);
        }
    }
    
    /**
     * Fade in effect - frame gradually appears from black
     */
    private Mat fadeIn(Mat frame, double progress) {
        Mat black = VideoProcessor.createBlankFrame(width, height);
        double alpha = easeInOut(progress);
        return VideoProcessor.blendFrames(black, frame, alpha);
    }
    
    /**
     * Fade out effect - frame gradually disappears to black
     */
    private Mat fadeOut(Mat frame, double progress) {
        Mat black = VideoProcessor.createBlankFrame(width, height);
        double alpha = 1.0 - easeInOut(progress);
        return VideoProcessor.blendFrames(black, frame, alpha);
    }
    
    /**
     * Crossfade effect - smooth blend between two frames with enhanced blending
     */
    private Mat crossfade(Mat frame1, Mat frame2, double progress) {
        double alpha = easeInOut(progress);
        return VideoProcessor.blendFramesSmooth(frame1, frame2, alpha);
    }
    
    /**
     * Dissolve effect - enhanced with smooth cosine interpolation
     */
    private Mat dissolve(Mat frame1, Mat frame2, double progress) {
        // Apply smooth cosine interpolation for natural-looking fades
        double smoothProgress = (1 - Math.cos(progress * Math.PI)) / 2;
        return VideoProcessor.blendFramesSmooth(frame1, frame2, progress);
    }
}
