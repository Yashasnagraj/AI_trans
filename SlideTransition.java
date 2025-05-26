import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import static org.bytedeco.opencv.global.opencv_core.*;

/**
 * Slide transition effects (slide in different directions)
 */
public class SlideTransition extends BaseTransition {
    private TransitionType type;
    
    public SlideTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.type = type;
    }
    
    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        switch (type) {
            case SLIDE_LEFT:
                return slideLeft(frame1, frame2, progress);
            case SLIDE_RIGHT:
                return slideRight(frame1, frame2, progress);
            case SLIDE_UP:
                return slideUp(frame1, frame2, progress);
            case SLIDE_DOWN:
                return slideDown(frame1, frame2, progress);
            case PUSH_LEFT:
                return pushLeft(frame1, frame2, progress);
            case PUSH_RIGHT:
                return pushRight(frame1, frame2, progress);
            default:
                return slideLeft(frame1, frame2, progress);
        }
    }
    
    /**
     * Slide left - new frame slides in from right
     */
    private Mat slideLeft(Mat frame1, Mat frame2, double progress) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        double easedProgress = easeInOut(progress);
        int offset = (int)(width * easedProgress);
        
        // Copy remaining part of frame1
        if (offset < width) {
            Rect frame1Roi = new Rect(0, 0, width - offset, height);
            Rect resultRoi1 = new Rect(0, 0, width - offset, height);
            new Mat(frame1, frame1Roi).copyTo(new Mat(result, resultRoi1));
        }
        
        // Copy incoming part of frame2
        if (offset > 0) {
            Rect frame2Roi = new Rect(0, 0, offset, height);
            Rect resultRoi2 = new Rect(width - offset, 0, offset, height);
            new Mat(frame2, frame2Roi).copyTo(new Mat(result, resultRoi2));
        }
        
        return result;
    }
    
    /**
     * Slide right - new frame slides in from left
     */
    private Mat slideRight(Mat frame1, Mat frame2, double progress) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        double easedProgress = easeInOut(progress);
        int offset = (int)(width * easedProgress);
        
        // Copy remaining part of frame1
        if (offset < width) {
            Rect frame1Roi = new Rect(offset, 0, width - offset, height);
            Rect resultRoi1 = new Rect(offset, 0, width - offset, height);
            new Mat(frame1, frame1Roi).copyTo(new Mat(result, resultRoi1));
        }
        
        // Copy incoming part of frame2
        if (offset > 0) {
            Rect frame2Roi = new Rect(width - offset, 0, offset, height);
            Rect resultRoi2 = new Rect(0, 0, offset, height);
            new Mat(frame2, frame2Roi).copyTo(new Mat(result, resultRoi2));
        }
        
        return result;
    }
    
    /**
     * Slide up - new frame slides in from bottom
     */
    private Mat slideUp(Mat frame1, Mat frame2, double progress) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        double easedProgress = easeInOut(progress);
        int offset = (int)(height * easedProgress);
        
        // Copy remaining part of frame1
        if (offset < height) {
            Rect frame1Roi = new Rect(0, 0, width, height - offset);
            Rect resultRoi1 = new Rect(0, 0, width, height - offset);
            new Mat(frame1, frame1Roi).copyTo(new Mat(result, resultRoi1));
        }
        
        // Copy incoming part of frame2
        if (offset > 0) {
            Rect frame2Roi = new Rect(0, 0, width, offset);
            Rect resultRoi2 = new Rect(0, height - offset, width, offset);
            new Mat(frame2, frame2Roi).copyTo(new Mat(result, resultRoi2));
        }
        
        return result;
    }
    
    /**
     * Slide down - new frame slides in from top
     */
    private Mat slideDown(Mat frame1, Mat frame2, double progress) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        double easedProgress = easeInOut(progress);
        int offset = (int)(height * easedProgress);
        
        // Copy remaining part of frame1
        if (offset < height) {
            Rect frame1Roi = new Rect(0, offset, width, height - offset);
            Rect resultRoi1 = new Rect(0, offset, width, height - offset);
            new Mat(frame1, frame1Roi).copyTo(new Mat(result, resultRoi1));
        }
        
        // Copy incoming part of frame2
        if (offset > 0) {
            Rect frame2Roi = new Rect(0, height - offset, width, offset);
            Rect resultRoi2 = new Rect(0, 0, width, offset);
            new Mat(frame2, frame2Roi).copyTo(new Mat(result, resultRoi2));
        }
        
        return result;
    }
    
    /**
     * Push left - both frames move left together
     */
    private Mat pushLeft(Mat frame1, Mat frame2, double progress) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        double easedProgress = easeInOut(progress);
        int offset = (int)(width * easedProgress);
        
        // Position frame1 (moving left)
        if (offset < width) {
            Rect frame1Roi = new Rect(0, 0, width - offset, height);
            Rect resultRoi1 = new Rect(-offset, 0, width - offset, height);
            if (resultRoi1.x() >= 0) {
                new Mat(frame1, frame1Roi).copyTo(new Mat(result, resultRoi1));
            }
        }
        
        // Position frame2 (coming from right)
        Rect frame2Roi = new Rect(0, 0, width, height);
        Rect resultRoi2 = new Rect(width - offset, 0, width, height);
        if (resultRoi2.x() < width) {
            int copyWidth = Math.min(width, width - resultRoi2.x());
            if (copyWidth > 0) {
                Rect adjustedFrame2Roi = new Rect(0, 0, copyWidth, height);
                Rect adjustedResultRoi2 = new Rect(resultRoi2.x(), 0, copyWidth, height);
                new Mat(frame2, adjustedFrame2Roi).copyTo(new Mat(result, adjustedResultRoi2));
            }
        }
        
        return result;
    }
    
    /**
     * Push right - both frames move right together
     */
    private Mat pushRight(Mat frame1, Mat frame2, double progress) {
        Mat result = VideoProcessor.createBlankFrame(width, height);
        double easedProgress = easeInOut(progress);
        int offset = (int)(width * easedProgress);
        
        // Position frame1 (moving right)
        if (offset < width) {
            Rect frame1Roi = new Rect(offset, 0, width - offset, height);
            Rect resultRoi1 = new Rect(offset, 0, width - offset, height);
            new Mat(frame1, frame1Roi).copyTo(new Mat(result, resultRoi1));
        }
        
        // Position frame2 (coming from left)
        if (offset > 0) {
            Rect frame2Roi = new Rect(width - offset, 0, offset, height);
            Rect resultRoi2 = new Rect(0, 0, offset, height);
            new Mat(frame2, frame2Roi).copyTo(new Mat(result, resultRoi2));
        }
        
        return result;
    }
}
