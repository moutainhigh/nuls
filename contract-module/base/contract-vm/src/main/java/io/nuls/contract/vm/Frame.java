package io.nuls.contract.vm;

import io.nuls.contract.vm.code.MethodCode;
import io.nuls.contract.vm.instructions.references.Athrow;
import lombok.Getter;
import org.objectweb.asm.tree.*;

@Getter
public class Frame {

    private final VM vm;

    private final Heap heap;

    private final MethodArea methodArea;

    private final MethodCode methodCode;

    private final int maxStack;

    private final int maxLocals;

    private final OperandStack operandStack;

    private final LocalVariables localVariables;

    private final Result result;

    private AbstractInsnNode currentInsnNode;

    private OpCode currentOpCode;

    public Frame(VM vm, MethodCode methodCode, Object[] args) {
        this.vm = vm;
        this.heap = vm.getHeap();
        this.methodArea = vm.getMethodArea();
        this.methodCode = methodCode;
        this.maxStack = this.methodCode.getMaxStack();
        this.maxLocals = this.methodCode.getMaxLocals();
        this.operandStack = new OperandStack(this.maxStack);
        this.localVariables = new LocalVariables(this.maxLocals, args);
        this.result = new Result(this.methodCode.getReturnVariableType());
        this.currentInsnNode = this.methodCode.getInstructions().getFirst();
    }

    public void step() {
        if (this.currentInsnNode != null) {
            this.currentInsnNode = this.currentInsnNode.getNext();
        }
    }

    public void jump() {
        this.currentInsnNode = jumpInsnNode().label;
    }

    public void jump(LabelNode label) {
        this.currentInsnNode = label;
    }

    public OpCode currentOpCode() {
        if (this.currentInsnNode != null) {
            this.currentOpCode = OpCode.valueOf(this.currentInsnNode.getOpcode());
        } else {
            this.currentOpCode = null;
        }
        return this.currentOpCode;
    }

    public int getLine(LabelNode labelNode) {
        AbstractInsnNode abstractInsnNode = labelNode;
        while (!(abstractInsnNode instanceof LineNumberNode)) {
            abstractInsnNode = abstractInsnNode.getNext();
        }
        return ((LineNumberNode) abstractInsnNode).line;
    }

    public int getLine() {
        AbstractInsnNode abstractInsnNode = this.currentInsnNode;
        while (!(abstractInsnNode instanceof LineNumberNode)) {
            abstractInsnNode = abstractInsnNode.getPrevious();
        }
        return ((LineNumberNode) abstractInsnNode).line;
    }

    public boolean checkArray(ObjectRef arrayRef, int index) {
        if (arrayRef == null) {
            throwNullPointerException();
            return false;
        }
        int length = arrayRef.getDimensions()[0];
        if (index < 0 || index >= length) {
            throwArrayIndexOutOfBoundsException(index);
            return false;
        }
        return true;
    }

    private void throwException(ObjectRef objectRef) {
        this.operandStack.pushRef(objectRef);
        Athrow.athrow(this);
    }

    public void throwNullPointerException() {
        ObjectRef objectRef = this.heap.newObject(NullPointerException.class.getName());
        throwException(objectRef);
    }

    public void throwArrayIndexOutOfBoundsException(int index) {
        ObjectRef objectRef = this.heap.newObject(ArrayIndexOutOfBoundsException.class.getName());
        throwException(objectRef);
    }

    public void throwNegativeArraySizeException() {
        ObjectRef objectRef = this.heap.newObject(NegativeArraySizeException.class.getName());
        throwException(objectRef);
    }

    public void throwClassCastException() {
        ObjectRef objectRef = this.heap.newObject(ClassCastException.class.getName());
        throwException(objectRef);
    }

    private void throwError(ObjectRef objectRef) {
        this.vm.getResult().error(objectRef);
    }

    public void throwStackOverflowError() {
        ObjectRef objectRef = this.heap.newObject(StackOverflowError.class.getName());
        throwError(objectRef);
    }

    public void nonsupportOpCode() {
        int line = getLine();
        throw new RuntimeException(String.format("nonsupport opcode：class(%s), line(%d)", methodCode.getClassName(), line));
    }

    public void nonsupportMethod(MethodCode methodCode) {
        throw new RuntimeException("nonsupport method: " + methodCode.getClassName() + "." + methodCode.getName() + methodCode.getDesc());
    }

    public InsnNode insnNode() {
        return (InsnNode) this.currentInsnNode;
    }

    public IntInsnNode intInsnNode() {
        return (IntInsnNode) this.currentInsnNode;
    }

    public VarInsnNode varInsnNode() {
        return (VarInsnNode) this.currentInsnNode;
    }

    public TypeInsnNode typeInsnNode() {
        return (TypeInsnNode) this.currentInsnNode;
    }

    public FieldInsnNode fieldInsnNode() {
        return (FieldInsnNode) this.currentInsnNode;
    }

    public MethodInsnNode methodInsnNode() {
        return (MethodInsnNode) this.currentInsnNode;
    }

    public InvokeDynamicInsnNode invokeDynamicInsnNode() {
        return (InvokeDynamicInsnNode) this.currentInsnNode;
    }

    public JumpInsnNode jumpInsnNode() {
        return (JumpInsnNode) this.currentInsnNode;
    }

    public LabelNode labelNode() {
        return (LabelNode) this.currentInsnNode;
    }

    public LdcInsnNode ldcInsnNode() {
        return (LdcInsnNode) this.currentInsnNode;
    }

    public IincInsnNode iincInsnNode() {
        return (IincInsnNode) this.currentInsnNode;
    }

    public TableSwitchInsnNode tableSwitchInsnNode() {
        return (TableSwitchInsnNode) this.currentInsnNode;
    }

    public LookupSwitchInsnNode lookupSwitchInsnNode() {
        return (LookupSwitchInsnNode) this.currentInsnNode;
    }

    public MultiANewArrayInsnNode multiANewArrayInsnNode() {
        return (MultiANewArrayInsnNode) this.currentInsnNode;
    }

    public FrameNode frameNode() {
        return (FrameNode) this.currentInsnNode;
    }

    public LineNumberNode lineNumberNode() {
        return (LineNumberNode) this.currentInsnNode;
    }

}
