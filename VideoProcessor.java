import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Utility class for video processing operations
 */
public class VideoProcessor {
    
    /**
     * Resize a frame to specified dimensions
     */
    public static Mat resizeFrame(Mat frame, int width, int height) {
        Mat resized = new Mat();
        resize(frame, resized, new Size(width, height));
        return resized;
    }
    
    /**
     * Convert Frame to Mat
     */
    public static Mat frameToMat(Frame frame) {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        return converter.convert(frame);
    }
    
    /**
     * Convert Mat to Frame
     */
    public static Frame matToFrame(Mat mat) {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        return converter.convert(mat);
    }
    
    /**
     * Create a blank frame with specified dimensions
     */
    public static Mat createBlankFrame(int width, int height) {
        return new Mat(height, width, CV_8UC3, new Scalar(0, 0, 0, 0));
    }
    
    /**
     * Blend two frames with specified alpha
     */
    public static Mat blendFrames(Mat frame1, Mat frame2, double alpha) {
        Mat result = new Mat();
        addWeighted(frame1, alpha, frame2, 1.0 - alpha, 0, result);
        return result;
    }
    
    /**
     * Apply Gaussian blur to frame
     */
    public static Mat applyBlur(Mat frame, int kernelSize) {
        Mat blurred = new Mat();
        GaussianBlur(frame, blurred, new Size(kernelSize, kernelSize), 0);
        return blurred;
    }
    
    /**
     * Create a circular mask
     */
    public static Mat createCircularMask(int width, int height, int radius, Point center) {
        Mat mask = new Mat(height, width, CV_8UC1, new Scalar(0));
        circle(mask, center, radius, new Scalar(255), -1);
        return mask;
    }
    
    /**
     * Apply mask to frame
     */
    public static Mat applyMask(Mat frame, Mat mask) {
        Mat result = new Mat();
        frame.copyTo(result, mask);
        return result;
    }
    
    /**
     * Rotate frame by specified angle
     */
    public static Mat rotateFrame(Mat frame, double angle) {
        Point2f center = new Point2f(frame.cols() / 2.0f, frame.rows() / 2.0f);
        Mat rotationMatrix = getRotationMatrix2D(center, angle, 1.0);
        Mat rotated = new Mat();
        warpAffine(frame, rotated, rotationMatrix, new Size(frame.cols(), frame.rows()));
        return rotated;
    }
    
    /**
     * Scale frame by specified factor
     */
    public static Mat scaleFrame(Mat frame, double scaleFactor) {
        int newWidth = (int)(frame.cols() * scaleFactor);
        int newHeight = (int)(frame.rows() * scaleFactor);
        Mat scaled = new Mat();
        resize(frame, scaled, new Size(newWidth, newHeight));
        
        // Center the scaled frame
        Mat result = createBlankFrame(frame.cols(), frame.rows());
        int offsetX = (frame.cols() - newWidth) / 2;
        int offsetY = (frame.rows() - newHeight) / 2;
        
        if (scaleFactor <= 1.0) {
            // If scaling down, place in center
            Rect roi = new Rect(offsetX, offsetY, newWidth, newHeight);
            scaled.copyTo(new Mat(result, roi));
        } else {
            // If scaling up, crop from center
            int cropX = (newWidth - frame.cols()) / 2;
            int cropY = (newHeight - frame.rows()) / 2;
            Rect cropRoi = new Rect(cropX, cropY, frame.cols(), frame.rows());
            new Mat(scaled, cropRoi).copyTo(result);
        }
        
        return result;
    }
    
    /**
     * Create pixelated effect
     */
    public static Mat pixelateFrame(Mat frame, int pixelSize) {
        Mat small = new Mat();
        Mat pixelated = new Mat();
        
        // Downscale
        resize(frame, small, new Size(frame.cols() / pixelSize, frame.rows() / pixelSize));
        // Upscale back with nearest neighbor interpolation
        resize(small, pixelated, new Size(frame.cols(), frame.rows()), 0, 0, INTER_NEAREST);
        
        return pixelated;
    }
}
