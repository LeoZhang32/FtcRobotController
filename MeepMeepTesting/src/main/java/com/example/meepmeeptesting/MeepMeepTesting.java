package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import com.acmerobotics.roadrunner.Actions;
public class MeepMeepTesting {

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-8, 66, Math.toRadians(90)))
//                .lineToX(50)
//                .turn(Math.toRadians(90))
                .strafeTo(new Vector2d(-8, 34))
                .strafeTo(new Vector2d(-38, 36))

                .strafeTo(new Vector2d(-40, 14.5))

                .strafeTo(new Vector2d(-48, 14.5))
                .strafeTo(new Vector2d(-48, 54))
                .strafeTo(new Vector2d(-48, 14.5))
                .strafeTo(new Vector2d(-56, 14.5))
                .strafeTo(new Vector2d(-56, 54))

                .strafeTo(new Vector2d(-56, 46))
                .strafeToLinearHeading(new Vector2d(-48,46), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(-48,66), Math.toRadians(-90))

                .strafeTo(new Vector2d(-48, 58))
                .strafeToLinearHeading(new Vector2d(-10,42), Math.toRadians(90))
                .strafeTo(new Vector2d(-10, 34))

                .strafeTo(new Vector2d(-10, 42))
                .strafeToLinearHeading(new Vector2d(-48,58), Math.toRadians(-90))
                .strafeTo(new Vector2d(-48, 66))

                .strafeTo(new Vector2d(-48, 58))
                .strafeToLinearHeading(new Vector2d(-6,42), Math.toRadians(90))
                .strafeTo(new Vector2d(-6, 34))

                .strafeTo(new Vector2d(-6, 42))
                .strafeToLinearHeading(new Vector2d(-48,60), Math.toRadians(-90))

                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }

}