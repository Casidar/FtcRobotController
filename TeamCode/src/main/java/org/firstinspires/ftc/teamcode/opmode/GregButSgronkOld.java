package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Greg;

@Disabled
@TeleOp

public class GregButSgronkOld extends LinearOpMode
{

    final float UP_DUMP_LOC = 0.88f;
    final float DOWN_DUMP_LOC = 0.35f;

    final float INTAKESPEED = 1;
    final float CAROUSELSPEED = 0.38f;

    private DcMotor MotorLeftFront;
    private DcMotor MotorLeftBack;
    private DcMotor MotorRightFront;
    private DcMotor MotorRightBack;

    private DcMotor MotorIntake;
    private DcMotor MotorSlide;
    private DcMotor MotorCarousel;

    private Servo ServoDump;

    private float sensitivity = 0.75f;
    private int sensiInc = 1;
    private boolean leftInc = false;
    private boolean rightInc = false;

    @Override
    public void runOpMode()
    {
        /* Sets motor variables */
        MotorLeftFront = hardwareMap.get(DcMotor.class, "Motor0");
        MotorLeftBack = hardwareMap.get(DcMotor.class, "Motor1");
        MotorRightFront = hardwareMap.get(DcMotor.class, "Motor2");
        MotorRightBack = hardwareMap.get(DcMotor.class, "Motor3");

        MotorIntake = hardwareMap.get(DcMotor.class, "Motor7");
        MotorSlide = hardwareMap.get(DcMotor.class, "Motor6");
        MotorCarousel = hardwareMap.get(DcMotor.class, "Motor5");

        ServoDump = hardwareMap.get(Servo.class, "Servo0");

        /* Sets zero power behavior of motors */
        MotorLeftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorLeftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorIntake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        MotorSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        /* Sets motors to run with encoders */
//        MotorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        MotorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        MotorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        MotorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        ServoDump.setPosition(UP_DUMP_LOC);

        telemetry.addData("Status of Greg: ", "Ready to be Sgronk");
        telemetry.update();

        waitForStart(); /* Wait for start to be pressed */

        while (opModeIsActive()) { /*loops when running opMode */
            sensitivityControl(); /* Controls Sensitivity */
            intakeControl(); /* Controls intake motors */
            linearSlideControl(); /* Controls linear slide motor */
            servoControl(); /* Controls servos */
            carouselControl();
            locomotionControl(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, sensitivity); /* Controls Movement motors */
            
            telemetry.addData("Status of Greg: ", "Sgronk");
            telemetry.addData("sensitivity: ", sensitivity);
            telemetry.update();
        }
    }

    private void sensitivityControl(){
        if(gamepad1.right_trigger > 0.5){
            if(!rightInc){
                sensiInc++;
                rightInc = true;
                if(sensiInc > 2) sensiInc = 2;
            }
        }
        else rightInc = false;
        if(gamepad1.right_trigger > 0.5){
            if(!leftInc) {
                sensiInc--;
                leftInc = true;
                if(sensiInc < 0) sensiInc = 0;
            }
        }
        else leftInc = false;

        switch(sensiInc){
            case 0:
                sensitivity = 0.25f; break;
            case 1:
                sensitivity = 0.75f; break;
            case 2:
                sensitivity = 1; break;
            default:
                break;
        }
    }

    private void intakeControl(){
        if(gamepad2.right_trigger > 0.5){
            MotorIntake.setPower(INTAKESPEED);
        }
        else if(gamepad2.left_trigger > 0.5){
            MotorIntake.setPower(-INTAKESPEED);
        }
        else{
            MotorIntake.setPower(0);
        }
    }

    private void linearSlideControl(){
        MotorSlide.setPower(gamepad2.left_stick_y);
//        if(gamepad2.left_stick_y < -0.8){
//            ServoDump.setPosition(UP_DUMP_LOC);
//        }
    }

    private void servoControl(){
        if(gamepad2.dpad_up){
            ServoDump.setPosition(UP_DUMP_LOC);
        }
        else if(gamepad2.dpad_down){
            ServoDump.setPosition(DOWN_DUMP_LOC);
        }
    }

    private void carouselControl(){
        if(gamepad2.x){
            MotorCarousel.setPower(CAROUSELSPEED);
        }
        else if(gamepad2.y){
            MotorCarousel.setPower(-CAROUSELSPEED);
        }
        else{
            MotorCarousel.setPower(0);
        }
    }

    private void locomotionControl(float horiPower, float vertPower, float turnPower, float speed){
        /* Sets motor values using the values received by the gamepad */
        MotorLeftFront.setPower(speed*(vertPower+horiPower+turnPower));
        MotorLeftBack.setPower(speed*(vertPower-horiPower+turnPower));
        MotorRightFront.setPower(speed*(-vertPower+horiPower+turnPower));
        MotorRightBack.setPower(speed*(-vertPower-horiPower+turnPower));
    }
}