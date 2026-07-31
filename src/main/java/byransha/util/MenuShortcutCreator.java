package byransha.util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MenuShortcutCreator {

    public static void createShortcut(String appName, Path targetExecutable, Path iconPath) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("linux")) {
            createLinuxDesktopEntry(appName, targetExecutable, iconPath);
        } else if (os.contains("win")) {
            createWindowsStartMenuShortcut(appName, targetExecutable, iconPath);
        } else if (os.contains("mac")) {
            System.out.println("macOS uses .app bundles in ~/Applications instead of standard shortcut files.");
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + os);
        }
    }

    /**
     * Linux: Writes a .desktop file to ~/.local/share/applications/
     */
    private static void createLinuxDesktopEntry(String appName, Path targetExecutable, Path iconPath) throws IOException {
        Path menuDir = Paths.get(System.getProperty("user.home"), ".local", "share", "applications");
        Files.createDirectories(menuDir);

        String desktopFileName = appName.toLowerCase().replaceAll("\\s+", "-") + ".desktop";
        Path desktopFilePath = menuDir.resolve(desktopFileName);

        StringBuilder content = new StringBuilder();
        content.append("[Desktop Entry]\n");
        content.append("Type=Application\n");
        content.append("Name=").append(appName).append("\n");
        content.append("Exec=\"").append(targetExecutable.toAbsolutePath()).append("\"\n");
        if (iconPath != null && Files.exists(iconPath)) {
            content.append("Icon=").append(iconPath.toAbsolutePath()).append("\n");
        }
        content.append("Terminal=false\n");
        content.append("Categories=Utility;\n");

        Files.writeString(desktopFilePath, content.toString(), 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Make executable (Linux permission rule)
        File file = desktopFilePath.toFile();
        file.setExecutable(true, false);

        System.out.println("Linux menu shortcut created at: " + desktopFilePath);
    }

    /**
     * Windows: Uses PowerShell WScript.Shell to generate a .lnk shortcut in the Start Menu.
     */
    private static void createWindowsStartMenuShortcut(String appName, Path targetExecutable, Path iconPath) throws IOException, InterruptedException {
        String appData = System.getenv("APPDATA"); // C:\Users\<User>\AppData\Roaming
        Path startMenuDir = Paths.get(appData, "Microsoft", "Windows", "Start Menu", "Programs");
        Files.createDirectories(startMenuDir);

        Path shortcutPath = startMenuDir.resolve(appName + ".lnk");

        // PowerShell script string to construct the .lnk via WScript.Shell
        StringBuilder psScript = new StringBuilder();
        psScript.append("$WshShell = New-Object -ComObject WScript.Shell; ");
        psScript.append("$Shortcut = $WshShell.CreateShortcut('").append(shortcutPath.toAbsolutePath()).append("'); ");
        psScript.append("$Shortcut.TargetPath = '").append(targetExecutable.toAbsolutePath()).append("'; ");
        psScript.append("$Shortcut.WorkingDirectory = '").append(targetExecutable.getParent().toAbsolutePath()).append("'; ");
        
        if (iconPath != null && Files.exists(iconPath)) {
            psScript.append("$Shortcut.IconLocation = '").append(iconPath.toAbsolutePath()).append("'; ");
        }
        psScript.append("$Shortcut.Save()");

        // Execute via PowerShell process
        ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", psScript.toString());
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Windows Start Menu shortcut created at: " + shortcutPath);
        } else {
            throw new IOException("Failed to create Windows shortcut. PowerShell exited with code: " + exitCode);
        }
    }

    public static void main(String[] args) {
        try {
            Path target = Paths.get("/path/to/your/app.exe"); // or /path/to/script.sh
            Path icon = Paths.get("/path/to/your/icon.png");

            createShortcut("My Java App", target, icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}