package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@TeleOp
public class TensorTest extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
    };

    private static final String TFOD_TEAMOBJECT = "model.tflite";
    private static final String[] LABEL = {
            "Object"
    };

    private static final String VUFORIA_KEY =
            "AV8nCrL/////AAABmY1RQgl9UU0Moa5MI7jvnWwLqdRDEZDwZQM5dkoLMVrVkYdqDpAYfEvGMbW9OsOCKCEYsgtkSmhGN9qlf4Wbb9u/GyqrTUXum6gsd7wwrdg1C4gOrLownUavUByWl2js3O6k+mbz7ZmEKZN7F3Nld8hnWd9NdzbeV/RUJYDdUwvBB0Fn0iyy2G3obE8g92m5k+NL6RSxjklqGuPAAmExvC1nRLg8fYqAsx7xsSF7eoFdUSJRUhNCPd8KV8pPol635VeK3N5vfdh+awdHwjZofr6o4bGm/UUzVRUFS2GQCnZc+ympDdQik2/IxiETHeBRXqvHxc4we8GsCKbGnxtabvWT8qva3y8Si9XGLtuLZ6c9";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(1, 16.0/9.0);
        }

        telemetry.addData("Status of Greg", "Ready to be Sgronk");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());

                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        telemetry.addData(String.format("%s (%d)", recognition.getLabel(), i), String.format("(%.0f, %.0f)", (recognition.getLeft()+recognition.getRight())/2, (recognition.getTop()+recognition.getBottom())/2));
                        i++;
                    }
                    telemetry.update();
                }
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;
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