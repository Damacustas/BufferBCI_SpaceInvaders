package com.badlogic.invaders;

import nl.fcdonders.fieldtrip.bufferclient.BufferEvent;

/**
 * Created by lars on 11/21/15.
 */
public class InvadersController extends BufferBciController {
    public static final int AXIS_X = 0;
    public static final int BTN_FIRE = 1;

    public InvadersController() {
        super();

        addAxis(AXIS_X, new BufferBciController.BufferBciAxisProcessor() {
            @Override
            public boolean trigger(BufferEvent evt) {
                return evt.getType().toString().equals("AXIS_X");
            }

            @Override
            public float getValue(BufferEvent evt) {
                String v = evt.getValue().toString();
                return Float.parseFloat(v);
            }
        });

        addButton(BTN_FIRE, new BufferBciController.BufferBciButtonProcessor() {
            @Override
            public boolean trigger(BufferEvent evt) {
                return evt.getType().toString().equals("BTN_FIRE");
            }

            @Override
            public boolean isActivated(BufferEvent evt) {
                return evt.getValue().toString().equals("down");
            }
        });
    }
}
