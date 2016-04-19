Ceylon IDE for IntelliJ [![Join the chat at https://gitter.im/ceylon/ceylon-ide-intellij](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ceylon/ceylon-ide-intellij) [![Build Status](https://ci-ceylon.rhcloud.com/buildStatus/icon?job=ceylon.formatter)](https://ci-ceylon.rhcloud.com/job/ceylon.formatter)
=======================
# Status

This is an attempt at adding (awesome) support for the Ceylon programming language in IntelliJ IDEA.

# Requirements

This plugin is being written using IntelliJ 2016.1. It will work on both Community and Ultimate editions.

We have a [dedicated branch](https://github.com/ceylon/ceylon-ide-intellij/tree/141.x-compat) for older versions of IntelliJ (14/15) *and* the current version of Android Studio (1.5.1), which is based on IntelliJ 14.1. Please note that these older versions of the IntelliJ platform have slightly different APIs, which means a few things may not work as expected. In particular, they contain [a bug that is critical](https://youtrack.jetbrains.com/issue/IDEA-132606) to the correct loading of Ceylon classes, which has been fixed in IntelliJ 2016.1. We'll try to backport a fix on this branch for better user experience.

We embed a full Ceylon distribution in the plugin, so you won't necessarily have to download Ceylon separately. You may need a local repository though.

# Features

While we are working very hard at making this a great plugin, only a few features are working at the moment. Please note that the plugin is not currently ready for daily usage.

See the [Features](https://github.com/ceylon/ceylon-ide-intellij/wiki/Features) page for a detailed list of things that are working and that will be implemented in the future.

# Testing & Hacking

For the moment, we do not provide an official release package in the Jetbrains plugins repository, since it is still under early development.
However, installable zips of the current development version are generated and will be made availabe in an alternate plugin repository, on a regular basis (see below).
You can either install the Ceylon IDE for IntelliJ from there, or build it from sources.

## Common pre-requisites

- a _Community_ or _Ultimate_ version of **[IntelliJ 2016.1](http://www.jetbrains.com/idea/download/)**
- **OSX users**: this plugin needs a JRE 7+, but IntelliJ 14 requires a JDK 6 by default. To run Ceylon IDE, you can either:
  - download IntelliJ with a [custom JDK bundled](https://confluence.jetbrains.com/display/IntelliJIDEA/Previous+IntelliJ+IDEA+Releases)
  - download the standard version, install it, then modify `Info.plist` to [force a recent version of Java](https://intellij-support.jetbrains.com/hc/en-us/articles/206827547-Selecting-the-JDK-version-the-IDE-will-run-under)

## Install from the Development plugin repository

_This is the **simplest way** to install the Ceyon IDEA IntelliJ plugin._ 

Inside the Intellij IDEA environment, follow the instructions [there](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html) in order to add the following URL :

**IntelliJ 15/2016.1+ and Android Studio 2.0+:**
Preview builds:
```
http://downloads.ceylon-lang.org/ide/intellij/development/updatePlugins.xml
```

Nightly builds:
```
https://ci-ceylon.rhcloud.com/job/ceylon-ide-intellij/ws/out/installation-packages/updatePlugins.xml
```

**IntelliJ 14 and Android Studio 1.5:**
```
http://downloads.ceylon-lang.org/ide/intellij/development/legacy/updatePlugins.xml
```

to the list of custom plugin repositories.

Then, from this new repository, you should be able to install those 2 plugins at the same time :
- **CeylonIDEA**, the Ceylon development environment for IntelliJ IDEA
- **CeylonRuntime**, a required plugin that allows running Ceylon code inside an IntelliJ plugin. It is required by Ceylon IDE since it contains parts developped in Ceylon. 

After installing both plugins and restarting IntelliJ IDEA, you should have the ability to create Ceylon IntelliJ modules in which you can develop and run Ceylon code.

## Install from sources 

If you want to use the current version, or debug/hack this plugin, you will need to build it from sources:

1. Make sure you are using **IntelliJ 2016.1** (support for IntelliJ 14/15 and Android Studio is planned in a [separate branch](https://github.com/ceylon/ceylon-ide-intellij/tree/141.x-compat))

2. A clone of https://github.com/JetBrains/intellij-community is highly recommended for hacking since you will likely have to debug code from the IntelliJ platform

3. Ensure that the following IntelliJ plugins are installed and enabled in your IDEA instance:
    - **Plugin DevKit**, 
    - **UI Designer**, 
    - **PsiViewer** (optional but recommended)

4. Set up a clone of [`ceylon`](http://github.com/ceylon/ceylon) and all the other required dependencies:
    - `mkdir ceylon && cd ceylon`
    - `git clone https://github.com/ceylon/ceylon`
    - `cd ceylon/dist`
    - `ant setup-sdk setup-ide` (this will create sibling projects such as: `../ceylon-sdk`, `../ceylon-ide-common`, etc)

5. Edit `../ceylon-ide-intellij/plugin-ceylon-code/build.properties` and add the following line:
    - `ideaRoot=/Applications/IntelliJ IDEA 2016.1 CE.app/Contents/` (change the path to point to your local IntelliJ installation)

7. Build a full Ceylon distribution locally (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details):
    - In the `../ceylon/dist` directory run : `ant clean-all dist sdk intellij`
    - This will create a `dist` sub-directory in `../ceylon/dist`, with a local Ceylon distribution.
    - This will also compile everything needed to build Ceylon IDE (SDK, Java to Ceylon converter, ceylon-ide-common)
    - Finally, this will compile Ceylon IDE

8. Open the project `ceylon-ide-intellij` in IDEA. You might be requested to enter the value of 2 path variables (see next point).

9. In `Settings > Build, Execution, Deployment > Path Variable`, you should add a variabla named
`̀CEYLON_DIST` that points to `../ceylon/dist/dist`.

10. In the IDE's `Preferences > File Types`, under `Recognized File Types`, register `*.car` as `Archive files`

11. Go to `File > Project Structure > SDKs`

12. Click on the `+` icon and add a new `JDK` pointing to a *Java SDK 1.7*

13. Click on the `+` icon and add a new `IntelliJ Platform Plugin SDK` pointing to where IntelliJ is installed (the correct folder should be preselected)

14. In the `Project` part, set the `Project SDK` to the previously created IntelliJ plugin SDK

15. Set the `Project language level` to 7.0

16. Set the `Project compiler output` to the `./out` directory

17. Apply changes and close the settings dialog

18. In `Run > Edit configurations`, create a new run configuration with type `Plugin` and leave the default options

19. Run this configuration and enjoy writing Ceylon in IntelliJ!
