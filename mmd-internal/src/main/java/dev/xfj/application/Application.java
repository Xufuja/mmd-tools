package dev.xfj.application;

import dev.xfj.Layer;
import dev.xfj.LayerStack;
import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.application.WindowCloseEvent;
import dev.xfj.events.application.WindowResizeEvent;
import dev.xfj.events.key.KeyPressedEvent;
import dev.xfj.events.key.KeyReleasedEvent;
import imgui.*;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45;

import java.util.ListIterator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Application {
    public static ImFont[] fonts;
    private static int dockspace_flags;
    private static Application instance;
    private ApplicationSpecification specification;
    private long windowHandle;
    private boolean running;
    private boolean minimized;
    private float timeStep;
    private float frameTime;
    private float lastFrameTime;
    private LayerStack layerStack;
    private EventCallBack.EventCallbackFn eventCallback;
    private final ImGuiImplGlfw imGuiGlfw;
    private final ImGuiImplGl3 imGuiGl3;

    public Application(ApplicationSpecification specification) {
        this.specification = specification;
        this.minimized = false;
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

        PointerBuffer monitors = glfwGetMonitors();
        GLFWVidMode videoMode = glfwGetVideoMode(monitors.get(0));

        int[] monitorX = new int[1];
        int[] monitorY = new int[1];
        glfwGetMonitorPos(monitors.get(0), monitorX, monitorY);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        windowHandle = glfwCreateWindow(specification.width, specification.height, specification.name, NULL, NULL);

        glfwSetWindowSizeLimits(windowHandle, specification.width, specification.height, specification.width, specification.height);

        glfwDefaultWindowHints();

        glfwSetWindowPos(windowHandle, monitorX[0] + (videoMode.width() - specification.width) / 2, monitorY[0] + (videoMode.height() - specification.height) / 2);
        glfwShowWindow(windowHandle);

        eventCallback = this::onEvent;

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 5);

        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        glfwSetWindowSizeCallback(windowHandle, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                specification.width = width;
                specification.height = height;

                WindowResizeEvent event = new WindowResizeEvent(width, height);
                eventCallback.handle(event);
            }
        });

        glfwSetWindowCloseCallback(windowHandle, new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                WindowCloseEvent event = new WindowCloseEvent();
                eventCallback.handle(event);
            }
        });

        glfwSetKeyCallback(windowHandle, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                switch (action) {
                    case GLFW_PRESS -> {
                        KeyPressedEvent event = new KeyPressedEvent(key);
                        eventCallback.handle(event);
                    }
                    case GLFW_RELEASE -> {
                        KeyReleasedEvent event = new KeyReleasedEvent(key);
                        eventCallback.handle(event);
                    }
                    case GLFW_REPEAT -> {
                        KeyPressedEvent event = new KeyPressedEvent(key, true);
                        eventCallback.handle(event);
                    }
                }
            }
        });

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

        setDarkThemeColors();

        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setFontDataOwnedByAtlas(false);

        float fontSize = 18.0f;
        fonts = new ImFont[]{
                io.getFonts().addFontFromFileTTF("assets/fonts/roboto/Roboto-Regular.ttf", fontSize),
                io.getFonts().addFontFromFileTTF("assets/fonts/notosansjp/static/NotoSansJP-Medium.ttf", fontSize, io.getFonts().getGlyphRangesJapanese())
        };

        io.setFontDefault(fonts[1]);

        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 450");
    }

    public void setDarkThemeColors() {
        ImGuiStyle style = ImGui.getStyle();

        float[][] colors = style.getColors();
        colors[ImGuiCol.WindowBg] = new float[]{0.1f, 0.105f, 0.11f, 1.0f};

        colors[ImGuiCol.Header] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.HeaderHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.HeaderActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.Button] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.ButtonHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.ButtonActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.FrameBg] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};
        colors[ImGuiCol.FrameBgHovered] = new float[]{0.3f, 0.305f, 0.31f, 1.0f};
        colors[ImGuiCol.FrameBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        colors[ImGuiCol.Tab] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabHovered] = new float[]{0.38f, 0.3805f, 0.381f, 1.0f};
        colors[ImGuiCol.TabActive] = new float[]{0.28f, 0.2805f, 0.281f, 1.0f};
        colors[ImGuiCol.TabUnfocused] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TabUnfocusedActive] = new float[]{0.2f, 0.205f, 0.21f, 1.0f};

        colors[ImGuiCol.TitleBg] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgActive] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};
        colors[ImGuiCol.TitleBgCollapsed] = new float[]{0.15f, 0.1505f, 0.151f, 1.0f};

        style.setColors(colors);
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

            int windowFlags = ImGuiWindowFlags.NoDocking;

            windowFlags |= ImGuiWindowFlags.MenuBar;

            ImGuiViewport viewport = ImGui.getMainViewport();
            ImGui.setNextWindowPos(viewport.getWorkPosX(), viewport.getWorkPosY());
            ImGui.setNextWindowSize(viewport.getWorkSizeX(), viewport.getWorkSizeY());
            ImGui.setNextWindowViewport(viewport.getID());
            ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
            ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
            windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove;
            windowFlags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;


            if ((dockspace_flags & ImGuiDockNodeFlags.PassthruCentralNode) != 0) {
                windowFlags |= ImGuiWindowFlags.NoBackground;
            }

            ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
            ImGui.begin("DockSpace Demo", new ImBoolean(true), windowFlags);
            ImGui.popStyleVar();

            ImGui.popStyleVar(2);

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

    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(WindowCloseEvent.class, this::onWindowClose);
        eventDispatcher.dispatch(WindowResizeEvent.class, this::onWindowResize);

        ListIterator<Layer> it = layerStack.getLayers().listIterator(layerStack.getLayers().size());
        while (it.hasPrevious()) {
            Layer layer = it.previous();

            if (event.isHandled()) {
                break;
            }

            layer.onEvent(event);
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

    public static Application getInstance() {
        return instance;
    }

    public static void close(Application instance) {
        instance.close();
    }

    private void close() {
        this.running = false;
    }

    private boolean onWindowClose(WindowCloseEvent windowCloseEvent) {
        close();
        return true;
    }

    private boolean onWindowResize(WindowResizeEvent windowResizeEvent) {
        if (windowResizeEvent.getWidth() == 0 || windowResizeEvent.getHeight() == 0) {
            minimized = true;
            return false;
        }

        minimized = false;
        GL45.glViewport(0, 0, windowResizeEvent.getWidth(), windowResizeEvent.getHeight());
        return false;
    }

    public long getWindowHandle() {
        return windowHandle;
    }
}
