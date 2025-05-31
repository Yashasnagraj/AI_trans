import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_core.*;

public class ExposureMatcher {
    public static Mat matchExposure(Mat sourceFrame, Mat targetFrame, float blendFactor) {
        if (sourceFrame == null || targetFrame == null || sourceFrame.empty() || targetFrame.empty()) {
            return sourceFrame != null ? sourceFrame.clone() : new Mat();
        }
        try {
            Mat result = new Mat();
            addWeighted(sourceFrame, 1.0 - blendFactor, targetFrame, blendFactor, 0, result);
            return result;
        } catch (Exception e) {
            System.err.println("Error in exposure matching: " + e.getMessage());
            return sourceFrame.clone();
        }
    }
}
