// IBus.aidl
package com.sensorberg.easyipc;

import com.sensorberg.easyipc.ParcelableEvent;
import java.util.List;

interface IReceiver {
    void onEvent(in ParcelableEvent event);
    void setTypes(in List<String> types);
}
