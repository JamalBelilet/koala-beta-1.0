package koala.gui.caissier.controller.animation;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Created by bjama on 3/17/2017.
 */
public class Animator {
    public ParallelTransition parallelTransition = new ParallelTransition();;
    public ParallelTransition parallelTransition_reverse = new ParallelTransition();;
    public void animate(ImageView imageView) {
        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(500), imageView);
        scaleTransition.setToX(1.1f);
        scaleTransition.setToY(1.1f);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(1);

        scaleTransition.setAutoReverse(false);

        ScaleTransition scaleTransition2 =
                new ScaleTransition(Duration.millis(480), imageView.getClip());
        scaleTransition2.setToX(.902f);
        scaleTransition2.setToY(.902f);
        scaleTransition2.setAutoReverse(false);
        parallelTransition.getChildren().addAll(
                scaleTransition,
                scaleTransition2
        );
        parallelTransition.setCycleCount(1);

        class AnimationBooleanInterpolator extends Interpolator {
            @Override
            protected double curve(double t) {
                return (t*t)*0.5 ;
            }
        }
        parallelTransition.setInterpolator(new AnimationBooleanInterpolator());
        ScaleTransition scaleTransition_reverse =
                new ScaleTransition(Duration.millis(500), imageView);
        scaleTransition_reverse.setToX(1f);
        scaleTransition_reverse.setToY(1f);
        scaleTransition_reverse.setCycleCount(1);
        scaleTransition_reverse.setAutoReverse(true);
//            scaleTransition.setCycleCount(Timeline.INDEFINITE);
        scaleTransition_reverse.setCycleCount(1);

        scaleTransition_reverse.setAutoReverse(false);

        ScaleTransition scaleTransition_reverse2 =
                new ScaleTransition(Duration.millis(480), imageView.getClip());
        scaleTransition_reverse2.setToX(1f);
        scaleTransition_reverse2.setToY(1f);
        scaleTransition_reverse2.setAutoReverse(false);
//            scaleTransition.setCycleCount(Timeline.INDEFINITE);

        parallelTransition_reverse.getChildren().addAll(
                scaleTransition_reverse,
                scaleTransition_reverse2
        );
        parallelTransition_reverse.setCycleCount(1);

        parallelTransition_reverse.setInterpolator(new AnimationBooleanInterpolator());
    }
}
