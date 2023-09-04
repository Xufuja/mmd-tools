package dev.xfj;

import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;

public class AppLayer implements Layer {
    private static int item_current_idx = 0;

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
        String[] items = new String[]{"AAAA", "BBBB", "CCCC", "DDDD", "EEEE", "FFFF", "GGGG", "HHHH", "IIII", "JJJJ", "KKKK", "LLLLLLL", "MMMM", "OOOOOOO"};


        int tab_bar_flags = ImGuiTabBarFlags.None;
        if (ImGui.beginTabBar("MyTabBar", tab_bar_flags)) {
            if (ImGui.beginTabItem("Avocado")) {
                if (ImGui.beginListBox("listbox 1")) {
                    for (int n = 0; n < items.length; n++) {
                        boolean is_selected = (item_current_idx == n);
                        if (ImGui.selectable(items[n], is_selected)) {
                            item_current_idx = n;
                        }

                        // Set the initial focus when opening the combo (scrolling + keyboard navigation focus)
                        if (is_selected)
                            ImGui.setItemDefaultFocus();
                    }
                    ImGui.endListBox();
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Broccoli")) {
                ImGui.text("This is the Broccoli tab!\nblah blah blah blah blah");
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Cucumber")) {
                ImGui.text("This is the Cucumber tab!\nblah blah blah blah blah");
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.text("Selected: " + items[item_current_idx]);

    }
}
