package com.neos.trackandroll.cervo.controller.distribution.maestro;

import com.neos.trackandroll.cervo.communication.maestro.message.MaestroMessage;
import com.neos.trackandroll.cervo.communication.maestro.message.Params;
import com.neos.trackandroll.cervo.communication.maestro.protocol.MaestroProcessOut;
import com.neos.trackandroll.cervo.communication.maestro.protocol.MaestroProtocolVocabulary;
import com.neos.trackandroll.cervo.controller.Brain;
import com.neos.trackandroll.cervo.controller.distribution.AbstractActionForBrain;
import com.neos.trackandroll.cervo.utils.LogUtils;

public class ActionSendTagDisconnected extends AbstractActionForBrain {

    /**
     * Main constructor of the action to send when the tag is disconnected
     *
     * @param brain : the brain of the system
     */
    public ActionSendTagDisconnected(Brain brain) {
        super(brain);
    }

    /**
     * Method called to execute the action for brain
     *
     * @param params : the different params given for the action
     */
    @Override
    public void execute(String[] params) {
        LogUtils.d("Send tag disconnected => " + params[0]);

        Params maestroMessageParams = new Params();
        maestroMessageParams.setSensorId(params[0]);
        maestroMessageParams.setSensorType(MaestroProtocolVocabulary.CAPTEUR_TYPE_DOSSARD);
        MaestroMessage maestroMessage = new MaestroMessage(
                MaestroProcessOut.PROCESS_NOTIFY_DISCONNECTION, maestroMessageParams
        );
        brain.getMaestroCommunication().getMaestroProxy().sendMessage(maestroMessage);
    }
}
