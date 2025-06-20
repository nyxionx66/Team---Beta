package dev.mindle.team.gui.utils;

import net.minecraft.util.math.MathHelper;

public class AnimationUtils {
    
    // Animation easing functions
    public static float easeInCubic(float t) {
        return t * t * t;
    }
    
    public static float easeOutCubic(float t) {
        float f = 1 - t;
        return 1 - f * f * f;
    }
    
    public static float easeInOutCubic(float t) {
        if (t < 0.5f) {
            return 4 * t * t * t;
        } else {
            float f = (2 * t) - 2;
            return 1 + f * f * f / 2;
        }
    }
    
    public static float easeInQuad(float t) {
        return t * t;
    }
    
    public static float easeOutQuad(float t) {
        return 1 - (1 - t) * (1 - t);
    }
    
    public static float easeInOutQuad(float t) {
        if (t < 0.5f) {
            return 2 * t * t;
        } else {
            return 1 - (float) Math.pow(-2 * t + 2, 2) / 2;
        }
    }
    
    public static float easeInExpo(float t) {
        return t == 0 ? 0 : (float) Math.pow(2, 10 * (t - 1));
    }
    
    public static float easeOutExpo(float t) {
        return t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
    }
    
    public static float easeInOutExpo(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        if (t < 0.5f) {
            return (float) Math.pow(2, 20 * t - 10) / 2;
        } else {
            return (2 - (float) Math.pow(2, -20 * t + 10)) / 2;
        }
    }
    
    // Animation update helpers
    public static float updateAnimation(float current, float target, float speed) {
        return updateAnimation(current, target, speed, 0.001f);
    }
    
    public static float updateAnimation(float current, float target, float speed, float threshold) {
        if (Math.abs(current - target) < threshold) {
            return target;
        }
        
        float difference = target - current;
        return current + difference * speed;
    }
    
    public static float lerp(float start, float end, float factor) {
        return start + (end - start) * MathHelper.clamp(factor, 0.0f, 1.0f);
    }
    
    public static double lerp(double start, double end, double factor) {
        return start + (end - start) * MathHelper.clamp(factor, 0.0, 1.0);
    }
    
    public static int lerpInt(int start, int end, float factor) {
        return Math.round(lerp(start, end, factor));
    }
    
    // Spring animation
    public static class SpringAnimation {
        private float value;
        private float velocity;
        private final float stiffness;
        private final float damping;
        
        public SpringAnimation(float stiffness, float damping) {
            this.stiffness = stiffness;
            this.damping = damping;
            this.value = 0;
            this.velocity = 0;
        }
        
        public void update(float target, float deltaTime) {
            float force = (target - value) * stiffness;
            velocity += force * deltaTime;
            velocity *= (1 - damping * deltaTime);
            value += velocity * deltaTime;
        }
        
        public float getValue() {
            return value;
        }
        
        public void setValue(float value) {
            this.value = value;
            this.velocity = 0;
        }
        
        public boolean isAtRest(float threshold) {
            return Math.abs(velocity) < threshold;
        }
    }
    
    // Smooth hover animation
    public static class HoverAnimation {
        private float progress = 0.0f;
        private boolean hovered = false;
        
        public void update(boolean isHovered, float deltaTime) {
            this.hovered = isHovered;
            float target = isHovered ? 1.0f : 0.0f;
            progress = updateAnimation(progress, target, deltaTime * 6.0f);
        }
        
        public float getProgress() {
            return progress;
        }
        
        public float getEasedProgress() {
            return easeOutCubic(progress);
        }
        
        public boolean isHovered() {
            return hovered;
        }
    }
    
    // Scale animation for buttons
    public static class ScaleAnimation {
        private float scale = 1.0f;
        private boolean pressed = false;
        
        public void update(boolean isPressed, boolean isHovered, float deltaTime) {
            float targetScale = 1.0f;
            
            if (isPressed) {
                targetScale = 0.95f;
            } else if (isHovered) {
                targetScale = 1.05f;
            }
            
            scale = updateAnimation(scale, targetScale, deltaTime * 8.0f);
            this.pressed = isPressed;
        }
        
        public float getScale() {
            return scale;
        }
        
        public boolean isPressed() {
            return pressed;
        }
    }
    
    // Fade animation
    public static class FadeAnimation {
        private float alpha = 0.0f;
        private boolean visible = false;
        
        public void update(boolean isVisible, float deltaTime) {
            this.visible = isVisible;
            float target = isVisible ? 1.0f : 0.0f;
            alpha = updateAnimation(alpha, target, deltaTime * 4.0f);
        }
        
        public void setImmediate(boolean visible) {
            this.visible = visible;
            this.alpha = visible ? 1.0f : 0.0f;
        }
        
        public float getAlpha() {
            return alpha;
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public boolean isFullyVisible() {
            return alpha > 0.99f;
        }
        
        public boolean isFullyHidden() {
            return alpha < 0.01f;
        }
    }
    
    // Slide animation
    public static class SlideAnimation {
        private float offset = 0.0f;
        private boolean expanded = false;
        
        public void update(boolean isExpanded, float deltaTime) {
            this.expanded = isExpanded;
            float target = isExpanded ? 0.0f : -1.0f;
            offset = updateAnimation(offset, target, deltaTime * 5.0f);
        }
        
        public float getOffset() {
            return offset;
        }
        
        public float getEasedOffset() {
            return easeOutCubic(Math.abs(offset));
        }
        
        public boolean isExpanded() {
            return expanded;
        }
    }
}