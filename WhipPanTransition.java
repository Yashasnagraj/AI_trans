import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Whip Pan transitions with realistic camera pan motion and directional motion blur
 */
public class WhipPanTransition extends BaseTransition {
    private TransitionType type;
    
    public WhipPanTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case WHIP_PAN_LEFT:
                return whipPanLeft(frame1, frame2, progress);
            case WHIP_PAN_RIGHT:
                return whipPanRight(frame1, frame2, progress);
            case WHIP_PAN_UP:
                return whipPanUp(frame1, frame2, progress);
            case WHIP_PAN_DOWN:
                return whipPanDown(frame1, frame2, progress);
            default:
                return whipPanLeft(frame1, frame2, progress);
        }
    }
    
    /**
     * Whip pan left transition with horizontal motion blur
     */
    private Mat whipPanLeft(Mat frame1, Mat frame2, double progress) {
        return createWhipPan(frame1, frame2, progress, "left");
    }
    
    /**
     * Whip pan right transition with horizontal motion blur
     */
    private Mat whipPanRight(Mat frame1, Mat frame2, double progress) {
        return createWhipPan(frame1, frame2, progress, "right");
    }
    
    /**
     * Whip pan up transition with vertical motion blur
     */
    private Mat whipPanUp(Mat frame1, Mat frame2, double progress) {
        return createWhipPan(frame1, frame2, progress, "up");
    }
    
    /**
     * Whip pan down transition with vertical motion blur
     */
    private Mat whipPanDown(Mat frame1, Mat frame2, double progress) {
        return createWhipPan(frame1, frame2, progress, "down");
    }
    
    /**
     * Create whip pan effect with motion blur and frame translation
     */
    private Mat createWhipPan(Mat frame1, Mat frame2, double progress, String direction) {
        // Create result frame
        Mat result = VideoProcessor.createBlankFrame(width, height);
        
        // Calculate motion intensity (peaks in middle for maximum blur effect)
        double motionIntensity = 4.0 * progress * (1.0 - progress);
        
        // Apply directional motion blur to both frames
        Mat blurredFrame1 = VideoProcessor.applyMotionBlur(frame1, motionIntensity, direction);
        Mat blurredFrame2 = VideoProcessor.applyMotionBlur(frame2, motionIntensity, direction);
        
        // Calculate translation offset based on direction and progress
        int offsetX = 0, offsetY = 0;
        
        switch (direction.toLowerCase()) {
            case "left":
                offsetX = (int)(-width * progress);
                break;
            case "right":
                offsetX = (int)(width * progress);
                break;
            case "up":
                offsetY = (int)(-height * progress);
                break;
            case "down":
                offsetY = (int)(height * progress);
                break;
        }
        
        // Position frames with translation
        Mat translatedFrame1 = translateFrame(blurredFrame1, offsetX, offsetY);
        Mat translatedFrame2 = translateFrame(blurredFrame2, offsetX - getDirectionOffset(direction), 
                                            offsetY - getDirectionOffset(direction));
        
        // Blend frames with smooth alpha progression
        double alpha = (1 - Math.cos(progress * Math.PI)) / 2;
        return VideoProcessor.blendFramesSmooth(translatedFrame1, translatedFrame2, alpha);
    }
    
    /**
     * Translate frame by specified offset
     */
    private Mat translateFrame(Mat frame, int offsetX, int offsetY) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        
        // Calculate source and destination regions
        int srcX = Math.max(0, -offsetX);
        int srcY = Math.max(0, -offsetY);
        int dstX = Math.max(0, offsetX);
        int dstY = Math.max(0, offsetY);
        
        int copyWidth = Math.min(width - Math.abs(offsetX), frame.cols() - srcX);
        int copyHeight = Math.min(height - Math.abs(offsetY), frame.rows() - srcY);
        
        if (copyWidth > 0 && copyHeight > 0) {
            Rect srcRect = new Rect(srcX, srcY, copyWidth, copyHeight);
            Rect dstRect = new Rect(dstX, dstY, copyWidth, copyHeight);
            
            new Mat(frame, srcRect).copyTo(new Mat(result, dstRect));
        }
        
        return result;
    }
    
    /**
     * Get direction offset for frame positioning
     */
    private int getDirectionOffset(String direction) {
        switch (direction.toLowerCase()) {
            case "left":
            case "right":
                return width;
            case "up":
            case "down":
                return height;
            default:
                return width;
        }
    }
}