import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core.*;
import org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

/**
 * Object-aware transition that uses AI-generated masks to create smooth transitions
 * focused on the main subject in the video frames
 */
public class ObjectRevealTransition extends BaseTransition {

    private TransitionType transitionType;
    private TransitionConfig config;
    private MaskGenerator maskGenerator;
    private boolean maskGeneratorInitialized = false;

    public ObjectRevealTransition(int width, int height, int transitionFrames, TransitionType type) {
        super(width, height, transitionFrames);
        this.transitionType = type;
        this.config = TransitionConfig.loadPreset("MAGIC");
        this.maskGenerator = new MaskGenerator();
    }

    public ObjectRevealTransition(int width, int height, int transitionFrames, TransitionType type, TransitionConfig config) {
        super(width, height, transitionFrames);
        this.transitionType = type;
        this.config = config != null ? config : TransitionConfig.loadPreset("MAGIC");
        this.maskGenerator = new MaskGenerator();
    }

    /**
     * Initialize mask generator with model path
     */
    public boolean initializeMaskGenerator(String modelPath) {
        maskGeneratorInitialized = maskGenerator.initializeModel(modelPath);
        return maskGeneratorInitialized;
    }

    @Override
    public Mat applyTransition(Mat frame1, Mat frame2, double progress) {
        if (frame1 == null || frame2 == null || frame1.empty() || frame2.empty()) {
            return frame1 != null ? frame1.clone() : new Mat();
        }

        try {
            // Apply easing function from config
            double easedProgress = config.applyEasing(progress);

            // Generate or use cached masks
            Mat mask1 = generateMask(frame1);
            Mat mask2 = generateMask(frame2);

            // Apply exposure matching if enabled
            Mat adjustedFrame2 = frame2.clone();
            if (config.isEnableExposureMatching()) {
                adjustedFrame2 = ExposureMatcher.matchExposure(frame2, frame1, config.getExposureBlendFactor());
            }

            // Apply object-aware transition based on type
            Mat result = applyObjectAwareTransition(frame1, adjustedFrame2, mask1, mask2, easedProgress);

            // Clean up
            mask1.release();
            mask2.release();
            if (!adjustedFrame2.equals(frame2)) {
                adjustedFrame2.release();
            }

            return result;

        } catch (Exception e) {
            System.err.println("Error applying object reveal transition: " + e.getMessage());
            e.printStackTrace();
            return frame1.clone();
        }
    }

    /**
     * Generate mask for frame using AI or fallback method
     */
    private Mat generateMask(Mat frame) {
        if (config.isUseObjectDetection() && maskGeneratorInitialized) {
            Mat aiMask = maskGenerator.getMask(frame, config.getTargetObjectClass(),
                                             config.getObjectConfidenceThreshold());

            // Process the mask (feather, shrink, etc.)
            return MaskProcessor.featherMask(aiMask, (int) config.getFeatherRadius(),
                                           config.getMaskShrinkFactor());
        } else {
            // Use fallback center region mask
            return maskGenerator.createManualMask(frame,
                new Rect(width / 4, height / 4, width / 2, height / 2));
        }
    }

    /**
     * Apply object-aware transition effect
     */
    private Mat applyObjectAwareTransition(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress) {
        switch (transitionType) {
            case OBJECT_REVEAL:
                return applyObjectReveal(frame1, frame2, mask1, mask2, progress);
            case OBJECT_ZOOM_IN:
                return applyObjectZoom(frame1, frame2, mask1, mask2, progress, true);
            case OBJECT_ZOOM_OUT:
                return applyObjectZoom(frame1, frame2, mask1, mask2, progress, false);
            case OBJECT_SLIDE_LEFT:
                return applyObjectSlide(frame1, frame2, mask1, mask2, progress, "left");
            case OBJECT_SLIDE_RIGHT:
                return applyObjectSlide(frame1, frame2, mask1, mask2, progress, "right");
            case OBJECT_FADE_IN:
                return applyObjectFade(frame1, frame2, mask1, mask2, progress, true);
            case OBJECT_FADE_OUT:
                return applyObjectFade(frame1, frame2, mask1, mask2, progress, false);
            case OBJECT_ROTATE_IN:
                return applyObjectRotate(frame1, frame2, mask1, mask2, progress, true);
            case OBJECT_ROTATE_OUT:
                return applyObjectRotate(frame1, frame2, mask1, mask2, progress, false);
            case OBJECT_SCALE_TRANSITION:
                return applyObjectScale(frame1, frame2, mask1, mask2, progress);
            default:
                return applyObjectReveal(frame1, frame2, mask1, mask2, progress);
        }
    }

