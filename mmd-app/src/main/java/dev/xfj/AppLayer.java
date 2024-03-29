package dev.xfj;

import dev.xfj.application.Application;
import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.key.KeyPressedEvent;
import dev.xfj.format.pmx.PMXFile;
import dev.xfj.format.pmx.PMXFileDisplayFrame;
import dev.xfj.input.Input;
import dev.xfj.input.KeyCodes;
import dev.xfj.parsing.PMXParser;
import dev.xfj.tabs.DisplayTab;
import dev.xfj.tabs.InfoTab;
import dev.xfj.writer.PMXWriter;
import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;

import java.nio.file.Path;
import java.util.Optional;

public class AppLayer implements Layer {

    private PMXFile pmxFile;
    private InfoTab infoTab;
    private DisplayTab displayTab;


    @Override
    public void onAttach() {
        pmxFile = new PMXFile();
        infoTab = new InfoTab(this);
        displayTab = new DisplayTab(this);
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
            if (ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Delete Empty Frames", "Ctrl+D")) {
                    deleteEmptyFrames();
                }

                if (ImGui.menuItem("Fix Root and Exp", "Ctrl+E")) {
                    fixSpecialNames();
                }

                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }

        if (ImGui.beginTabBar("##TabBar", ImGuiTabBarFlags.None)) {

            infoTab.onImGuiRender();
            displayTab.onImGuiRender();

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

    private void deleteEmptyFrames() {
        if (pmxFile.getDisplayFrames() != null) {
            pmxFile.getDisplayFrames().removeIf(frame -> frame.getFrameCount() == 0);
            pmxFile.setDisplayFrameCount(pmxFile.getDisplayFrames().size());
        }
    }

    private void fixSpecialNames() {
        if (pmxFile.getDisplayFrames() != null) {
            pmxFile.getDisplayFrames().stream().filter(frame -> frame.getSpecialFlag() == (byte) 1).forEach(frame -> {
                adjustName(frame, "Root", "Root");
                adjustName(frame, "Exp", "表情");
            });
        }
    }

    private void adjustName(PMXFileDisplayFrame frame, String en, String jp) {
        if (frame.getDisplayFrameNameEnglish().equals(en) && !frame.getDisplayFrameNameJapanese().equals(jp)) {
            frame.setDisplayFrameNameJapanese(jp);
        } else if (frame.getDisplayFrameNameJapanese().equals(jp) && !frame.getDisplayFrameNameEnglish().equals(en)) {
            frame.setDisplayFrameNameEnglish(en);
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
            case KeyCodes.S -> {
                if (control) {
                    saveModel();
                    return true;
                }
            }
            case KeyCodes.D -> {
                if (control) {
                    deleteEmptyFrames();
                    return true;
                }
            }
            case KeyCodes.E -> {
                if (control) {
                    fixSpecialNames();
                    return true;
                }
            }
        }
        return false;
    }

    public PMXFile getPmxFile() {
        return pmxFile;
    }
}
