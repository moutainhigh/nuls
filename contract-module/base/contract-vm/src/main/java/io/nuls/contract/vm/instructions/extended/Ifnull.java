package io.nuls.contract.vm.instructions.extended;

import io.nuls.contract.vm.Frame;
import io.nuls.contract.vm.ObjectRef;
import io.nuls.contract.vm.util.Log;

public class Ifnull {

    public static void ifnull(final Frame frame) {
        ObjectRef value = frame.getOperandStack().popRef();
        boolean result = value == null;
        if (result) {
            frame.jump();
        }

        Log.result(frame.getCurrentOpCode(), result, value, "==", null);
    }

}
