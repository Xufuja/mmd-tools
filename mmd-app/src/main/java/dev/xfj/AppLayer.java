package dev.xfj;

import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.type.ImString;

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
                if (ImGui.beginListBox("##listbox 1")) {
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

                    ImGui.sameLine();

                    ImGui.button("Yes");
                    ImGui.sameLine();
                    ImGui.button("No");
                    ImGui.sameLine();
                    ImGui.button("Maybe");
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Broccoli")) {
                ImGui.text("Header");
                ImGui.separator();
                ImGui.inputTextMultiline("##source", new ImString(), ImGuiInputTextFlags.AllowTabInput);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Cucumber")) {
                String combo_preview_value = items[item_current_idx];  // Pass in the preview value visible before opening the combo (it could be anything)
                if (ImGui.beginCombo("##combo 1", combo_preview_value, 0)) {
                    for (int n = 0; n < items.length; n++) {
                        boolean is_selected = (item_current_idx == n);
                        if (ImGui.selectable(items[n], is_selected)) {
                            item_current_idx = n;
                        }
                        // Set the initial focus when opening the combo (scrolling + keyboard navigation focus)
                        if (is_selected)
                            ImGui.setItemDefaultFocus();
                    }
                    ImGui.endCombo();
                }
                ImGui.separator();
                ImGui.beginGroup();
                ImGui.button("AAA");
                ImGui.sameLine();
                ImGui.button("BBB");
                ImGui.sameLine();
                ImGui.beginGroup();
                ImGui.button("CCC");
                ImGui.button("DDD");
                ImGui.endGroup();
                ImGui.sameLine();
                ImGui.button("EEE");
                ImGui.endGroup();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.text("Selected: " + items[item_current_idx]);

    }
}
