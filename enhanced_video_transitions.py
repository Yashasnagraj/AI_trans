#!/usr/bin/env python3
"""
Enhanced Video Transition Engine - Quality Improvements Demo
Samsung PRISM Internship Implementation

This script demonstrates the quality improvements implemented in the JavaCV-based engine:
1. Enhanced dissolve with smooth cosine interpolation
2. Progressive blur with dynamic kernel sizing
3. Enhanced frame blending with cosine-smoothed alpha
4. New whip pan transitions with directional motion blur
"""

import cv2
import numpy as np
import os
import math
from pathlib import Path

class EnhancedVideoTransitions:
    def __init__(self, output_width=1280, output_height=720, fps=30):
        self.output_width = output_width
        self.output_height = output_height
        self.fps = fps
        
    def cosine_interpolation(self, progress):
        """Enhanced easing using cosine interpolation for natural transitions"""
        return (1 - math.cos(progress * math.pi)) / 2
    
    def progressive_blur_intensity(self, progress):
        """Calculate progressive blur intensity using parabolic curve"""
        return 4.0 * progress * (1.0 - progress)
    
    def motion_blur_kernel_size(self, motion_intensity):
        """Calculate motion blur kernel size"""
        return max(3, int(motion_intensity * 30) + 1)
    
    def enhanced_blend_frames(self, frame1, frame2, alpha):
        """Enhanced frame blending with smooth cosine alpha progression"""
        smooth_alpha = self.cosine_interpolation(alpha)
        return cv2.addWeighted(frame1, 1.0 - smooth_alpha, frame2, smooth_alpha, 0)
    
    def apply_progressive_blur(self, frame, blur_intensity):
        """Apply progressive blur with dynamic kernel sizing"""
        kernel_size = max(3, int(blur_intensity * 30) + 1)
        if kernel_size % 2 == 0:
            kernel_size += 1
        return cv2.GaussianBlur(frame, (kernel_size, kernel_size), 0)
    
    def apply_motion_blur(self, frame, motion_intensity, direction):
        """Apply directional motion blur for whip pan effects"""
        kernel_size = self.motion_blur_kernel_size(motion_intensity)
        if kernel_size % 2 == 0:
            kernel_size += 1
            
        if direction in ['left', 'right', 'horizontal']:
            # Horizontal motion blur
            kernel = np.zeros((1, kernel_size))
            kernel[0, :] = 1.0 / kernel_size
            return cv2.filter2D(frame, -1, kernel)
        elif direction in ['up', 'down', 'vertical']:
            # Vertical motion blur
            kernel = np.zeros((kernel_size, 1))
            kernel[:, 0] = 1.0 / kernel_size
            return cv2.filter2D(frame, -1, kernel)
        else:
            return frame
    
    def create_enhanced_dissolve_transition(self, video1_path, video2_path, output_path, duration_frames=60):
        """Create enhanced dissolve transition with smooth cosine interpolation"""
        print("🎭 Creating Enhanced Dissolve Transition...")
        
        cap1 = cv2.VideoCapture(video1_path)
        cap2 = cv2.VideoCapture(video2_path)
        
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(output_path, fourcc, self.fps, (self.output_width, self.output_height))
        
        for frame_num in range(duration_frames):
            ret1, frame1 = cap1.read()
            ret2, frame2 = cap2.read()
            
            if not ret1 or not ret2:
                break
                
            # Resize frames
            frame1 = cv2.resize(frame1, (self.output_width, self.output_height))
            frame2 = cv2.resize(frame2, (self.output_width, self.output_height))
            
            # Calculate progress and apply enhanced blending
            progress = frame_num / (duration_frames - 1)
            result_frame = self.enhanced_blend_frames(frame1, frame2, progress)
            
            # Add progress indicator
            cv2.putText(result_frame, f"Enhanced Dissolve - Progress: {progress:.2f}", 
                       (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
            cv2.putText(result_frame, f"Cosine Alpha: {self.cosine_interpolation(progress):.3f}", 
                       (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)
            
            out.write(result_frame)
        
        cap1.release()
        cap2.release()
        out.release()
        print(f"   ✅ Enhanced dissolve saved to: {output_path}")
    
    def create_progressive_blur_transition(self, video1_path, video2_path, output_path, duration_frames=60):
        """Create progressive blur transition with dynamic kernel sizing"""
        print("🌀 Creating Progressive Blur Transition...")
        
        cap1 = cv2.VideoCapture(video1_path)
        cap2 = cv2.VideoCapture(video2_path)
        
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(output_path, fourcc, self.fps, (self.output_width, self.output_height))
        
        for frame_num in range(duration_frames):
            ret1, frame1 = cap1.read()
            ret2, frame2 = cap2.read()
            
            if not ret1 or not ret2:
                break
                
            # Resize frames
            frame1 = cv2.resize(frame1, (self.output_width, self.output_height))
            frame2 = cv2.resize(frame2, (self.output_width, self.output_height))
            
            # Calculate progress and blur intensity
            progress = frame_num / (duration_frames - 1)
            blur_intensity = self.progressive_blur_intensity(progress)
            
            # Apply progressive blur to both frames
            blurred_frame1 = self.apply_progressive_blur(frame1, blur_intensity)
            blurred_frame2 = self.apply_progressive_blur(frame2, blur_intensity)
            
            # Enhanced blending
            result_frame = self.enhanced_blend_frames(blurred_frame1, blurred_frame2, progress)
            
            # Add progress indicator
            cv2.putText(result_frame, f"Progressive Blur - Intensity: {blur_intensity:.3f}", 
                       (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
            cv2.putText(result_frame, f"Kernel Size: {max(3, int(blur_intensity * 30) + 1)}", 
                       (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)
            
            out.write(result_frame)
        
        cap1.release()
        cap2.release()
        out.release()
        print(f"   ✅ Progressive blur saved to: {output_path}")
    
    def create_whip_pan_transition(self, video1_path, video2_path, output_path, direction='left', duration_frames=60):
        """Create whip pan transition with directional motion blur"""
        print(f"📹 Creating Whip Pan {direction.upper()} Transition...")
        
        cap1 = cv2.VideoCapture(video1_path)
        cap2 = cv2.VideoCapture(video2_path)
        
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(output_path, fourcc, self.fps, (self.output_width, self.output_height))
        
        for frame_num in range(duration_frames):
            ret1, frame1 = cap1.read()
            ret2, frame2 = cap2.read()
            
            if not ret1 or not ret2:
                break
                
            # Resize frames
            frame1 = cv2.resize(frame1, (self.output_width, self.output_height))
            frame2 = cv2.resize(frame2, (self.output_width, self.output_height))
            
            # Calculate progress and motion intensity
            progress = frame_num / (duration_frames - 1)
            motion_intensity = 4.0 * progress * (1.0 - progress)  # Peaks at middle
            
            # Apply motion blur
            blurred_frame1 = self.apply_motion_blur(frame1, motion_intensity, direction)
            blurred_frame2 = self.apply_motion_blur(frame2, motion_intensity, direction)
            
            # Calculate translation offset
            if direction in ['left', 'right']:
                offset = int(self.output_width * progress * (1 if direction == 'right' else -1))
                translated_frame1 = np.roll(blurred_frame1, offset, axis=1)
                translated_frame2 = np.roll(blurred_frame2, offset - self.output_width, axis=1)
            else:  # up, down
                offset = int(self.output_height * progress * (1 if direction == 'down' else -1))
                translated_frame1 = np.roll(blurred_frame1, offset, axis=0)
                translated_frame2 = np.roll(blurred_frame2, offset - self.output_height, axis=0)
            
            # Enhanced blending
            result_frame = self.enhanced_blend_frames(translated_frame1, translated_frame2, progress)
            
            # Add progress indicator
            cv2.putText(result_frame, f"Whip Pan {direction.upper()} - Motion: {motion_intensity:.3f}", 
                       (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)
            cv2.putText(result_frame, f"Kernel Size: {self.motion_blur_kernel_size(motion_intensity)}", 
                       (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 255), 2)
            
            out.write(result_frame)
        
        cap1.release()
        cap2.release()
        out.release()
        print(f"   ✅ Whip pan {direction} saved to: {output_path}")
    
    def create_comparison_demo(self, video1_path, video2_path, output_path, duration_frames=60):
        """Create side-by-side comparison of old vs new methods"""
        print("📊 Creating Before/After Comparison Demo...")
        
        cap1 = cv2.VideoCapture(video1_path)
        cap2 = cv2.VideoCapture(video2_path)
        
        # Double width for side-by-side comparison
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(output_path, fourcc, self.fps, (self.output_width * 2, self.output_height))
        
        for frame_num in range(duration_frames):
            ret1, frame1 = cap1.read()
            ret2, frame2 = cap2.read()
            
            if not ret1 or not ret2:
                break
                
            # Resize frames
            frame1 = cv2.resize(frame1, (self.output_width, self.output_height))
            frame2 = cv2.resize(frame2, (self.output_width, self.output_height))
            
            progress = frame_num / (duration_frames - 1)
            
            # Left side: Linear blending (old method)
            linear_alpha = progress
            left_result = cv2.addWeighted(frame1, 1.0 - linear_alpha, frame2, linear_alpha, 0)
            
            # Right side: Enhanced cosine blending (new method)
            right_result = self.enhanced_blend_frames(frame1, frame2, progress)
            
            # Combine side by side
            comparison_frame = np.hstack([left_result, right_result])
            
            # Add labels
            cv2.putText(comparison_frame, "LINEAR (Old)", (10, 30), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2)
            cv2.putText(comparison_frame, "COSINE ENHANCED (New)", (self.output_width + 10, 30), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2)
            cv2.putText(comparison_frame, f"Progress: {progress:.2f}", (10, self.output_height - 20), 
                       cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 255), 2)
            
            out.write(comparison_frame)
        
        cap1.release()
        cap2.release()
        out.release()
        print(f"   ✅ Comparison demo saved to: {output_path}")

def main():
    print("🎬 AI Video Transition Engine - Quality Improvements Demo")
    print("========================================================")
    print("Samsung PRISM Internship - Enhanced Transition Quality")
    print()
    
    # Setup paths
    video1_path = "input_videos/video1.mp4"
    video2_path = "input_videos/video2.mp4"
    output_dir = Path("enhanced_transitions_output")
    output_dir.mkdir(exist_ok=True)
    
    # Check if input videos exist
    if not os.path.exists(video1_path) or not os.path.exists(video2_path):
        print("❌ Input videos not found!")
        return
    
    # Initialize the enhanced transition engine
    engine = EnhancedVideoTransitions(output_width=1280, output_height=720, fps=30)
    
    print("🚀 Generating Enhanced Transition Demonstrations...")
    print()
    
    # 1. Enhanced Dissolve Transition
    engine.create_enhanced_dissolve_transition(
        video1_path, video2_path, 
        str(output_dir / "enhanced_dissolve_demo.mp4"),
        duration_frames=90
    )
    
    # 2. Progressive Blur Transition
    engine.create_progressive_blur_transition(
        video1_path, video2_path,
        str(output_dir / "progressive_blur_demo.mp4"),
        duration_frames=90
    )
    
    # 3. Whip Pan Transitions
    for direction in ['left', 'right', 'up', 'down']:
        engine.create_whip_pan_transition(
            video1_path, video2_path,
            str(output_dir / f"whip_pan_{direction}_demo.mp4"),
            direction=direction,
            duration_frames=90
        )
    
    # 4. Before/After Comparison
    engine.create_comparison_demo(
        video1_path, video2_path,
        str(output_dir / "before_after_comparison.mp4"),
        duration_frames=90
    )
    
    print()
    print("🎉 Quality Improvements Demo Complete!")
    print("=====================================")
    print(f"📁 Output files saved to: {output_dir}/")
    print()
    print("📊 Quality Enhancements Demonstrated:")
    print("✅ Smooth cosine interpolation for dissolve transitions")
    print("✅ Progressive blur with dynamic kernel sizing")
    print("✅ Enhanced frame blending with cosine-smoothed alpha")
    print("✅ New whip pan transitions with directional motion blur")
    print()
    print("🎯 Result: Professional-grade transition quality achieved!")
    
    # List generated files
    print("\n📋 Generated Files:")
    for file in sorted(output_dir.glob("*.mp4")):
        print(f"   • {file.name}")

if __name__ == "__main__":
    main()