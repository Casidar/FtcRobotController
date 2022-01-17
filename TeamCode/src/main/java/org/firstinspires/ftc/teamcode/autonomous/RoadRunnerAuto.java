package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;


@Autonomous
public class RoadRunnerAuto extends LinearOpMode
{
    @Override
    public void runOpMode(){
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(0, 0);

        drive.setPoseEstimate(startPose);

        Trajectory testTrajectory = drive.trajectoryBuilder(startPose)
                .strafeTo(new Vector2d(-7,14))
                .strafeRight(14)
                .build();

        waitForStart();
        if(isStopRequested()) return;

        drive.followTrajectory(testTrajectory);
    }
}
