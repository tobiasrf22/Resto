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

package org.codinjutsu.tools.mongo.view.editor;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.util.LocalTimeCounter;
import org.codinjutsu.tools.mongo.ServerConfiguration;
import org.codinjutsu.tools.mongo.view.model.navigation.Navigation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

public class MongoObjectFile extends VirtualFile {

    private final long myModStamp;
    private final ServerConfiguration configuration;
    private final Navigation navigation;
    private final Project project;
    private final String path;

    public MongoObjectFile(Project project, ServerConfiguration configuration, Navigation navigation) {
        this.project = project;
        this.configuration = configuration;
        this.navigation = navigation;
        this.myModStamp = LocalTimeCounter.currentTime();
        this.path = getName();
    }

    @NotNull
    @Override
    public String getName() {
        return String.format("%s/%s", configuration.getLabel(), navigation.getCurrentWayPoint().getLabel());
    }

    @NotNull
    public FileType getFileType() {
        return MongoFakeFileType.INSTANCE;
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return MongoFileSystem.getInstance();
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public ServerConfiguration getConfiguration() {
        return configuration;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public Project getProject() {
        return project;
    }

    //    Unused methods
    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) {
        throw new UnsupportedOperationException("MongoResultFile is read-only");
    }

    @Override
    public long getModificationStamp() {
        return myModStamp;
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray() {
        return new byte[0];
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {

    }

    @Override
    public InputStream getInputStream() {
        return null;
    }
}
