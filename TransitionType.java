/**
 * Enum defining all available video transition types
 */
public enum TransitionType {
    // Fade transitions
    FADE_IN,
    FADE_OUT,
    CROSSFADE,

    // Slide transitions
    SLIDE_LEFT,
    SLIDE_RIGHT,
    SLIDE_UP,
    SLIDE_DOWN,

    // Wipe transitions
    WIPE_LEFT,
    WIPE_RIGHT,
    WIPE_UP,
    WIPE_DOWN,
    WIPE_CIRCLE,

    // Zoom transitions
    ZOOM_IN,
    ZOOM_OUT,

    // Rotation transitions
    ROTATE_CLOCKWISE,
    ROTATE_COUNTERCLOCKWISE,

    // Effect transitions
    BLUR_TRANSITION,
    PIXELATE_TRANSITION,

    // Advanced transitions
    DISSOLVE,
    PUSH_LEFT,
    PUSH_RIGHT,
    IRIS_IN,
    IRIS_OUT,

    // AI-Powered Object-Aware Transitions
    OBJECT_REVEAL,
    OBJECT_ZOOM_IN,
    OBJECT_ZOOM_OUT,
    OBJECT_SLIDE_LEFT,
    OBJECT_SLIDE_RIGHT,
    OBJECT_FADE_IN,
    OBJECT_FADE_OUT,
    OBJECT_ROTATE_IN,
    OBJECT_ROTATE_OUT,
    OBJECT_SCALE_TRANSITION
}
