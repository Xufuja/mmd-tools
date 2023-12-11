package dev.xfj;

import dev.xfj.application.Application;
import dev.xfj.application.ApplicationSpecification;
import imgui.ImGui;

public class Main {
    public static void main(String[] args) {
        ApplicationSpecification spec = new ApplicationSpecification();
        spec.name = "MMD Tools";
        Application app = new Application(spec);
        app.pushLayer(new AppLayer());
        app.run();
    }

}