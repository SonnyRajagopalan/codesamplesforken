/*
 * This source code file is the property of Pacifi, Inc.
 * Copyright: 2015-2016.
 * All rights reserved.  
 * 
 * @author: Sonny Rajagopalan
 * @copyright: Pacifi, Inc.
 */
package com.hola.serverSide.ariApplication.ari;

public enum ARITransactionType
{
    AsteriskInfo            (1),
    BuildInfo               (2),
    ConfigInfo              (3),
    SetId                   (4),
    StatusInfo              (5),
    SystemInfo              (6),
    Variable                (7),
    Endpoint                (8),
    TextMessage             (9),
    TextMessageVariable     (10),
    CallerID                (11),
    Channel                 (12),
    Dialed                  (13),
    DialplanCEP             (14),
    Bridge                  (15),
    LiveRecording           (16),
    StoredRecording         (17),
    FormatLangPair          (18),
    Sound                   (19),
    Playback                (20),
    DeviceState             (21),
    Mailbox                 (22),
    ApplicationReplaced     (23),
    BridgeAttendedTransfer  (24),
    BridgeBlindTransfer     (25),
    BridgeCreated           (26),
    BridgeDestroyed         (27),
    BridgeMerged            (28),
    ChannelCallerId         (29),
    ChannelConnectedLine    (30),
    ChannelCreated          (31),
    ChannelDestroyed        (32),
    ChannelDialplan         (33),
    ChannelDtmfReceived     (34),
    ChannelEnteredBridge    (35),
    ChannelHangupRequest    (36),
    ChannelLeftBridge       (37),
    ChannelStateChange      (38),
    ChannelTalkingFinished  (39),
    ChannelTalkingStarted   (40),
    ChannelUserevent        (41),
    ChannelVarset           (42),
    DeviceStateChanged      (43),
    Dial                    (44),
    EndpointStateChange     (45),
    Event                   (46),
    Message                 (47),
    MissingParams           (48),
    PlaybackFinished        (49),
    PlaybackStarted         (50),
    RecordingFailed         (51),
    RecordingFinished       (52),
    RecordingStarted        (53),
    StasisEnd               (54),
    StasisStart             (55),
    TextMessageReceived     (56),
    Application             (57),
    // The following added in 13.6.0 ?
    ConfigTuple             (58),
    LogChannel              (59),
    Module                  (60),
    ChannelHold             (61),
    ChannelUnhold           (62),
    ContactInfo             (63),
    ContactStatusChange     (64),
    Peer                    (65),
    PeerStatusChange        (66),
    /*
     *
     */
    Unknown                 (99);
    private final int typeInt;


    ARITransactionType (int _typeInt)
    {
	this.typeInt = _typeInt;
    }

    public int getTransactionTypeInt ()
    {
	return typeInt;
    }