    /**
     * Object reveal transition - objects appear/disappear smoothly
     */
    private Mat applyObjectReveal(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress) {
        Mat result = new Mat(frame1.size(), frame1.type());

        try {
            // Create transition mask based on progress
            Mat transitionMask = new Mat();
            addWeighted(mask1, 1.0 - progress, mask2, progress, 0, transitionMask);

            // Apply feathering to transition mask
            transitionMask = MaskProcessor.featherMask(transitionMask, (int) config.getFeatherRadius(), 1.0f);

            // Blend frames using transition mask
            blendFramesWithMask(frame1, frame2, transitionMask, result);

            transitionMask.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error in object reveal: " + e.getMessage());
            frame1.copyTo(result);
            return result;
        }
    }

    /**
     * Object zoom transition
     */
    private Mat applyObjectZoom(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress, boolean zoomIn) {
        Mat result = new Mat(frame1.size(), frame1.type());

        try {
            float zoomFactor = zoomIn ? 1.0f + (config.getZoomLevel() - 1.0f) * (float) progress :
                                      config.getZoomLevel() - (config.getZoomLevel() - 1.0f) * (float) progress;

            // Apply zoom to the object region
            Mat zoomedFrame = applyZoomToMaskedRegion(zoomIn ? frame1 : frame2,
                                                    zoomIn ? mask1 : mask2, zoomFactor);

            // Blend with the other frame
            addWeighted(zoomIn ? zoomedFrame : frame1, 1.0 - progress,
                       zoomIn ? frame2 : zoomedFrame, progress, 0, result);

            zoomedFrame.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error in object zoom: " + e.getMessage());
            frame1.copyTo(result);
            return result;
        }
    }

    /**
     * Object slide transition
     */
    private Mat applyObjectSlide(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress, String direction) {
        Mat result = new Mat(frame1.size(), frame1.type());

        try {
            // Calculate slide offset
            int offsetX = 0, offsetY = 0;
            switch (direction) {
                case "left":
                    offsetX = (int) (-width * progress);
                    break;
                case "right":
                    offsetX = (int) (width * progress);
                    break;
                case "up":
                    offsetY = (int) (-height * progress);
                    break;
                case "down":
                    offsetY = (int) (height * progress);
                    break;
            }

            // Create sliding effect for masked regions
            Mat slidFrame1 = applySlidToMaskedRegion(frame1, mask1, -offsetX, -offsetY);
            Mat slidFrame2 = applySlidToMaskedRegion(frame2, mask2, offsetX, offsetY);

            // Blend frames
            addWeighted(slidFrame1, 1.0 - progress, slidFrame2, progress, 0, result);

            slidFrame1.release();
            slidFrame2.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error in object slide: " + e.getMessage());
            frame1.copyTo(result);
            return result;
        }
    }

    /**
     * Object fade transition
     */
    private Mat applyObjectFade(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress, boolean fadeIn) {
        Mat result = new Mat(frame1.size(), frame1.type());

        try {
            Mat activeMask = fadeIn ? mask2 : mask1;
            Mat activeFrame = fadeIn ? frame2 : frame1;
            Mat baseFrame = fadeIn ? frame1 : frame2;

            // Apply fade to masked region
            Mat fadedFrame = applyFadeToMaskedRegion(activeFrame, activeMask, (float) progress);

            // Blend with base frame
            addWeighted(baseFrame, 1.0 - progress, fadedFrame, progress, 0, result);

            fadedFrame.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error in object fade: " + e.getMessage());
            frame1.copyTo(result);
            return result;
        }
    }

    /**
     * Object rotate transition
     */
    private Mat applyObjectRotate(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress, boolean rotateIn) {
        Mat result = new Mat(frame1.size(), frame1.type());

        try {
            float angle = rotateIn ? (float) (360.0 * progress) : (float) (360.0 * (1.0 - progress));

            Mat rotatedFrame = applyRotationToMaskedRegion(rotateIn ? frame2 : frame1,
                                                         rotateIn ? mask2 : mask1, angle);

            addWeighted(rotateIn ? frame1 : rotatedFrame, 1.0 - progress,
                       rotateIn ? rotatedFrame : frame2, progress, 0, result);

            rotatedFrame.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error in object rotate: " + e.getMessage());
            frame1.copyTo(result);
            return result;
        }
    }

    /**
     * Object scale transition
     */
    private Mat applyObjectScale(Mat frame1, Mat frame2, Mat mask1, Mat mask2, double progress) {
        Mat result = new Mat(frame1.size(), frame1.type());

        try {
            float scale1 = 1.0f - (float) progress * 0.5f;
            float scale2 = (float) progress;

            Mat scaledFrame1 = applyScaleToMaskedRegion(frame1, mask1, scale1);
            Mat scaledFrame2 = applyScaleToMaskedRegion(frame2, mask2, scale2);

            addWeighted(scaledFrame1, 1.0 - progress, scaledFrame2, progress, 0, result);

            scaledFrame1.release();
            scaledFrame2.release();
            return result;

        } catch (Exception e) {
            System.err.println("Error in object scale: " + e.getMessage());
            frame1.copyTo(result);
            return result;
        }
    }

