import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Rotation transition effects
 */
public class RotateTransition extends BaseTransition {
    private TransitionType type;
    
    public RotateTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case ROTATE_CLOCKWISE:
                return rotateClockwise(frame1, frame2, progress);
            case ROTATE_COUNTERCLOCKWISE:
                return rotateCounterclockwise(frame1, frame2, progress);
            default:
                return rotateClockwise(frame1, frame2, progress);
        }
    }
    
    /**
     * Clockwise rotation transition
     */
    private Mat rotateClockwise(Mat frame1, Mat frame2, double progress) {
        double easedProgress = easeInOut(progress);
        
        // Rotate frame1 out (0 to 180 degrees)
        double angle1 = easedProgress * 180.0;
        Mat rotatedFrame1 = VideoProcessor.rotateFrame(frame1, angle1);
        
        // Rotate frame2 in (-180 to 0 degrees)
        double angle2 = -180.0 + (easedProgress * 180.0);
        Mat rotatedFrame2 = VideoProcessor.rotateFrame(frame2, angle2);
        
        // Blend based on progress
        if (progress < 0.5) {
            // First half: show rotating frame1
            double alpha = 1.0 - (progress * 2.0);
            return VideoProcessor.blendFrames(VideoProcessor.createBlankFrame(width, height), rotatedFrame1, alpha);
        } else {
            // Second half: show rotating frame2
            double alpha = (progress - 0.5) * 2.0;
            return VideoProcessor.blendFrames(VideoProcessor.createBlankFrame(width, height), rotatedFrame2, alpha);
        }
    }
    
    /**
     * Counterclockwise rotation transition
     */
    private Mat rotateCounterclockwise(Mat frame1, Mat frame2, double progress) {
        double easedProgress = easeInOut(progress);
        
        // Rotate frame1 out (0 to -180 degrees)
        double angle1 = -easedProgress * 180.0;
        Mat rotatedFrame1 = VideoProcessor.rotateFrame(frame1, angle1);
        
        // Rotate frame2 in (180 to 0 degrees)
        double angle2 = 180.0 - (easedProgress * 180.0);
        Mat rotatedFrame2 = VideoProcessor.rotateFrame(frame2, angle2);
        
        // Blend based on progress
        if (progress < 0.5) {
            // First half: show rotating frame1
            double alpha = 1.0 - (progress * 2.0);
            return VideoProcessor.blendFrames(VideoProcessor.createBlankFrame(width, height), rotatedFrame1, alpha);
        } else {
            // Second half: show rotating frame2
            double alpha = (progress - 0.5) * 2.0;
            return VideoProcessor.blendFrames(VideoProcessor.createBlankFrame(width, height), rotatedFrame2, alpha);
        }
    }
}