    public String getTransactionTypeString ()
    {
	switch (this)
	    {
	    case AsteriskInfo:
		{
		    return "AsteriskInfo";
		}
	    case BuildInfo:
		{
		    return "BuildInfo";
		}
	    case ConfigInfo:
		{
		    return "ConfigInfo";
		}
	    case SetId:
		{
		    return "SetId";
		}
	    case StatusInfo:
		{
		    return "StatusInfo";
		}
	    case SystemInfo:
		{
		    return "SystemInfo";
		}
	    case Variable:
		{
		    return "Variable";
		}
	    case Endpoint:
		{
		    return "Endpoint";
		}
	    case TextMessage:
		{
		    return "TextMessage";
		}
	    case TextMessageVariable:
		{
		    return "TextMessageVariable";
		}
	    case CallerID:
		{
		    return "CallerID";
		}
	    case Channel:
		{
		    return "Channel";
		}
	    case Dialed:
		{
		    return "Dialed";
		}
	    case DialplanCEP:
		{
		    return "DialplanCEP";
		}
	    case Bridge:
		{
		    return "Bridge";
		}
	    case LiveRecording:
		{
		    return "LiveRecording";
		}
	    case StoredRecording:
		{
		    return "StoredRecording";
		}
	    case FormatLangPair:
		{
		    return "FormatLangPair";
		}
	    case Sound:
		{
		    return "Sound";
		}
	    case Playback:
		{
		    return "Playback";
		}
	    case DeviceState:
		{
		    return "DeviceState";
		}
	    case Mailbox:
		{
		    return "Mailbox";
		}
	    case ApplicationReplaced:
		{
		    return "ApplicationReplaced";
		}
	    case BridgeAttendedTransfer:
		{
		    return "BridgeAttendedTransfer";
		}
	    case BridgeBlindTransfer:
		{
		    return "BridgeBlindTransfer";
		}
	    case BridgeCreated:
		{
		    return "BridgeCreated";
		}
	    case BridgeDestroyed:
		{
		    return "BridgeDestroyed";
		}
	    case BridgeMerged:
		{
		    return "BridgeMerged";
		}
	    case ChannelCallerId:
		{
		    return "ChannelCallerId";
		}
	    case ChannelConnectedLine:
		{
		    return "ChannelConnectedLine";
		}
	    case ChannelCreated:
		{
		    return "ChannelCreated";
		}
	    case ChannelDestroyed:
		{
		    return "ChannelDestroyed";
		}
	    case ChannelDialplan:
		{
		    return "ChannelDialplan";
		}
	    case ChannelDtmfReceived:
		{
		    return "ChannelDtmfReceived";
		}
	    case ChannelEnteredBridge:
		{
		    return "ChannelEnteredBridge";
		}
	    case ChannelHangupRequest:
		{
		    return "ChannelHangupRequest";
		}
	    case ChannelLeftBridge:
		{
		    return "ChannelLeftBridge";
		}
	    case ChannelStateChange:
		{
		    return "ChannelStateChange";
		}
	    case ChannelTalkingFinished:
		{
		    return "ChannelTalkingFinished";
		}
	    case ChannelTalkingStarted:
		{
		    return "ChannelTalkingStarted";
		}
	    case ChannelUserevent:
		{
		    return "ChannelUserevent";
		}
	    case ChannelVarset:
		{
		    return "ChannelVarset";
		}
	    case DeviceStateChanged:
		{
		    return "DeviceStateChanged";
		}
	    case Dial:
		{
		    return "Dial";
		}
	    case EndpointStateChange:
		{
		    return "EndpointStateChange";
		}
	    case Event:
		{
		    return "Event";
		}
	    case Message:
		{
		    return "Message";
		}
	    case MissingParams:
		{
		    return "MissingParams";
		}
	    case PlaybackFinished:
		{
		    return "PlaybackFinished";
		}
	    case PlaybackStarted:
		{
		    return "PlaybackStarted";
		}
	    case RecordingFailed:
		{
		    return "RecordingFailed";
		}
	    case RecordingFinished:
		{
		    return "RecordingFinished";
		}
	    case RecordingStarted:
		{
		    return "RecordingStarted";
		}
	    case StasisEnd:
		{
		    return "StasisEnd";
		}
	    case StasisStart:
		{
		    return "StasisStart";
		}
	    case TextMessageReceived:
		{
		    return "TextMessageReceived";
		}
	    case Application:
		{
		    return "Application";
		}
	    case ConfigTuple:
		{
		    return "ConfigTuple";
		}
	    case LogChannel:
		{
		    return "LogChannel";
		}
	    case Module:
		{
		    return "Module";
		}
	    case ChannelHold:
		{
		    return "ChannelHold";
		}
	    case ChannelUnhold:
		{
		    return "ChannelUnhold";
		}
	    case ContactInfo:
		{
		    return "ContactInfo";
		}
	    case ContactStatusChange:
		{
		    return "ContactStatusChange";
		}
	    case Peer:
		{
		    return "Peer";
		}
	    case PeerStatusChange:
		{
		    return "PeerStatusChange";
		}
	    default:
		return "Unknown"; 
	    }
    }

