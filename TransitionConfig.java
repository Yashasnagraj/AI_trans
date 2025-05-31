import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for transition animation parameters and presets
 * Supports customizable easing functions, zoom levels, blur amounts, and other effects
 */
public class TransitionConfig {
    
    // Animation parameters
    private String easingFunction = "EASE_IN_OUT";
    private float zoomLevel = 1.2f;
    private float blurAmount = 5.0f;
    private float shakeIntensity = 0.0f;
    private float transitionDuration = 1.0f; // in seconds
    private float featherRadius = 10.0f;
    private float maskShrinkFactor = 0.95f;
    
    // Object detection parameters
    private boolean useObjectDetection = false;
    private float objectConfidenceThreshold = 0.5f;
    private String targetObjectClass = "person"; // Default to person detection
    
    // Exposure matching
    private boolean enableExposureMatching = true;
    private float exposureBlendFactor = 0.5f;
    
    // Preset configurations
    private static final Map<String, TransitionConfig> PRESETS = new HashMap<>();
    
    static {
        // Initialize default presets
        initializePresets();
    }
    
    public TransitionConfig() {}
    
    public TransitionConfig(String easingFunction, float zoomLevel, float blurAmount, 
                           float shakeIntensity, float transitionDuration) {
        this.easingFunction = easingFunction;
        this.zoomLevel = zoomLevel;
        this.blurAmount = blurAmount;
        this.shakeIntensity = shakeIntensity;
        this.transitionDuration = transitionDuration;
    }
    
    /**
     * Initialize default preset configurations
     */
    private static void initializePresets() {
        // Smooth preset
        TransitionConfig smooth = new TransitionConfig();
        smooth.setEasingFunction("EASE_IN_OUT");
        smooth.setZoomLevel(1.1f);
        smooth.setBlurAmount(2.0f);
        smooth.setShakeIntensity(0.0f);
        smooth.setTransitionDuration(1.5f);
        PRESETS.put("SMOOTH", smooth);
        
        // Dynamic preset
        TransitionConfig dynamic = new TransitionConfig();
        dynamic.setEasingFunction("EASE_OUT");
        dynamic.setZoomLevel(1.3f);
        dynamic.setBlurAmount(8.0f);
        dynamic.setShakeIntensity(2.0f);
        dynamic.setTransitionDuration(0.8f);
        PRESETS.put("DYNAMIC", dynamic);
        
        // Cinematic preset
        TransitionConfig cinematic = new TransitionConfig();
        cinematic.setEasingFunction("EASE_IN_OUT");
        cinematic.setZoomLevel(1.05f);
        cinematic.setBlurAmount(1.0f);
        cinematic.setShakeIntensity(0.0f);
        cinematic.setTransitionDuration(2.0f);
        cinematic.setFeatherRadius(15.0f);
        PRESETS.put("CINEMATIC", cinematic);
        
        // Magic preset (AI-powered)
        TransitionConfig magic = new TransitionConfig();
        magic.setEasingFunction("EASE_IN_OUT");
        magic.setZoomLevel(1.15f);
        magic.setBlurAmount(3.0f);
        magic.setShakeIntensity(0.5f);
        magic.setTransitionDuration(1.2f);
        magic.setUseObjectDetection(true);
        magic.setFeatherRadius(12.0f);
        magic.setMaskShrinkFactor(0.92f);
        PRESETS.put("MAGIC", magic);
    }
    
    /**
     * Load a preset configuration
     */
    public static TransitionConfig loadPreset(String presetName) {
        TransitionConfig preset = PRESETS.get(presetName.toUpperCase());
        if (preset != null) {
            return preset.copy();
        }
        return new TransitionConfig(); // Return default if preset not found
    }
    
    /**
     * Create a copy of this configuration
     */
    public TransitionConfig copy() {
        TransitionConfig copy = new TransitionConfig();
        copy.easingFunction = this.easingFunction;
        copy.zoomLevel = this.zoomLevel;
        copy.blurAmount = this.blurAmount;
        copy.shakeIntensity = this.shakeIntensity;
        copy.transitionDuration = this.transitionDuration;
        copy.featherRadius = this.featherRadius;
        copy.maskShrinkFactor = this.maskShrinkFactor;
        copy.useObjectDetection = this.useObjectDetection;
        copy.objectConfidenceThreshold = this.objectConfidenceThreshold;
        copy.targetObjectClass = this.targetObjectClass;
        copy.enableExposureMatching = this.enableExposureMatching;
        copy.exposureBlendFactor = this.exposureBlendFactor;
        return copy;
    }
    
