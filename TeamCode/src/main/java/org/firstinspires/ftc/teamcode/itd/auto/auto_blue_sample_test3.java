package org.firstinspires.ftc.teamcode.itd.auto;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.rr.MecanumDrive;


@Autonomous (name = "auto_blue_sample_test3")

public final class auto_blue_sample_test3 extends LinearOpMode {

    private final ElapsedTime runtime = new ElapsedTime();

    public class Lift {
        private final DcMotorEx frontViper;
        private final DcMotorEx backViper;

        public Lift(HardwareMap hardwareMap) {
            frontViper = hardwareMap.get(DcMotorEx.class, "frontViper");
            frontViper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            frontViper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontViper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            frontViper.setDirection(DcMotorSimple.Direction.REVERSE);
            backViper = hardwareMap.get(DcMotorEx.class, "backViper");
            backViper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            backViper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            backViper.setDirection(DcMotorSimple.Direction.FORWARD);
             }

        public class LiftUp implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                if (!initialized) {

                    frontViper.setPower(1);
                    backViper.setPower(1);
                    telemetry.addData("Position", frontViper.getCurrentPosition());
                    telemetry.addData("front Power", frontViper.getPower());
                    telemetry.addData("back Power", backViper.getPower());
                    telemetry.update();

                    initialized = true;
                }

