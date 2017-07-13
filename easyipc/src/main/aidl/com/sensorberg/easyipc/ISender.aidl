// ISender.aidl
package com.sensorberg.easyipc;

import com.sensorberg.easyipc.IReceiver;
import com.sensorberg.easyipc.ParcelableEvent;
import java.util.List;

interface ISender {
    void setReceiver(in IReceiver receiver);
    void setTypes(in List<String> types);
    void onEvent(in ParcelableEvent event);
}
