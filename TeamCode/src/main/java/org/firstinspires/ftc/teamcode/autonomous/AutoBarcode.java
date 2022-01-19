package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Greg;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Autonomous
public class AutoBarcode extends LinearOpMode {


    private ElapsedTime timer;

    private Greg.Side direction = Greg.Side.RED;

    private enum BarCode{
        left,
        right
    }

    private enum Level{
        bottom,
        middle,
        top
    }

    BarCode barCode;
    Level level;

    Greg greg;

    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    private static final String VUFORIA_KEY =
            "AV8nCrL/////AAABmY1RQgl9UU0Moa5MI7jvnWwLqdRDEZDwZQM5dkoLMVrVkYdqDpAYfEvGMbW9OsOCKCEYsgtkSmhGN9qlf4Wbb9u/GyqrTUXum6gsd7wwrdg1C4gOrLownUavUByWl2js3O6k+mbz7ZmEKZN7F3Nld8hnWd9NdzbeV/RUJYDdUwvBB0Fn0iyy2G3obE8g92m5k+NL6RSxjklqGuPAAmExvC1nRLg8fYqAsx7xsSF7eoFdUSJRUhNCPd8KV8pPol635VeK3N5vfdh+awdHwjZofr6o4bGm/UUzVRUFS2GQCnZc+ympDdQik2/IxiETHeBRXqvHxc4we8GsCKbGnxtabvWT8qva3y8Si9XGLtuLZ6c9";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        telemetry.addData("Status of Greg", "Almost ready to be Sgronk");
        telemetry.update();
        greg = new Greg(hardwareMap);
        direction = direction.RED;

        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(1, 16.0/9.0);
        }

        while(!isStarted()){
            telemetry.addData("Status of Greg", "Ready to be Sgronk");
            telemetry.addLine();
            telemetry.addData("Side", direction);
            telemetry.addData("Blue", "Press X");
            telemetry.addData("Red", "Press B");
            telemetry.addData("","");


            if(gamepad1.x  || gamepad2.x){
                direction = Greg.Side.BLUE;
            }
            else if(gamepad1.b || gamepad2.b){
                direction = Greg.Side.RED;
            }
            if(gamepad1.dpad_up || gamepad2.dpad_up){
                break;
            }

            if (tfod != null) {
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if(updatedRecognitions != null){
                    for (Recognition recognition : updatedRecognitions) {
                        if(recognition.getLabel().equals("Duck")){
                            float x = (recognition.getRight()+recognition.getLeft())/2;
                            if(x < 300){
                                barCode = BarCode.left;
                            }
                            else if(x > 300){
                                barCode = BarCode.right;
                            }
                        }
                    }
                }
            }

            telemetry.addData("TensorFlow Detect", (barCode == null) ? "nothing" : barCode);

            telemetry.update();
        }

        if(barCode == null){
            level = Level.top;
        }
        else if(barCode == BarCode.right){
            level = Level.middle;
        }
        else if(barCode == barCode.left){
            level = Level.bottom;
        }

        waitForStart();

        timer = new ElapsedTime();

        if (opModeIsActive()) {
            telemetry.addData("Status of Greg: ", "Sgronk");
            telemetry.addData("Tower Level", level);
            telemetry.update();

            greg.setSpeed(0.2f);

            greg.autoLocoControl(0,-1,0,1000); //moves away from wall
            sleep(500);
            greg.autoLocoControl(0,0, 1,1650); //turns
            sleep(500);
            greg.autoLocoControl(0,(direction == Greg.Side.BLUE) ? 1 : -1,0,(direction == Greg.Side.BLUE) ? 1000 : 3000); //moves to tower side
            greg.lift(0.6f); //lifts the lift
            sleep(500);
            greg.lift(0.2f);
            greg.turret(-0.5f); //turns the turret while keeping the lift in place
            sleep(1400);
            greg.lift(0);
            greg.turret(0); //stops the lift an turret, lets the lift fall
            sleep(1000);
            greg.lift((level == Level.top) ? 0.8f : 0.6f);
            sleep((level == Level.top) ? 800 : ((level == Level.middle) ? 750: 150)); //lifts the lift to the correct height
            greg.lift((level == Level.top) ? 0.3f : 0.2f);
            sleep(500);
            greg.autoLocoControl(1,0,0,(level == Level.top) ? 1000 : 400); //moves toward tower
            sleep(500);
            greg.intake(false); //pushes cube out
            sleep(1500);
            greg.intake(0);
            greg.autoLocoControl(1,0,0,(level == Level.top) ? 1500 : 1000); //moves away from tower
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}