/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.*;
import phat.commands.MovArmCommand;
import phat.commands.PHATCommand;
import phat.commands.PHATCommandListener;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateAccelerometerSensorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.environment.SpatialEnvironmentAPI;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.server.ServerAppState;
import phat.server.commands.ActivateAccelerometerServerCommand;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 * Class example Test rum simulatios.
 * <br/>
 * Interval Execute Classificator<br/>
 * float timeToChange = 10f;<br/>
 * ...<br/>
 * if (cont > timeToChange && cont < timeToChange + 1 && !fall) {<br/>
 * ...<br/>
 * if (fall && cont > timeToChange + 10) {<br/>
 * <br/>
 * Interval Execute Capture Data<br/>
 * float timeToChange = 2f;<br/>
 * ...<br/>
 * if (cont > timeToChange && cont < timeToChange + 1 && !fall) {<br/>
 * ...<br/>
 * if (fall && cont > timeToChange + 2) {<br/>
 * <br/>
 * Optimal min distance between animation<br/>
 * 0.1 * 0.1 * 0.2 <br/>
 * <br/>
 * <b>Gesture</b>
 * SpinSpindle: 	abrir puerta con dificultad<br/>
 * Hands2Hips: 		llevar manos a la cadera, (dolor de espalda)<br/>
 * Hand2Belly: 		llevar la mano al vientre, (dolor de vientre)<br/>
 * Wave: 			pedir ayuda o llamar atención<br/>
 * ScratchArm: 		rascar el codo<br/>
 * LeverPole: 		molestias en el movimiento y pedir ayuda   <br/>
 * @author UCM
 */
public class ActvityMonitoringDemo implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(ActvityMonitoringDemo.class.getName());
    BodiesAppState bodiesAppState;

    @Override
    public void init(SimpleApplication app) {
        logger.info("init");
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        SpatialEnvironmentAPI seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2016, 2 ,18, 12, 30, 0);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House2", HouseFactory.HouseType.BrickHouse60m));

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);
        openObject("Patient1","Fridge1");

        //Se crean los personajes
        bodiesAppState.createBody(BodiesAppState.BodyType.Elder, "Patient1");

        //Se crean los personajes
        bodiesAppState.createBody(BodiesAppState.BodyType.Young, "Patient2");

        //Se posicionan en la casa
        bodiesAppState.setInSpace("Patient1", "House2", "LivingRoom");

        bodiesAppState.setInSpace("Patient2", "House2", "LivingRoom");

        bodiesAppState.runCommand(new MovArmCommand("Patient1", true, MovArmCommand.LEFT_ARM));

        bodiesAppState.runCommand(new MovArmCommand("Patient2", true, MovArmCommand.LEFT_ARM));

        goCloseToObject("Patient2", "Fridge1");



        //bodiesAppState.runCommand(new MovArmCommand("Patient1", true, MovArmCommand.RIGHT_ARM));

        //app.getCamera().setLocation(new Vector3f(9.692924f, 11.128746f, 4.5429335f));
        app.getCamera().setLocation(new Vector3f(7f, 7.25f, 3.1f));
        app.getCamera().setRotation(new Quaternion(0.44760564f, -0.5397514f, 0.4186186f, 0.5771274f));

    }

    /**
     * Go Up Hand
     * @param idPersont person or patient to actions
     * @param door object to close
     */
    private void goCloseToObject(final String idPersont, final String door) {
        logger.info("goToClose: " + idPersont + ", object: " + door);
        GoCloseToObjectCommand gtc = new GoCloseToObjectCommand(idPersont, door, new PHATCommandListener() {
            @Override
            public void commandStateChanged(PHATCommand command) {
                if (command.getState() == PHATCommand.State.Success) {
                    bodiesAppState.runCommand(new MovArmCommand(idPersont, false, MovArmCommand.LEFT_ARM));
                    bodiesAppState.runCommand(new AlignWithCommand(idPersont, door));
                    bodiesAppState.runCommand(new CloseObjectCommand(idPersont, door));
                    bodiesAppState.runCommand(new MovArmCommand(idPersont,true, MovArmCommand.LEFT_ARM));
                }
            }
        });
        gtc.setMinDistance(0.1f);
        bodiesAppState.runCommand(gtc);
    }

    /**
     * Open Door Actions.
     * @param idPersont
     * @param door
     */
    private void openObject(final String idPersont, final String door) {
        OpenObjectCommand gtc = new OpenObjectCommand(idPersont, door);
        bodiesAppState.runCommand(gtc);
    }

    /**
     * Main Class, executios Test.
     * @param args
     */
    public static void main(String[] args) {
        ActvityMonitoringDemo app = new ActvityMonitoringDemo();

        PHATApplication phat = new PHATApplication(app);
        phat.setDisplayFps(false);
        phat.setDisplayStatView(false);
        phat.setShowSettings(false);

        phat.start();
    }
}