#!/bin/bash
echo "========================================"
echo "AI Video Transition Engine - Enhanced"
echo "Quality Improvements Compilation"
echo "Samsung PRISM Internship"
echo "========================================"
echo

# Check if JavaCV exists
if [ ! -f "javacv-1.5.8.jar" ]; then
    echo "ERROR: JavaCV library not found!"
    echo "Please ensure javacv-1.5.8.jar is in this directory."
    exit 1
fi

echo "Compiling Enhanced Transition Engine..."
echo

# Compile core enhanced classes
echo "[1/4] Compiling core enhanced classes..."
javac -cp "javacv-1.5.8.jar" VideoProcessor.java BaseTransition.java TransitionType.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile core classes!"
    exit 1
fi

echo "[2/4] Compiling enhanced transition classes..."
javac -cp "javacv-1.5.8.jar" FadeTransition.java EffectTransition.java WhipPanTransition.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile transition classes!"
    exit 1
fi

echo "[3/4] Compiling quality demonstration classes..."
javac -cp "javacv-1.5.8.jar" QualityMathDemo.java RealVideoQualityDemo.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile demo classes!"
    exit 1
fi

echo "[4/4] Verifying compilation..."
if [ -f "VideoProcessor.class" ]; then
    echo "  ✓ VideoProcessor.class"
else
    echo "  ✗ VideoProcessor.class MISSING"
fi

if [ -f "FadeTransition.class" ]; then
    echo "  ✓ FadeTransition.class"
else
    echo "  ✗ FadeTransition.class MISSING"
fi

if [ -f "EffectTransition.class" ]; then
    echo "  ✓ EffectTransition.class"
else
    echo "  ✗ EffectTransition.class MISSING"
fi

if [ -f "WhipPanTransition.class" ]; then
    echo "  ✓ WhipPanTransition.class"
else
    echo "  ✗ WhipPanTransition.class MISSING"
fi

if [ -f "QualityMathDemo.class" ]; then
    echo "  ✓ QualityMathDemo.class"
else
    echo "  ✗ QualityMathDemo.class MISSING"
fi

echo
echo "========================================"
echo "COMPILATION COMPLETE!"
echo "========================================"
echo
echo "Enhanced Features Available:"
echo "✓ Smooth cosine interpolation for dissolve transitions"
echo "✓ Progressive blur with dynamic kernel sizing"
echo "✓ Enhanced frame blending with cosine-smoothed alpha"
echo "✓ New whip pan transitions with directional motion blur"
echo
echo "To test mathematical functions:"
echo "  java -cp \".:javacv-1.5.8.jar\" QualityMathDemo"
echo
echo "To process real videos (requires full JavaCV):"
echo "  java -cp \".:javacv-1.5.8.jar\" RealVideoQualityDemo"
echo
echo "Quality Improvements Successfully Implemented!"
echo "Samsung PRISM Internship - Mission Accomplished!"
echo