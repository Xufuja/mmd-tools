package dev.xfj.core.renderer;

import org.joml.Vector4f;

public abstract class RendererAPIBase implements RendererAPI {
    private static final API api;

    static {
        api = API.OpenGL;
    }

    public enum API {
        None,
        OpenGL
    }

    public abstract void setClearColor(Vector4f color);

    public abstract void clear();

    public abstract void drawIndexed(VertexArray vertexArray);


    public static API getAPI() {
        return api;
    }
}
