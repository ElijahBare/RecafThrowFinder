package github.ebs;

import me.coley.recaf.control.Controller;
import me.coley.recaf.plugin.api.BasePlugin;
import me.coley.recaf.plugin.api.ContextMenuInjectorPlugin;
import me.coley.recaf.plugin.api.StartupPlugin;
import me.coley.recaf.ui.ContextBuilder;
import me.coley.recaf.ui.controls.ActionMenuItem;
import me.coley.recaf.workspace.JavaResource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import javafx.scene.control.ContextMenu;
import org.plugface.core.annotations.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Plugin that scans the project for exceptions being thrown outside a try-catch statement.
 */
@Plugin(name = "UnsafeThrowScanner")
public class ExceptionScanPlugin implements BasePlugin, ContextMenuInjectorPlugin, StartupPlugin {

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Scans the project for exceptions being thrown outside of a try-catch statement.";
    }

    @Override
    public void onStart(Controller controller) {
    }

    @Override
    public void forClass(ContextBuilder builder, ContextMenu menu, String name) {
        menu.getItems().add(new ActionMenuItem("Scan for throws",
                () -> decompile(builder.getResource())));
    }

    private void decompile(JavaResource resource) {
        Map<MethodNode, Integer> methodToLineMap = new HashMap<>();

        resource.getClasses().keySet().forEach(className -> {
            byte[] classBytes = resource.getClasses().get(className);
            if (classBytes != null) {
                ClassNode classNode = getClassNode(classBytes);

                for (MethodNode method : classNode.methods) {
                    InsnList instructions = method.instructions;
                    for (AbstractInsnNode instruction : instructions) {
                        if (instruction instanceof LabelNode) {
                            LabelNode labelNode = (LabelNode) instruction;
                            int lineNumber = getLineNumber(labelNode, method);
                            if (lineNumber != -1) {
                                methodToLineMap.put(method, lineNumber);
                                break;
                            }
                        }
                    }
                }

                if (!isExceptionThrown(classNode)) {
                    return;
                }
                if (isExceptionCaught(classNode)) {
                    return;
                }

                for (MethodNode method : classNode.methods) {
                    if (isExceptionThrown(method)) {
                        System.out.println("Class " + className + " throws an exception at line " + methodToLineMap.get(method));
                    }
                }
            }
        });
    }

    private int getLineNumber(LabelNode labelNode, MethodNode method) {
        InsnList instructions = method.instructions;
        for (AbstractInsnNode abstractInsn : instructions) {
            if (abstractInsn instanceof LineNumberNode) {
                LineNumberNode lineNumberNode = (LineNumberNode) abstractInsn;
                if (lineNumberNode.start.getLabel() == labelNode.getLabel()) {
                    return lineNumberNode.line;
                }
            }
        }
        return -1;
    }



    private boolean isExceptionCaught(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            InsnList instructions = method.instructions;
            for (AbstractInsnNode instruction : instructions) {
                if (instruction.getOpcode() == Opcodes.ATHROW) {
                    AbstractInsnNode prev = instruction.getPrevious();
                    while (prev != null) {
                        if (prev.getOpcode() == Opcodes.ATHROW) {
                            return true;
                        }
                        prev = prev.getPrevious();
                    }
                    return false;
                }
            }
        }
        return false;
    }
    private boolean isExceptionThrown(ClassNode classNode) {
        for (MethodNode method : classNode.methods) {
            if (isExceptionThrown(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExceptionThrown(MethodNode method) {
        InsnList instructions = method.instructions;
        for (AbstractInsnNode instruction : instructions) {
            if (instruction.getOpcode() == Opcodes.ATHROW) {
                return true;
            }
        }
        return false;
    }

    private ClassNode getClassNode(byte[] classBytes) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        return classNode;
    }
}
