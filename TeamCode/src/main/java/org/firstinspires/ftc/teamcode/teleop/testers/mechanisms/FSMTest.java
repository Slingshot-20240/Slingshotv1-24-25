package org.firstinspires.ftc.teamcode.teleop.testers.mechanisms;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.mechanisms.Cycle;
import org.firstinspires.ftc.teamcode.mechanisms.DriveTrain;
import org.firstinspires.ftc.teamcode.mechanisms.intake.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.mechanisms.outtake.Outtake;
import org.firstinspires.ftc.teamcode.misc.gamepad.GamepadMapping;

@TeleOp
public class FSMTest extends OpMode {
    private GamepadMapping controls;
    private Cycle cycle;
    private Robot robot;
    private static IntakeConstants.IntakeState intakeState;
    private Intake intake;
    private Outtake outtake;

    @Override
    public void init() {
        controls = new GamepadMapping(gamepad1, gamepad2);
        robot = new Robot(hardwareMap, telemetry, controls);
        intake = robot.intake;
        outtake = robot.outtake;
        robot.outtake.resetEncoders();
        robot.outtake.returnToRetracted();
        // robot.resetHardware();
        intakeState = IntakeConstants.IntakeState.FULLY_RETRACTED;
        // cycle = new Cycle(hardwareMap, telemetry, controls);
    }

    @Override
    public void loop() {
        controls.update();
        intake.update();
//        if(controls.extend.value()) {
//            intake.motorRollerOnToIntake();
//        } else {
//            intake.motorRollerOff();
//        }

    }
}
