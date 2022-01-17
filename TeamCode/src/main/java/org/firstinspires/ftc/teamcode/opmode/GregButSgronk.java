package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Greg;

@TeleOp

public class GregButSgronk extends LinearOpMode
{
    Greg greg;

    final private float[] sensitivity = {0.3f, 0.5f, 0.75f, 1}; // sensitivity starting from lowest to highest

    private int sensiInc = 2; // starting sensitivity loc: starting from 0 in sensitivity array

    private boolean leftInc = false;
    private boolean rightInc = false;

    private ElapsedTime carTimer;

    @Override
    public void runOpMode()
    {
        greg = new Greg(hardwareMap);

        carTimer = new ElapsedTime();

        telemetry.addData("Status of Greg: ", "Ready to be Sgronk");
        telemetry.update();

        waitForStart(); /* Wait for start to be pressed */

        while (opModeIsActive()) { /*loops when running opMode */
            sensitivityControl(); /* Controls Sensitivity */
            intakeControl(); /* Controls intake motors */
            turretControl(); /* Controls turret servos */
            liftControl(); /* Controls lift servos */
            carouselControl(); /* Controls carousel */
            clawControl(); /*Controls lift and turret by dpad */
            greg.locomotionControl(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, sensitivity[sensiInc]); /* Controls Movement motors */
            
            telemetry.addData("Status of Greg", "Sgronk");
            telemetry.addData("Sensitivity", sensitivity[sensiInc]);
            telemetry.addData("Encoders:", "");
            telemetry.addData("\tLeftFront", greg.MotorLeftFront.getCurrentPosition());
            telemetry.addData("\tLeftBack", greg.MotorLeftBack.getCurrentPosition());
            telemetry.addData("\tRightFront", greg.MotorRightFront.getCurrentPosition());
            telemetry.addData("\tRightBack", greg.MotorRightBack.getCurrentPosition());
            telemetry.update();
        }
    }

    /**
     * Controls the sensitivity of the locomotion by using the right and left triggers
     */
    private void sensitivityControl(){
        if(gamepad1.right_trigger > 0.5){
            if(!rightInc){
                sensiInc++;
                rightInc = true;
                if(sensiInc >= sensitivity.length)
                    sensiInc = sensitivity.length - 1;
            }
        }
        else rightInc = false;
        if(gamepad1.left_trigger > 0.5){
            if(!leftInc) {
                sensiInc--;
                leftInc = true;
                if(sensiInc < 0)
                    sensiInc = 0;
            }
        }
        else leftInc = false;
    }

    private void turretControl(){
        greg.turret(gamepad2.right_stick_x);
    }

    private void liftControl(){
        greg.lift(gamepad2.left_stick_y);
    }

    /**
     * controls the turret and lift by the dpad keys -
     * Up = lift up -
     * Down = lift down -
     * Left = turret turns left -
     * Right = turret turns right -
     * If the lift is already close to the position you are trying to set it to, it will reset to the center
     */
    private void clawControl(){
        if(gamepad2.dpad_up){
            greg.clawUp(false);
        }
        if(gamepad2.dpad_down){
            greg.clawUp(true);
        }
        if(gamepad2.dpad_right){
            greg.clawClose(true);
        }
        if(gamepad2.dpad_left){
            greg.clawClose(false);
        }
    }

    /**
     * Controls the intake, right trigger pulls objects in, left trigger pushes it out
     */
    private void intakeControl(){
        if(gamepad2.right_trigger > 0.5)
            greg.intake(true);
        else if(gamepad2.left_trigger > 0.5)
            greg.intake(false);
        else
            greg.intakeBreak();
    }

    /**
     * Controls the carousel wheel, X is default rotation, Y is opposite
     */
    private void carouselControl(){
        telemetry.addData("Timer", carTimer.seconds());
        telemetry.addData("Motor Speed", greg.MotorCarousel.getPower());
        if(gamepad2.x)
            greg.carousel((float)carTimer.seconds()*5);
        else if(gamepad2.y)
            greg.carousel(-(float)carTimer.seconds()*5);
        else
            carTimer.reset();
            greg.carousel(0);
    }
}