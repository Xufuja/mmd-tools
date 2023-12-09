package dev.xfj;

import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImInt;
import imgui.type.ImString;

public class AppLayer implements Layer {
    private int encodingIndex = 0;
    private int uvIndex = 0;
    private int displayIndex = 0;
    private int boneIndex = 0;

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(float ts) {

    }

    @Override
    public void onUIRender() {

        int tab_bar_flags = ImGuiTabBarFlags.None;
        int tableFlags = ImGuiTableFlags.None;

        if (ImGui.beginTabBar("##tabbar", tab_bar_flags)) {
            if (ImGui.beginTabItem("Info")) {
                ImGui.text("System");
                ImGui.separator();

                if (ImGui.beginTable("##table1", 6, tableFlags)) {
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    ImGui.text("PMX Version");
                    ImGui.tableSetColumnIndex(1);
                    ImGui.text("v");
                    ImGui.tableSetColumnIndex(2);
                    ImGui.text("Encoding");
                    ImGui.tableSetColumnIndex(3);

                    String[] encodingItems = {"UTF16", "UTF8"};

                    if (ImGui.beginCombo("##combo 1", encodingItems[encodingIndex], ImGuiComboFlags.None)) {
                        for (int n = 0; n < encodingItems.length; n++) {
                            boolean isSelected = (encodingIndex == n);

                            if (ImGui.selectable(encodingItems[n], isSelected)) {
                                encodingIndex = n;
                            }

                            if (isSelected) {
                                ImGui.setItemDefaultFocus();
                            }
                        }
                        ImGui.endCombo();
                    }

                    ImGui.tableSetColumnIndex(4);
                    ImGui.text("UV Number");
                    ImGui.tableSetColumnIndex(5);

                    String[] uvItems = {"1", "2", "3", "4"};

                    if (ImGui.beginCombo("##combo 2", uvItems[uvIndex], ImGuiComboFlags.None)) {
                        for (int n = 0; n < uvItems.length; n++) {
                            boolean isSelected = (uvIndex == n);

                            if (ImGui.selectable(uvItems[n], isSelected)) {
                                uvIndex = n;
                            }

                            if (isSelected) {
                                ImGui.setItemDefaultFocus();
                            }
                        }
                        ImGui.endCombo();
                    }
                    ImGui.endTable();
                }

                ImGui.text("Model");
                ImGui.separator();

                if (ImGui.beginTable("##table2", 2, tableFlags)) {
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    ImGui.text("Name");
                    ImGui.tableSetColumnIndex(1);
                    ImGui.inputText("##input1", new ImString(""));
                    ImGui.sameLine();
                    ImGui.button("JP");
                    ImGui.sameLine();
                    ImGui.button("EN");
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    ImGui.text("Comment");
                    ImGui.tableSetColumnIndex(1);
                    ImGui.inputTextMultiline("##input2", new ImString(""));

                    ImGui.endTable();
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Vertex")) {
                ImGui.text("Header");
                ImGui.separator();
                ImGui.inputTextMultiline("##source", new ImString(), ImGuiInputTextFlags.AllowTabInput);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Faces")) {

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Material")) {

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
            if (ImGui.beginTabItem("Bone")) {

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
            if (ImGui.beginTabItem("Morph")) {

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
            if (ImGui.beginTabItem("Display")) {
                if (ImGui.beginTable("##table1", 6, tableFlags)) {
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);

                    ImGui.inputText("##common", new ImString());
                    ImGui.sameLine();
                    ImGui.inputText("##displayframe", new ImString());

                    String[] displayItems = {"Root", "Center"};

                    if (ImGui.beginListBox("##listbox 1")) {
                        for (int n = 0; n < displayItems.length; n++) {
                            boolean isSelected = (displayIndex == n);

                            if (ImGui.selectable(displayItems[n], isSelected)) {
                                displayIndex = n;

                                if (isSelected) {
                                    ImGui.setItemDefaultFocus();
                                }
                            }
                        }
                        ImGui.endListBox();
                    }

                    ImGui.button("T");
                    ImGui.sameLine();
                    ImGui.button("^");
                    ImGui.sameLine();
                    ImGui.button("v");
                    ImGui.sameLine();
                    ImGui.button("B");
                    ImGui.sameLine();
                    ImGui.button("+");
                    ImGui.sameLine();
                    ImGui.button("x");

                    ImGui.tableSetColumnIndex(1);

                    ImGui.text("In-frame element");
                    ImGui.sameLine();
                    ImGui.button("JP");
                    ImGui.sameLine();
                    ImGui.button("EN");

                    ImGui.text("Name");
                    ImGui.sameLine();
                    ImGui.inputText("##displayselected", new ImString(displayItems[displayIndex]));

                    ImGui.inputText("##subindex", new ImString());
                    ImGui.sameLine();
                    ImGui.text("count here");

                    String[] boneItems = {"B0", "B1"};

                    if (ImGui.beginListBox("##listbox 2")) {
                        for (int n = 0; n < boneItems.length; n++) {
                            boolean isSelected = (boneIndex == n);

                            if (ImGui.selectable(boneItems[n], isSelected)) {
                                boneIndex = n;

                                if (isSelected) {
                                    ImGui.setItemDefaultFocus();
                                }
                            }
                        }
                        ImGui.endListBox();
                    }

                    ImGui.button("T");
                    ImGui.sameLine();
                    ImGui.button("^");
                    ImGui.sameLine();
                    ImGui.button("v");
                    ImGui.sameLine();
                    ImGui.button("B");
                    ImGui.sameLine();
                    ImGui.button("+");
                    ImGui.sameLine();
                    ImGui.button("x");

                    ImGui.endTable();
                }

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Rigid Body")) {

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
            if (ImGui.beginTabItem("Joint")) {

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
            if (ImGui.beginTabItem("Soft Body")) {

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

    }
}
