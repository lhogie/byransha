jpackage --input target/ \
         --dest dist/ \
         --name ByranshaApp \
         --main-jar "target/byransha-0.0.3-SNAPSHOT.jar" \
         --main-class byransha.Main \
         --type pkg \
#         --win-shortcut \
#         --win-menu
#         --icon src/main/resources/icons/app_icon.ico