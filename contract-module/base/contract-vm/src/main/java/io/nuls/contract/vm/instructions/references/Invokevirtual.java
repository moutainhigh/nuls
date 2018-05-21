package io.nuls.contract.vm.instructions.references;

import io.nuls.contract.vm.Frame;
import io.nuls.contract.vm.MethodArgs;
import io.nuls.contract.vm.ObjectRef;
import io.nuls.contract.vm.Result;
import io.nuls.contract.vm.code.MethodCode;
import io.nuls.contract.vm.code.VariableType;
import io.nuls.contract.vm.natives.NativeMethod;
import io.nuls.contract.vm.util.Log;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.List;

public class Invokevirtual {

    public static void invokevirtual(Frame frame) {
        MethodInsnNode methodInsnNode = frame.methodInsnNode();
        String className = methodInsnNode.owner;
        String methodName = methodInsnNode.name;
        String methodDesc = methodInsnNode.desc;

        List<VariableType> variableTypes = VariableType.parseArgs(methodDesc);
        MethodArgs methodArgs = new MethodArgs(variableTypes, frame.getOperandStack(), false);
        ObjectRef objectRef = methodArgs.getObjectRef();
        if (objectRef == null) {
            frame.throwNullPointerException();
            return;
        }

        className = objectRef.getVariableType().getType();
        MethodCode methodCode = frame.getMethodArea().loadMethod(className, methodName, methodDesc);

        Log.opcode(frame.getCurrentOpCode(), objectRef, methodName, methodDesc);

        Result result = NativeMethod.run(methodCode, methodArgs, frame);
        if (result != null) {
            return;
        }

        frame.getVm().run(methodCode, methodArgs.getFrameArgs());
    }

}
