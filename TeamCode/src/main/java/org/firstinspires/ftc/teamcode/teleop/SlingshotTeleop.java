package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.mechanisms.Cycle;
import org.firstinspires.ftc.teamcode.mechanisms.drive.DriveTrain;
import org.firstinspires.ftc.teamcode.misc.gamepad.GamepadMapping;

@TeleOp
public class SlingshotTeleop extends OpMode {
    private GamepadMapping controls;
    private DriveTrain dt;
    private Robot robot;
    private Cycle cycle;
    @Override
    public void init() {
        controls = new GamepadMapping(gamepad1, gamepad2);
        robot = new Robot(hardwareMap, telemetry, controls);
        cycle = new Cycle(telemetry, controls, robot);

        robot.outtake.setMotorsToTeleOpMode();
    }

    @Override
    public void init_loop() {
        telemetry.addLine("If our alliance is blue, press gamepad1's x, BEFORE you start");
        telemetry.addLine("The alliance color defaults to blue");
        telemetry.addData("Color Sensor Is Blue", robot.intake.colorSensor.getIsBlue());
        controls.isBlue.update(gamepad1.x);
        if (controls.isBlue.value()) {
            robot.intake.colorSensor.setIsBlue(true);
        } else {
            robot.intake.colorSensor.setIsBlue(false);
        }
    }

    // TODO ask jihoon, this is so we don't move during init
    @Override
    public void start() {
        // run once when we start
        robot.hardwareSoftReset();
    }

    @Override
    public void loop() {
        telemetry.addData("Color Sensor Is Blue", robot.intake.colorSensor.getIsBlue());
        cycle.update();
    }
}
