# Mod Pack Manager (MMP)
Welcome to Mod Pack Manager! This program downloads and installs mods for supported game and game versions! Please note that this program requires access to the internet as well as your file system to function as intended. A launch script will be made available for each version released of MMP.
## Supported Games/Game Versions/Sites/OSes
MMP has to be coded manually to be able to access sites on the internet or the correct files and folders.
### Supported Sites

 - v0.5.0
   - Reika's Site
     - Reika's site is linked here: https://reikakalseki.github.io/index.html
     - Information about the site and the person behind it can be found once you paste the link into your internet browser
 - v1.0.0
   - Nexus Mods
### Supported Games & their respective versions
 - v0.5.0
   - Subnautica
     - Legacy
 - v1.0.0
   - Subnautica
     - Legacy
     - March 2023
     - 2025
### Supported OSes + Architectures
 - All Versions
   - Direct Support (installer included)
     - Windows x86_64
     - macOS Apple Silicon/ARM64
     - Linux x86_64
       - Debian based distros
         - Ubuntu based distros
   - All other OSes are indirectly supported because of how Java works with `.jar` files.
     - Please **do NOT contact me** asking to directly support more OSes. I probably am not able to directly support your OS due to lack of devices. In the specific case of Arch based distros, the command to package installers (`jpackage`) does not support any Arch installers.
## Releases, Downloads, and Updates
### Update Information
Currently, v0.5.0 is in progress, and almost ready for release. Update Information can be found in the Supported Game/Game Versions/Sites header. Support for each game/game version/site will be added one by one for each update. My plans will be in the header, so **DO NOT CONTACT ME ABOUT UPDATE PLANS**
### Releases
v0.5.0 is coming out soon, hopefully before 12/25/2025. I hope I can release v1.0.0 by then too, but that depends.
### How To Download
Once a release is officially out, you'll be able to download a zip file on the right side of this GitHub repository below `About`. The `Releases` section will have this app until v1.0.0 where it will be released on Nexus
#### Setup
 - Windows x84_64
   - Download the `.exe` installer and run it
   - Follow the instructions.
   - Done!
 - Linux x86_64
   - Debian-based distro (including ubuntu and it's subsequences)
     - Download the `.deb` installer and run it.
     - Follow the instructions
     - Done!
 - macOS Apple Silicon/ARM64
   - Download the `.dmg` installer and run it
   - Follow the instructions
   - Done!
 - Indirectly Supported OSes
   - Each OS has direct support through an installer. The rest have indirect support because of how Java works. You can run MMP because of the `java` command in all terminals. This does require a .jar file, with the extension `-jar` tacked onto the command, which I have provided in the `Releases` section (See header How To Download). Follow these instructions if your OS/architecture was not listed above
     - Download the `.jar` file.
     - Ensure you have a version of Java (go look it up for your specific distribution and architecture (x86_64 or ARM))
     - In terminal, run `java -jar path/to/downloaded/mmp.jar`
     - Have at it!
   - **32-bit systems MAY NOT be able to run this. IF YOU ATTEMPT TO RUN THIS APP ON A 32-BIT SYSTEM, EXPECT MANY CRASH RELATED BUGS**
