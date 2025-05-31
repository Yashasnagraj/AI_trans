import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Utility class for processing segmentation masks
 * Provides feathering, blurring, shrinking, and other mask operations
 */
public class MaskProcessor {

    /**
     * Apply feathering to a binary mask for smooth edges
     */
    public static Mat featherMask(Mat binaryMask, int blurRadius, float shrinkFactor) {
        if (binaryMask == null || binaryMask.empty()) {
            return new Mat();
        }

        Mat processedMask = binaryMask.clone();

        try {
            // Step 1: Shrink the mask if requested
            if (shrinkFactor < 1.0f && shrinkFactor > 0.0f) {
                processedMask = shrinkMask(processedMask, shrinkFactor);
            }

            // Step 2: Apply Gaussian blur for feathering
            if (blurRadius > 0) {
                Size kernelSize = new Size(blurRadius * 2 + 1, blurRadius * 2 + 1);
                GaussianBlur(processedMask, processedMask, kernelSize, blurRadius / 3.0, blurRadius / 3.0, BORDER_DEFAULT);
            }

            return processedMask;

        } catch (Exception e) {
            System.err.println("Error feathering mask: " + e.getMessage());
            e.printStackTrace();
            return binaryMask.clone();
        }
    }

    /**
     * Shrink mask by applying erosion
     */
    public static Mat shrinkMask(Mat mask, float shrinkFactor) {
        if (shrinkFactor >= 1.0f || shrinkFactor <= 0.0f) {
            return mask.clone();
        }

        try {
            Mat shrunkMask = mask.clone();

            // Calculate erosion kernel size based on shrink factor
            int kernelSize = (int) Math.max(1, (1.0f - shrinkFactor) * 20);
            Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(kernelSize, kernelSize));

            // Apply erosion to shrink the mask
            erode(shrunkMask, shrunkMask, kernel, new Point(-1, -1), 1, BORDER_CONSTANT, morphologyDefaultBorderValue());

            kernel.release();
            return shrunkMask;

        } catch (Exception e) {
            System.err.println("Error shrinking mask: " + e.getMessage());
            e.printStackTrace();
            return mask.clone();
        }
    }

    /**
     * Expand mask by applying dilation
     */
    public static Mat expandMask(Mat mask, float expandFactor) {
        if (expandFactor <= 1.0f) {
            return mask.clone();
        }

        try {
            Mat expandedMask = mask.clone();

            // Calculate dilation kernel size based on expand factor
            int kernelSize = (int) Math.max(1, (expandFactor - 1.0f) * 20);
            Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(kernelSize, kernelSize));

            // Apply dilation to expand the mask
            dilate(expandedMask, expandedMask, kernel, new Point(-1, -1), 1, BORDER_CONSTANT, morphologyDefaultBorderValue());

            kernel.release();
            return expandedMask;

        } catch (Exception e) {
            System.err.println("Error expanding mask: " + e.getMessage());
            e.printStackTrace();
            return mask.clone();
        }
    }

    /**
     * Apply morphological operations to clean up mask
     */
    public static Mat cleanMask(Mat mask, int morphOperation, int kernelSize) {
        try {
            Mat cleanedMask = mask.clone();
            Mat kernel = getStructuringElement(MORPH_ELLIPSE, new Size(kernelSize, kernelSize));

            morphologyEx(cleanedMask, cleanedMask, morphOperation, kernel, new Point(-1, -1), 1,
                        BORDER_CONSTANT, morphologyDefaultBorderValue());

            kernel.release();
            return cleanedMask;

        } catch (Exception e) {
            System.err.println("Error cleaning mask: " + e.getMessage());
            e.printStackTrace();
            return mask.clone();
        }
    }

    /**
     * Create a gradient mask for smooth transitions
     */
    public static Mat createGradientMask(int width, int height, String direction, float falloff) {
        Mat gradientMask = new Mat(height, width, CV_8UC1);

        try {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    float value = 0.0f;

                    switch (direction.toLowerCase()) {
                        case "left":
                            value = (float) x / width;
                            break;
                        case "right":
                            value = 1.0f - (float) x / width;
                            break;
                        case "top":
                            value = (float) y / height;
                            break;
                        case "bottom":
                            value = 1.0f - (float) y / height;
                            break;
                        case "center":
                            float centerX = width / 2.0f;
                            float centerY = height / 2.0f;
                            float maxDist = (float) Math.sqrt(centerX * centerX + centerY * centerY);
                            float dist = (float) Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY));
                            value = 1.0f - Math.min(1.0f, dist / maxDist);
                            break;
                        default:
                            value = 1.0f;
                    }

                    // Apply falloff curve
                    if (falloff != 1.0f) {
                        value = (float) Math.pow(value, falloff);
                    }

                    // Set pixel value
                    gradientMask.ptr(y, x).put((byte) (value * 255));
                }
            }

            return gradientMask;

        } catch (Exception e) {
            System.err.println("Error creating gradient mask: " + e.getMessage());
            e.printStackTrace();
            gradientMask.release();
            return Mat.zeros(height, width, CV_8UC1).asMat();
        }
    }

    /**
     * Combine two masks using specified blend mode
     */
    public static Mat combineMasks(Mat mask1, Mat mask2, String blendMode, float alpha) {
        if (mask1 == null || mask2 == null || mask1.empty() || mask2.empty()) {
            return mask1 != null ? mask1.clone() : new Mat();
        }

        try {
            Mat result = new Mat();

            switch (blendMode.toLowerCase()) {
                case "add":
                    add(mask1, mask2, result);
                    break;
                case "multiply":
                    multiply(mask1, mask2, result, 1.0 / 255.0, -1);
                    break;
                case "screen":
                    Mat invMask1 = new Mat();
                    Mat invMask2 = new Mat();
                    Mat invResult = new Mat();
                    bitwise_not(mask1, invMask1);
                    bitwise_not(mask2, invMask2);
                    multiply(invMask1, invMask2, invResult, 1.0 / 255.0, -1);
                    bitwise_not(invResult, result);
                    invMask1.release();
                    invMask2.release();
                    invResult.release();
                    break;
                case "overlay":
                    // Simple overlay implementation
                    addWeighted(mask1, 1.0 - alpha, mask2, alpha, 0, result);
                    break;
                default: // blend
                    addWeighted(mask1, 1.0 - alpha, mask2, alpha, 0, result);
            }

            return result;

        } catch (Exception e) {
            System.err.println("Error combining masks: " + e.getMessage());
            e.printStackTrace();
            return mask1.clone();
        }
    }

    /**
     * Apply mask to an image with specified blend mode
     */
    public static Mat applyMaskToImage(Mat image, Mat mask, String blendMode) {
        if (image == null || mask == null || image.empty() || mask.empty()) {
            return image != null ? image.clone() : new Mat();
        }

        try {
            Mat result = new Mat();
            Mat normalizedMask = new Mat();

            // Normalize mask to 0-1 range
            mask.convertTo(normalizedMask, CV_32F, 1.0 / 255.0, 0.0);

            // Ensure mask has same number of channels as image
            Mat maskChannels = new Mat();
            if (image.channels() == 3 && normalizedMask.channels() == 1) {
                Mat[] channels = {normalizedMask, normalizedMask, normalizedMask};
                merge(new MatVector(channels), maskChannels);
            } else {
                maskChannels = normalizedMask.clone();
            }

            switch (blendMode.toLowerCase()) {
                case "multiply":
                    multiply(image, maskChannels, result);
                    break;
                case "screen":
                    Mat ones = Mat.ones(image.size(), image.type()).asMat();
                    Mat invMask = new Mat();
                    subtract(ones, maskChannels, invMask);
                    Mat invImage = new Mat();
                    subtract(ones, image, invImage);
                    multiply(invImage, invMask, result);
                    subtract(ones, result, result);
                    ones.release();
                    invMask.release();
                    invImage.release();
                    break;
                default: // normal blend
                    multiply(image, maskChannels, result);
            }

            normalizedMask.release();
            maskChannels.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error applying mask to image: " + e.getMessage());
            e.printStackTrace();
            return image.clone();
        }
    }

    /**
     * Create a vignette mask
     */
    public static Mat createVignetteMask(int width, int height, float intensity, float radius) {
        Mat vignette = new Mat(height, width, CV_8UC1);

        try {
            float centerX = width / 2.0f;
            float centerY = height / 2.0f;
            float maxRadius = radius * Math.min(centerX, centerY);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    float dx = x - centerX;
                    float dy = y - centerY;
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);

                    float value = 1.0f;
                    if (distance > maxRadius) {
                        float falloff = (distance - maxRadius) / (Math.min(centerX, centerY) - maxRadius);
                        value = Math.max(0.0f, 1.0f - falloff * intensity);
                    }

                    vignette.ptr(y, x).put((byte) (value * 255));
                }
            }

            return vignette;

        } catch (Exception e) {
            System.err.println("Error creating vignette mask: " + e.getMessage());
            e.printStackTrace();
            vignette.release();
            return Mat.ones(height, width, CV_8UC1).asMat();
        }
    }

    /**
     * Invert a mask
     */
    public static Mat invertMask(Mat mask) {
        try {
            Mat inverted = new Mat();
            bitwise_not(mask, inverted);
            return inverted;
        } catch (Exception e) {
            System.err.println("Error inverting mask: " + e.getMessage());
            e.printStackTrace();
            return mask.clone();
        }
    }
}
