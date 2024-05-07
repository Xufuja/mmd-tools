package dev.xfj.tabs;

import dev.xfj.AppLayer;
import dev.xfj.format.pmm.PMMFile;
import dev.xfj.format.pmx.PMXFile;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

public class SceneInfoTab {
    private final AppLayer layer;
    private boolean english;

    public SceneInfoTab(AppLayer layer) {
        this.layer = layer;
        this.english = false;
    }

    public void onImGuiRender() {
        PMMFile pmmFile = layer.getPmmFile();

        if (ImGui.beginTabItem("Scene Info")) {
            if (pmmFile != null) {
                ImGui.text(pmmFile.getVersion());
            }
            ImGui.endTabItem();
        }
    }
}
