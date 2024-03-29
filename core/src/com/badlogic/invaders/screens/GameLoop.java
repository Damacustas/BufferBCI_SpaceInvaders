/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.badlogic.invaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.invaders.*;
import com.badlogic.invaders.simulation.Simulation;
import com.badlogic.invaders.simulation.SimulationListener;
import nl.fcdonders.fieldtrip.bufferclient.BufferEvent;

public class GameLoop extends InvadersScreen implements SimulationListener {
	/** the simulation **/
	private final Simulation simulation;
	/** the renderer **/
	private final Renderer renderer;
	/** explosion sound **/
	private final Sound explosion;
	/** shot sound **/
	private final Sound shot;

	/****** BufferBCI support ******/
    /** buffer client **/
    protected final BufferBciInput buffer;
	/** buffer_bci controller **/
	protected final InvadersController controller;

	/** controller **/
	private int buttonsPressed = 0;
	private ControllerListener listener = new ControllerAdapter() {
		@Override
		public boolean buttonDown(Controller controller, int buttonIndex) {
			buttonsPressed++;
			return true;
		}

		@Override
		public boolean buttonUp(Controller controller, int buttonIndex) {
			buttonsPressed--;
			return true;
		}
	};

	public GameLoop (Invaders invaders) {
		super(invaders);
		simulation = new Simulation();
		simulation.listener = this;
		renderer = new Renderer();
		explosion = Gdx.audio.newSound(Gdx.files.internal("data/explosion.wav"));
		shot = Gdx.audio.newSound(Gdx.files.internal("data/shot.wav"));

		// Connect to buffer.
        buffer = new BufferBciInput(10);
        buffer.connect("localhost", 1972);

		// Create a controller and add it as a listener.
		controller = new InvadersController();
		controller.addListener(listener);
		buffer.addArrivedEventsListener(controller);
	}

	@Override
	public void dispose () {
		renderer.dispose();
		shot.dispose();
		explosion.dispose();
		if (invaders.getController() != null) {
			invaders.getController().removeListener(listener);
		}
		simulation.dispose();
	}

	@Override
	public boolean isDone () {
		return simulation.ship.lives == 0;
	}

	@Override
	public void draw (float delta) {
		renderer.render(simulation, delta);
	}

	@Override
	public void update (float delta) {
		// update simulation.
		simulation.update(delta);

		// update controller.
		controller.update();

		// move ship.
		float axisValue = controller.getAxis(InvadersController.AXIS_X) * 0.5f;
		if (Math.abs(axisValue) > 0.125f) { // Use a threshold for actuation.
			if (axisValue > 0) {
				simulation.moveShipRight(delta, axisValue);
			} else {
				simulation.moveShipLeft(delta, -axisValue);
			}
		}

		// shoot
		if(buttonsPressed > 0) {
			simulation.shot();
		}
	}

	@Override
	public void explosion () {
		explosion.play();
	}

	@Override
	public void shot () {
		shot.play();
	}
}
