package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Greg {
    final float UP_DUMP_LOC = 0.88f; // Servo position when dump is up
    final float CAROUSELSPEED = 0.4f; // Carousel speed
    final float turretSpeed = 0.3f;
    final float liftSpeed = 0.4f;
    final float intakeSpeed0 = -0.35f;
    final float intakeSpeed1 = 0.20f;
    final float outtakeSpeed0 = 0.6f;
    final float outtakeSpeed1 = -0.25f;
    final float clawUp = 1;
    final float clawDown = 0.3f;
    final float clawClosed = 1;
    final float clawOpen = 0.5f;

    final int liftPosUp = 100;
    final int liftPosCen = 50;
    final int liftPosDown = 0;
    final float turretTimeLeft = 1;
    final float turretTimeRight = 1;

    Thread liftThread = new Thread();
    Thread turretThread = new Thread();

    public DcMotor MotorLeftFront;
    public DcMotor MotorLeftBack;
    public DcMotor MotorRightFront;
    public DcMotor MotorRightBack;

    public DcMotor MotorLift;
    public DcMotor MotorCarousel;

    public CRServo ServoIntake0;
    public CRServo ServoIntake1;
    public CRServo ServoTurret;

    public Servo ServoClawUp;
    public Servo ServoClawOpen;

    public float locoSpeed = 1;

    public enum Side {RED, BLUE};

    public Greg(@NonNull HardwareMap hardwareMap){
        /* Sets motor variables */
        MotorLeftFront = hardwareMap.get(DcMotor.class, "leftFront");
        MotorLeftBack = hardwareMap.get(DcMotor.class, "leftBack");
        MotorRightFront = hardwareMap.get(DcMotor.class, "rightFront");
        MotorRightBack = hardwareMap.get(DcMotor.class, "rightBack");

        MotorLift = hardwareMap.get(DcMotor.class, "liftMotor");
        MotorCarousel = hardwareMap.get(DcMotor.class, "duckMotor");

        ServoIntake0 = hardwareMap.get(CRServo.class, "intakeServo0");
        ServoIntake1 = hardwareMap.get(CRServo.class, "intakeServo1");
        ServoTurret = hardwareMap.get(CRServo.class, "turretServo");

        ServoClawUp = hardwareMap.get(Servo.class, "clawServo0");
        ServoClawOpen = hardwareMap.get(Servo.class, "clawServo1");

        /* Sets zero power behavior of motors */
        MotorLeftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorLeftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorLift.setDirection(DcMotorSimple.Direction.REVERSE);
        /* Sets motors to run with encoders */
        MotorLeftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        MotorLeftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        MotorRightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        MotorRightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        MotorLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Controls the locomotion of Greg
     * @param horiPower strafing power
     * @param vertPower forward power
     * @param turnPower turning power
     * @param speed power multiplier
     */
    public void locomotionControl(float horiPower, float vertPower, float turnPower, float speed){
        /* Sets motor values using the values received */
        MotorLeftFront.setPower(speed*(vertPower-horiPower+turnPower));
        MotorLeftBack.setPower(speed*(vertPower+horiPower+turnPower));
        MotorRightFront.setPower(speed*(-vertPower-horiPower+turnPower));
        MotorRightBack.setPower(speed*(-vertPower+horiPower+turnPower));
    }

    /**
     * Controls the locomotion of Greg with preset speed
     * @param horiPower strafing power
     * @param vertPower forward power
     * @param turnPower turning power
     */
    public void locomotionControl(float horiPower, float vertPower, float turnPower){
        /* Sets motor values using the values received */
        MotorLeftFront.setPower(locoSpeed*(vertPower+horiPower+turnPower));
        MotorLeftBack.setPower(locoSpeed*(vertPower-horiPower+turnPower));
        MotorRightFront.setPower(locoSpeed*(-vertPower+horiPower+turnPower));
        MotorRightBack.setPower(locoSpeed*(-vertPower-horiPower+turnPower));
    }

    public void autoLocoControl(float horiPower, float vertPower, float turnPower, long time){
        MotorLeftFront.setPower(locoSpeed*(vertPower+horiPower+turnPower));
        MotorLeftBack.setPower(locoSpeed*(vertPower-horiPower+turnPower));
        MotorRightFront.setPower(locoSpeed*(-vertPower+horiPower+turnPower));
        MotorRightBack.setPower(locoSpeed*(-vertPower-horiPower+turnPower));
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MotorLeftFront.setPower(0);
        MotorLeftBack.setPower(0);
        MotorRightFront.setPower(0);
        MotorRightBack.setPower(0);
    }

    public void setSpeed(float speed){
        this.locoSpeed = speed;
    }

    public void locomotionBreak(){
        /* Stops motors */
        MotorLeftFront.setPower(0);
        MotorLeftBack.setPower(0);
        MotorRightFront.setPower(0);
        MotorRightBack.setPower(0);
    }

    public void carousel(boolean direction){
        MotorCarousel.setPower(CAROUSELSPEED * (direction ? 1 : -1));
    }

    public void carousel(float speed){
        MotorCarousel.setPower(speed);
    }

    public void lift(float speed){
        MotorLift.setPower(-speed * liftSpeed);
    }

    public void turret(float speed){
        ServoTurret.setPower(speed * turretSpeed);
    }

    public void intake(float speed){
        ServoIntake0.setPower(-speed);
        ServoIntake1.setPower(speed);
    }

    public void intake(boolean direction){
        ServoIntake0.setPower(direction ? intakeSpeed0 : outtakeSpeed0);
        ServoIntake1.setPower(direction ? intakeSpeed1 : outtakeSpeed1);
    }

    public void intakeBreak(){
        ServoIntake0.setPower(0);
        ServoIntake1.setPower(0);
    }

    public void clawUp(boolean direction){
        ServoClawUp.setPosition(direction ? clawDown : clawUp);
    }

    public void clawClose(boolean direction){
        ServoClawOpen.setPosition(direction ? clawClosed : clawOpen);
    }
}
