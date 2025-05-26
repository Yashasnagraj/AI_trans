import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Zoom transition effects (zoom in, zoom out)
 */
public class ZoomTransition extends BaseTransition {
    private TransitionType type;
    
    public ZoomTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case ZOOM_IN:
                return zoomIn(frame1, frame2, progress);
            case ZOOM_OUT:
                return zoomOut(frame1, frame2, progress);
            default:
                return zoomIn(frame1, frame2, progress);
        }
    }
    
    /**
     * Zoom in transition - old frame zooms in while new frame fades in
     */
    private Mat zoomIn(Mat frame1, Mat frame2, double progress) {
        double easedProgress = easeInOut(progress);
        
        // Scale factor: starts at 1.0, goes to 2.0
        double scaleFactor = 1.0 + easedProgress;
        Mat scaledFrame1 = VideoProcessor.scaleFrame(frame1, scaleFactor);
        
        // Fade between scaled frame1 and frame2
        double alpha = easedProgress;
        return VideoProcessor.blendFrames(scaledFrame1, frame2, alpha);
    }
    
    /**
     * Zoom out transition - new frame starts zoomed in and zooms out
     */
    private Mat zoomOut(Mat frame1, Mat frame2, double progress) {
        double easedProgress = easeInOut(progress);
        
        // Scale factor: starts at 2.0, goes to 1.0
        double scaleFactor = 2.0 - easedProgress;
        Mat scaledFrame2 = VideoProcessor.scaleFrame(frame2, scaleFactor);
        
        // Fade between frame1 and scaled frame2
        double alpha = easedProgress;
        return VideoProcessor.blendFrames(frame1, scaledFrame2, alpha);
    }
}
