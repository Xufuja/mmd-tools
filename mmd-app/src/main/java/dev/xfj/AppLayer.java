package dev.xfj;

import dev.xfj.application.Application;
import dev.xfj.events.Event;
import dev.xfj.events.EventDispatcher;
import dev.xfj.events.key.KeyPressedEvent;
import dev.xfj.format.pmm.PMMFile;
import dev.xfj.format.pmx.PMXFile;
import dev.xfj.format.pmx.PMXFileDisplayFrame;
import dev.xfj.input.Input;
import dev.xfj.input.KeyCodes;
import dev.xfj.parsing.PMMParser;
import dev.xfj.parsing.PMXParser;
import dev.xfj.tabs.ModelDisplayTab;
import dev.xfj.tabs.ModelInfoTab;
import dev.xfj.tabs.SceneInfoTab;
import dev.xfj.writer.PMMWriter;
import dev.xfj.writer.PMXWriter;
import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;

import java.nio.file.Path;
import java.util.Optional;

public class AppLayer implements Layer {

    private PMXFile pmxFile;
    private PMMFile pmmFile;
    private ModelInfoTab modelInfoTab;
    private ModelDisplayTab modelDisplayTab;
    private SceneInfoTab sceneInfoTab;
    private boolean modelLoaded;
    private boolean sceneLoaded;

    @Override
    public void onAttach() {
        pmxFile = new PMXFile();
        pmmFile = new PMMFile();
        modelInfoTab = new ModelInfoTab(this);
        modelDisplayTab = new ModelDisplayTab(this);
        sceneInfoTab = new SceneInfoTab(this);
        modelLoaded = false;
        sceneLoaded = false;
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

                if (ImGui.menuItem("Load Scene...", "Ctrl+Shift+O")) {
                    loadScene();
                }

                if (modelLoaded) {
                    if (ImGui.menuItem("Save PMX...", "Ctrl+S")) {
                        saveModel();
                    }
                }

                if (sceneLoaded) {
                    if (ImGui.menuItem("Save Scene...", "Ctrl+Shift+S")) {
                        saveModel();
                    }
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

            modelInfoTab.onImGuiRender();
            modelDisplayTab.onImGuiRender();
            sceneInfoTab.onImGuiRender();

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
        Optional<String> filePath = fileDialog.openFile("PMX File (*.pmx)\0*.pmx\0");
        filePath.ifPresent(path -> loadModel(Path.of(path)));
    }

    private void loadScene() {
        FileDialog fileDialog = new FileDialog();
        Optional<String> filePath = fileDialog.openFile("PMM File (*.pmm)\0*.pmm\0");
        filePath.ifPresent(path -> loadScene(Path.of(path)));
    }

    private void loadModel(Path filePath) {
        try {
            PMXParser pmxParser = new PMXParser(filePath);
            pmxFile = pmxParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void loadScene(Path filePath) {
        try {
            PMMParser pmmParser = new PMMParser(filePath);
            pmmFile = pmmParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveModel() {
        FileDialog fileDialog = new FileDialog();
        Optional<String> filePath = fileDialog.saveFile("PMX File(*.pmx)\0*.pmx\0");
        filePath.ifPresent(path -> saveModel(Path.of(path)));
    }

    private void saveScene() {
        FileDialog fileDialog = new FileDialog();
        Optional<String> filePath = fileDialog.saveFile("PMM File(*.pmm)\0*.pmm\0");
        filePath.ifPresent(path -> saveScene(Path.of(path)));
    }

    private void saveModel(Path filePath) {
        try {
            PMXWriter pmxWriter = new PMXWriter(pmxFile, false);
            pmxWriter.write(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveScene(Path filePath) {
        try {
            PMMWriter pmmWriter = new PMMWriter(pmmFile, false);
            pmmWriter.write(filePath);
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
                    if (!shift) {
                        loadModel();
                    } else {
                        loadScene();
                    }
                    return true;
                }
            }
            case KeyCodes.S -> {
                if (control) {
                    if (!shift) {
                        saveModel();
                    } else {
                        saveScene();
                    }
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

    public PMMFile getPmmFile() {
        return pmmFile;
    }
}
