package dev.xfj;

import dev.xfj.application.Application;
import dev.xfj.application.ApplicationSpecification;
import imgui.ImGui;

public class Main {
    public static void main(String[] args) {
        ApplicationSpecification spec = new ApplicationSpecification();
        spec.name = "MMD Tools";
        //Width and height from PMX Editor
        spec.width = 644;
        spec.height = 363;
        Application app = new Application(spec);
        app.pushLayer(new AppLayer());
        app.run();
    }

}