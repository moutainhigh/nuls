package io.nuls.contract.vm.natives.java.lang;

import io.nuls.contract.vm.Frame;
import io.nuls.contract.vm.MethodArgs;
import io.nuls.contract.vm.ObjectRef;
import io.nuls.contract.vm.Result;
import io.nuls.contract.vm.code.MethodCode;
import io.nuls.contract.vm.natives.NativeMethod;

public class NativeThrowable {

    public static final String TYPE = "java/lang/Throwable";

    public static Result run(MethodCode methodCode, MethodArgs methodArgs, Frame frame) {
        Result result = null;
        switch (methodCode.getName()) {
            case "fillInStackTrace":
                result = fillInStackTrace(methodCode, methodArgs, frame);
                break;
            default:
                frame.nonsupportMethod(methodCode);
                break;
        }
        return result;
    }

    private static Result fillInStackTrace(MethodCode methodCode, MethodArgs methodArgs, Frame frame) {
        int dummy = (int) methodArgs.getInvokeArgs()[0];
        ObjectRef objectRef = methodArgs.getObjectRef();
        Result result = NativeMethod.result(methodCode, objectRef, frame);
        return result;
    }

}
