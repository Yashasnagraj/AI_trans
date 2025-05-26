import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Point;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 * Wipe transition effects (wipe in different directions and shapes)
 */
public class WipeTransition extends BaseTransition {
    private TransitionType type;
    
    public WipeTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case WIPE_LEFT:
                return wipeLeft(frame1, frame2, progress);
            case WIPE_RIGHT:
                return wipeRight(frame1, frame2, progress);
            case WIPE_UP:
                return wipeUp(frame1, frame2, progress);
            case WIPE_DOWN:
                return wipeDown(frame1, frame2, progress);
            case WIPE_CIRCLE:
                return wipeCircle(frame1, frame2, progress);
            case IRIS_IN:
                return irisIn(frame1, frame2, progress);
            case IRIS_OUT:
                return irisOut(frame1, frame2, progress);
            default:
                return wipeLeft(frame1, frame2, progress);
        }
    }
    
    /**
     * Wipe left - new frame appears from left edge
     */
    private Mat wipeLeft(Mat frame1, Mat frame2, double progress) {
        Mat result = frame1.clone();
        double easedProgress = easeInOut(progress);
        int wipeWidth = (int)(width * easedProgress);
        
        if (wipeWidth > 0) {
            Rect roi = new Rect(0, 0, Math.min(wipeWidth, width), height);
            new Mat(frame2, roi).copyTo(new Mat(result, roi));
        }
        
        return result;
    }
    
    /**
     * Wipe right - new frame appears from right edge
     */
    private Mat wipeRight(Mat frame1, Mat frame2, double progress) {
        Mat result = frame1.clone();
        double easedProgress = easeInOut(progress);
        int wipeWidth = (int)(width * easedProgress);
        
        if (wipeWidth > 0) {
            int startX = width - wipeWidth;
            Rect roi = new Rect(startX, 0, wipeWidth, height);
            new Mat(frame2, roi).copyTo(new Mat(result, roi));
        }
        
        return result;
    }
    
    /**
     * Wipe up - new frame appears from top edge
     */
    private Mat wipeUp(Mat frame1, Mat frame2, double progress) {
        Mat result = frame1.clone();
        double easedProgress = easeInOut(progress);
        int wipeHeight = (int)(height * easedProgress);
        
        if (wipeHeight > 0) {
            Rect roi = new Rect(0, 0, width, Math.min(wipeHeight, height));
            new Mat(frame2, roi).copyTo(new Mat(result, roi));
        }
        
        return result;
    }
    
    /**
     * Wipe down - new frame appears from bottom edge
     */
    private Mat wipeDown(Mat frame1, Mat frame2, double progress) {
        Mat result = frame1.clone();
        double easedProgress = easeInOut(progress);
        int wipeHeight = (int)(height * easedProgress);
        
        if (wipeHeight > 0) {
            int startY = height - wipeHeight;
            Rect roi = new Rect(0, startY, width, wipeHeight);
            new Mat(frame2, roi).copyTo(new Mat(result, roi));
        }
        
        return result;
    }
    
    /**
     * Circular wipe - new frame appears in expanding circle
     */
    private Mat wipeCircle(Mat frame1, Mat frame2, double progress) {
        Mat result = frame1.clone();
        double easedProgress = easeInOut(progress);
        
        // Calculate maximum radius (diagonal of frame)
        int maxRadius = (int)Math.sqrt(width * width + height * height) / 2;
        int currentRadius = (int)(maxRadius * easedProgress);
        
        if (currentRadius > 0) {
            Point center = new Point(width / 2, height / 2);
            Mat mask = VideoProcessor.createCircularMask(width, height, currentRadius, center);
            
            // Apply mask to frame2 and blend with frame1
            Mat maskedFrame2 = VideoProcessor.applyMask(frame2, mask);
            Mat invertedMask = new Mat();
            bitwise_not(mask, invertedMask);
            Mat maskedFrame1 = VideoProcessor.applyMask(result, invertedMask);
            
            add(maskedFrame1, maskedFrame2, result);
        }
        
        return result;
    }
    
    /**
     * Iris in - circular opening reveals new frame
     */
    private Mat irisIn(Mat frame1, Mat frame2, double progress) {
        return wipeCircle(frame1, frame2, progress);
    }
    
    /**
     * Iris out - circular closing hides old frame
     */
    private Mat irisOut(Mat frame1, Mat frame2, double progress) {
        Mat result = frame2.clone();
        double easedProgress = easeInOut(1.0 - progress); // Reverse progress
        
        // Calculate maximum radius
        int maxRadius = (int)Math.sqrt(width * width + height * height) / 2;
        int currentRadius = (int)(maxRadius * easedProgress);
        
        if (currentRadius > 0) {
            Point center = new Point(width / 2, height / 2);
            Mat mask = VideoProcessor.createCircularMask(width, height, currentRadius, center);
            
            // Apply mask to frame1 and blend with frame2
            Mat maskedFrame1 = VideoProcessor.applyMask(frame1, mask);
            Mat invertedMask = new Mat();
            bitwise_not(mask, invertedMask);
            Mat maskedFrame2 = VideoProcessor.applyMask(result, invertedMask);
            
            add(maskedFrame1, maskedFrame2, result);
        }
        
        return result;
    }
}