    public static ARITransactionType getTransactionTypeIntFromString (String transactionType)
    {
	switch (transactionType)
	    {
	    case "AsteriskInfo":
		return AsteriskInfo;
	    case "BuildInfo":
		return BuildInfo;
	    case "ConfigInfo":
		return ConfigInfo;
	    case "SetId":
		return SetId;
	    case "StatusInfo":
		return StatusInfo;
	    case "SystemInfo":
		return SystemInfo;
	    case "Variable":
		return Variable;
	    case "Endpoint":
		return Endpoint;
	    case "TextMessage":
		return TextMessage;
	    case "TextMessageVariable":
		return TextMessageVariable;
	    case "CallerID":
		return CallerID;
	    case "Channel":
		return Channel;
	    case "Dialed":
		return Dialed;
	    case "DialplanCEP":
		return DialplanCEP;
	    case "Bridge":
		return Bridge;
	    case "LiveRecording":
		return LiveRecording;
	    case "StoredRecording":
		return StoredRecording;
	    case "FormatLangPair":
		return FormatLangPair;
	    case "Sound":
		return Sound;
	    case "Playback":
		return Playback;
	    case "DeviceState":
		return DeviceState;
	    case "Mailbox":
		return Mailbox;
	    case "ApplicationReplaced":
		return ApplicationReplaced;
	    case "BridgeAttendedTransfer":
		return BridgeAttendedTransfer;
	    case "BridgeBlindTransfer":
		return BridgeBlindTransfer;
	    case "BridgeCreated":
		return BridgeCreated;
	    case "BridgeDestroyed":
		return BridgeDestroyed;
	    case "BridgeMerged":
		return BridgeMerged;
	    case "ChannelCallerId":
		return ChannelCallerId;
	    case "ChannelConnectedLine":
		return ChannelConnectedLine;
	    case "ChannelCreated":
		return ChannelCreated;
	    case "ChannelDestroyed":
		return ChannelDestroyed;
	    case "ChannelDialplan":
		return ChannelDialplan;
	    case "ChannelDtmfReceived":
		return ChannelDtmfReceived;
	    case "ChannelEnteredBridge":
		return ChannelEnteredBridge;
	    case "ChannelHangupRequest":
		return ChannelHangupRequest;
	    case "ChannelLeftBridge":
		return ChannelLeftBridge;
	    case "ChannelStateChange":
		return ChannelStateChange;
	    case "ChannelTalkingFinished":
		return ChannelTalkingFinished;
	    case "ChannelTalkingStarted":
		return ChannelTalkingStarted;
	    case "ChannelUserevent":
		return ChannelUserevent;
	    case "ChannelVarset":
		return ChannelVarset;
	    case "DeviceStateChanged":
		return DeviceStateChanged;
	    case "Dial":
		return Dial;
	    case "EndpointStateChange":
		return EndpointStateChange;
	    case "Event":
		return Event;
	    case "Message":
		return Message;
	    case "MissingParams":
		return MissingParams;
	    case "PlaybackFinished":
		return PlaybackFinished;
	    case "PlaybackStarted":
		return PlaybackStarted;
	    case "RecordingFailed":
		return RecordingFailed;
	    case "RecordingFinished":
		return RecordingFinished;
	    case "RecordingStarted":
		return RecordingStarted;
	    case "StasisEnd":
		return StasisEnd;
	    case "StasisStart":
		return StasisStart;
	    case "TextMessageReceived":
		return TextMessageReceived;
	    case "Application":
		return Application;
	    case "ConfigTuple":
		return ConfigTuple;
	    case "LogChannel":
		return LogChannel;
	    case "Module":
		return Module;
	    case "ChannelHold":
		return ChannelHold;
	    case "ChannelUnhold":
		return ChannelUnhold;    
	    case "ContactInfo":
		return ContactInfo;
	    case "ContactStatusChange":
		return ContactStatusChange;
	    case "Peer":
		return Peer;
	    case "PeerStatusChange":
		return PeerStatusChange;
	    default:
		return Unknown;
	    }
    }

