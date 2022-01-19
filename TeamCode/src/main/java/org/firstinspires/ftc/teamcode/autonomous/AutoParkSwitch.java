package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@Autonomous
public class AutoParkSwitch extends LinearOpMode {

    private ElapsedTime timer = new ElapsedTime();
    private enum Side {RED, BLUE};
    private Side direction = Side.RED;

    private DcMotor MotorLeftFront;
    private DcMotor MotorLeftBack;
    private DcMotor MotorRightFront;
    private DcMotor MotorRightBack;

    @Override
    public void runOpMode() {
        timer.reset();
        MotorLeftFront = hardwareMap.get(DcMotor.class, "Motor0");
        MotorLeftBack = hardwareMap.get(DcMotor.class, "Motor1");
        MotorRightFront = hardwareMap.get(DcMotor.class, "Motor2");
        MotorRightBack = hardwareMap.get(DcMotor.class, "Motor3");

        MotorLeftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorLeftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        MotorRightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status of Greg", "Ready to be Sgronk");
        telemetry.update();

        direction = direction.RED;

        while(!isStarted()){
            if(gamepad1.x  || gamepad2.x){
                direction = Side.BLUE;
            }
            else if(gamepad1.b || gamepad2.b){
                direction = Side.RED;
            }
            if(gamepad1.dpad_up || gamepad2.dpad_up){
                break;
            }
            telemetry.addData("Status of Greg", "Ready to be Sgronk");
            telemetry.addLine();
            telemetry.addData("Side", direction);
            telemetry.addData("Blue", "Press X");
            telemetry.addData("Red", "Press B");
            telemetry.addData("Submit Side", "Press dpad Up");
            telemetry.update();
        }

        telemetry.addData("Status of Greg", "Ready to be Sgronk");
        telemetry.addLine();
        telemetry.addData("Side", direction);
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            telemetry.addData("Status of Greg: ", "Sgronk");
            telemetry.update();

            timer.reset();
            while(timer.seconds() <= 1){}
            locomotionControl((direction == Side.RED) ? -1 : 1, 0, 0, 0.5f);
            timer.reset();
            while(timer.seconds() <= 1){}
            locomotionControl(0, -1, 0, 0.9f);
            timer.reset();
            while(timer.seconds() <= 3){}
            locomotionControl(0,0,0,0);
            timer.reset();
            while(timer.seconds() <= 1){}
        }
    }

    private void locomotionControl(float horiPower, float vertPower, float turnPower, float speed){
        /* Sets motor values using the values received by the gamepad */
        MotorLeftFront.setPower(speed*(-vertPower+horiPower+turnPower));
        MotorLeftBack.setPower(speed*(-vertPower-horiPower+turnPower));
        MotorRightFront.setPower(speed*(vertPower+horiPower+turnPower));
        MotorRightBack.setPower(speed*(vertPower-horiPower+turnPower));
    }
}
