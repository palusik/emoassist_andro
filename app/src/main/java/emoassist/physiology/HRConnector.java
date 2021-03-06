package emoassist.physiology;


import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import emoassist.R;
import emoassist.activities.EmotionsActivity;
import emoassist.services.HeartRateRecordingService;

import com.dsi.ant.plugins.AntPluginMsgDefines;
import com.dsi.ant.plugins.AntPluginPcc.IDeviceStateChangeReceiver;
import com.dsi.ant.plugins.AntPluginPcc.IPluginAccessResultReceiver;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc.IHeartRateDataReceiver;

public class HRConnector
        extends HeartRateWatcher implements IDeviceStateChangeReceiver, IPluginAccessResultReceiver<AntPlusHeartRatePcc>
{
    private boolean connected = false;
    private AntPlusHeartRatePcc hrPcc;


    public HRConnector (EmotionsActivity a) {
        super(a);
        // myAct.startStopButton.setEnabled(false);
    }

    public void connect() {
        myact.setStatus("Connecting...");
        // register with the ANT+ plugin
        AntPlusHeartRatePcc.requestAccess(myact, myact, this, this);
        myact.connectButton.setText(myact.getString(R.string.disconnect));
    }

    public void disconnect(boolean skipReleaseAccess) {
        connected = false;
        if (!skipReleaseAccess) { hrPcc.releaseAccess(); }
        hrPcc = null;
        myact.setStatus("Disconnected");
        myact.connectButton.setText(myact.getString(R.string.connect));
    }

    public boolean isConnected() {
        return connected;
    }



    public void stop() {
        super.stop();
        disconnect(false);

    }

    private void newBeat(HeartRateRecordingService hrrec) {
        // add beat to list if recording is running
//        if (isRunning()) { r.add(hrrec); };
    }

    public void onDeviceStateChange(final int newDeviceState) {
        // this method is called back by the ANT+ plugin to notify of device state changes
        myact.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myact.setStatus(hrPcc.getDeviceName() + ": " + AntPlusHeartRatePcc.statusCodeToPrintableString(newDeviceState));
                if (newDeviceState == AntPluginMsgDefines.DeviceStateCodes.DEAD) {
                    disconnect(false);
                }
            }
        });
    }

    public void onResultReceived(AntPlusHeartRatePcc result, int resultCode, int initialDeviceStateCode) {
        // this method is called back by the ANT+ plugin with connection result
        switch(resultCode) {
            // connected
            case AntPluginMsgDefines.MSG_REQACC_RESULT_whatSUCCESS:
                Toast.makeText(myact, "Connected", Toast.LENGTH_SHORT).show();
                hrPcc = result;
                connected = true;
                //myAct.startStopButton.setEnabled(true);
                myact.setStatus(result.getDeviceName() + ": " + AntPlusHeartRatePcc.statusCodeToPrintableString(initialDeviceStateCode));
                subscribeToEvents();
                break;
            // error handling
            case AntPluginMsgDefines.MSG_REQACC_RESULT_whatCHANNELNOTAVAILABLE:
                Toast.makeText(myact, "Channel Not Available", Toast.LENGTH_SHORT).show();
                myact.setStatus("Channel Not Available");
                break;
            case AntPluginMsgDefines.MSG_REQACC_RESULT_whatOTHERFAILURE:
                Toast.makeText(myact, "RequestAccess failed, see logcat for details", Toast.LENGTH_SHORT).show();
                myact.setStatus("RequestAccess failed. Do Menu->Reset.");
                break;
            case AntPluginMsgDefines.MSG_REQACC_RESULT_whatDEPENDENCYNOTINSTALLED:
                Toast.makeText(myact, "ANT+ dependencies not installed", Toast.LENGTH_SHORT).show();
                myact.setStatus("Please install ANT+ dependencies!");
                AlertDialog.Builder adlgBldr = new AlertDialog.Builder(myact);
                adlgBldr.setTitle("Missing Dependency");
                adlgBldr.setMessage("The required application\n\"" + AntPlusHeartRatePcc.getMissingDependencyName() + "\"\n is not installed. Do you want to launch the Play Store to search for it?");
                adlgBldr.setCancelable(true);
                adlgBldr.setPositiveButton("Go to Store", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startStore = null;
                        startStore = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=" + AntPlusHeartRatePcc.getMissingDependencyPackageName()));
                        startStore.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        myact.startActivity(startStore);
                    }
                });
                adlgBldr.setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog waitDialog = adlgBldr.create();
                waitDialog.show();
                break;
            case AntPluginMsgDefines.MSG_REQACC_RESULT_whatUSERCANCELLED:
                Toast.makeText(myact, "User cancelled operation", Toast.LENGTH_SHORT).show();
                myact.setStatus("Cancelled");
                break;
            default:
                Toast.makeText(myact, "Unrecognized result: " + resultCode, Toast.LENGTH_SHORT).show();
                myact.setStatus("Unrecognized result: " + resultCode + ". Do Menu->Reset.");
                break;
        }
    }

    // Subscribe to heart rate events, connecting them to display their data.
    private void subscribeToEvents() {
        hrPcc.subscribeHeartRateDataEvent(new IHeartRateDataReceiver() {
            @Override
            public void onNewHeartRateData(final int currentMessageCount, final int computedHeartRate, final long heartBeatCounter) {
                myact.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myact.setPulse(currentMessageCount, (byte) computedHeartRate, (int) heartBeatCounter);
                    }
                });
                // send a new heart rate record to the watcher
                HeartRateRecordingService hrrec = new HeartRateRecordingService( currentMessageCount, new Date(), (byte) computedHeartRate, (int) heartBeatCounter );
                newBeat(hrrec);
            }
        });

    }
}