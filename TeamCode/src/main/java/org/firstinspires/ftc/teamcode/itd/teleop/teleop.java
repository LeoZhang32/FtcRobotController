package org.firstinspires.ftc.teamcode.itd.teleop;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class teleop extends LinearOpMode{
    //drivetrain
    DcMotor frontRight;
    DcMotor frontLeft;
    DcMotor backRight;
    DcMotor backLeft;

    IMU imu;

    //viper slides
    DcMotor frontViper;
    DcMotor backViper;
    Boolean VS_auto_up_button_pressed = false;
    Boolean VS_auto_up = false;
    Boolean VS_auto_down_button_pressed = false;
    Boolean VS_auto_down = false;
    Boolean VS_stop_button_pressed = false;
    Boolean VS_stop = false;

    //servos
    Servo sample;
    Boolean sample_button_pressed = false;
    Boolean sample_closed = false;


    Servo intakeRight;
    Servo intakeLeft;
    Servo intakeBack;
    int pressCount = 0;

    Boolean previousXState = false;
    Boolean currentXState;
    Boolean previousYState = false;
    Boolean currentYState;
    Boolean previousBState = false;
    Boolean currentBState;

    Servo bucket;
    Boolean bucket_button_pressed = false;
    Boolean bucket_dumped = false;

    Servo specimen;
    Boolean specimen_button_pressed = false;
    Boolean specimen_closed = false;

    Servo hangRight;
    Boolean hangRight_button_pressed = false;
    Boolean hangRight_activated = false;

    Servo hangLeft;
    Boolean hangLeft_button_pressed = false;
    Boolean hangLeft_activated = false;

    NormalizedColorSensor colorSensor;
    View relativeLayout;
    Boolean sample_color = false;

//    DigitalChannel limitSwitch;
//    Boolean limitSwitchPressed = !limitSwitch.getState();
//    Boolean gamepad2_dpad_down_isPressed = false;

    @Override
    public void runOpMode() throws InterruptedException {
        // drivetrain motors
        frontRight = hardwareMap.dcMotor.get("frontRight");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        // Retrieve the IMU from the hardware map
        imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        //viper slides motors
        frontViper = hardwareMap.dcMotor.get("frontViper");
        backViper = hardwareMap.dcMotor.get("backViper");

        frontViper.setDirection(DcMotor.Direction.REVERSE);

        // Reset the encoder during initialization
        frontViper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backViper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontViper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backViper.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //servos
        sample = hardwareMap.servo.get("sample");
        intakeRight = hardwareMap.servo.get("intakeRight");
        intakeLeft = hardwareMap.servo.get("intakeLeft");
        intakeBack = hardwareMap.servo.get("intakeBack");
        bucket = hardwareMap.servo.get("bucket");
        specimen = hardwareMap.servo.get("specimen");
        hangRight = hardwareMap.servo.get("hangRight");
        hangLeft = hardwareMap.servo.get("hangLeft");


        // Set the initial positions for intakeRight, intakeLeft and intakeBack
        intakeRight.setPosition(0.57);
        intakeLeft.setPosition(0.43);
        intakeBack.setPosition(0.45);

        //sensors
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
//        limitSwitch = hardwareMap.get(DigitalChannel.class, "limitSwitch");
//        limitSwitch.setMode(DigitalChannel.Mode.INPUT);

        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        try {
            runSample(); // actually execute the sample
        } finally {
            // On the way out, *guarantee* that the background is reasonable. It doesn't actually start off
            // as pure white, but it's too much work to dig out what actually was used, and this is good
            // enough to at least make the screen reasonable again.
            // Set the panel back to the default color
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.WHITE);
                }
            });
        }
    }
    protected void runSample() {
        // You can give the sensor a gain value, will be multiplied by the sensor's raw value before the
        // normalized color values are calculated. Color sensors (especially the REV Color Sensor V3)
        // can give very low values (depending on the lighting conditions), which only use a small part
        // of the 0-1 range that is available for the red, green, and blue values. In brighter conditions,
        // you should use a smaller gain than in dark conditions. If your gain is too high, all of the
        // colors will report at or near 1, and you won't be able to determine what color you are
        // actually looking at. For this reason, it's better to err on the side of a lower gain
        // (but always greater than  or equal to 1).
        float gain = 2;

        // Once per loop, we will update this hsvValues array. The first element (0) will contain the
        // hue, the second element (1) will contain the saturation, and the third element (2) will
        // contain the value. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
        // for an explanation of HSV color.
        final float[] hsvValues = new float[3];

        // Get a reference to our sensor object. It's recommended to use NormalizedColorSensor over
        // ColorSensor, because NormalizedColorSensor consistently gives values between 0 and 1, while
        // the values you get from ColorSensor are dependent on the specific sensor you're using.


        waitForStart();
        if (isStopRequested()) return;
        while (!isStopRequested() && opModeIsActive()) {



            //drivetrain
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.start) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeft.setPower(frontLeftPower * 1);
            backLeft.setPower(backLeftPower * 1);
            frontRight.setPower(frontRightPower * 1);
            backRight.setPower(backRightPower * 1);

            //viper slides
            if (gamepad2.dpad_up) {
                frontViper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                backViper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                frontViper.setPower(1);
                backViper.setPower(1);
            } else if (gamepad2.dpad_down) {
                frontViper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                backViper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                frontViper.setPower(-1);
                backViper.setPower(-1);
            } else {
                frontViper.setPower(0);
                backViper.setPower(0);
            }

            //viper slides limit switch
//            if (limitSwitchPressed && gamepad2.dpad_down){
//                frontViper.setPower(0);
//                backViper.setPower(0);
//                telemetry.addData("Viper Slides", "Stopped");
//            }


            // viper slides auto action


//            if (gamepad2.start){
//                if (!VS_stop_button_pressed){
//                    VS_stop = !VS_stop;
//                }
//                VS_stop_button_pressed = true;
//            } else VS_stop_button_pressed = false;




            if (gamepad2.x) {
                if (!VS_auto_up_button_pressed) {
                    VS_auto_up = !VS_auto_up;
                }
                VS_auto_up_button_pressed = true;
            } else VS_auto_up_button_pressed = false;

            if (VS_auto_up) {

                // viper slide going up
                // Set the motor's target position
                frontViper.setTargetPosition(4000);
                backViper.setTargetPosition(4000);

                // Switch to RUN_TO_POSITION mode
                frontViper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                backViper.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // Start the motor moving by setting power ratio

                frontViper.setPower(1);
                backViper.setPower(1);

                // Loop while the motor is moving to the target
                while ((frontViper.isBusy()) && (backViper.isBusy()) && !isStopRequested()) {

                // Let the drive team see that we're waiting on the motor
                    telemetry.addData("Status", "Waiting to reach top");
                    telemetry.addData("power", frontViper.getPower());
                    telemetry.addData("position", frontViper.getCurrentPosition());
                    telemetry.addData("is at target", !frontViper.isBusy());
                    telemetry.update();
                }
// One of the motor has reached its target position, and the program will continue

                // Stop all motion;
                frontViper.setPower(0);
                backViper.setPower(0);

                telemetry.addData("Status", "position achieved");
                telemetry.update();

                // Loop while the motor is moving to the target

                VS_auto_up = !VS_auto_up;
            }

            if (gamepad2.start) {
                // Stop all motion;
                frontViper.setPower(0);
                frontViper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                backViper.setPower(0);
                backViper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                telemetry.addData("Button Pressed", gamepad2.start);
                telemetry.update();
            }





            if (gamepad2.b) {
                if (!VS_auto_down_button_pressed) {
                    VS_auto_down = !VS_auto_down;
                }
                VS_auto_down_button_pressed = true;
            } else VS_auto_down_button_pressed = false;


            if (VS_auto_down) {

                // viper slide going down
                frontViper.setTargetPosition(0);
                backViper.setTargetPosition(0);

                // Switch to RUN_TO_POSITION mode
                frontViper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                backViper.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                frontViper.setPower(1);
                backViper.setPower(1);

                // Loop while the motor is moving to the target
                while ((frontViper.isBusy()) && backViper.isBusy() && !isStopRequested()) {

                    // Let the drive team see that we're waiting on the motor
                    telemetry.addData("Status", "Waiting to reach bottom");
                    telemetry.addData("power", frontViper.getPower());
                    telemetry.addData("position", frontViper.getCurrentPosition());
                    telemetry.addData("is at target", !frontViper.isBusy());
                    telemetry.update();
                }

                // One of the motor has reached its target position, and the program will continue
                // Stop all motion;
                frontViper.setPower(0);
                backViper.setPower(0);

                telemetry.addData("Status", "position achieved");
                telemetry.update();

                // Loop while the motor is moving to the target

                VS_auto_down = !VS_auto_down;

            }




            //sticky key presses
            //sample
            if (gamepad1.a){
                if (!sample_button_pressed){
                    sample_closed = !sample_closed;
                }
                sample_button_pressed= true;
            } else sample_button_pressed = false;

            //intakeActions
            currentXState = gamepad1.x;
            currentYState = gamepad1.y;
            currentBState = gamepad1.b;

            if (currentXState && !previousXState) { // Prevent "button held down" behavior
                // Increment the press count and ensure it loops between 0 and 2
                pressCount++;

                // Cycle through the positions
                if (pressCount == 1) {
                    intakeRight.setPosition(0.35);
                    intakeLeft.setPosition(0.65);
                    intakeBack.setPosition(0.72); // Position 1 (aiming position)
                } else if (pressCount == 2) {
                    intakeRight.setPosition(0.3);
                    intakeLeft.setPosition(0.7);
                    intakeBack.setPosition(0.8); // Position 2 (taking sample position)
                } else if (pressCount == 3) {
                    intakeRight.setPosition(0.35); //this is the initial position
                    intakeLeft.setPosition(0.65); // this = 1-intakeRight position
                    intakeBack.setPosition(0.65); // Position 3 (holding sample position)
                }else if (pressCount == 4) {
                    intakeRight.setPosition(0.57); //this is the initial position
                    intakeLeft.setPosition(0.43); //this = 1-intakeRight position
                    intakeBack.setPosition(0.45); //this is the initial position - Position 3 (drop sample into bucket)
                    pressCount = 0; // Reset to cycle back to initial position
                }
            }

            previousXState = currentXState;

            if (currentYState && !previousYState) {
                pressCount = 1;

                if (pressCount == 1){
                    intakeRight.setPosition(0.3);
                    intakeLeft.setPosition(0.7);
                    intakeBack.setPosition(0.72);
                }
            }

            previousYState = currentYState;

            if (currentBState && !previousBState) {
                pressCount = 4;

                if (pressCount == 4){
                    intakeRight.setPosition(0.57); //this is the initial position
                    intakeLeft.setPosition(0.43); //this = 1-intakeRight position
                    intakeBack.setPosition(0.45); //this is the initial position
                    // Position 3 (drop sample into bucket)
                    pressCount = 0; // Reset to cycle back to initial position
                }
            }

            previousBState = currentBState;

            //bucket
            if (gamepad2.y) {
                if (!bucket_button_pressed) {
                    bucket_dumped = !bucket_dumped;
                }
                bucket_button_pressed = true;
            } else bucket_button_pressed = false;

            //specimen
            if (gamepad2.a) {
                if (!specimen_button_pressed) {
                    specimen_closed = !specimen_closed;
                }
                specimen_button_pressed = true;
            } else specimen_button_pressed = false;

            //hang
            if (gamepad2.dpad_right) {
                if (!hangRight_button_pressed) {
                    hangRight_activated = !hangRight_activated;
                }
                hangRight_button_pressed = true;

            } else hangRight_button_pressed = false;

            if (gamepad2.dpad_right) {
                if (!hangLeft_button_pressed) {
                    hangLeft_activated = !hangLeft_activated;
                }
                hangLeft_button_pressed = true;

            } else hangLeft_button_pressed = false;

            updateBooleans();

            // Color Sensor Sample Code:
            // Explain basic gain information via telemetry

            // Show the gain value via telemetry
            telemetry.addData("Gain", gain);

            // Tell the sensor our desired gain value (normally you would do this during initialization,
            // not during the loop)
            colorSensor.setGain(gain);

            // Get the normalized colors from the sensor
            NormalizedRGBA colors = colorSensor.getNormalizedColors();

            /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
             * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
             * HSV (hue, saturation and value) values. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
             * for an explanation of HSV color. */

            // Update the hsvValues array by passing it to Color.colorToHSV()
            Color.colorToHSV(colors.toColor(), hsvValues);

            telemetry.addLine()
                    .addData("Red", "%.3f", colors.red)
                    .addData("Green", "%.3f", colors.green)
                    .addData("Blue", "%.3f", colors.blue);
            telemetry.addLine()
                    .addData("Hue", "%.3f", hsvValues[0])
                    .addData("Saturation", "%.3f", hsvValues[1])
                    .addData("Value", "%.3f", hsvValues[2]);
            telemetry.addData("Alpha", "%.3f", colors.alpha);

            /* If this color sensor also has a distance sensor, display the measured distance.
             * Note that the reported distance is only useful at very close range, and is impacted by
             * ambient light and surface reflectivity. */
            if (colorSensor instanceof DistanceSensor) {
                telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM));
            }
            if (hsvValues[0] == 0){
                continue;
            }
            else if (hsvValues[0] >= -10 && hsvValues[0] <= 50) {
                sample_color = true;
            }
            else if (hsvValues[0] >= 200 && hsvValues[0] <=280){
                sample_color = false;
                telemetry.addData("Color is Blue", "Sample claw doesn't close.");
            }
            else {
                sample_color = false;
                telemetry.addData("Color Is Not Seen", "Sample claw doesn't close");
            }

            telemetry.update();
