package com.michaeltroger.datarecording.messaging;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.michaeltroger.datarecording.IView;
import com.michaeltroger.datarecording.R;

import java.util.Set;

/**
 * Responsible for sending messages to the smartphone
 */
public class RemoteControlSenderService {
    private static final String TAG = RemoteControlSenderService.class.getSimpleName();

    /**
     * The message path for which the receiver (smartphone) listens to
     */
    private static final String DATARECORDING_REMOTECONTROL_MESSAGE_PATH = "/datarecording_remotecontrol";
    /**
     * The capability which the receiver (smartphone) needs to get this message
     */
    private static final String DATARECORDING_REMOTECONTROL_CAPABILITY_NAME = "datarecording_remotecontrol";

    /**
     * The sensing occurs via the Google API
     */
    private GoogleApiClient googleApiClient;
    /**
     * The conneced node's id, i.e. of the smartphone
     */
    private String transcriptionNodeId;
    /**
     * Needed to display toasts
     */
    private IView view;

    /**
     * Creates an instance by initializing everything needed in
     * order to send a command later on
     * @param context
     * @param view
     */
    public RemoteControlSenderService(@NonNull final Context context, @NonNull final IView view) {
        this.view = view;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
        setupDatarecodingRemotecontrol();
    }

    /**
     * Unsubscribe from the Google API
     */
    public void cancel() {
        googleApiClient.disconnect();
    }

    /**
     * Sends a command to the smartphone
     * @param command The command to send
     */
    public void sendCommandToMobile(@NonNull final String command) {
        if (transcriptionNodeId == null) {
            view.displayToast(R.string.error_no_device);
            Log.e(TAG, "Unable to retrieve node with datarecording remotecontrol capability");
            return;
        }

        Wearable.MessageApi.sendMessage(
                googleApiClient,
                transcriptionNodeId,
                DATARECORDING_REMOTECONTROL_MESSAGE_PATH,
                command.getBytes()
        ).setResultCallback(
                result -> {
                    if (result.getStatus().isSuccess()) {
                        view.displayToast(R.string.sent_message);
                        Log.d(TAG, "command "+ command+" sent to mobile");
                    } else {
                        view.displayToast(R.string.error_failed_to_send_message);
                        Log.e(TAG, "failed to send command "+ command +" to mobile");
                    }
                }
        );

    }

    /**
     * Checks nearby for available devices with the needed capability
     * in case no device nearby -> a callback is registered to get to know
     * state changes
     */
    private void setupDatarecodingRemotecontrol() {
        new Thread(() -> {
            final CapabilityApi.GetCapabilityResult result =
                    Wearable.CapabilityApi.getCapability(
                            googleApiClient,
                            DATARECORDING_REMOTECONTROL_CAPABILITY_NAME,
                            CapabilityApi.FILTER_REACHABLE
                    ).await();

            updateTranscriptionCapability(result.getCapability());
        }).start();

        Wearable.CapabilityApi.addCapabilityListener(
                googleApiClient,
                this::updateTranscriptionCapability,
                DATARECORDING_REMOTECONTROL_CAPABILITY_NAME);
    }

    /**
     * Updates the node to send commands to
     * @param capabilityInfo the capability info to use
     */
    private void updateTranscriptionCapability(final CapabilityInfo capabilityInfo) {
        final Set<Node> connectedNodes = capabilityInfo.getNodes();
        transcriptionNodeId = pickBestNodeId(connectedNodes);
    }

    /**
     * Picks the best node for sending commands to
     * @param nodes a collection of nodes
     * @return the best node or one arbitrarily
     */
    private String pickBestNodeId(final Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (final Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }
}
