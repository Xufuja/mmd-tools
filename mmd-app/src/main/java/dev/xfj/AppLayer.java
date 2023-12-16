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
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class AppLayer implements Layer {
    private int encodingIndex = 0;
    private int uvIndex = 0;
    private int displayIndex = 0;
    private int boneIndex = 0;
    private PMXFile pmxFile;
    private boolean english;

    @Override
    public void onAttach() {
        pmxFile = new PMXFile();
        english = false;
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
                    ImGui.inputText("##input1", english ? new ImString(pmxFile.getModelNameEnglish()) : new ImString(pmxFile.getModelNameJapanese()));
                    ImGui.sameLine();

                    if (ImGui.checkbox("English", english)) {
                        english = !english;
                    }


                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    ImGui.text("Comment");
                    ImGui.tableSetColumnIndex(1);
                    ImGui.inputTextMultiline("##input2", english ? new ImString(pmxFile.getCommentsEnglish()) : new ImString(pmxFile.getCommentsJapanese()));

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
                    //ImGui.inputText("##displayframe", new ImString());
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

                    if (ImGui.button("x")) {
                        displayDeleted = true;
                    }

                    ImGui.tableSetColumnIndex(1);

                    ImGui.text("In-frame element");
                    ImGui.sameLine();

                    if (ImGui.checkbox("English", english)) {
                        english = !english;
                    }

                    ImGui.text("Name");
                    ImGui.sameLine();
                    ImGui.inputText("##displayselected", displayItems != null ? english && !displayItems.get(displayIndex).getDisplayFrameNameEnglish().isEmpty() ? new ImString(displayItems.get(displayIndex).getDisplayFrameNameEnglish()) : new ImString(displayItems.get(displayIndex).getDisplayFrameNameJapanese()) : new ImString(""));
                    ImGui.sameLine();
                    ImGui.text(displayItems != null && displayItems.get(displayIndex).getSpecialFlag() == 1 ? "Special frame" : "Normal frame");
                    List<PMXFileDisplayFrameData> boneItems = displayItems != null ? displayItems.get(displayIndex).getFrames() : null;

                    ImGui.inputText("##subindex", new ImString(String.valueOf(boneIndex)));

                    ImGui.sameLine();
                    ImGui.text(displayItems != null ? String.valueOf(displayItems.get(displayIndex).getFrameCount()) : "");

                    if (ImGui.beginListBox("##listbox 2")) {
                        for (int n = 0; displayItems != null && n < displayItems.get(displayIndex).getFrameCount(); n++) {
                            boolean isSelected = (boneIndex == n);

                            String name = switch (boneItems.get(n).getFrameType()) {
                                case 0 ->
                                        String.valueOf(n).concat(" : B".concat(boneItems.get(n).getFrameData().getValue().toString().concat(" | ").concat(english && !pmxFile.getBones().get((short) boneItems.get(n).getFrameData().getValue()).getBonenameEnglish().isEmpty() ? pmxFile.getBones().get((short) boneItems.get(n).getFrameData().getValue()).getBonenameEnglish() : pmxFile.getBones().get((short) boneItems.get(n).getFrameData().getValue()).getBoneNameJapanese())));

                                case 1 ->
                                        String.valueOf(n).concat(" : M".concat(boneItems.get(n).getFrameData().getValue().toString().concat(" | ").concat(english && !pmxFile.getMorphs().get((byte) boneItems.get(n).getFrameData().getValue()).getMorphNameEnglish().isEmpty() ? pmxFile.getMorphs().get((byte) boneItems.get(n).getFrameData().getValue()).getMorphNameEnglish() : pmxFile.getMorphs().get((byte) boneItems.get(n).getFrameData().getValue()).getMorphNameJapanese())));
                                default -> throw new RuntimeException("Invalid Display Frame Type!");
                            };

                            if (ImGui.selectable(name, isSelected)) {
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
        }
        return false;
    }
}
