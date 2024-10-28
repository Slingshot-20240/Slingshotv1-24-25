package org.firstinspires.ftc.teamcode.misc.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GamepadMapping {

    private Gamepad gamepad1;
    private Gamepad gamepad2;

    // INTAKE
    // Extend/Retract Intake (Intake automatically runs, this extends linkage) -> left bumper

    // Pivot Down - comes out in the middle of extension, automatic
    // Pivot Out
    // Outtake bad sample (automatically does this)

    // OUTTAKE
    // Slides go up/down -> toggle
    // Bucket flips & releases sample -> one of the buttons on the right side

    // SCORING
    // slides increment pos -> button to latch on (button once to go up, again to latch)
    // claw releases/closes -> on misc controller, toggle, buttons on right side

    // DRIVETRAIN
    // --------------
    public static double drive = 0.0;
    public static double strafe = 0.0;
    public static double turn = 0.0;

    // INTAKE (ACTIVE)
    // --------------
    public static Toggle extend; // extend intake
    public static Toggle clearIntake;
    public static Toggle transfer;
    public static Toggle intakeOnToIntake;
    public static Toggle intakeOnToClear;

    // INTAKE (CLAW)
    public static double wristYaw = 0;

    // INTAKE (ACTIVE CLAW)
    // --------------
    // this might be where we go between intaking and hovering, and then transfer pos is automatic reset when we extendo back in? (and transfer button moves it back too)
    // also a trigger
    // TODO edit these pivots, needs to be more automatic
    public static Toggle pivot;
    // transfer sample should be automatic here
    public static Toggle transferHover;


    // OUTTAKE
    // --------------
    public static Toggle flipBucket;
    public static Toggle highBasket;
    public static Toggle lowBasket;

    // SCORING
    // --------------
    public static Toggle latchSpecimen;
    public static Toggle switchClaw;
    public static Toggle L1hang;

    // LOCKED HEADING
    // -----------------
    public static Toggle toggleLockedHeading;
    public static boolean lock90 = false;
    public static boolean lock180 = false;
    public static boolean lock270 = false;
    public static boolean lock360 = false;

    // OTHER
    // --------------
    public static Toggle botToBaseState;
    public static Toggle isBlue;

    // TESTING BUTTONS
    // NOT TO BE USED FOR COMP
    // -------------------------------

    // claw
    public static Toggle closeClaw;
    public static Toggle armDown;

    public GamepadMapping(Gamepad gamepad1, Gamepad gamepad2) {
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

        extend = new Toggle(false);
        intakeOnToIntake = new Toggle(false);
        intakeOnToClear = new Toggle(false);
        transfer = new Toggle(false);

        pivot = new Toggle(false);
        transferHover = new Toggle(false);

        flipBucket = new Toggle(false);
        highBasket = new Toggle(false);
        lowBasket = new Toggle(false);
        L1hang = new Toggle(false);

//        latchSpecimen = new Toggle(false);
//        switchClaw = new Toggle(false);

        botToBaseState = new Toggle(false);
        clearIntake = new Toggle(false);
        isBlue = new Toggle(false);

        // claw (could be real?)
        closeClaw = new Toggle(false);
        armDown = new Toggle(false);
    }

    public void update() {
        joystickUpdate();

        // Intake (Active)
        activeIntakeUpdate();

        // Outtake (All Gamepad2)
        lowBasket.update(gamepad2.right_bumper);
        highBasket.update(gamepad2.left_bumper);
        flipBucket.update(gamepad2.a);

        // Specimen
//        latchSpecimen.update(gamepad2.a);
//        switchClaw.update(gamepad1.x);
        L1hang.update(gamepad2.dpad_up); // TODO Ask Drivers

        // Reset/Fail Safes (Both controllers should have these)
        botToBaseState.update(gamepad1.dpad_down);
        botToBaseState.update(gamepad2.dpad_down);
    }

    public void joystickUpdate() {
        drive = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;
    }

    public void clawUpdate() {
        wristYaw = gamepad2.right_stick_x;
    }

    public void activeClawUpdate() {
        extend.update(gamepad1.right_bumper);
        pivot.update(gamepad1.a); // hover and intaking

        intakeOnToIntake.update(gamepad1.right_trigger > 0.5);
        intakeOnToClear.update(gamepad1.left_trigger > 0.5);

        transferHover.update(gamepad1.left_bumper);
    }

    // v1 robot
    public void activeIntakeUpdate() {
        extend.update(gamepad1.right_bumper);
        intakeOnToIntake.update(gamepad1.right_trigger > 0.5);
        intakeOnToClear.update(gamepad1.left_trigger > 0.5);
        transfer.update(gamepad2.y);
    }
}
