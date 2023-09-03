package dev.xfj;

import imgui.ImGui;

public class ExampleLayer implements Layer {

    @Override
    public void onAttach() {
        System.out.println("Not implemented!");
    }

    @Override
    public void onDetach() {
        System.out.println("Not implemented!");
    }

    @Override
    public void onUpdate(float ts) {
        System.out.println("Not implemented!");
    }

    @Override
    public void onUIRender() {
        ImGui.begin("Hello");
        ImGui.button("Button");
        ImGui.end();;

        ImGui.showDemoWindow();
    }
}
