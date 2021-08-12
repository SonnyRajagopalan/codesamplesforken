package com.hola.serverSide.appInterface.call;

/*
 * Hola specific imports
 */
import com.hola.serverSide.appInterface.policy.PolicyContext;
import com.hola.serverSide.appInterface.policy.ParticipantInfo;
import com.hola.serverSide.appInterface.call.Location;

public class ProtoCall extends PolicyContext
{
    public ProtoCall (ParticipantInfo _callerInfo, ParticipantInfo _calleeInfo)
    {
	this.callerInfo = _callerInfo;
	this.calleeInfo = _calleeInfo;
    }

    public ParticipantInfo getCallerInfo ()
    {
	return this.callerInfo;
    }

    public ParticipantInfo getCalleeInfo ()
    {
	return this.calleeInfo;
    }
}