//            if (sample_color){
//                sample.setPosition(1);
//                telemetry.addData("sample_color", "true");
//            }
//            else {
//                sample.setPosition(0.4);
//                telemetry.addData("sample_color", "false");
//            }
            // Change the Robot Controller's background color to match the color detected by the color sensor.
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.HSVToColor(hsvValues));
                }
            });


        }

    }
    public void updateBooleans() {
        if (sample_closed && sample_color) {
            sample.setPosition(0.4);
        }
        else if (sample_closed || sample_color) {
            sample.setPosition(1);
            telemetry.addData("sample_color", "true");
            telemetry.addData("Color is Red", "Sample claw closes");
         }

        else{
            sample.setPosition(0.4);//this is the initial position;
            }

        if (bucket_dumped){
            bucket.setPosition(0.57);
        }
        else {
            bucket.setPosition(1);
        }

        if (specimen_closed){
            specimen.setPosition(0.67);
        }
        else {
            specimen.setPosition(0.76); //this is the initial position
        }

        if (hangRight_activated){
            hangRight.setPosition(0.7);
        }
        else {
            hangRight.setPosition(1); //this is the initial position
        }
        if (hangLeft_activated){
            hangLeft.setPosition(0.92);
        }
        else {
            hangLeft.setPosition(0.62); //this is the initial position
        }

    }


}

