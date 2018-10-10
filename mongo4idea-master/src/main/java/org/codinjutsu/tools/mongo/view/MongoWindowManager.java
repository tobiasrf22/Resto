/*
 * Copyright (c) 2018 David Boissier.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codinjutsu.tools.mongo.view;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.codinjutsu.tools.mongo.logic.MongoManager;
import org.codinjutsu.tools.mongo.logic.Notifier;
import org.codinjutsu.tools.mongo.utils.GuiUtils;

import javax.swing.*;


public class MongoWindowManager {

    private static final Icon MONGO_ICON = GuiUtils.loadIcon("mongo_logo.png");

    private static final String MONGO_RUNNER = "Mongo Runner";

    private static final String MONGO_EXPLORER = "Mongo Explorer";

    private final Project project;
    private final MongoExplorerPanel mongoExplorerPanel;

    public static MongoWindowManager getInstance(Project project) {
        return ServiceManager.getService(project, MongoWindowManager.class);
    }

    private MongoWindowManager(Project project) {
        this.project = project;

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        mongoExplorerPanel = new MongoExplorerPanel(project, MongoManager.getInstance(project), Notifier.getInstance(project));
        Content mongoExplorer = ContentFactory.SERVICE.getInstance().createContent(mongoExplorerPanel, null, false);

        ToolWindow toolMongoExplorerWindow = toolWindowManager.registerToolWindow(MONGO_EXPLORER, false, ToolWindowAnchor.RIGHT, project, true);

        toolMongoExplorerWindow.getContentManager().addContent(mongoExplorer);
        toolMongoExplorerWindow.setIcon(MONGO_ICON);
    }

    public void unregisterMyself() {
        mongoExplorerPanel.dispose();
        ToolWindowManager.getInstance(project).unregisterToolWindow(MONGO_RUNNER);
        ToolWindowManager.getInstance(project).unregisterToolWindow(MONGO_EXPLORER);
    }
}
