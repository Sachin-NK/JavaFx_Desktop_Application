package com.example.demo3;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Utility class for managing smooth view transitions and animations
 * Provides fade, slide, and scale transition effects for view changes
 */
public class TransitionManager {
    
    private static final Duration DEFAULT_DURATION = Duration.millis(300);
    private static final Duration FAST_DURATION = Duration.millis(200);
    private static final Duration SLOW_DURATION = Duration.millis(500);
    
    /**
     * Fade transition between two views
     */
    public static void fadeTransition(StackPane container, Node oldView, Node newView) {
        fadeTransition(container, oldView, newView, DEFAULT_DURATION, null);
    }
    
    /**
     * Fade transition with callback
     */
    public static void fadeTransition(StackPane container, Node oldView, Node newView, 
                                    Duration duration, Runnable onComplete) {
        if (oldView == null) {
            // No old view, just fade in the new view
            newView.setOpacity(0);
            container.getChildren().setAll(newView);
            
            FadeTransition fadeIn = new FadeTransition(duration, newView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(e -> {
                if (onComplete != null) onComplete.run();
            });
            fadeIn.play();
            return;
        }
        
        // Fade out old view
        FadeTransition fadeOut = new FadeTransition(duration.divide(2), oldView);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        fadeOut.setOnFinished(e -> {
            // Replace view and fade in new view
            newView.setOpacity(0);
            container.getChildren().setAll(newView);
            
            FadeTransition fadeIn = new FadeTransition(duration.divide(2), newView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(e2 -> {
                if (onComplete != null) onComplete.run();
            });
            fadeIn.play();
        });
        
        fadeOut.play();
    }
    
    /**
     * Slide transition from right to left
     */
    public static void slideTransition(StackPane container, Node oldView, Node newView) {
        slideTransition(container, oldView, newView, SlideDirection.LEFT, DEFAULT_DURATION, null);
    }
    
    /**
     * Slide transition with direction and callback
     */
    public static void slideTransition(StackPane container, Node oldView, Node newView, 
                                     SlideDirection direction, Duration duration, Runnable onComplete) {
        if (oldView == null) {
            // No old view, just slide in the new view
            setupSlideIn(newView, direction);
            container.getChildren().setAll(newView);
            animateSlideIn(newView, direction, duration, onComplete);
            return;
        }
        
        // Setup new view position
        setupSlideIn(newView, direction);
        container.getChildren().add(newView);
        
        // Create parallel transitions
        Timeline slideOut = createSlideOut(oldView, direction, duration);
        Timeline slideIn = createSlideIn(newView, direction, duration);
        
        ParallelTransition parallel = new ParallelTransition(slideOut, slideIn);
        parallel.setOnFinished(e -> {
            container.getChildren().remove(oldView);
            if (onComplete != null) onComplete.run();
        });
        
        parallel.play();
    }
    
    /**
     * Scale transition (zoom effect)
     */
    public static void scaleTransition(StackPane container, Node oldView, Node newView) {
        scaleTransition(container, oldView, newView, DEFAULT_DURATION, null);
    }
    
    /**
     * Scale transition with callback
     */
    public static void scaleTransition(StackPane container, Node oldView, Node newView, 
                                     Duration duration, Runnable onComplete) {
        if (oldView == null) {
            // No old view, just scale in the new view
            newView.setScaleX(0.8);
            newView.setScaleY(0.8);
            newView.setOpacity(0);
            container.getChildren().setAll(newView);
            
            ScaleTransition scaleIn = new ScaleTransition(duration, newView);
            scaleIn.setFromX(0.8);
            scaleIn.setFromY(0.8);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            
            FadeTransition fadeIn = new FadeTransition(duration, newView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            ParallelTransition parallel = new ParallelTransition(scaleIn, fadeIn);
            parallel.setOnFinished(e -> {
                if (onComplete != null) onComplete.run();
            });
            parallel.play();
            return;
        }
        
        // Scale out old view and scale in new view
        ScaleTransition scaleOut = new ScaleTransition(duration.divide(2), oldView);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);
        
        FadeTransition fadeOut = new FadeTransition(duration.divide(2), oldView);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        
        ParallelTransition exitTransition = new ParallelTransition(scaleOut, fadeOut);
        exitTransition.setOnFinished(e -> {
            newView.setScaleX(0.8);
            newView.setScaleY(0.8);
            newView.setOpacity(0);
            container.getChildren().setAll(newView);
            
            ScaleTransition scaleIn = new ScaleTransition(duration.divide(2), newView);
            scaleIn.setFromX(0.8);
            scaleIn.setFromY(0.8);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            
            FadeTransition fadeIn = new FadeTransition(duration.divide(2), newView);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            ParallelTransition enterTransition = new ParallelTransition(scaleIn, fadeIn);
            enterTransition.setOnFinished(e2 -> {
                if (onComplete != null) onComplete.run();
            });
            enterTransition.play();
        });
        
        exitTransition.play();
    }
    
    /**
     * Loading animation for content loading states
     */
    public static Timeline createLoadingAnimation(Node loadingIndicator) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(1), loadingIndicator);
        rotate.setByAngle(360);
        rotate.setCycleCount(Timeline.INDEFINITE);
        
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), loadingIndicator);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        
        ParallelTransition loading = new ParallelTransition(rotate, fade);
        return new Timeline(new KeyFrame(Duration.ZERO, e -> loading.play()));
    }
    
    /**
     * Bounce animation for interactive elements
     */
    public static void bounceAnimation(Node node) {
        bounceAnimation(node, null);
    }
    
    /**
     * Bounce animation with callback
     */
    public static void bounceAnimation(Node node, Runnable onComplete) {
        ScaleTransition bounce = new ScaleTransition(Duration.millis(100), node);
        bounce.setFromX(1.0);
        bounce.setFromY(1.0);
        bounce.setToX(1.1);
        bounce.setToY(1.1);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);
        bounce.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        bounce.play();
    }
    
    /**
     * Shake animation for error states
     */
    public static void shakeAnimation(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setAutoReverse(true);
        shake.setCycleCount(6);
        shake.setOnFinished(e -> node.setTranslateX(0));
        shake.play();
    }
    
    // Helper methods
    private static void setupSlideIn(Node view, SlideDirection direction) {
        switch (direction) {
            case LEFT:
                view.setTranslateX(view.getBoundsInLocal().getWidth());
                break;
            case RIGHT:
                view.setTranslateX(-view.getBoundsInLocal().getWidth());
                break;
            case UP:
                view.setTranslateY(view.getBoundsInLocal().getHeight());
                break;
            case DOWN:
                view.setTranslateY(-view.getBoundsInLocal().getHeight());
                break;
        }
    }
    
    private static void animateSlideIn(Node view, SlideDirection direction, Duration duration, Runnable onComplete) {
        TranslateTransition slide = new TranslateTransition(duration, view);
        slide.setToX(0);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.setOnFinished(e -> {
            if (onComplete != null) onComplete.run();
        });
        slide.play();
    }
    
    private static Timeline createSlideOut(Node view, SlideDirection direction, Duration duration) {
        double targetX = 0, targetY = 0;
        switch (direction) {
            case LEFT:
                targetX = -view.getBoundsInLocal().getWidth();
                break;
            case RIGHT:
                targetX = view.getBoundsInLocal().getWidth();
                break;
            case UP:
                targetY = -view.getBoundsInLocal().getHeight();
                break;
            case DOWN:
                targetY = view.getBoundsInLocal().getHeight();
                break;
        }
        
        return new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(view.translateXProperty(), 0),
                new KeyValue(view.translateYProperty(), 0)
            ),
            new KeyFrame(duration,
                new KeyValue(view.translateXProperty(), targetX, Interpolator.EASE_IN),
                new KeyValue(view.translateYProperty(), targetY, Interpolator.EASE_IN)
            )
        );
    }
    
    private static Timeline createSlideIn(Node view, SlideDirection direction, Duration duration) {
        return new Timeline(
            new KeyFrame(duration,
                new KeyValue(view.translateXProperty(), 0, Interpolator.EASE_OUT),
                new KeyValue(view.translateYProperty(), 0, Interpolator.EASE_OUT)
            )
        );
    }
    
    /**
     * Slide direction enumeration
     */
    public enum SlideDirection {
        LEFT, RIGHT, UP, DOWN
    }
    
    /**
     * Transition type enumeration
     */
    public enum TransitionType {
        FADE, SLIDE, SCALE
    }
    
    /**
     * Get duration based on speed preference
     */
    public static Duration getDuration(String speed) {
        switch (speed.toLowerCase()) {
            case "fast":
                return FAST_DURATION;
            case "slow":
                return SLOW_DURATION;
            default:
                return DEFAULT_DURATION;
        }
    }
}