                double pos = frontViper.getCurrentPosition();
                packet.put("liftPos", pos);
                if (pos < 4000.0) {
                    return true;
                } else {
                    frontViper.setPower(0);
                    backViper.setPower(0);

                    return false;
                }
            }
        }
        public Action liftUp() {
            return new LiftUp();
        }




        public class LiftDown implements Action {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    frontViper.setPower(-1);
                    backViper.setPower(-1);
                    telemetry.addData("Position", frontViper.getCurrentPosition());
                    telemetry.addData("front Power", frontViper.getPower());
                    telemetry.addData("back Power", backViper.getPower());
                    telemetry.update();
                    initialized = true;
                }

                double pos = frontViper.getCurrentPosition();
                packet.put("liftPos", pos);
                if (pos > 50.0) {
                    return true;
                } else {
                    frontViper.setPower(0);
                    backViper.setPower(0);
                    return false;
                }
            }
        }
        public Action liftDown(){
            return new LiftDown();
        }
    }





    //bucket servo class
    public class ScoringSample {
        private final Servo bucket;

        public ScoringSample(HardwareMap hardwareMap) {
            bucket = hardwareMap.get(Servo.class, "bucket");
        }


        public class DumpBucket implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                bucket.setPosition(0.55);
                return false;
            }
        }

        public Action dumpBucket() {
            return new DumpBucket();
        }


        public class RestoreBucket implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                bucket.setPosition(1);
                return false;
            }
        }

        public Action restoreBucket() {
            return new RestoreBucket();
        }

    }



    //intake servos class
    public class IntakeSample {
        private final Servo intakeRight;
        private final Servo intakeLeft;
        private final Servo intakeBack;

        public IntakeSample(HardwareMap hardwareMap) {
            intakeRight = hardwareMap.get(Servo.class, "intakeRight");
            intakeLeft = hardwareMap.get(Servo.class, "intakeLeft");
            intakeBack = hardwareMap.get(Servo.class, "intakeBack");
        }


        public class ExtendArm implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                intakeRight.setPosition(0.3);
                intakeLeft.setPosition(0.7);
                intakeBack.setPosition(0.8);
                return false;
            }
        }

        public Action extendArm() {
            return new IntakeSample.ExtendArm();
        }


        public class RetractArm implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                intakeRight.setPosition(0.6);
                intakeLeft.setPosition(0.4);
                intakeBack.setPosition(0.5);
                return false;
            }
        }

        public Action retractArm() {  return new IntakeSample.RetractArm();
        }

    }






    //sample claw servo class
    public class PickupSample {
        private final Servo sample;

        public PickupSample(HardwareMap hardwareMap) {
            sample = hardwareMap.get(Servo.class, "sample");
        }


        public class CloseSClaw implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                sample.setPosition(1);
                return false;
            }
        }

        public Action closeSClaw() {
            return new CloseSClaw();
        }


        public class OpenSClaw implements Action {
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                sample.setPosition(0.4);
                return false;
            }
        }

        public Action openSClaw() {
            return new OpenSClaw();
        }

    }


    @Override
    public void runOpMode() throws InterruptedException {


        Pose2d beginPose = new Pose2d(36, 67, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);
        ScoringSample bucket = new ScoringSample(hardwareMap);
        Lift lift = new Lift(hardwareMap);
        IntakeSample arm = new IntakeSample(hardwareMap);
        PickupSample sclaw = new PickupSample(hardwareMap);
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        Actions.runBlocking(sclaw.openSClaw());


        Action trajectoryAction1;
        trajectoryAction1 = drive.actionBuilder(drive.pose)

                .lineToY(64)
                .strafeToLinearHeading(new Vector2d(60, 60), Math.toRadians(-135))
                .build();


        Action trajectoryAction2;
        trajectoryAction2 = drive.actionBuilder(drive.pose)

                .strafeToLinearHeading(new Vector2d(68, 50), Math.toRadians(-75))
                .build();


        Action trajectoryAction3;
        trajectoryAction3 = drive.actionBuilder(drive.pose)

                .strafeToLinearHeading(new Vector2d(60, 60), Math.toRadians(-135))
                .build();


        Action trajectoryAction4;
        trajectoryAction4 = drive.actionBuilder(drive.pose)

                .strafeToLinearHeading(new Vector2d(62, 51), Math.toRadians(-90))
                .build();


        Action trajectoryAction5;
        trajectoryAction5 = drive.actionBuilder(drive.pose)

                .strafeToLinearHeading(new Vector2d(52, 51), Math.toRadians(-90))
                .build();


        waitForStart();
        runtime.reset();
        while (opModeIsActive() && runtime.seconds() <= 1.5) {

            Actions.runBlocking(new SequentialAction(

                    new ParallelAction(

                        trajectoryAction1,
                        new SequentialAction(
                            new SleepAction(0.5),
                            lift.liftUp(),
                            bucket.dumpBucket(),
                            new SleepAction(0.8),
                            bucket.restoreBucket(),
                            lift.liftDown()
                            )
                        ),


                    trajectoryAction2,
                    arm.extendArm(),
                    new SleepAction(0.5),
                    sclaw.closeSClaw(),
                    new SleepAction(0.5),
                    arm.retractArm(),
                    new SleepAction(0.5),
                    sclaw.openSClaw(),
                    new SleepAction(0.5),
                    new ParallelAction(


                            trajectoryAction3,
                            new SequentialAction(
                                    new SleepAction(0.5),
                                    lift.liftUp(),
                                    bucket.dumpBucket(),
                                    new SleepAction(0.8),
                                    bucket.restoreBucket(),
                                    lift.liftDown()
                            )

                    ),

                    trajectoryAction4,
                    arm.extendArm(),
                    new SleepAction(0.5),
                    sclaw.closeSClaw(),
                    new SleepAction(0.5),
                    arm.retractArm(),
                    new SleepAction(0.5),
                    sclaw.openSClaw(),
                    new SleepAction(0.5),
                    new ParallelAction(

                            trajectoryAction3,
                            new SequentialAction(
                                    new SleepAction(0.5),
                                    lift.liftUp(),
                                    bucket.dumpBucket(),
                                    new SleepAction(0.8),
                                    bucket.restoreBucket(),
                                    lift.liftDown()
                            )

                    ),

                    trajectoryAction5,
                    arm.extendArm(),
                    new SleepAction(0.5),
                    sclaw.closeSClaw(),
                    new SleepAction(0.5),
                    arm.retractArm(),
                    new SleepAction(0.5),
                    sclaw.openSClaw(),
                    new SleepAction(0.5),
                    new ParallelAction(

                            trajectoryAction3,
                            new SequentialAction(
                                    new SleepAction(0.5),
                                    lift.liftUp(),
                                    bucket.dumpBucket(),
                                    new SleepAction(0.8),
                                    bucket.restoreBucket(),
                                    lift.liftDown()
                            )

                    )


                    )

            );


        }
    }

}
