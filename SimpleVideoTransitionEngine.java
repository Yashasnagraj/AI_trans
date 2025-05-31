import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified Video Transition Engine for testing transitions without FFmpeg
 * This version works with Mat objects directly for testing purposes
 */
public class SimpleVideoTransitionEngine {
    
    private int outputWidth = 1920;
    private int outputHeight = 1080;
    private double frameRate = 30.0;
    private int transitionFrames = 30; // 1 second at 30fps
    
    /**
     * Test transition between two sample frames
     */
    public List<Mat> testTransition(TransitionType transitionType, int frameCount) {
        System.out.println("Testing transition: " + transitionType);
        
        List<Mat> resultFrames = new ArrayList<>();
        
        try {
            // Create sample frames
            Mat frame1 = createSampleFrame(new Scalar(100, 150, 200, 255), "Frame 1"); // Blue-ish
            Mat frame2 = createSampleFrame(new Scalar(200, 100, 150, 255), "Frame 2"); // Red-ish
            
            // Get appropriate transition
            BaseTransition transition = getTransition(transitionType);
            
            // Generate transition frames
            for (int i = 0; i < frameCount; i++) {
                double progress = (double) i / (frameCount - 1);
                Mat transitionFrame = transition.applyTransition(frame1, frame2, progress);
                resultFrames.add(transitionFrame.clone());
                
                System.out.println("Generated frame " + (i + 1) + "/" + frameCount + 
                                 " (progress: " + String.format("%.2f", progress) + ")");
            }
            
            // Clean up sample frames
            frame1.release();
            frame2.release();
            
            System.out.println("Transition test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error testing transition: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultFrames;
    }
    
    /**
     * Test AI-enhanced transition with TransitionConfig
     */
    public List<Mat> testAITransition(TransitionType transitionType, String presetName, int frameCount) {
        System.out.println("Testing AI transition: " + transitionType + " with preset: " + presetName);
        
        List<Mat> resultFrames = new ArrayList<>();
        
        try {
            // Load transition configuration
            TransitionConfig config = TransitionConfig.loadPreset(presetName);
            System.out.println("Loaded preset: " + presetName);
            System.out.println("  Easing: " + config.getEasingFunction());
            System.out.println("  Zoom: " + config.getZoomLevel());
            System.out.println("  AI enabled: " + config.isUseObjectDetection());
            
            // Create sample frames with different characteristics
            Mat frame1 = createComplexSampleFrame(1);
            Mat frame2 = createComplexSampleFrame(2);
            
            // Get appropriate transition
            BaseTransition transition = getTransition(transitionType);
            
            // Generate transition frames with easing
            for (int i = 0; i < frameCount; i++) {
                double progress = (double) i / (frameCount - 1);
                double easedProgress = config.applyEasing(progress);
                
                Mat transitionFrame = transition.applyTransition(frame1, frame2, easedProgress);
                resultFrames.add(transitionFrame.clone());
                
                System.out.println("Generated AI frame " + (i + 1) + "/" + frameCount + 
                                 " (progress: " + String.format("%.2f", progress) + 
                                 " -> eased: " + String.format("%.2f", easedProgress) + ")");
            }
            
            // Clean up
            frame1.release();
            frame2.release();
            
            System.out.println("AI transition test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error testing AI transition: " + e.getMessage());
            e.printStackTrace();
        }
        
        return resultFrames;
    }
    
    /**
     * Create a simple sample frame for testing
     */
    private Mat createSampleFrame(Scalar color, String text) {
        Mat frame = new Mat(outputHeight, outputWidth, CV_8UC3, color);
        
        // Add some visual elements to make the transition more visible
        // Draw a rectangle in the center
        int rectWidth = outputWidth / 4;
        int rectHeight = outputHeight / 4;
        int rectX = (outputWidth - rectWidth) / 2;
        int rectY = (outputHeight - rectHeight) / 2;
        
        Rect centerRect = new Rect(rectX, rectY, rectWidth, rectHeight);
        rectangle(frame, centerRect, new Scalar(255, 255, 255, 255), -1, 8, 0);
        
        // Draw a border
        rectangle(frame, new Rect(10, 10, outputWidth - 20, outputHeight - 20), 
                 new Scalar(0, 0, 0, 255), 5, 8, 0);
        
        return frame;
    }
    
    /**
     * Create a more complex sample frame for AI testing
     */
    private Mat createComplexSampleFrame(int frameNumber) {
        Mat frame = new Mat(outputHeight, outputWidth, CV_8UC3);
        
        if (frameNumber == 1) {
            // Frame 1: Gradient from blue to green
            frame.setTo(new Scalar(100, 150, 50, 255));
            
            // Add some geometric shapes
            circle(frame, new Point(outputWidth / 3, outputHeight / 3), 100, 
                  new Scalar(255, 100, 100, 255), -1, 8, 0);
            
        } else {
            // Frame 2: Gradient from red to yellow
            frame.setTo(new Scalar(50, 100, 200, 255));
            
            // Add different shapes
            rectangle(frame, new Rect(outputWidth / 2, outputHeight / 2, 200, 150), 
                     new Scalar(100, 255, 100, 255), -1, 8, 0);
        }
        
        return frame;
    }
    
    /**
     * Get transition instance based on type
     */
    private BaseTransition getTransition(TransitionType type) {
        switch (type) {
            case FADE:
                return new FadeTransition();
            case SLIDE_LEFT:
            case SLIDE_RIGHT:
            case SLIDE_UP:
            case SLIDE_DOWN:
                return new SlideTransition(type);
            case ZOOM_IN:
            case ZOOM_OUT:
                return new ZoomTransition(type);
            case ROTATE_LEFT:
            case ROTATE_RIGHT:
                return new RotateTransition(type);
            case WIPE_LEFT:
            case WIPE_RIGHT:
            case WIPE_UP:
            case WIPE_DOWN:
                return new WipeTransition(type);
            case BLUR:
            case SHAKE:
                return new EffectTransition(type);
            default:
                return new FadeTransition(); // Default fallback
        }
    }
    
    /**
     * Clean up resources from a list of frames
     */
    public void releaseFrames(List<Mat> frames) {
        for (Mat frame : frames) {
            if (frame != null && !frame.isNull()) {
                frame.release();
            }
        }
        frames.clear();
    }
    
    /**
     * Get frame information
     */
    public void printFrameInfo(Mat frame, String description) {
        if (frame != null && !frame.empty()) {
            System.out.println(description + ": " + frame.cols() + "x" + frame.rows() + 
                             " channels=" + frame.channels() + " type=" + frame.type());
        } else {
            System.out.println(description + ": Invalid or empty frame");
        }
    }
    
    // Getters and setters
    public void setOutputSize(int width, int height) {
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
