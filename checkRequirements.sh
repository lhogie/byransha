#!/bin/bash

echo " *** Checking requirements *** "
MISSING=()

# Fonction pour demander l'installation
install_tool() {
    local tool=$1
    local install_cmd=$2
    
    read -p "Do you want to download $tool now? (y/n): " answer
    if [[ "$answer" =~ ^[Yy]$ ]]; then
        echo "***"
        echo "Installing $tool..."
        eval "$install_cmd"
        if [ $? -eq 0 ]; then
            echo " $tool installed successfully"
        else
            echo " Error installing $tool"
            return 1
        fi
    else
        echo "Installation of $tool ignored by user."
        return 1
    fi
}

# Vérifier Node.js
if command -v node >/dev/null 2>&1; then
    echo " Node.js is installed ($(node -v))"
else
    echo " Node.js is not installed"
    MISSING+=("node")
fi

# Vérifier npm
if command -v npm >/dev/null 2>&1; then
    echo "npm is installed ($(npm -v))"
else
    echo "npm is not installed"
    MISSING+=("npm")
fi

# Vérifier Maven
if command -v mvn >/dev/null 2>&1; then
    echo "Maven is installed ($(mvn -v | head -n1 | cut -d' ' -f3))"
else
    echo "Maven is not installed"
    MISSING+=("maven")
fi

# Vérifier Bun
if command -v bun >/dev/null 2>&1; then
    echo "Bun is installed ($(bun --version))"
else
    echo "Bun is not installed"
    MISSING+=("bun")
fi

# Si des outils manquent, proposer l'installation
if [ ${#MISSING[@]} -gt 0 ]; then
    echo "***" 
    echo "  ${#MISSING[@]} outil(s) manquant(s): ${MISSING[*]}"
    echo "***"
    
    # Détecter l'OS
    OS="unknown"
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        OS="linux"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        OS="macos"
    elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
        OS="windows"
    fi
    # Vérifier Chocolatey sur Windows (optionnel)
    HAS_CHOCO=false
    if [ "$OS" == "windows" ]; then
        if command -v choco >/dev/null 2>&1; then
            echo "***"
            echo " Chocolatey is installed"
            echo "***"
            HAS_CHOCO=true
        else
            echo "***"
            echo " Chocolatey is not installed"
            echo "You can install missing tools manually or install Chocolatey for automation."
            echo "To install Chocolatey, run in PowerShell (as Administrator):"
            echo "Set-ExecutionPolicy Bypass -Scope Process -Force; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"
            echo "***"
        fi
    fi

    
    
    # Proposer l'installation pour chaque outil manquant
    for tool in "${MISSING[@]}"; do
        case "$tool" in
            node|npm)
                if [ "$OS" == "macos" ]; then
                    install_tool "Node.js & npm" "brew install node"
                elif [ "$OS" == "linux" ]; then
                    install_tool "Node.js & npm" "curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash - && sudo apt-get install -y nodejs"
                else
                    if [ "$HAS_CHOCO" = true ]; then
                        install_tool "Node.js & npm" "choco install nodejs --version=\"24.11.1\""
                    else
                        echo "📥 Node.js & npm - Download from: https://nodejs.org/"
                    fi
                fi
                ;;
            maven)
                if [ "$OS" == "macos" ]; then
                    install_tool "Maven" "brew install maven"
                elif [ "$OS" == "linux" ]; then
                    install_tool "Maven" "sudo apt-get install -y maven"
                else
                    if [ "$HAS_CHOCO" = true ]; then
                        install_tool "Maven" "choco install maven"
                    else
                        echo " Maven - Download from: https://maven.apache.org/download.cgi"
                        echo "Or you can install it via IntelliJ IDEA"
                    fi
                fi
                ;;
            bun) 
                if [ "$OS" != "windows" ]; then
                    if [ "$HAS_CHOCO" = true ]; then
                        install_tool "Bun" "choco install bun"
                    else
                        echo " Bun - Install with PowerShell: powershell -c \"irm bun.sh/install.ps1|iex\""
                    fi
                fi
                ;;
        esac
    done
    
    echo "***"
    echo " Redo this script after installing the missing tools"
    exit 1
fi

echo "***"
echo " All requirements are met!"
exit 0


