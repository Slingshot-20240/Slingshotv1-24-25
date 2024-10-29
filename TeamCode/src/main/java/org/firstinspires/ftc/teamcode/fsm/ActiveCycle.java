package org.firstinspires.ftc.teamcode.fsm;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.mechanisms.intake.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.intake.IntakeConstants;
import org.firstinspires.ftc.teamcode.mechanisms.outtake.Outtake;
import org.firstinspires.ftc.teamcode.misc.gamepad.GamepadMapping;

public class ActiveCycle {
    private Intake intake;
    private Outtake outtake;
    private GamepadMapping controls;
    private Robot robot;

    private ActiveCycle.TransferState transferState;
    private Telemetry telemetry;
    private ElapsedTime loopTime;
    private double startTime;
    public ActiveCycle(Telemetry telemetry, GamepadMapping controls, Robot robot) {
        this.robot = robot;
        this.intake = robot.intake;
        this.outtake = robot.outtake;
        this.controls = controls;

        this.telemetry = telemetry;

        transferState = ActiveCycle.TransferState.BASE_STATE;

        loopTime = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        startTime = loopTime.milliseconds();
    }
    public void activeIntakeUpdate() {
        controls.update();
        robot.drivetrain.update();

        switch (transferState) {
            case BASE_STATE:
                robot.hardwareSoftReset();
                transferState = ActiveCycle.TransferState.EXTENDO_FULLY_RETRACTED;
                break;
            case EXTENDO_FULLY_RETRACTED:
                // have to constantly set power of slide motors back
                outtake.returnToRetracted();
                if (controls.extend.value()) {
                    transferState = ActiveCycle.TransferState.EXTENDO_FULLY_EXTENDED;
                    intake.extendoFullExtend();
                } else if (controls.transfer.value()) {
                    transferState = ActiveCycle.TransferState.TRANSFERING;
                } else if (controls.highBasket.value()) {
                    transferState = ActiveCycle.TransferState.HIGH_BASKET;
                } else if (controls.lowBasket.value()) {
                    transferState = ActiveCycle.TransferState.LOW_BASKET;
                } else if (controls.L1hang.value()) {
                    transferState = ActiveCycle.TransferState.HANGING;
                }
                break;
            case EXTENDO_FULLY_EXTENDED:
                outtake.returnToRetracted();
                if (!controls.extend.value()) {
                    intake.activeIntake.flipUp();
                    intake.activeIntake.transferOff();
                    intake.extendoFullRetract();
                    intake.activeIntake.pivotAxon.setPosition(IntakeConstants.ActiveIntakeStates.TRANSFER.pivotPos());
                    transferState = ActiveCycle.TransferState.EXTENDO_FULLY_RETRACTED;
                }
                if (controls.intakeOnToIntake.locked() || controls.intakeOnToClear.locked()) {
                    transferState = ActiveCycle.TransferState.INTAKING;
                }
                if (controls.botToBaseState.value()) {
                    transferState = ActiveCycle.TransferState.BASE_STATE;
                }
                break;
            case INTAKING:
                outtake.returnToRetracted();
                if (!controls.extend.value()) {
                    transferState = ActiveCycle.TransferState.EXTENDO_FULLY_EXTENDED;
                } else if (controls.intakeOnToIntake.locked()) {
                    intake.activeIntake.flipDownFull();
                    intake.activeIntake.motorRollerOnToIntake();
                    intake.activeIntake.backRollerIdle();
                    if (intake.activeIntake.colorSensor.checkSample().equals(IntakeConstants.SampleTypes.BLUE) && !intake.activeIntake.colorSensor.isBlue) {
                        transferState = ActiveCycle.TransferState.PUSH_OUT_BAD_COLOR;
                        startTime = loopTime.milliseconds();
                    } else if (intake.activeIntake.colorSensor.checkSample().equals(IntakeConstants.SampleTypes.RED) && intake.activeIntake.colorSensor.isBlue) {
                        transferState = ActiveCycle.TransferState.PUSH_OUT_BAD_COLOR;
                        startTime = loopTime.milliseconds();
                    }
                } else if (controls.intakeOnToClear.locked()) {
                    intake.activeIntake.flipDownToClear();
                    intake.activeIntake.clearIntake();
                } else if (!controls.intakeOnToIntake.locked() || !controls.intakeOnToClear.locked()) {
                    intake.activeIntake.flipUp();
                    intake.activeIntake.transferOff();
                } else {
                    if (intake.activeIntake.colorSensor.checkSample().equals(IntakeConstants.SampleTypes.BLUE) && !intake.activeIntake.colorSensor.isBlue) {
                        transferState = ActiveCycle.TransferState.PUSH_OUT_BAD_COLOR;
                        startTime = loopTime.milliseconds();
                    } else if (intake.activeIntake.colorSensor.checkSample().equals(IntakeConstants.SampleTypes.RED) && intake.activeIntake.colorSensor.isBlue) {
                        transferState = ActiveCycle.TransferState.PUSH_OUT_BAD_COLOR;
                        startTime = loopTime.milliseconds();
                    }
                }
                if (intake.activeIntake.colorSensor.checkSample().equals(IntakeConstants.SampleTypes.BLUE) && !intake.activeIntake.colorSensor.isBlue) {
                    transferState = ActiveCycle.TransferState.PUSH_OUT_BAD_COLOR;
                    startTime = loopTime.milliseconds();
                } else if (intake.activeIntake.colorSensor.checkSample().equals(IntakeConstants.SampleTypes.RED) && intake.activeIntake.colorSensor.isBlue) {
                    transferState = ActiveCycle.TransferState.PUSH_OUT_BAD_COLOR;
                    startTime = loopTime.milliseconds();
                }
                break;
            case TRANSFERING:
                outtake.returnToRetracted();
                intake.activeIntake.flipUp();
                intake.extendoFullRetract();
                intake.activeIntake.transferSample();
                if (!controls.transfer.value()) {
                    intake.activeIntake.transferOff();
                    transferState = ActiveCycle.TransferState.EXTENDO_FULLY_RETRACTED;
                }
                break;
            case HIGH_BASKET:
                intake.activeIntake.pivotUpForOuttake();
                intake.extendForOuttake();
                outtake.extendToHighBasket();
                if (controls.flipBucket.value()) {
                    outtake.bucketDeposit();
                }
                if (!controls.highBasket.value()) {
                    transferState = ActiveCycle.TransferState.SLIDES_RETRACTED;
                    break;
                }
                break;
            case LOW_BASKET:
                intake.activeIntake.pivotUpForOuttake();
                intake.extendForOuttake();
                outtake.extendToLowBasket();
                if (controls.flipBucket.value()) {
                    outtake.bucketDeposit();
                }
                if (!controls.lowBasket.value()) {
                    transferState = ActiveCycle.TransferState.SLIDES_RETRACTED;
                }
                break;
            case SLIDES_RETRACTED:
                controls.flipBucket.set(false);
                controls.transfer.set(false);
                // could also do to base state
                outtake.bucketToReadyForTransfer();
                outtake.returnToRetracted();
                intake.extendoFullRetract();
                transferState = ActiveCycle.TransferState.EXTENDO_FULLY_RETRACTED;
                break;
            case HANGING:
                outtake.hang();
                if (!controls.L1hang.value()) {
                    transferState = ActiveCycle.TransferState.SLIDES_RETRACTED;
                }
                break;
            case PUSH_OUT_BAD_COLOR:
                if (loopTime.milliseconds() - startTime <= 1000 && loopTime.milliseconds() - startTime >= 0) {
                    intake.activeIntake.pushOutSample();
                } else {
                    intake.activeIntake.transferOff();
                    transferState = ActiveCycle.TransferState.INTAKING;
                }
                break;
        }
    }
    public enum TransferState {
        BASE_STATE("BASE_STATE"),
        EXTENDO_FULLY_RETRACTED("EXTENDO_FULLY_RETRACTED"),
        EXTENDO_FULLY_EXTENDED("EXTENDO_FULLY_EXTENDED"),
        INTAKING("INTAKING"),
        TRANSFERING("TRANSFERING"),
        SLIDES_RETRACTED("SLIDES_RETRACTED"),
        HIGH_BASKET("HIGH_BASKET"),
        LOW_BASKET("LOW_BASKET"),
        PUSH_OUT_BAD_COLOR("PUSH_OUT_BAD_COLOR"),
        HANGING("HANGING");

        private String state;

        TransferState(String stateName) {
            state = stateName;
        }

        public String stateName() {
            return state;
        }
    }
}