    /**
     * Apply easing function to progress value
     */
    public double applyEasing(double progress) {
        switch (easingFunction.toUpperCase()) {
            case "LINEAR":
                return progress;
            case "EASE_IN":
                return progress * progress;
            case "EASE_OUT":
                return 1.0 - Math.pow(1.0 - progress, 2.0);
            case "EASE_IN_OUT":
                return progress * progress * (3.0 - 2.0 * progress);
            case "EASE_IN_CUBIC":
                return progress * progress * progress;
            case "EASE_OUT_CUBIC":
                return 1.0 - Math.pow(1.0 - progress, 3.0);
            case "EASE_IN_OUT_CUBIC":
                return progress < 0.5 ? 4.0 * progress * progress * progress : 
                       1.0 - Math.pow(-2.0 * progress + 2.0, 3.0) / 2.0;
            case "BOUNCE":
                return bounceEasing(progress);
            default:
                return progress * progress * (3.0 - 2.0 * progress); // Default to ease-in-out
        }
    }
    
    /**
     * Bounce easing function
     */
    private double bounceEasing(double progress) {
        if (progress < 1.0 / 2.75) {
            return 7.5625 * progress * progress;
        } else if (progress < 2.0 / 2.75) {
            progress -= 1.5 / 2.75;
            return 7.5625 * progress * progress + 0.75;
        } else if (progress < 2.5 / 2.75) {
            progress -= 2.25 / 2.75;
            return 7.5625 * progress * progress + 0.9375;
        } else {
            progress -= 2.625 / 2.75;
            return 7.5625 * progress * progress + 0.984375;
        }
    }
    
    // Getters and setters
    public String getEasingFunction() { return easingFunction; }
    public void setEasingFunction(String easingFunction) { this.easingFunction = easingFunction; }
    
    public float getZoomLevel() { return zoomLevel; }
    public void setZoomLevel(float zoomLevel) { this.zoomLevel = zoomLevel; }
    
    public float getBlurAmount() { return blurAmount; }
    public void setBlurAmount(float blurAmount) { this.blurAmount = blurAmount; }
    
    public float getShakeIntensity() { return shakeIntensity; }
    public void setShakeIntensity(float shakeIntensity) { this.shakeIntensity = shakeIntensity; }
    
    public float getTransitionDuration() { return transitionDuration; }
    public void setTransitionDuration(float transitionDuration) { this.transitionDuration = transitionDuration; }
    
    public float getFeatherRadius() { return featherRadius; }
    public void setFeatherRadius(float featherRadius) { this.featherRadius = featherRadius; }
    
    public float getMaskShrinkFactor() { return maskShrinkFactor; }
    public void setMaskShrinkFactor(float maskShrinkFactor) { this.maskShrinkFactor = maskShrinkFactor; }
    
    public boolean isUseObjectDetection() { return useObjectDetection; }
    public void setUseObjectDetection(boolean useObjectDetection) { this.useObjectDetection = useObjectDetection; }
    
    public float getObjectConfidenceThreshold() { return objectConfidenceThreshold; }
    public void setObjectConfidenceThreshold(float objectConfidenceThreshold) { 
        this.objectConfidenceThreshold = objectConfidenceThreshold; 
    }
    
    public String getTargetObjectClass() { return targetObjectClass; }
    public void setTargetObjectClass(String targetObjectClass) { this.targetObjectClass = targetObjectClass; }
    
    public boolean isEnableExposureMatching() { return enableExposureMatching; }
    public void setEnableExposureMatching(boolean enableExposureMatching) { 
        this.enableExposureMatching = enableExposureMatching; 
    }
    
    public float getExposureBlendFactor() { return exposureBlendFactor; }
    public void setExposureBlendFactor(float exposureBlendFactor) { 
        this.exposureBlendFactor = exposureBlendFactor; 
    }
    
    @Override
    public String toString() {
        return String.format("TransitionConfig{easing=%s, zoom=%.2f, blur=%.2f, shake=%.2f, duration=%.2f, " +
                           "objectDetection=%b, feather=%.2f, shrink=%.2f}", 
                           easingFunction, zoomLevel, blurAmount, shakeIntensity, transitionDuration,
                           useObjectDetection, featherRadius, maskShrinkFactor);
    }
}
