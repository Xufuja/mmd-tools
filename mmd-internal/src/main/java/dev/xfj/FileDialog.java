package dev.xfj;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.win32.StdCallLibrary;
import dev.xfj.application.Application;

import java.util.Optional;

import static org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window;

public class FileDialog {
    public final static int OFN_READONLY = 0x00000001;
    public final static int OFN_OVERWRITEPROMPT = 0x00000002;
    public final static int OFN_HIDEREADONLY = 0x00000004;
    public final static int OFN_NOCHANGEDIR = 0x00000008;
    public final static int OFN_SHOWHELP = 0x00000010;
    public final static int OFN_ENABLEHOOK = 0x00000020;
    public final static int OFN_ENABLETEMPLATE = 0x00000040;
    public final static int OFN_ENABLETEMPLATEHANDLE = 0x00000080;
    public final static int OFN_NOVALIDATE = 0x00000100;
    public final static int OFN_ALLOWMULTISELECT = 0x00000200;
    public final static int OFN_EXTENSIONDIFFERENT = 0x00000400;
    public final static int OFN_PATHMUSTEXIST = 0x00000800;
    public final static int OFN_FILEMUSTEXIST = 0x00001000;
    public final static int OFN_CREATEPROMPT = 0x00002000;
    public final static int OFN_SHAREAWARE = 0x00004000;
    public final static int OFN_NOREADONLYRETURN = 0x00008000;
    public final static int OFN_NOTESTFILECREATE = 0x00010000;
    public final static int OFN_NONETWORKBUTTON = 0x00020000;
    public final static int OFN_NOLONGNAMES = 0x00040000;
    public final static int OFN_EXPLORER = 0x00080000;
    public final static int OFN_NODEREFERENCELINKS = 0x00100000;
    public final static int OFN_LONGNAMES = 0x00200000;
    public final static int OFN_ENABLEINCLUDENOTIFY = 0x00400000;
    public final static int OFN_ENABLESIZING = 0x00800000;
    public final static int OFN_DONTADDTORECENT = 0x02000000;
    public final static int OFN_FORCESHOWHIDDEN = 0x10000000;
    public final static int OFN_EX_NOPLACESBAR = 0x00000001;
    public final static int OFN_SHAREFALLTHROUGH = 2;
    public final static int OFN_SHARENOWARN = 1;
    public final static int OFN_SHAREWARN = 0;

    public interface Comdlg32 extends StdCallLibrary {
        Comdlg32 INSTANCE = Native.load("comdlg32", Comdlg32.class);

        boolean GetOpenFileNameA(OPENFILENAMEA lpofn);

        boolean GetSaveFileNameA(OPENFILENAMEA lpofn);
    }

    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        boolean GetCurrentDirectoryA(int nBufferLength, byte[] lpBuffer);
    }

    @FieldOrder({"lStructSize", "hwndOwner", "hInstance", "lpstrFilter",
            "lpstrCustomFilter", "nMaxCustFilter", "nFilterIndex",
            "lpstrFile", "nMaxFile", "lpstrFileTitle", "nMaxFileTitle",
            "lpstrInitialDir", "lpstrTitle", "Flags", "nFileOffset",
            "nFileExtension", "lpstrDefExt", "lCustData", "lpfnHook",
            "lpTemplateName"})
    public static class OPENFILENAMEA extends Structure {
        public int lStructSize;
        public Pointer hwndOwner;
        public Pointer hInstance;
        public String lpstrFilter;
        public Pointer lpstrCustomFilter;
        public int nMaxCustFilter;
        public int nFilterIndex;
        public String lpstrFile;
        public int nMaxFile;
        public String lpstrFileTitle;
        public int nMaxFileTitle;
        public String lpstrInitialDir;
        public String lpstrTitle;
        public int Flags;
        public short nFileOffset;
        public short nFileExtension;
        public String lpstrDefExt;
        public Pointer lCustData;
        public StdCallLibrary.StdCallCallback lpfnHook;
        public String lpTemplateName;
    }

    public Optional<String> openFileImpl(String filter) {
        OPENFILENAMEA ofn = new OPENFILENAMEA();
        byte[] szFile = new byte[260];
        byte[] currentDirectory = new byte[256];
        ofn.lStructSize = Native.getNativeSize(OPENFILENAMEA.class, null);
        ofn.hwndOwner = Pointer.createConstant(glfwGetWin32Window(Application.getInstance().getWindowHandle()));
        ofn.lpstrFile = new String(szFile);
        ofn.nMaxFile = szFile.length;

        if (Kernel32.INSTANCE.GetCurrentDirectoryA(256, currentDirectory)) {
            ofn.lpstrInitialDir = new String(currentDirectory);
        }

        ofn.lpstrFilter = filter;
        ofn.nFilterIndex = 1;
        ofn.Flags = OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST | OFN_NOCHANGEDIR;

        if (Comdlg32.INSTANCE.GetOpenFileNameA(ofn)) {
            return Optional.of(ofn.lpstrFile);
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> saveFileImpl(String filter) {
        OPENFILENAMEA ofn = new OPENFILENAMEA();
        byte[] szFile = new byte[260];
        byte[] currentDirectory = new byte[256];
        ofn.lStructSize = Native.getNativeSize(OPENFILENAMEA.class, null);
        ofn.hwndOwner = Pointer.createConstant(glfwGetWin32Window(Application.getInstance().getWindowHandle()));
        ofn.lpstrFile = new String(szFile);
        ofn.nMaxFile = szFile.length;

        if (Kernel32.INSTANCE.GetCurrentDirectoryA(256, currentDirectory)) {
            ofn.lpstrInitialDir = new String(currentDirectory);
        }

        ofn.lpstrFilter = filter;
        ofn.nFilterIndex = 1;
        ofn.Flags = OFN_PATHMUSTEXIST | OFN_OVERWRITEPROMPT | OFN_NOCHANGEDIR;
        ofn.lpstrDefExt = filter.substring(filter.lastIndexOf(".") + 1);

        if (Comdlg32.INSTANCE.GetSaveFileNameA(ofn)) {
            return Optional.of(ofn.lpstrFile);
        } else {
            return Optional.empty();
        }
    }
}
