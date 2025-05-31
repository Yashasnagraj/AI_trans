import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Simple test to verify AI dependencies are working
 */
public class SimpleAITest {

    public static void main(String[] args) {
        System.out.println("Testing AI Dependencies...");

        try {
            // Test OpenCV Mat creation
            Mat testMat = new Mat(100, 100, CV_8UC3);
            System.out.println("SUCCESS: OpenCV Mat creation successful");
            System.out.println("  Mat size: " + testMat.rows() + "x" + testMat.cols());

            // Test basic OpenCV operations
            Mat blurred = new Mat();
            GaussianBlur(testMat, blurred, new Size(5, 5), 1.0, 1.0, BORDER_DEFAULT);
            System.out.println("SUCCESS: OpenCV GaussianBlur operation successful");

            // Test TransitionConfig
            TransitionConfig config = new TransitionConfig();
            System.out.println("SUCCESS: TransitionConfig creation successful");
            System.out.println("  Default easing: " + config.getEasingFunction());
            System.out.println("  Default zoom: " + config.getZoomLevel());

            // Test preset loading
            TransitionConfig magicPreset = TransitionConfig.loadPreset("MAGIC");
            System.out.println("SUCCESS: Preset loading successful");
            System.out.println("  Magic preset AI enabled: " + magicPreset.isUseObjectDetection());

            // Test easing functions
            double progress = 0.5;
            double easedProgress = config.applyEasing(progress);
            System.out.println("SUCCESS: Easing function test successful");
            System.out.println("  Progress " + progress + " -> " + easedProgress);

            // Clean up
            testMat.release();
            blurred.release();

            System.out.println();
            System.out.println("SUCCESS: All basic AI dependencies are working!");
            System.out.println("SUCCESS: OpenCV: Ready");
            System.out.println("SUCCESS: TransitionConfig: Ready");
            System.out.println("SUCCESS: Easing Functions: Ready");

            // Test ONNX Runtime availability
            try {
                Class.forName("ai.onnxruntime.OrtEnvironment");
                System.out.println("SUCCESS: ONNX Runtime: Available");
                System.out.println();
                System.out.println("SUCCESS: AI-powered object detection is ready!");
                System.out.println("   You can now use AI transitions with a YOLOv8 model.");
            } catch (ClassNotFoundException e) {
                System.out.println("WARNING: ONNX Runtime: Not available");
                System.out.println("   AI features will use fallback rectangular masks.");
            }

        } catch (Exception e) {
            System.err.println("ERROR: Error testing dependencies: " + e.getMessage());
            e.printStackTrace();

            System.err.println();
            System.err.println("Troubleshooting:");
            System.err.println("1. Ensure all JAR files are in the classpath");
            System.err.println("2. Check that JavaCV and OpenCV versions are compatible");
            System.err.println("3. Verify Windows x64 native libraries are available");
        }
    }
}
