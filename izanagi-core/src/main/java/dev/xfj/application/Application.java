package dev.xfj.application;

import dev.xfj.Layer;
import dev.xfj.LayerStack;
import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Application {
    public static ImFont[] fonts;
    private static int dockspace_flags;
    private static Application instance;
    private ApplicationSpecification specification;
    private long windowHandle;
    private boolean running;
    private float timeStep;
    private float frameTime;
    private float lastFrameTime;
    private LayerStack layerStack;
    private Runnable menuBarCallback;
    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;

    public Application(ApplicationSpecification specification) {
        this.specification = specification;
        this.timeStep = 0.0f;
        this.frameTime = 0.0f;
        this.lastFrameTime = 0.0f;
        this.layerStack = new LayerStack();
        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();
        dockspace_flags = ImGuiDockNodeFlags.None;
        instance = this;
        init();
    }

    private void init() {
        boolean success = glfwInit();

        if (!success) {
            throw new RuntimeException("Could not initialize GLFW!");
        } else {
            glfwSetErrorCallback(new GLFWErrorCallback() {
                @Override
                public void invoke(int error, long description) {
                    System.err.println(String.format("GLFW error (%1$d): %2$d", error, description));
                }
            });
        }
        windowHandle = glfwCreateWindow(specification.width, specification.height, specification.name, NULL, NULL);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);

        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        ImGui.createContext();
        final ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        //io.addConfigFlags(ImGuiConfigFlags.NavEnableGamepad);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        //io.setConfigViewportsNoTaskBarIcon(true);
        //io.setConfigViewportsNoAutoMerge(true);

        ImGui.styleColorsDark();

        ImGuiStyle imGuiStyle = ImGui.getStyle();
        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            imGuiStyle.setWindowRounding(0.0f);
            float[][] colors = imGuiStyle.getColors();
            colors[ImGuiCol.WindowBg][3] = 1.0f;
            imGuiStyle.setColors(colors);
        }

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setFontDataOwnedByAtlas(false);

        fonts = new ImFont[]{
                io.getFonts().addFontFromFileTTF("assets/fonts/roboto/Roboto-Regular.ttf", 20)
        };
        io.setFontDefault(fonts[0]);

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 450");
    }

    public void run() {
        running = true;

        while (running) {
            glfwPollEvents();

            for (Layer layer : layerStack.getLayers()) {
                layer.onUpdate(timeStep);
            }

            glfwSwapBuffers(windowHandle);

            imGuiGl3.updateFontsTexture();
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            int window_flags = ImGuiWindowFlags.NoDocking;

            if (menuBarCallback != null) {
                window_flags |= ImGuiWindowFlags.MenuBar;
            }

            ImGuiViewport viewport = ImGui.getMainViewport();
            ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
            ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
            ImGui.setNextWindowViewport(viewport.getID());
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            window_flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
            window_flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;


            if ((dockspace_flags & ImGuiDockNodeFlags.PassthruCentralNode) != 0) {
                window_flags |= ImGuiWindowFlags.NoBackground;
            }

            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
            ImGui.begin("DockSpace Demo", new ImBoolean(true), window_flags);
            ImGui.popStyleVar();

            ImGui.popStyleVar(2);

            if (menuBarCallback != null) {
                if (ImGui.beginMenuBar()) {
                    menuBarCallback.run();
                    ImGui.endMenuBar();
                }
            }

            for (Layer layer : layerStack.getLayers()) {
                layer.onUIRender();
            }

            ImGui.end();

            final ImGuiIO io = ImGui.getIO();
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());
            if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                long backupCurrentContext = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupCurrentContext);
            }

            float time = getTime();
            frameTime = time - lastFrameTime;
            timeStep = Math.min(frameTime, 0.0333f);
            lastFrameTime = time;

        }
    }

    public void pushLayer(Layer layer) {
        layerStack.pushLayer(layer);
    }

    public void pushOverlay(Layer layer) {
        layerStack.pushOverlay(layer);
    }

    public float getTime() {
        return (float) glfwGetTime();
    }

    public void setMenuBarCallback(Runnable menuBarCallback) {
        this.menuBarCallback = menuBarCallback;
    }

    public static Application getInstance() {
        return instance;
    }

    public static void close(Application instance) {
        instance.close();
    }

    private void close() {
        this.running = false;
    }

    public long getWindowHandle() {
        return windowHandle;
    }
}
