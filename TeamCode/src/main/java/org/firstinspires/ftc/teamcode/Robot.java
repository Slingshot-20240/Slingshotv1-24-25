package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.fsm.ClawCycle;
import org.firstinspires.ftc.teamcode.mechanisms.drive.DriveTrain;
import org.firstinspires.ftc.teamcode.misc.gamepad.GamepadMapping;
import org.firstinspires.ftc.teamcode.mechanisms.intake.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.outtake.Outtake;

public class Robot{
    // odo:
    // 0 control hub -> back
    // 2 control hub -> side/right

    // intake:
    // pivot (max) -> 1 on expansion hub
    // left linkage (max) -> 4 on control hub
    // right linkage (max) -> 0 on control hub
    // back roller (mini) -> 0 on expansion hub
    // roller motor -> 1 on expansion hub
    // analog encoder -> 0 on expansion analog ports
    // color sensor -> 1 on control hub i2c ports

    // wrist (gobilda torque) ->
    // claw (mini) -> 0 on expansion hub
    // v4b (max) ->

    // drivetrain:
    // rightBack = 0 control
    // rightFront = 1 control
    // leftBack = 3 control
    // leftFront = 2 control

    // outtake:
    // slideLeft = expansion 3
    // slideRight = expansion 2
    // bucketServo = 2 control

    public DriveTrain drivetrain;
    // public ReLocalizer ultraSonics;
    public IMU imu;
    public Outtake outtake;
    public Intake intake;
    public GamepadMapping controls;

    public Robot(HardwareMap hardwareMap, Telemetry telemetry, GamepadMapping controls) {
        imu = hardwareMap.get(IMU.class, "imu");
        // params for slingshot robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT));

          // params for papaya (tester bot)
//        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
//                RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
//                RevHubOrientationOnRobot.UsbFacingDirection.LEFT));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        // will reset imu before auton only, in resetHardware

        this.controls = controls;
        drivetrain = new DriveTrain(hardwareMap, imu, telemetry, controls);
        intake = new Intake(hardwareMap, telemetry, controls);
        outtake = new Outtake(hardwareMap, 0, .02, 0, 0.0001, 0.03, telemetry, controls); // tune PID values
        //ultraSonics = new ReLocalizer(hardwareMap, imu);
    }

//    public Pose2d reLocalize(){
//        double currentAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) + 90;
//        double backDistance = ultraSonics.getBackDistance(currentAngle);
//        double sideDistance = ultraSonics.getSideDistance(currentAngle);
//        return new Pose2d(-72 + backDistance, -72 + sideDistance, Math.toRadians(currentAngle));
//    }

    // This is for EVERYTHING, to be called before auton and only before auton (or in the resetHardware auton class if we don't run auton)
    public void hardwareHardReset() {
        // reset outtake
        outtake.resetEncoders();
        outtake.resetHardware();
        // reset intake
        intake.resetHardware();
        // reset dt & ultrasonics
        imu.resetYaw();
        // reset specimen claw
    }

    // this is for teleop, when we ant to preserve encoder and sensor input
    public void hardwareSoftReset() {
        outtake.resetHardware();
        intake.resetHardware();
    }

    public void updateTelemetry() {
        intake.activeIntake.updateTelemetry();
        outtake.updateTelemetry();
    }
}