    public static String getARITransactionFamilyType (ARITransactionType ariTransactionType)
    {
	switch (ariTransactionType)
	    {
	    case ApplicationReplaced:
	    case BridgeAttendedTransfer:
	    case BridgeBlindTransfer:
	    case BridgeCreated:
	    case BridgeDestroyed:
	    case BridgeMerged:
	    case ChannelCallerId:
	    case ChannelConnectedLine:
	    case ChannelCreated:
	    case ChannelDestroyed:
	    case ChannelDialplan:
	    case ChannelDtmfReceived:
	    case ChannelEnteredBridge:
	    case ChannelHangupRequest:
	    case ChannelLeftBridge:
	    case ChannelStateChange:
	    case ChannelTalkingFinished:
	    case ChannelTalkingStarted:
	    case ChannelUserevent:
	    case ChannelVarset:
	    case DeviceStateChanged:
	    case Dial:
	    case EndpointStateChange:
	    case PlaybackFinished:
	    case PlaybackStarted:
	    case RecordingFailed:
	    case RecordingFinished:
	    case RecordingStarted:
	    case StasisEnd:
	    case StasisStart:
	    case TextMessageReceived:
	    case ConfigTuple:
	    case LogChannel:
	    case Module:
	    case ChannelHold:
	    case ChannelUnhold:
	    case ContactInfo:
	    case ContactStatusChange:
	    case Peer:
	    case PeerStatusChange:
		return "Event";
	    case Event:
	    case MissingParams:
		return "Message";
	    case AsteriskInfo:
	    case BuildInfo:
	    case ConfigInfo:
	    case SetId:
	    case StatusInfo:
	    case SystemInfo:
	    case Variable:
	    case Endpoint:
	    case TextMessage:
	    case TextMessageVariable:
	    case CallerID:
	    case Channel:
	    case Dialed:
	    case DialplanCEP:
	    case Bridge:
	    case LiveRecording:
	    case StoredRecording:
	    case FormatLangPair:
	    case Sound:
	    case Playback:
	    case DeviceState:
	    case Mailbox:
	    case Message:
	    case Application:
		return "RESTMessage";
	    default:
		return "Some other type of message family";
	    }
    }

    public static String getARITransactionFamilyTypeFromString (String ariTransactionTypeString)
    {
	switch (ariTransactionTypeString)
	    {
	    case "ApplicationReplaced":
	    case "BridgeAttendedTransfer":
	    case "BridgeBlindTransfer":
	    case "BridgeCreated":
	    case "BridgeDestroyed":
	    case "BridgeMerged":
	    case "ChannelCallerId":
	    case "ChannelConnectedLine":
	    case "ChannelCreated":
	    case "ChannelDestroyed":
	    case "ChannelDialplan":
	    case "ChannelDtmfReceived":
	    case "ChannelEnteredBridge":
	    case "ChannelHangupRequest":
	    case "ChannelLeftBridge":
	    case "ChannelStateChange":
	    case "ChannelTalkingFinished":
	    case "ChannelTalkingStarted":
	    case "ChannelUserevent":
	    case "ChannelVarset":
	    case "DeviceStateChanged":
	    case "Dial":
	    case "EndpointStateChange":
	    case "PlaybackFinished":
	    case "PlaybackStarted":
	    case "RecordingFailed":
	    case "RecordingFinished":
	    case "RecordingStarted":
	    case "StasisEnd":
	    case "StasisStart":
	    case "TextMessageReceived":
	    case "ConfigTuple":
	    case "LogChannel":
	    case "Module":
	    case "ChannelHold":
	    case "ChannelUnhold":
	    case "ContactInfo":
	    case "ContactStatusChange":
	    case "Peer":
	    case "PeerStatusChange":
		return "Event";
	    case "Event":
	    case "MissingParams":
		return "Message";
	    case "AsteriskInfo":
	    case "BuildInfo":
	    case "ConfigInfo":
	    case "SetId":
	    case "StatusInfo":
	    case "SystemInfo":
	    case "Variable":
	    case "Endpoint":
	    case "TextMessage":
	    case "TextMessageVariable":
	    case "CallerID":
	    case "Channel":
	    case "Dialed":
	    case "DialplanCEP":
	    case "Bridge":
	    case "LiveRecording":
	    case "StoredRecording":
	    case "FormatLangPair":
	    case "Sound":
	    case "Playback":
	    case "DeviceState":
	    case "Mailbox":
	    case "Message":
	    case "Application":
		return "RESTMessage";
	    default:
		return "Some other type of message family";
	    }
    }
}
