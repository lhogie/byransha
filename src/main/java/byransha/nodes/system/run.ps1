#!/opt/homebrew/bin//pwsh

$ErrorActionPreference = "Stop"

$BinDir = Join-Path $HOME ".byransha-app"
$BinDir = Join-Path $HOME ".byransha-data"

   if (! Test-Path $BinDir) {
                Download-Artifacts
	}



# Determine execution binary path format
$JavaExe = Join-Path $BinDir "jvm/bin/java.exe"
if (-not ($IsWindows -or $env:OS -like "*Windows*")) {
    $JavaExe = Join-Path $BinDir "jvm/bin/java"
}
$JarPath = Join-Path $BinDir "byransha.jar"

# 2. Main Execution Lifecycle Loop
while ($true) {
    Write-Host "Starting Java Application..."
    & $JavaExe -cp $JarPath

    if ($LASTEXITCODE -eq 46) {
            Download-Artifacts
    }else{
    break
    }
}



# Helper function to handle downloads to clean up the loop logic
function Download-Artifacts {
 if (Test-Path $BinDir) {
    Remove-Item -Path $BinDir -Recurse -Force
	}
	
	New-Item -ItemType Directory -Force -Path $BinDir | Out-Null


    Write-Host "Initializing/Updating application components..."
    
    # Download main jar
    $JarUrl = "https://webusers.i3s.unice.fr/~hogie/software/byransha/downloads/byransha.jar"
    $JarPath = Join-Path $BinDir "byransha.jar"
    Write-Host "Downloading " $JarUrl
    Invoke-WebRequest -Uri $JarUrl -OutFile $JarPath

    # Determine OS
    if ($IsWindows -or $env:OS -like "*Windows*") { $OS = "windows"
    
    # Define paths
$ScriptPath = $MyInvocation.MyCommand.Path
$ShortcutPath = Join-Path [Environment]::GetFolderPath("Desktop") "Launch App.lnk"
$IconPath = "C:\Path\To\Your\custom_icon.ico" # Must be a local .ico file

# Create Windows COM object to build a shortcut
$WshShell = New-Object -ComObject WScript.Shell
$Shortcut = $WshShell.CreateShortcut($ShortcutPath)

# Configure target to launch PowerShell invisibly running your file
$Shortcut.TargetPath = "powershell.exe"
$Shortcut.Arguments = "-ExecutionPolicy Bypass -WindowStyle Hidden -File `"$ScriptPath`""
$Shortcut.IconLocation = "$IconPath, 0"
$Shortcut.Description = "Launch Byransha Engine"

# Save shortcut to desktop
$Shortcut.Save()
    
     }
    elseif ($IsMacOS) { $OS = "mac-aarch64" }
    else { $OS = "linux-x64" }

    # Download JRE archive/jar
    #https://jdk.java.net/26/

    $JreUrl = "https://webusers.i3s.unice.fr/~hogie/software/byransha/downloads/jvm/$OS.zip"
    Write-Host "Downloading " $JreUrl -ForegroundColor Cyan
    $JrePath = Join-Path $BinDir "jre-$OS.tgz"
    Invoke-WebRequest -Uri $JreUrl -OutFile $JrePath
    
    Expand-Archive -Path $JrePath -DestinationPath $BinDir
}