    // Helper methods for applying transformations to masked regions
    private Mat applyZoomToMaskedRegion(Mat frame, Mat mask, float zoomFactor) {
        // Implementation would apply zoom transformation only to masked areas
        // For now, return a simple scaled version
        Mat result = frame.clone();
        if (zoomFactor != 1.0f) {
            Mat scaled = new Mat();
            resize(frame, scaled, new Size((int)(width * zoomFactor), (int)(height * zoomFactor)));
            // Center the scaled image
            int offsetX = (scaled.cols() - width) / 2;
            int offsetY = (scaled.rows() - height) / 2;
            if (offsetX >= 0 && offsetY >= 0) {
                new Mat(scaled, new Rect(offsetX, offsetY, width, height)).copyTo(result);
            }
            scaled.release();
        }
        return result;
    }

    private Mat applySlidToMaskedRegion(Mat frame, Mat mask, int offsetX, int offsetY) {
        Mat result = Mat.zeros(frame.size(), frame.type()).asMat();

        // Apply translation matrix
        Mat translationMatrix = Mat.eye(2, 3, CV_32F).asMat();
        translationMatrix.ptr(0, 2).putFloat(offsetX);
        translationMatrix.ptr(1, 2).putFloat(offsetY);

        warpAffine(frame, result, translationMatrix, frame.size());
        translationMatrix.release();

        return result;
    }

    private Mat applyFadeToMaskedRegion(Mat frame, Mat mask, float alpha) {
        Mat result = new Mat();
        // Create a scalar Mat for multiplication
        Mat alphaMat = new Mat(frame.size(), frame.type(), new Scalar(alpha, alpha, alpha, 1.0));
        multiply(frame, alphaMat, result);
        alphaMat.release();
        return result;
    }

    private Mat applyRotationToMaskedRegion(Mat frame, Mat mask, float angle) {
        Mat result = new Mat();
        Point2f center = new Point2f(width / 2.0f, height / 2.0f);
        Mat rotationMatrix = getRotationMatrix2D(center, angle, 1.0);
        warpAffine(frame, result, rotationMatrix, frame.size());
        rotationMatrix.release();
        return result;
    }

    private Mat applyScaleToMaskedRegion(Mat frame, Mat mask, float scale) {
        Mat result = new Mat();
        resize(frame, result, new Size((int)(width * scale), (int)(height * scale)));
        if (result.cols() != width || result.rows() != height) {
            Mat resized = new Mat();
            resize(result, resized, new Size(width, height));
            result.release();
            result = resized;
        }
        return result;
    }

    private void blendFramesWithMask(Mat frame1, Mat frame2, Mat mask, Mat result) {
        // Normalize mask
        Mat normalizedMask = new Mat();
        mask.convertTo(normalizedMask, CV_32F, 1.0 / 255.0, 0.0);

        // Convert frames to float
        Mat frame1Float = new Mat();
        Mat frame2Float = new Mat();
        frame1.convertTo(frame1Float, CV_32F);
        frame2.convertTo(frame2Float, CV_32F);

        // Ensure mask has same number of channels
        Mat maskChannels = new Mat();
        if (frame1.channels() == 3 && normalizedMask.channels() == 1) {
            Mat[] channels = {normalizedMask, normalizedMask, normalizedMask};
            merge(new MatVector(channels), maskChannels);
        } else {
            maskChannels = normalizedMask.clone();
        }

        // Blend: result = frame1 * (1 - mask) + frame2 * mask
        Mat invMask = new Mat();
        subtract(Mat.ones(maskChannels.size(), maskChannels.type()).asMat(), maskChannels, invMask);

        Mat blended1 = new Mat();
        Mat blended2 = new Mat();
        multiply(frame1Float, invMask, blended1);
        multiply(frame2Float, maskChannels, blended2);

        Mat resultFloat = new Mat();
        add(blended1, blended2, resultFloat);
        resultFloat.convertTo(result, CV_8U);

        // Clean up
        normalizedMask.release();
        frame1Float.release();
        frame2Float.release();
        maskChannels.release();
        invMask.release();
        blended1.release();
        blended2.release();
        resultFloat.release();
    }

    public void setConfig(TransitionConfig config) {
        this.config = config;
    }

    public TransitionConfig getConfig() {
        return config;
    }
}
