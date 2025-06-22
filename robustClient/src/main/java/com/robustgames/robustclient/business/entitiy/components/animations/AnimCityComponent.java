package com.robustgames.robustclient.business.entitiy.components.animations;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

public class AnimCityComponent extends Component {
    private Texture city1Texture;
    private final AnimatedTexture animatedTexture;
    private final AnimationChannel city1Animation;
    private final AnimationChannel city2Animation;
    private final AnimationChannel city3Animation;
    private final AnimationChannel city1AttackAnimation;
    private final AnimationChannel city2AttackAnimation;
    private final AnimationChannel city3AttackAnimation;
    private final AnimationChannel city4Animation;
    int currentHP;
    int maxHP;
    boolean underAttack;

    public AnimCityComponent(boolean underAttack) {
        //city1Texture = FXGL.getAssetLoader().loadTexture("city1.png");
        city1AttackAnimation = new AnimationChannel(FXGL.image("city1_attack.png"), 1, 128,128, Duration.seconds(0), 0, 0);
        city2AttackAnimation = new AnimationChannel(FXGL.image("city2_attack.png"), 19, 128,128, Duration.seconds(1.9), 0, 18);
        city3AttackAnimation = new AnimationChannel(FXGL.image("city3_attack.png"), 19, 128,128, Duration.seconds(1.9), 0, 18);
        city1Animation = new AnimationChannel(FXGL.image("city1.png"), 1, 128,128, Duration.seconds(0), 0, 0);
        city2Animation = new AnimationChannel(FXGL.image("city2.png"), 19, 128,128, Duration.seconds(1.9), 0, 18);
        city3Animation = new AnimationChannel(FXGL.image("city3.png"), 19, 128,128, Duration.seconds(1.9), 0, 18);
        city4Animation = new AnimationChannel(FXGL.image("city4.png"), 19, 128,128, Duration.seconds(1.9), 0, 18);
        animatedTexture = new AnimatedTexture(city1Animation);
        this.underAttack = underAttack;
    }

    @Override
    public void onAdded() {
        //city1Texture = entity.getViewComponent().getChild(0,Texture.class);
        entity.getViewComponent().addChild(animatedTexture);
        currentHP = entity.getComponent(HealthIntComponent.class).getValue();
        maxHP = entity.getComponent(HealthIntComponent.class).getMaxValue();
        //entity.getViewComponent().removeChild(city1Texture);

    }
    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);
        currentHP = entity.getComponent(HealthIntComponent.class).getValue();
        if (animatedTexture.getAnimationChannel() == city1Animation && underAttack) {
            animatedTexture.loopAnimationChannel(city1AttackAnimation);
            System.out.println("case 1");
        }
        if (maxHP - 1 == currentHP) {
            System.out.println("case 2 HP " + animatedTexture.getAnimationChannel().getImage().getUrl());
            System.out.println(underAttack);

            if (animatedTexture.getAnimationChannel() == city2Animation && underAttack){
                System.err.println("case 2 Attack");
                animatedTexture.loopAnimationChannel(city2AttackAnimation);
            }
            else if (animatedTexture.getAnimationChannel() == city1Animation){
                System.err.println("case 2 to city 2");
                animatedTexture.loopAnimationChannel(city2Animation);
            }
        }
        else if (maxHP - 2 == currentHP) {
            System.out.println("case 3 HP");
            if (animatedTexture.getAnimationChannel() == city3Animation && underAttack){
                System.err.println("case 3 Attack");
                animatedTexture.loopAnimationChannel(city3AttackAnimation);
            }
            else if (animatedTexture.getAnimationChannel() == city2Animation) {
                System.err.println("case 2 to city 3");
                animatedTexture.loopAnimationChannel(city3Animation);
            }
        }
        else if (maxHP - 3 == currentHP) {
            if (animatedTexture.getAnimationChannel() == city3Animation)
                animatedTexture.loopAnimationChannel(city4Animation);
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        entity.getViewComponent().removeChild(animatedTexture);
    }

}