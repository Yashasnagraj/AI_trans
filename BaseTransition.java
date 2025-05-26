import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Abstract base class for all video transitions
 */
public abstract class BaseTransition {
    protected int width;
    protected int height;
    protected int transitionFrames;
    
    public BaseTransition(int width, int height, int transitionFrames) {
        this.width = width;
        this.height = height;
        this.transitionFrames = transitionFrames;
    }
    
    /**
     * Apply transition effect between two frames
     * @param frame1 First frame (outgoing)
     * @param frame2 Second frame (incoming)
     * @param progress Transition progress (0.0 to 1.0)
     * @return Resulting frame with transition applied
     */
    public abstract Mat applyTransition(Mat frame1, Mat frame2, double progress);
    
    /**
     * Get the total number of frames for this transition
     */
    public int getTransitionFrames() {
        return transitionFrames;
    }
    
    /**
     * Calculate eased progress for smoother transitions
     */
    protected double easeInOut(double progress) {
        return progress * progress * (3.0 - 2.0 * progress);
    }
    
    /**
     * Calculate ease-in progress
     */
    protected double easeIn(double progress) {
        return progress * progress;
    }
    
    /**
     * Calculate ease-out progress
     */
    protected double easeOut(double progress) {
        return 1.0 - Math.pow(1.0 - progress, 2.0);
    }
}
