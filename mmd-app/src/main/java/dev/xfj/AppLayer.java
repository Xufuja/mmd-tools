package dev.xfj;

import dev.xfj.application.Application;
import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.key.KeyPressedEvent;
import dev.xfj.format.pmx.PMXFile;
import dev.xfj.format.pmx.PMXFileDisplayFrame;
import dev.xfj.format.pmx.PMXFileDisplayFrameData;
import dev.xfj.input.Input;
import dev.xfj.input.KeyCodes;
import dev.xfj.parsing.PMXParser;
import dev.xfj.writer.PMXWriter;
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AppLayer implements Layer {
    private int uvIndex = 0;
    private int displayIndex = 0;
    private int frameItemIndex = 0;
    private PMXFile pmxFile;
    private boolean english;
    private boolean scrollDisplayItems;
    private boolean scrollFrameItems;

    @Override
    public void onAttach() {
        pmxFile = new PMXFile();
        english = false;
        scrollDisplayItems = false;
        scrollFrameItems = false;
    }

    @Override
    public void onDetach() {

    }

    @Override
    public void onUpdate(float ts) {

    }

    @Override
    public void onUIRender() {
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Load PMX...", "Ctrl+O")) {
                    loadModel();
                }

                if (ImGui.menuItem("Save PMX...", "Ctrl+S")) {
                    saveModel();
                }

                ImGui.separator();

                if (ImGui.menuItem("Exit")) {
                    Application.close(Application.getInstance());
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }


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
                    ImGui.text(String.valueOf(pmxFile.getVersion()));
                    ImGui.tableSetColumnIndex(2);
                    ImGui.text("Encoding");
                    ImGui.tableSetColumnIndex(3);

                    ImGui.inputText("##locale", pmxFile.getGlobals() != null ? new ImString(pmxFile.getGlobals().getTextEncoding() == 1 ? "UTF8" : "UTF16-LE") : new ImString(""), ImGuiInputTextFlags.ReadOnly);

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
                    ImGui.inputText("##input1", english ? new ImString(pmxFile.getModelNameEnglish()) : new ImString(pmxFile.getModelNameJapanese()), ImGuiInputTextFlags.ReadOnly);
                    ImGui.sameLine();

                    if (ImGui.checkbox("English", english)) {
                        english = !english;
                    }


                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    ImGui.text("Comment");
                    ImGui.tableSetColumnIndex(1);
                    ImGui.inputTextMultiline("##input2", english ? new ImString(pmxFile.getCommentsEnglish()) : new ImString(pmxFile.getCommentsJapanese()), ImGuiInputTextFlags.ReadOnly);

                    ImGui.endTable();
                }
                ImGui.endTabItem();
            }
            boolean displayDeleted = false;

            if (ImGui.beginTabItem("Display")) {
                if (ImGui.beginTable("##table1", 3, tableFlags)) {
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);

                    ImGui.inputText("##common", new ImString(String.valueOf(displayIndex)));
                    //ImGui.sameLine();
                    //ImGui.inputText("##displayframe", new ImString(), ImGuiInputTextFlags.ReadOnly);
                    ImGui.sameLine();
                    ImGui.text(String.valueOf(pmxFile.getDisplayFrameCount()));

                    List<PMXFileDisplayFrame> displayItems = pmxFile.getDisplayFrames();

                    if (ImGui.beginListBox("##listbox 1")) {
                        for (int n = 0; displayItems != null && n < pmxFile.getDisplayFrameCount(); n++) {
                            boolean isSelected = (displayIndex == n);

                            if (ImGui.selectable(displayItems.get(n).getDisplayFrameNameJapanese(), isSelected)) {
                                displayIndex = n;

                                if (isSelected) {
                                    ImGui.setItemDefaultFocus();
                                }
                            }
                        }

                        if (displayItems != null && scrollDisplayItems) {
                            ImGui.setScrollY(normalize(displayIndex, 0, displayItems.size() - 1, 0, (int) ImGui.getScrollMaxY()));
                            scrollDisplayItems = false;
                        }

                        ImGui.endListBox();
                    }

                    if (ImGui.button("T##1")) {
                        if (displayItems != null) {
                            PMXFileDisplayFrame frame = displayItems.get(displayIndex);
                            displayItems.remove(frame);
                            displayItems.add(0, frame);
                            displayIndex = 0;
                            scrollDisplayItems = true;
                        }
                    }

                    ImGui.sameLine();

                    if (ImGui.button("^##1")) {
                        if (displayItems != null && displayItems.size() > 1) {
                            if (displayIndex - 1 > -1) {
                                Collections.swap(displayItems, displayIndex, displayIndex - 1);
                                displayIndex = displayIndex - 1;
                                scrollDisplayItems = true;
                            }
                        }
                    }

                    ImGui.sameLine();

                    if (ImGui.button("v##1")) {
                        if (displayItems != null && displayItems.size() > 1) {
                            if (displayIndex + 1 < displayItems.size()) {
                                Collections.swap(displayItems, displayIndex, displayIndex + 1);
                                displayIndex = displayIndex + 1;
                                scrollDisplayItems = true;
                            }
                        }
                    }

                    ImGui.sameLine();

                    if (ImGui.button("B")) {
                        if (displayItems != null) {
                            PMXFileDisplayFrame frame = displayItems.get(displayIndex);
                            displayItems.remove(frame);
                            displayItems.add(frame);
                            displayIndex = displayItems.size() - 1;
                            scrollDisplayItems = true;
                        }
                    }

                    ImGui.sameLine();
                    ImGui.button("+");
                    ImGui.sameLine();

                    ImGui.beginDisabled(displayItems != null && displayItems.get(displayIndex).getSpecialFlag() == 1);

                    if (ImGui.button("x")) {
                        displayDeleted = true;
                    }

                    ImGui.endDisabled();

                    ImGui.sameLine();

                    ImGui.tableSetColumnIndex(1);

                    ImGui.text("In-frame element");
                    ImGui.sameLine();

                    if (ImGui.checkbox("English", english)) {
                        english = !english;
                    }

                    ImGui.text("Name");
                    ImGui.sameLine();
                    ImGui.inputText("##displayselected", displayItems != null ? english && !displayItems.get(displayIndex).getDisplayFrameNameEnglish().isEmpty() ? new ImString(displayItems.get(displayIndex).getDisplayFrameNameEnglish()) : new ImString(displayItems.get(displayIndex).getDisplayFrameNameJapanese()) : new ImString(""), ImGuiInputTextFlags.ReadOnly);
                    ImGui.sameLine();
                    ImGui.text(displayItems != null && displayItems.get(displayIndex).getSpecialFlag() == 1 ? "Special frame" : "Normal frame");
                    List<PMXFileDisplayFrameData> frameItems = displayItems != null ? displayItems.get(displayIndex).getFrames() : null;

                    ImGui.inputText("##subindex", new ImString(String.valueOf(frameItemIndex)), ImGuiInputTextFlags.ReadOnly);

                    ImGui.sameLine();
                    ImGui.text(displayItems != null ? String.valueOf(displayItems.get(displayIndex).getFrameCount()) : "");

                    if (ImGui.beginListBox("##listbox 2")) {
                        for (int n = 0; displayItems != null && n < displayItems.get(displayIndex).getFrameCount(); n++) {
                            boolean isSelected = (frameItemIndex == n);

                            String name = switch (frameItems.get(n).getFrameType()) {
                                case 0 ->
                                        String.valueOf(n).concat(" : B".concat(frameItems.get(n).getFrameData().getValue().toString().concat(" | ").concat(english && !pmxFile.getBones().get((short) frameItems.get(n).getFrameData().getValue()).getBonenameEnglish().isEmpty() ? pmxFile.getBones().get((short) frameItems.get(n).getFrameData().getValue()).getBonenameEnglish() : pmxFile.getBones().get((short) frameItems.get(n).getFrameData().getValue()).getBoneNameJapanese())));

                                case 1 ->
                                        String.valueOf(n).concat(" : M".concat(frameItems.get(n).getFrameData().getValue().toString().concat(" | ").concat(english && !pmxFile.getMorphs().get((byte) frameItems.get(n).getFrameData().getValue()).getMorphNameEnglish().isEmpty() ? pmxFile.getMorphs().get((byte) frameItems.get(n).getFrameData().getValue()).getMorphNameEnglish() : pmxFile.getMorphs().get((byte) frameItems.get(n).getFrameData().getValue()).getMorphNameJapanese())));
                                default -> throw new RuntimeException("Invalid Display Frame Type!");
                            };

                            if (ImGui.selectable(name, isSelected)) {
                                frameItemIndex = n;

                                if (isSelected) {
                                    ImGui.setItemDefaultFocus();
                                }
                            }
                        }

                        if (frameItems != null && scrollFrameItems) {
                            ImGui.setScrollY(normalize(frameItemIndex, 0, frameItems.size() - 1, 0, (int) ImGui.getScrollMaxY()));
                            scrollFrameItems = false;
                        }

                        ImGui.endListBox();
                    }

                    if (ImGui.button("T##2")) {
                        if (frameItems != null) {
                            PMXFileDisplayFrameData frame = frameItems.get(frameItemIndex);
                            frameItems.remove(frame);
                            frameItems.add(0, frame);
                            frameItemIndex = 0;
                            scrollFrameItems = true;
                        }
                    }

                    ImGui.sameLine();

                    if (ImGui.button("^##2")) {
                        if (frameItems != null && frameItems.size() > 1) {
                            if (frameItemIndex - 1 > -1) {
                                Collections.swap(frameItems, frameItemIndex, frameItemIndex - 1);
                                frameItemIndex = frameItemIndex - 1;
                                scrollFrameItems = true;
                            }
                        }
                    }

                    ImGui.sameLine();

                    if (ImGui.button("v##2")) {
                        if (frameItems != null && frameItems.size() > 1) {
                            if (frameItemIndex + 1 < frameItems.size()) {
                                Collections.swap(frameItems, frameItemIndex, frameItemIndex + 1);
                                frameItemIndex = frameItemIndex + 1;
                                scrollFrameItems = true;
                            }
                        }
                    }

                    ImGui.sameLine();
                    ImGui.button("B");
                    ImGui.sameLine();
                    ImGui.button("+");
                    ImGui.sameLine();
                    ImGui.button("x");

                    ImGui.endTable();

                    if (displayDeleted) {
                        pmxFile.setDisplayFrameCount(pmxFile.getDisplayFrameCount() - 1);
                        pmxFile.getDisplayFrames().remove(displayIndex);
                        displayIndex--;

                        if (displayIndex < 0) {
                            displayIndex = 0;
                        }
                    }
                }

                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
    }

    @Override
    public void onEvent(Event event) {
        EventDispatcher eventDispatcher = new EventDispatcher(event);
        eventDispatcher.dispatch(KeyPressedEvent.class, this::onKeyPressed);
    }

    private void loadModel() {
        FileDialog fileDialog = new FileDialog();
        Optional<String> filePath = fileDialog.openFile("PMX (*.pmx)\0*.pmx\0");
        filePath.ifPresent(path -> loadModel(Path.of(path)));
    }

    private void loadModel(Path filePath) {
        try {
            PMXParser pmxParser = new PMXParser(filePath);
            pmxFile = pmxParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveModel() {
        FileDialog fileDialog = new FileDialog();
        Optional<String> filePath = fileDialog.saveFile("PMX (*.pmx)\0*.pmx\0");
        filePath.ifPresent(path -> saveModel(Path.of(path)));
    }

    private void saveModel(Path filePath) {
        try {
            PMXWriter pmxWriter = new PMXWriter(pmxFile, false);
            pmxWriter.write(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private int normalize(int value, int minValue, int maxValue, int minNormalize, int maxNormalize) {
        return minNormalize + ((value - minValue) * (maxNormalize - minNormalize)) / (maxValue - minValue);
    }

    private boolean onKeyPressed(KeyPressedEvent event) {
        if (event.isRepeat()) {
            return false;
        }

        boolean control = Input.isKeyDown(KeyCodes.LEFT_CONTROL) || Input.isKeyDown(KeyCodes.RIGHT_CONTROL);
        boolean shift = Input.isKeyDown(KeyCodes.LEFT_SHIFT) || Input.isKeyDown(KeyCodes.RIGHT_SHIFT);

        switch (event.getKeyCode()) {
            case KeyCodes.O -> {
                if (control) {
                    loadModel();
                    return true;
                }
            }
            case KeyCodes.S -> {
                if (control) {
                    saveModel();
                    return true;
                }
            }
        }
        return false;
    }
}
