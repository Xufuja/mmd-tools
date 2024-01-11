package dev.xfj.tabs;

import dev.xfj.AppLayer;
import dev.xfj.format.pmx.PMXFile;
import dev.xfj.format.pmx.PMXFileDisplayFrame;
import dev.xfj.format.pmx.PMXFileDisplayFrameData;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayTab {
    private final AppLayer layer;
    private boolean english;
    private boolean specialDelete;
    private boolean scrollDisplayItems;
    private boolean scrollFrameItems;

    private int displayIndex = 0;
    private int frameItemIndex = 0;

    public DisplayTab(AppLayer layer) {
        this.layer = layer;
        this.english = false;
        this.specialDelete = false;
        this.scrollDisplayItems = false;
        this.scrollFrameItems = false;
    }

    public void onImGuiRender() {
        PMXFile pmxFile = layer.getPmxFile();
        int tableFlags = ImGuiTableFlags.BordersOuter;

        boolean displayDeleted = false;
        boolean frameDeleted = false;

        if (ImGui.beginTabItem("Display")) {
            if (ImGui.beginTable("##Display", 2, tableFlags)) {
                ImGui.tableNextRow();
                ImGui.tableSetColumnIndex(0);

                List<PMXFileDisplayFrame> displayItems = new ArrayList<>();

                if (pmxFile.getDisplayFrames() != null) {
                    displayItems = pmxFile.getDisplayFrames();
                } else {
                    PMXFileDisplayFrame displayFrame = new PMXFileDisplayFrame();
                    displayFrame.setDisplayFrameNameJapanese("Root");
                    displayFrame.setDisplayFrameNameEnglish("Root");
                    displayFrame.setSpecialFlag((byte) 1);
                    displayFrame.setFrameCount(0);

                    displayItems.add(displayFrame);

                    displayFrame = new PMXFileDisplayFrame();
                    displayFrame.setDisplayFrameNameJapanese("表情");
                    displayFrame.setDisplayFrameNameEnglish("Exp");
                    displayFrame.setSpecialFlag((byte) 1);
                    displayFrame.setFrameCount(0);

                    displayItems.add(displayFrame);

                    pmxFile.setDisplayFrameCount(displayItems.size());
                }

                if (ImGui.beginTable("##SubDisplay", 2, ImGuiTableFlags.None)) {
                    ImGui.tableNextRow();

                    ImGui.tableSetColumnIndex(0);
                    ImGui.text("Common");
                    ImGui.tableSetColumnIndex(1);
                    ImGui.text("Display Frame");

                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);

                    ImGui.inputText("##Common", new ImString(String.valueOf(displayIndex)));
                    ImGui.tableSetColumnIndex(1);
                    ImGui.inputText("##DisplayFrame", new ImString(displayItems.get(displayIndex).getSpecialFlag() == 1 ? "Special" : "Normal"), ImGuiInputTextFlags.ReadOnly);

                    ImGui.sameLine(ImGui.getContentRegionAvail().x - 15);
                    ImGui.text(String.valueOf(pmxFile.getDisplayFrameCount()));
                    ImGui.endTable();
                }

                ImGui.setNextItemWidth(-1);
                if (ImGui.beginListBox("##DisplayFrameCount")) {
                    for (int n = 0; n < pmxFile.getDisplayFrameCount(); n++) {
                        boolean isSelected = (displayIndex == n);

                        if (ImGui.selectable(displayItems.get(n).getDisplayFrameNameJapanese(), isSelected)) {
                            displayIndex = n;

                            if (isSelected) {
                                ImGui.setItemDefaultFocus();
                            }
                        }
                    }

                    if (scrollDisplayItems) {
                        if (displayItems.size() > 1) {
                            ImGui.setScrollY(normalize(displayIndex, 0, displayItems.size() - 1, 0, (int) ImGui.getScrollMaxY()));
                            scrollDisplayItems = false;
                        }
                    }

                    ImGui.endListBox();
                }

                ImGui.pushItemWidth(ImGui.calcItemWidth() / 6);
                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 5, 10);

                float lineHeight = ImGui.getFont().getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
                ImVec2 buttonSize = new ImVec2(lineHeight + lineHeight, lineHeight);

                if (ImGui.button("T##1", buttonSize.x, buttonSize.y)) {
                    PMXFileDisplayFrame frame = displayItems.get(displayIndex);
                    displayItems.remove(frame);
                    displayItems.add(0, frame);
                    displayIndex = 0;
                    scrollDisplayItems = true;
                }

                ImGui.sameLine();

                if (ImGui.button("^##1", buttonSize.x, buttonSize.y)) {
                    if (displayItems.size() > 1) {
                        if (displayIndex - 1 > -1) {
                            Collections.swap(displayItems, displayIndex, displayIndex - 1);
                            displayIndex = displayIndex - 1;
                            scrollDisplayItems = true;
                        }
                    }
                }

                ImGui.sameLine();

                if (ImGui.button("v##1", buttonSize.x, buttonSize.y)) {
                    if (displayItems.size() > 1) {
                        if (displayIndex + 1 < displayItems.size()) {
                            Collections.swap(displayItems, displayIndex, displayIndex + 1);
                            displayIndex = displayIndex + 1;
                            scrollDisplayItems = true;
                        }
                    }
                }

                ImGui.sameLine();

                if (ImGui.button("B##1", buttonSize.x, buttonSize.y)) {
                    PMXFileDisplayFrame frame = displayItems.get(displayIndex);
                    displayItems.remove(frame);
                    displayItems.add(frame);
                    displayIndex = displayItems.size() - 1;
                    scrollDisplayItems = true;
                }

                ImGui.sameLine();

                if (ImGui.button("+##1", buttonSize.x, buttonSize.y)) {
                    pmxFile.setDisplayFrameCount(displayItems.size() + 1);

                    PMXFileDisplayFrame frame = new PMXFileDisplayFrame();
                    frame.setDisplayFrameNameJapanese("New Object");
                    frame.setDisplayFrameNameEnglish("New Object");
                    frame.setSpecialFlag((byte) 0);
                    frame.setFrameCount(0);

                    displayItems.add(frame);

                    displayIndex = displayItems.size() - 1;
                    scrollDisplayItems = true;
                }

                ImGui.sameLine();

                ImGui.beginDisabled(displayItems.get(displayIndex).getSpecialFlag() == 1 && !specialDelete);

                if (ImGui.button("x##1", buttonSize.x, buttonSize.y)) {
                    displayDeleted = true;
                }

                ImGui.endDisabled();

                ImGui.popItemWidth();

                ImGui.popStyleVar();

                if (ImGui.checkbox("Allow special frame deletion", specialDelete)) {
                    specialDelete = !specialDelete;
                }

                pmxFile.setDisplayFrames(displayItems);

                ImGui.sameLine();

                ImGui.tableSetColumnIndex(1);

                ImGui.text("In-frame element");

                ImGui.text("Name");
                ImGui.sameLine();
                ImGui.setNextItemWidth(ImGui.getContentRegionAvail().x - 85);
                ImGui.inputText("##DisplaySelected", english && !displayItems.get(displayIndex).getDisplayFrameNameEnglish().isEmpty() ? new ImString(displayItems.get(displayIndex).getDisplayFrameNameEnglish()) : new ImString(displayItems.get(displayIndex).getDisplayFrameNameJapanese()), ImGuiInputTextFlags.ReadOnly);
                ImGui.sameLine();

                if (ImGui.checkbox("English", english)) {
                    english = !english;
                }

                List<PMXFileDisplayFrameData> frameItems = new ArrayList<>();

                if (displayItems.get(displayIndex).getFrames() != null) {
                    frameItems = displayItems.get(displayIndex).getFrames();
                }

                ImGui.inputText("##SubIndex", new ImString(String.valueOf(frameItemIndex)), ImGuiInputTextFlags.ReadOnly);

                ImGui.sameLine(ImGui.getContentRegionAvail().x - 15);

                ImGui.text(String.valueOf(displayItems.get(displayIndex).getFrameCount()));

                ImGui.setNextItemWidth(-1);
                if (ImGui.beginListBox("##DisplayFrameData")) {
                    for (int n = 0; n < displayItems.get(displayIndex).getFrameCount(); n++) {
                        boolean isSelected = (frameItemIndex == n);
                        String name = switch (frameItems.get(n).getFrameType()) {
                            case 0 ->
                                    String.format("%1$s : B%2$s | %3$s", n, frameItems.get(n).getFrameData().getValue().toString(), english && !pmxFile.getBones().get((short) frameItems.get(n).getFrameData().getValue()).getBonenameEnglish().isEmpty() ? pmxFile.getBones().get((short) frameItems.get(n).getFrameData().getValue()).getBonenameEnglish() : pmxFile.getBones().get((short) frameItems.get(n).getFrameData().getValue()).getBoneNameJapanese());

                            case 1 ->
                                    String.format("%1$s : M%2$s | %3$s", n, frameItems.get(n).getFrameData().getValue().toString(), english && !pmxFile.getMorphs().get((byte) frameItems.get(n).getFrameData().getValue()).getMorphNameEnglish().isEmpty() ? pmxFile.getMorphs().get((byte) frameItems.get(n).getFrameData().getValue()).getMorphNameEnglish() : pmxFile.getMorphs().get((byte) frameItems.get(n).getFrameData().getValue()).getMorphNameJapanese());

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

                ImGui.pushItemWidth(ImGui.calcItemWidth() / 6);
                ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 5, 10);
                
                if (ImGui.button("T##2", buttonSize.x, buttonSize.y)) {
                    if (!frameItems.isEmpty()) {
                        PMXFileDisplayFrameData frame = frameItems.get(frameItemIndex);
                        frameItems.remove(frame);
                        frameItems.add(0, frame);
                        frameItemIndex = 0;
                        scrollFrameItems = true;
                    }
                }

                ImGui.sameLine();

                if (ImGui.button("^##2", buttonSize.x, buttonSize.y)) {
                    if (frameItems.size() > 1) {
                        if (frameItemIndex - 1 > -1) {
                            Collections.swap(frameItems, frameItemIndex, frameItemIndex - 1);
                            frameItemIndex = frameItemIndex - 1;
                            scrollFrameItems = true;
                        }
                    }
                }

                ImGui.sameLine();

                if (ImGui.button("v##2", buttonSize.x, buttonSize.y)) {
                    if (frameItems.size() > 1) {
                        if (frameItemIndex + 1 < frameItems.size()) {
                            Collections.swap(frameItems, frameItemIndex, frameItemIndex + 1);
                            frameItemIndex = frameItemIndex + 1;
                            scrollFrameItems = true;
                        }
                    }
                }

                ImGui.sameLine();

                if (ImGui.button("B##2", buttonSize.x, buttonSize.y)) {
                    if (!frameItems.isEmpty()) {
                        PMXFileDisplayFrameData frame = frameItems.get(frameItemIndex);
                        frameItems.remove(frame);
                        frameItems.add(frame);
                        frameItemIndex = frameItems.size() - 1;
                        scrollFrameItems = true;
                    }
                }

                ImGui.sameLine();

                //Never seems to be enabled
                ImGui.beginDisabled(true);
                ImGui.button("+##2", buttonSize.x, buttonSize.y);
                ImGui.endDisabled();

                ImGui.sameLine();

                if (ImGui.button("x##2", buttonSize.x, buttonSize.y)) {
                    frameDeleted = true;
                }

                ImGui.popItemWidth();

                ImGui.popStyleVar();

                ImGui.endTable();

                if (frameDeleted) {
                    if (!frameItems.isEmpty()) {
                        displayItems.get(displayIndex).setFrameCount(displayItems.get(displayIndex).getFrameCount() - 1);
                        displayItems.get(displayIndex).getFrames().remove(frameItemIndex);
                        frameItemIndex--;

                        if (frameItemIndex < 0) {
                            frameItemIndex = 0;
                        }
                    }
                }

                if (displayDeleted) {
                    if (!frameItems.isEmpty()) {
                        displayItems.get(displayIndex).getFrames().removeAll(displayItems.get(displayIndex).getFrames());
                        displayItems.get(displayIndex).setFrameCount(0);
                    }

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
    }

    private int normalize(int value, int minValue, int maxValue, int minNormalize, int maxNormalize) {
        return minNormalize + ((value - minValue) * (maxNormalize - minNormalize)) / (maxValue - minValue);
    }
}
