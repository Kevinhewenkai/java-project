# Technical Setup/Tips 🛠️

## Contents

[[_TOC_]]

## 0. Change Log

None

## 1. Technical Setup

Because this project uses JavaFX, to use it on a non-CSE computer, additional setup will be needed.

The repository provided has already been setup to run the starter-game, and tested on a CSE machine (using VLAB). To play the starter game, clone the repository onto your CSE machine on VLAB, open the root directory of the repository in VSCode, and click the *"Run"* link above the *main* method of [*LoopManiaApplication.java*](src/unsw/loopmania/LoopManiaApplication.java)

Note that this works because *lib/symlink_javafx* is a symbolic link to a copy of JavaFX in the cs2511 class account, and you should have installed the below VSCode extensions during labs. To make this work on a non-CSE computer, you will need to follow some of the steps below under [Setup of Libraries on Your Home Computer](#setup-of-libraries-on-your-home-computer).

The recommended VSCode extensions for this project are:

* Java Extension Pack - enables having "Run" links for your application, and "Run Test" links your JUnit tests, from within VSCode so you don't have to run a compiler command
* Code Runner - enables running snippets of code
* Draw.io Integration extensions - helps for drawing diagrams offline

We recommend you **do not** use the `VSCode SSH` extension, because the Java Extension pack does not work with this due to a bug in the `VSCode SSH` extension.

**IMPORTANT**: Please do not push the contents of the *lib*, *bin*, *build*, or *.gradle* folders to your Gitlab repository. Similarly, please do not push compiled java *.class* files. Pushing these files or folders may cause your repository to hold large files in the commit history, which could make it slow for you, your team members, and staff, to clone, push to, and pull from your repository (even after deleting the files). Additionally, some of these are OS-dependent files/folders for *JavaFX*, so could interfere with your team members' ability to run *JavaFX* (they may have to repeat setup steps to be able to run the game).

### Setup of Libraries on Your Home Computer

If you cannot use VLAB, or prefer to use your home computer, you can install all libraries onto your computer locally. This may be particularly advantageous if you have slower or less reliable internet.

## 2. Installing JavaFX

Delete the *symlink_javafx* symbolic link, then download and unzip the latest version of the JavaFX JDK for Java 11 for your Operating System (taking into account if you have a 64 or 32 bit machine), and transfer the contents of the *lib* folder inside the JDK download into the *lib* folder on your cloned repository. You will also need to change the [*launch.json*](.vscode/launch.json) file to refer to **"./lib"** instead of **./lib/symlink_javafx** in the *"vmArgs"* configuration (note these modifications were tested on Windows 10) as per below;

```diff
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    // NOTE - we turn assertions on to help debugging, although they shouldn't be used in production code
    "version": "0.2.0",
    "cwd": "${workspaceRoot}",
    "configurations": [
        {
            "type": "java",
            "name": "CodeLens (Launch) - LoopManiaApplication",
            "request": "launch",
-           "vmArgs": "--module-path ./lib/symlink_javafx --add-modules javafx.controls,javafx.fxml -enableassertions",
+           "vmArgs": "--module-path ./lib --add-modules javafx.controls,javafx.fxml -enableassertions",
            "mainClass": "unsw.loopmania.LoopManiaApplication"
        }
    ]
}
```

You may also need to copy the contents of the *bin* folder in the unzipped JavaFX JDK download into a *bin* folder under the root directory of your cloned repository (e.g. for Windows).

The following version of the JavaFX JDK is recommended if you choose to run it on your computer, since it is the same version as on the CSE machines:

https://gluonhq.com/products/javafx/

Note that if you deviate from this precise directory structure, you may need to modify the VSCode configuration in [*launch.json*](.vscode/launch.json) to be able to run the game in VSCode.

If these steps worked (and you setup java, and the recommended VSCode extensions properly), you should be able to run the starter code game.

## 3. Installing Gradle 5.4.1

Download the zip file from (download should start automatically): https://gradle.org/next-steps/?version=5.4.1&format=bin

You should follow the installation instructions provided:

https://gradle.org/install/#manually

**Linux**

For Linux users, note that you may have to edit the `~/.bashrc` file to permanently change the PATH variable by appending the line:

`export PATH=$PATH:/opt/gradle/gradle-5.4.1/bin`

After modifying the `~/.bashrc` file, you would also need to close all terminals and then make a new terminal (`~/.bashrc` runs when you start a terminal). Note here that `/opt/gradle` is the directory you chose to install gradle in, as specified in the [manual gradle install instructions](https://gradle.org/install/#manually) above.

**Mac**

For Mac users, you *may* need to add `/opt/gradle/gradle-5.4.1/bin` to your *PATH* environment variable by appending to `/etc/paths` instead of `~/.bashrc`, as described in this article: https://www.architectryan.com/2012/10/02/add-to-the-path-on-mac-os-x-mountain-lion/

**Windows**

For Windows users, you will probably need to add `/opt/gradle/gradle-5.4.1/bin` to your environment variables, as described in the above [manual gradle install instructions](https://gradle.org/install/#manually).

**Other Notes**

Note that Gradle 5.4.1 is the same version as on CSE machines. It has been chosen so that common syntax can be used for the `test.gradle` file to Jacoco coverage testing. We will run Gradle 5.4.1 and the provided `test.gradle` script to perform coverage checking of your submission for milestone 2, which will contribute towards your mark for testing - so you should check the coverage checking command works on a CSE machine (command provided below under [*Running coverage checking*](#6-running-coverage-checking)).

If these steps worked (and you setup java and JavaFX), you should be able to run the coverage checking (see [details below here](#6-running-coverage-checking)).

## 4. Directory Structures

We recommend you adhere to the provided layout of the directories, and add additional Java application files with new Java classes in the [*src/unsw/loopmania*](src/unsw/loopmania) folder, and add additional test files in the [*src/test*](src/test) folder. Deviating from this may result in you having to deal with Java package issues when running your code.

## 5. Repository Size

A likely reason for your project repository becoming very large is uploading Java class files, or JavaFX binaries in the *lib* or *bin* folders. You should not push these to your Gitlab repository - particularly since these folders/files may configured to your Operating System, which might not match your partner's configuration or the CSE machine. The [*.gitignore*](.gitignore) file has been configured to prevent pushing the contents of your *lib* or *bin* folders - please do not adjust this. Also remember that you should be making your code/tests work on the CSE machine, so there is no reason to push your JavaFX binaries.

A likely problem which could arise from a large project repository is that pushing/pulling/cloning from the repository will slow down drastically, particularly if your peers have a weak internet connection.

## 6. Running Coverage Checking

To run coverage checking, on a CSE machine in the root directory of your repository:

```bash
$ gradle test -b test.gradle
```

During the milestone 2 and 3 demos, your tutor will run the above command over VLAB to produce a coverage report to mark your tests, from the commit tagged in the submission's tag. You should ensure this command produces the coverage report correctly on VLAB when submitting.

The coverage checking report will be in: *build/reports/jacoco/test/html/index.html*

The test report will be in: *build/reports/tests/test/index.html*

Please do not push the *build* directory to your Gitlab repository. The [*.gitignore*](.gitignore) file has been configured to ensure this in the most recent version of this repository.

## 7. VSCode Technical Tips

* It is very important that you open the folder *containing* the *src* folder (e.g. *21T2-cs2511-project*) in VSCode. Otherwise the JavaFX "Run" link might not appear, you might not be able to run tests, and you may get errors saying imports such as JavaFX and JUnit cannot be found, or errors stating that the package doesn't match the expected package.
* Sometimes you may need to restart VSCode, change parent folder name, clean java language workspace, or add the root folder to workspace when VSCode claims the [*unsw.loopmania.LoopManiaApplication* class](src/unsw/loopmania/LoopManiaApplication.java) is not on the classpath or that the package should be "": https://stackoverflow.com/questions/48260426/vs-code-expected-java-package-name-error
* For JUnit in VSCode - it can be useful to run CTRL-SHIFT-P and run "Clean the Java language server workspace" if you are having issues with packages/tests recognition.
* For JUnit tests - for this to work in VSCode, it is important to make the test classes public and named after the file they are inside.
* "Debug: Select and Start Debugging" command in VSCode allows selecting the launch configuration you wish to run, if VSCode is not following your expected configuration in the file [*launch.json*](.vscode/launch.json) and instead making a new configuration in that file: https://code.visualstudio.com/docs/editor/debugging
* You can try right clicking applicable folders (such as the *loopmania* folder, but you might need to do it for others too) and click "Remove Folder from Java Source Path", then right click the "src" folder and click "Add Folder to Java Source Path". This might make VSCode recognise the correct package structure if it claims the package should be "".

## 8. Scenebuilder

You can run SceneBuilder on CSE lab machines with `2511 scenebuilder`. For other computers, you'll need to download SceneBuilder from [here](https://gluonhq.com/products/scene-builder/). To ensure compatibility with your Java 11 project, use the Java 11 version of SceneBuilder.

You may wish to install the VSCode extension *Scenebuilder Extension for Visual Studio Code*. This will enable you to run Scenebuilder for a particular file from VSCode, by right clicking the file and selecting "Open in Scenebuilder". If you wish to do this, after installing the extension, you will need to configure the Scenebuilder path in VSCode by pressing CTRL-SHIFT-P (to open the command palette), selecting "Configure Scene Builder path", and navigating to and selecting the Scenebuilder application on your machine (will vary by Operating System). On the CSE machine you would need to navigate to and select "/import/ravel/3/cs2511/bin/scenebuilder". Note that to navigate to the root directory "/" on a CSE machine, you need to select "+ Other Locations" at the bottom of the scrolling list on the left side of the file manager.

If Scenebuilder isn't working on VLAB when you run:

`2511 scenebuilder`

Try logging in via a different VLAB server - e.g.:

`vx6.cse.unsw.edu.au:5920`