/**
 * Copyright 2016 - 2018 Huawei Technologies Co., Ltd. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.huawei.cloud.servicestage.eclipse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Farhan Arshad
 */
public class Util implements Resources {
    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str));
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static IDialogSettings loadDialogSettings(IProject project)
            throws IOException {
        File settingsFile = getSettingsFile(project);

        if (settingsFile == null || !settingsFile.exists()) {
            return new DialogSettings("Servicestage Settings");
        }

        DialogSettings settings = new DialogSettings("Servicestage Settings");
        project.getWorkspace().toString();
        settings.load(settingsFile.getAbsolutePath());
        return settings;
    }

    public static File getSettingsFile(IProject project) {
        IFile settingsFile = project
                .getFile(ConfigConstants.DIALOG_SETTINGS_FILE_NAME);

        return new File(
                settingsFile.getRawLocation().makeAbsolute().toString());
    }

    public static void refreshSettingsFile(IProject project) {
        IFile settingsFile = project
                .getFile(ConfigConstants.DIALOG_SETTINGS_FILE_NAME);

        try {
            project.refreshLocal(IResource.DEPTH_ONE, null);
            settingsFile.refreshLocal(IResource.DEPTH_ZERO, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    public static void saveDialogSettings(IProject project,
            IDialogSettings settings) throws IOException {
        File settingsFile = getSettingsFile(project);

        settings.save(settingsFile.getAbsolutePath());
        refreshSettingsFile(project);
    }

    public static URL getPluginFileUrl(String path) {
        return Activator.getDefault().getBundle().getEntry(path);
    }

    public static String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        e.printStackTrace(pw);

        return sw.toString();
    }

    public static void showInfoDialog(String title, String msg, Shell shell) {
        MessageDialog dialog = new MessageDialog(shell, title, null, "", 0, 0,
                new String[] { IDialogConstants.OK_LABEL }) {
            @Override
            protected Control createCustomArea(Composite parent) {
                Link link = new Link(parent, SWT.WRAP);
                link.setText(msg);

                link.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        try {
                            PlatformUI.getWorkbench().getBrowserSupport()
                                    .getExternalBrowser()
                                    .openURL(new URL(e.text));
                        } catch (PartInitException | MalformedURLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                return link;
            }
        };
        dialog.open();
        Logger.info(title, msg);
    }

    public static void showInfoDialog(String title, String message,
            String details, Shell shell) {
        MultiStatus info = new MultiStatus(Activator.PLUGIN_ID, IStatus.INFO,
                SEE_DETAILS, null);
        info.add(new Status(IStatus.INFO, Activator.PLUGIN_ID, details, null));
        ErrorDialog.openError(shell, title, message, info);
        Logger.info(title, message, details);
    }

    public static void showExceptionDialog(String message, Shell shell,
            Exception e) {
        showExceptionDialog(ERROR, message, shell, e);
    }

    public static void showExceptionDialog(String title, String message,
            Shell shell, Exception e) {
        MultiStatus error = new MultiStatus(Activator.PLUGIN_ID, IStatus.ERROR,
                SEE_DETAILS, null);
        error.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                Util.exceptionToString(e), null));
        ErrorDialog.openError(shell, title, message, error);
        Logger.exception(title, e);
    }

    public static void showJobInfoDialog(String title, String message,
            Shell shell) {
        Action action = new Action() {
            public void run() {
                Util.showInfoDialog(title, message, shell);
            }
        };

        Display.getDefault().asyncExec(() -> {
            action.run();
        });
    }

    public static void showJobInfoDialog(String title, String message,
            String details, Shell shell) {
        Action action = new Action() {
            public void run() {
                Util.showInfoDialog(title, message, details, shell);
            }
        };

        Display.getDefault().asyncExec(() -> {
            action.run();
        });
    }

    public static void showJobExceptionDialog(String message, Shell shell,
            Exception e) {
        showJobExceptionDialog(ERROR, message, shell, e);
    }

    public static void showJobExceptionDialog(String title, String message,
            Shell shell, Exception e) {
        Action action = new Action() {
            public void run() {
                Util.showExceptionDialog(title, message, shell, e);
            }
        };

        Display.getDefault().asyncExec(() -> {
            action.run();
        });
    }

    public static String createZipFile(IProject project) throws IOException {
        return createZipFile(
                Paths.get(project.getRawLocation().makeAbsolute().toString()));
    }

    /**
     * Creates a temporary zip file of the specified project.<br>
     * <br>
     * Note: the zip file is set to be deleted on JVM exit!
     * 
     * @param projectPath
     * @return
     * @throws IOException
     */
    public static String createZipFile(Path projectPath) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");

        String projectName = projectPath.getFileName().toString();
        Path tmpDir = Files.createTempDirectory("servicestage");

        String zipfilePath = Paths
                .get(tmpDir.toAbsolutePath().toString(), projectName + ".zip")
                .toString();

        new File(zipfilePath).deleteOnExit();

        URI uri = URI.create("jar:file:" + zipfilePath);

        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Iterable<Path> roots = zipfs.getRootDirectories();
            Path zipRoot = roots.iterator().next();
            Files.walkFileTree(projectPath, new FileVisitor<Path>() {

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                        IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                        BasicFileAttributes attrs) throws IOException {
                    // skip node_modules directory (for nodejs projects)
                    if (!dir.getFileName().toString().equals("node_modules")) {
                        Files.createDirectories(zipRoot.resolve(
                                projectPath.relativize(dir).toString()));
                        return FileVisitResult.CONTINUE;
                    } else {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attrs) throws IOException {
                    Files.copy(file,
                            zipRoot.resolve(
                                    projectPath.relativize(file).toString()),
                            StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file,
                        IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

            });
        }

        return zipfilePath;
    }

    /**
     * Converts the provided array of keys and values to a map. The order is
     * kept as-is.
     * 
     * @param keys
     * @param values
     * @return
     */
    public static Map<String, String> arraysToMap(String[] keys,
            String[] values) {
        Map<String, String> map = new LinkedHashMap<>();

        if (keys != null && values != null && keys.length == values.length) {
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values[i]);
            }
        }

        return map;
    }
}
