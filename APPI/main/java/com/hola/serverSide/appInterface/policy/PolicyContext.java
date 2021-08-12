package com.hola.serverSide.appInterface.policy;

public abstract class PolicyContext
{
    protected ParticipantInfo callerInfo;
    protected ParticipantInfo calleeInfo;

    public abstract ParticipantInfo getCalleeInfo ();

    public abstract ParticipantInfo getCallerInfo ();
}
