IntelliJ Plugin for Ceylon
==========================
# Status
This is an attempt at adding (awesome) support for the Ceylon programming language in IntelliJ IDEA.

# Requirements

This plugin is being written using IntelliJ EAP 13. It will work on both Community and Ultimate editions.
We do not guarantee that this plugin will work with previous versions of IntelliJ (12 and below), as we may use APIs that were introduced in version 13.

Only a subset of the Ceylon SDK (0.6.1) is embedded within the plugin, mainly to parse and highlight errors in `.ceylon` files. If you intend to compile/run a Ceylon project, you will also need to download the latest Ceylon SDK (currently 0.6.1).

# Features

While we are working very hard at making this a great plugin, only a few features are working at the moment:

- `.ceylon` files recognition
- parsing and syntax highlighting
- project make / file compilation
- running a top-level method
- nifty lang features (code commenting, braces/quotes matching, code folding, structure viewing)

Other features will come soon!

# Testing & Hacking

For the moment, we do not provide any pre-built version of the plugin, since it is under heavy development. If you want to try it, here are the steps to follow:

If you want to start testing or hacking on this plugin, you will need:

- a Community or Ultimate version of **[IntelliJ EAP 13](http://confluence.jetbrains.com/display/IDEADEV/IDEA+13+EAP)**
- the following plugins enabled: "Plugin DevKit", "UI Designer" + "UI Designer (core)", "PsiViewer" (optional but recommended)
- a clone of https://github.com/JetBrains/intellij-community/ is highly recommended for hacking since you will likely have to debug code from the IntelliJ platform
- a clone of https://github.com/ceylon/ceylon-ide-intellij (obviously)

# Building & running the plugin

- make sure you are using **IntelliJ 13** (won't work with the current stable version 12.x)
- open the project `ceylon-ide-intellij` in IDEA
- go to `File > Project Structure > SDKs`
- click on the '+' icon and add a new `JDK` pointing to a Java SDK 1.7
- click on the '+' icon and add a new `IntelliJ Platform Plugin SDK` pointing to where IntelliJ is installed (the correct folder should be preselected)
- in the `Project` part, set the `Project SDK` to the previously created IntelliJ plugin SDK
- set the `Project language level` to 7.0
- set the `Project compiler output` to any directory you want (for example `out`)
- apply changes and close the settings dialog
- in the IDE's `Preferences > File Types`, under `Recognized File Types`, register `*.car` as `Archive files`
- in `Run > Edit configurations`, create a new run configuration with type `Plugin` and leave the default options
- run this configuration and enjoy writing Ceylon in IntelliJ!

We tried to reduce as much as possible the number of external dependencies, the plugin should be self-sufficient.
