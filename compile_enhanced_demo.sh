#!/bin/bash

echo "🔧 Compiling Enhanced JavaCV Transition Demo..."
echo "=============================================="

# Set classpath with all JavaCV libraries
CLASSPATH=".:javacv-1.5.8.jar:opencv-4.6.0-1.5.8.jar:ffmpeg-5.1.2-1.5.8.jar"

echo "📦 Classpath: $CLASSPATH"
echo

# Compile the enhanced demo
echo "🔨 Compiling EnhancedTransitionDemo.java..."
javac -cp "$CLASSPATH" EnhancedTransitionDemo.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo
    echo "🚀 Running Enhanced Transition Demo..."
    echo "====================================="
    java -cp "$CLASSPATH" EnhancedTransitionDemo
else
    echo "❌ Compilation failed!"
    exit 1
fi