package dev.xfj.tabs;

import dev.xfj.AppLayer;
import dev.xfj.format.pmx.PMXFile;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

public class InfoTab {
    private final AppLayer layer;
    private boolean english;

    public InfoTab(AppLayer layer) {
        this.layer = layer;
        this.english = false;
    }
    public void onImGuiRender() {
        PMXFile pmxFile = layer.getPmxFile();
        int tableFlags = ImGuiTableFlags.BordersOuter;

        if (ImGui.beginTabItem("Info")) {
            ImGui.text("System");

            if (ImGui.beginTable("##System", 6, tableFlags)) {
                ImGui.tableNextRow();
                ImGui.tableSetColumnIndex(0);

                ImGui.text("PMX Version");
                ImGui.tableSetColumnIndex(1);
                ImGui.alignTextToFramePadding();
                ImGui.text(String.valueOf(pmxFile.getVersion()));
                ImGui.tableSetColumnIndex(2);
                ImGui.text("Encoding");
                ImGui.tableSetColumnIndex(3);
                ImGui.setNextItemWidth(-1);
                ImGui.inputText("##locale", pmxFile.getGlobals() != null ? new ImString(pmxFile.getGlobals().getTextEncoding() == 1 ? "UTF8" : "UTF16-LE") : new ImString(""), ImGuiInputTextFlags.ReadOnly);

                ImGui.tableSetColumnIndex(4);
                ImGui.text("UV Number");

                ImGui.tableSetColumnIndex(5);
                ImGui.setNextItemWidth(-1);
                ImGui.inputText("##UV", pmxFile.getGlobals() != null ? new ImString(pmxFile.getGlobals().getAdditionalVec4Count()) : new ImString("0"), ImGuiInputTextFlags.ReadOnly);

                ImGui.endTable();
            }

            ImGui.text("Model");

            if (ImGui.beginTable("##Model", 2, tableFlags)) {
                ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed);
                ImGui.tableSetupColumn("data", ImGuiTableColumnFlags.WidthStretch);

                ImGui.tableNextRow();
                ImGui.tableSetColumnIndex(0);
                ImGui.text("Name");

                ImGui.tableSetColumnIndex(1);
                ImGui.setNextItemWidth(ImGui.getContentRegionAvail().x - 85);
                ImGui.inputText("##Name", english ? new ImString(pmxFile.getModelNameEnglish()) : new ImString(pmxFile.getModelNameJapanese()), ImGuiInputTextFlags.ReadOnly);

                ImGui.sameLine();

                if (ImGui.checkbox("English", english)) {
                    english = !english;
                }

                ImGui.tableNextRow();
                ImGui.tableSetColumnIndex(0);
                ImGui.text("Comment");
                ImGui.tableSetColumnIndex(1);
                ImGui.setNextItemWidth(-1);
                ImGui.inputTextMultiline("##Comment", english ? new ImString(pmxFile.getCommentsEnglish()) : new ImString(pmxFile.getCommentsJapanese()), ImGuiInputTextFlags.ReadOnly);

                ImGui.endTable();
            }
            ImGui.endTabItem();
        }
    }
}
