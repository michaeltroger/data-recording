package com.michaeltroger.datarecording.messaging;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.michaeltroger.datarecording.IView;
import com.michaeltroger.datarecording.R;

import java.util.Set;

public class Messaging {
    private static final String TAG = Messaging.class.getSimpleName();

    private static final String DATARECORDING_REMOTECONTROL_MESSAGE_PATH = "/datarecording_remotecontrol";
    private static final String DATARECORDING_REMOTECONTROL_CAPABILITY_NAME = "datarecording_remotecontrol";

    private GoogleApiClient googleApiClient;
    private String transcriptionNodeId;
    private IView view;

    public Messaging(@NonNull final Context context, @NonNull final IView view) {
        this.view = view;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        googleApiClient.connect();
        setupDatarecodingRemotecontrol();
    }

    public void cancel() {
        googleApiClient.disconnect();
    }

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

    private void updateTranscriptionCapability(final CapabilityInfo capabilityInfo) {
        final Set<Node> connectedNodes = capabilityInfo.getNodes();
        transcriptionNodeId = pickBestNodeId(connectedNodes);
    }

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
