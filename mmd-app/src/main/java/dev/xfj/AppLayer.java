package dev.xfj;

import imgui.ImGui;

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
        ImGui.sameLine();
        if (ImGui.beginListBox("listbox 2")) {
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

    }
}
